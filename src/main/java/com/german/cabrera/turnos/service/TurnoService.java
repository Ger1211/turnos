package com.german.cabrera.turnos.service;

import com.german.cabrera.turnos.model.*;
import com.german.cabrera.turnos.repository.ClienteRepository;
import com.german.cabrera.turnos.repository.DisponibilidadRepository;
import com.german.cabrera.turnos.repository.ProfesionalRepository;
import com.german.cabrera.turnos.repository.TurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final ClienteRepository clienteRepository;
    private final ProfesionalRepository profesionalRepository;
    private final TurnoRepository turnoRepository;
    private final DisponibilidadRepository disponibilidadRepository;

    @Transactional
    public Turno reservar(Long clienteId, Long profesionalId, LocalDate fecha, LocalTime hora) {
        validarFecha(fecha, hora);

        Profesional profesional = obtenerProfesional(profesionalId);
        Disponibilidad disponibilidad = obtenerDisponibilidad(profesional, fecha, hora);
        Cliente cliente = obtenerCliente(clienteId);

        validarTurnoOcupado(disponibilidad, fecha, hora);
        validarTurnoSuperpuesto(cliente, fecha, hora);

        Turno turno = Turno.builder()
                .fecha(fecha)
                .hora(hora)
                .cliente(cliente)
                .disponibilidad(disponibilidad)
                .build();

        return turnoRepository.save(turno);
    }

    public void cancelar(Long turnoId, Long clienteId) {
        Turno turno = obtenerTurnoDeCliente(turnoId, clienteId);

        turnoRepository.delete(turno);
    }

    private Turno obtenerTurnoDeCliente(Long turnoId, Long clienteId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));

        if(!turno.getCliente().getId().equals(clienteId)) {
            throw new IllegalStateException("El turno no pertenece al cliente");
        }
        return turno;
    }

    private Profesional obtenerProfesional(Long profesionalId) {
        return profesionalRepository.findById(profesionalId)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
    }

    private Cliente obtenerCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    private Disponibilidad obtenerDisponibilidad(Profesional profesional, LocalDate fecha, LocalTime hora) {
        return disponibilidadRepository.findByProfesionalAndDiaAndHora(profesional, fecha.getDayOfWeek(), hora)
                .orElseThrow(() -> new EntityNotFoundException("El profesional no tiene disponiblidad"));
    }

    private void validarTurnoOcupado(Disponibilidad disponibilidad, LocalDate fecha, LocalTime hora) {
        boolean turnoOcupado = turnoRepository
                .findByDisponibilidadAndFechaAndHora(disponibilidad, fecha, hora)
                .isPresent();

        if (turnoOcupado) {
            throw new IllegalStateException("El turno no está disponible");
        }
    }

    private void validarTurnoSuperpuesto(Cliente cliente, LocalDate fecha, LocalTime hora) {
        boolean clienteTieneTurnoEnEsaFecha = turnoRepository
                .findByClienteAndFechaAndHora(cliente, fecha, hora)
                .isPresent();

        if (clienteTieneTurnoEnEsaFecha) {
            throw new IllegalStateException("El cliente ya tiene un turno reservado en esa fecha y hora");
        }
    }

    private void validarFecha(LocalDate fecha, LocalTime hora) {
        if (fecha.atTime(hora).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede reservar un turno en una fecha pasada");
        }
    }

}
