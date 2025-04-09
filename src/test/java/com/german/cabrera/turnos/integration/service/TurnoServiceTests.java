package com.german.cabrera.turnos.integration.service;

import com.german.cabrera.turnos.builder.ClienteBuilder;
import com.german.cabrera.turnos.builder.ProfesionalBuilder;
import com.german.cabrera.turnos.builder.TurnoBuilder;
import com.german.cabrera.turnos.builder.UsuarioBuilder;
import com.german.cabrera.turnos.model.*;
import com.german.cabrera.turnos.repository.TurnoRepository;
import com.german.cabrera.turnos.service.TurnoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        LocalDateTime fechaHora = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(9, 30);
        TurnoBuilder.basic(profesional, fechaHora).withEstado(EstadoTurno.DISPONIBLE).build(entityManager);

        Turno turno = turnoService.reservar(cliente.getId(), profesional.getId(), fechaHora);

        assertNotNull(turno);
        assertEquals(cliente.getId(), turno.getCliente().getId());
        assertEquals(profesional.getId(), turno.getProfesional().getId());
        assertEquals(fechaHora, turno.getFechaHora());
        assertEquals(EstadoTurno.RESERVADO, turno.getEstado());

        Optional<Turno> turnoEnDB = turnoRepository.findById(turno.getId());
        assertTrue(turnoEnDB.isPresent());
    }

    @Test
    void reservarTurno_CuandoNoExiste_DeberiaLanzarExcepcion() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDateTime fechaHora = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(9, 30);
        TurnoBuilder.basic(profesional, fechaHora).withEstado(EstadoTurno.DISPONIBLE).build(entityManager);

        LocalDateTime fechaErronea = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).atTime(9, 30);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> turnoService.reservar(cliente.getId(), profesional.getId(), fechaErronea)
        );

        assertEquals("Turno no encontrado para ese profesional en esa fecha y hora", ex.getMessage());
    }

    @Test
    void reservar_CuandoProfesionalNoExiste_DeberiaLanzarExcepcion() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDateTime fechaHora = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(9, 30);
        TurnoBuilder.basic(profesional, fechaHora).withEstado(EstadoTurno.DISPONIBLE).build(entityManager);
        Long profesionalInexistente = -1L;

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> turnoService.reservar(cliente.getId(), profesionalInexistente, fechaHora)
        );

        assertEquals("Profesional no encontrado", ex.getMessage());
    }

    @Test
    void reservar_CuandoClienteNoExiste_DeberiaLanzarExcepcion() {
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDateTime fechaHora = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(9, 30);
        TurnoBuilder.basic(profesional, fechaHora).withEstado(EstadoTurno.DISPONIBLE).build(entityManager);

        Long clienteInexistente = -1L;

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> turnoService.reservar(clienteInexistente, profesional.getId(), fechaHora)
        );

        assertEquals("Cliente no encontrado", ex.getMessage());
    }

    @Test
    void reservarTurno_CuandoNoEstaDisponible_DeberiaLanzarExcepcion() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDateTime fechaHora = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(9, 30);
        TurnoBuilder.basic(profesional, fechaHora).withEstado(EstadoTurno.RESERVADO).build(entityManager);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> turnoService.reservar(cliente.getId(), profesional.getId(), fechaHora)
        );

        assertEquals("El turno no está disponible", ex.getMessage());
    }

    @Test
    void cancelarTurno_deberiaLiberarTurnoReservado() {
        Usuario usuario1 = UsuarioBuilder.basic().cliente().build(entityManager);
        Usuario usuario2 = UsuarioBuilder.basic().profesional().build(entityManager);
        Cliente cliente = ClienteBuilder.basic(usuario1).build(entityManager);
        Profesional profesional = ProfesionalBuilder.basic(usuario2).build(entityManager);
        LocalDateTime fechaHora = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(9, 30);
        Turno turno = TurnoBuilder.basic(profesional, fechaHora).withEstado(EstadoTurno.RESERVADO).withCliente(cliente).build(entityManager);

        turnoService.cancelar(turno.getId(), cliente.getId());

        Turno turnoCancelado = turnoRepository.findById(turno.getId())
                .orElseThrow();

        assertNull(turnoCancelado.getCliente());
        assertEquals(EstadoTurno.DISPONIBLE, turnoCancelado.getEstado());
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
        LocalDateTime fechaHora = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(9, 30);
        Turno turno = TurnoBuilder.basic(profesional, fechaHora).withEstado(EstadoTurno.RESERVADO).withCliente(cliente).build(entityManager);
        Cliente otroCliente = ClienteBuilder.basic(usuario1).build(entityManager);


        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                turnoService.cancelar(turno.getId(), otroCliente.getId())
        );

        assertEquals("El turno no pertenece al cliente", exception.getMessage());
    }


}
