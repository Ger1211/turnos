package com.german.cabrera.turnos.integration.service;

import com.german.cabrera.turnos.builder.*;
import com.german.cabrera.turnos.dto.turnos.DisponibilidadDTO;
import com.german.cabrera.turnos.dto.turnos.TurnoDTO;
import com.german.cabrera.turnos.model.Cliente;
import com.german.cabrera.turnos.model.Disponibilidad;
import com.german.cabrera.turnos.model.Profesional;
import com.german.cabrera.turnos.model.Usuario;
import com.german.cabrera.turnos.service.ProfesionalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfesionalServiceTests extends IntegrationTests {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProfesionalService profesionaService;

    @Test
    void consultarDisponibilidad_obtieneDisponibilidadDeProfesional() {
        Usuario usuario = UsuarioBuilder.basic().cliente().build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario).build(entityManager);
        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9).withMinute(0);
        long cantidadDeTurnos = 8;
        DisponibilidadBuilder.basic(profesional, fecha.getDayOfWeek(), hora, hora.plusHours(cantidadDeTurnos)).build(entityManager);

        DisponibilidadDTO disponiblidad = profesionaService.consultarDisponibilidad(profesional.getId(), fecha.getDayOfWeek());

        assertNotNull(disponiblidad);
        assertEquals(disponiblidad.getTurnosDisponibles().get(0).getHoraInicio(), hora);
        assertEquals(disponiblidad.getTurnosDisponibles().get(disponiblidad.getTurnosDisponibles().size()-1).getHoraFin(), hora.plusHours(cantidadDeTurnos));
    }

    @Test
    void consultarDisponibilidad_profesionalNoExiste_lanzaExcepcion() {
        Long idInexistente = -1L;

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            profesionaService.consultarDisponibilidad(idInexistente, LocalDate.now().getDayOfWeek());
        });

        assertEquals("Profesional no encontrado", ex.getMessage());
    }

    @Test
    void consultarDisponibilidad_sinDisponibilidad_lanzaExcepcion() {
        Usuario usuario = UsuarioBuilder.basic().profesional().build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario).build(entityManager);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            profesionaService.consultarDisponibilidad(profesional.getId(), LocalDate.now().getDayOfWeek());
        });

        assertEquals("El profesional no tiene disponibilidad.", ex.getMessage());
    }

    @Test
    void consultarDisponibilidad_turnosOcupados_noIncluyeHorasReservadas() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().cliente().build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario1).build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario2).build(entityManager);

        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalTime horaInicio = LocalTime.of(9, 0);
        LocalTime horaFin = horaInicio.plusHours(3);

        Disponibilidad disponibilidad = DisponibilidadBuilder.basic(profesional, fecha.getDayOfWeek(), horaInicio, horaFin)
                .build(entityManager);

        TurnoBuilder.basic(disponibilidad, fecha, horaInicio.plusHours(1)).withCliente(cliente).build(entityManager);

        DisponibilidadDTO result = profesionaService.consultarDisponibilidad(profesional.getId(), fecha.getDayOfWeek());

        List<LocalTime> horasDevueltas = result.getTurnosDisponibles().stream()
                .map(TurnoDTO::getHoraInicio)
                .toList();

        assertFalse(horasDevueltas.contains(LocalTime.of(10, 0)));
        assertTrue(horasDevueltas.contains(LocalTime.of(9, 0)));
        assertTrue(horasDevueltas.contains(LocalTime.of(11, 0)));
    }
}
