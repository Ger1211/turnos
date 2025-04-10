package com.german.cabrera.turnos.integration.service;

import com.german.cabrera.turnos.builder.*;
import com.german.cabrera.turnos.model.*;
import com.german.cabrera.turnos.repository.TurnoRepository;
import com.german.cabrera.turnos.service.TurnoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TurnoServiceTests extends IntegrationTests {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private TurnoRepository turnoRepository;

    @Test
    void reservar_ReservaExitosa() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9).withMinute(0);
        DisponibilidadBuilder.basic(profesional, fecha.getDayOfWeek(), hora, hora.plusHours(8)).build(entityManager);

        Turno turno = turnoService.reservar(cliente.getId(), profesional.getId(), fecha, hora);

        assertNotNull(turno);
        assertEquals(cliente.getId(), turno.getCliente().getId());
        assertEquals(profesional.getId(), turno.getDisponibilidad().getProfesional().getId());
        assertEquals(fecha, turno.getFecha());
        assertEquals(hora, turno.getHora());

        Optional<Turno> turnoEnDB = turnoRepository.findById(turno.getId());
        assertTrue(turnoEnDB.isPresent());
    }

    @Test
    void reservarTurno_CuandoNoHayDisponibilidad_DeberiaLanzarExcepcion() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9).withMinute(0);
        DisponibilidadBuilder.basic(profesional, fecha.getDayOfWeek(), hora, hora.plusHours(8)).build(entityManager);

        LocalDate fechaErronea = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> turnoService.reservar(cliente.getId(), profesional.getId(), fechaErronea, hora)
        );

        assertEquals("El profesional no tiene disponiblidad", ex.getMessage());
    }

    @Test
    void reservar_CuandoProfesionalNoExiste_DeberiaLanzarExcepcion() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9).withMinute(0);

        Long profesionalInexistente = -1L;

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> turnoService.reservar(cliente.getId(), profesionalInexistente, fecha, hora)
        );

        assertEquals("Profesional no encontrado", ex.getMessage());
    }

    @Test
    void reservar_CuandoClienteNoExiste_DeberiaLanzarExcepcion() {
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9).withMinute(0);
        DisponibilidadBuilder.basic(profesional, fecha.getDayOfWeek(), hora, hora.plusHours(8)).build(entityManager);

        Long clienteInexistente = -1L;

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> turnoService.reservar(clienteInexistente, profesional.getId(), fecha, hora)
        );

        assertEquals("Cliente no encontrado", ex.getMessage());
    }

    @Test
    void reservarTurno_CuandoNoEstaDisponible_DeberiaLanzarExcepcion() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Usuario usuario3 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Cliente otroCliente = ClienteBuilder.basic(usuario3).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9).withMinute(0);
        Disponibilidad disponibilidad = DisponibilidadBuilder.basic(profesional, fecha.getDayOfWeek(), hora, hora.plusHours(8)).build(entityManager);
        TurnoBuilder.basic(disponibilidad, fecha, hora).withCliente(otroCliente).build(entityManager);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> turnoService.reservar(cliente.getId(), profesional.getId(), fecha, hora)
        );

        assertEquals("El turno no está disponible", exception.getMessage());
    }

    @Test
    void reservarTurno_clienteYaTieneTurnoEnLaMismaFechaYHora_debeLanzarExcepcion() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Usuario usuario3 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional1 = ProfesionalBuilder.basic(usuario2).build(entityManager);
        Profesional profesional2 = ProfesionalBuilder.basic(usuario3).build(entityManager);

        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9).withMinute(0);

        Disponibilidad disponibilidad = DisponibilidadBuilder.basic(profesional1, fecha.getDayOfWeek(), hora, hora.plusHours(8)).build(entityManager);
        DisponibilidadBuilder.basic(profesional2, fecha.getDayOfWeek(), hora, hora.plusHours(8)).build(entityManager);

        TurnoBuilder.basic(disponibilidad, fecha, hora).withCliente(cliente).build(entityManager);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                turnoService.reservar(cliente.getId(), profesional2.getId(), fecha, hora)
        );

        assertEquals("El cliente ya tiene un turno reservado en esa fecha y hora", exception.getMessage());
    }

    @Test
    void reservarTurno_FechaPasada_deberiaLanzarExcepcion() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);

        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9);

        DisponibilidadBuilder.basic(profesional, fecha.getDayOfWeek(), hora, hora.plusHours(8)).build(entityManager);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                turnoService.reservar(cliente.getId(), profesional.getId(), fecha, hora)
        );

        assertEquals("No se puede reservar un turno en una fecha pasada", exception.getMessage());
    }


    @Test
    void cancelarTurno_deberiaLiberarTurnoReservado() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9);
        Disponibilidad disponibilidad = DisponibilidadBuilder.basic(profesional, fecha.getDayOfWeek(), hora, hora.plusHours(8)).build(entityManager);
        Turno turno = TurnoBuilder.basic(disponibilidad, fecha, hora).withCliente(cliente).build(entityManager);

        turnoService.cancelar(turno.getId(), cliente.getId());

        Optional<Turno> turnoEnDB = turnoRepository.findById(turno.getId());
        assertFalse(turnoEnDB.isPresent());
    }

    @Test
    void cancelarTurno_deberiaLanzarExcepcion_SiTurnoNoExiste() {
        Usuario usuario = UsuarioBuilder.basic().cliente().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario).build(entityManager);
        Long turnoIdInexistente = -1L;

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                turnoService.cancelar(turnoIdInexistente, cliente.getId())
        );

        assertEquals("Turno no encontrado", exception.getMessage());
    }

    @Test
    void cancelarTurno_deberiaLanzarExcepcion_SiTurnoNoPerteneceAlCliente() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        LocalTime hora = LocalTime.now().withHour(9);
        Disponibilidad disponibilidad = DisponibilidadBuilder.basic(profesional, fecha.getDayOfWeek(), hora, hora.plusHours(8)).build(entityManager);
        Turno turno = TurnoBuilder.basic(disponibilidad, fecha, hora).withCliente(cliente).build(entityManager);
        Cliente otroCliente = ClienteBuilder.basic(usuario1).build(entityManager);


        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                turnoService.cancelar(turno.getId(), otroCliente.getId())
        );

        assertEquals("El turno no pertenece al cliente", exception.getMessage());
    }


}
