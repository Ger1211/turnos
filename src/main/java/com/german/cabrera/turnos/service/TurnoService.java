package com.german.cabrera.turnos.service;

import com.german.cabrera.turnos.dto.turnos.DisponibilidadDTO;
import com.german.cabrera.turnos.dto.turnos.TurnoDTO;
import com.german.cabrera.turnos.model.Cliente;
import com.german.cabrera.turnos.model.Disponibilidad;
import com.german.cabrera.turnos.model.Profesional;
import com.german.cabrera.turnos.model.Turno;
import com.german.cabrera.turnos.repository.ClienteRepository;
import com.german.cabrera.turnos.repository.DisponibilidadRepository;
import com.german.cabrera.turnos.repository.ProfesionalRepository;
import com.german.cabrera.turnos.repository.TurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public DisponibilidadDTO consultarDisponibilidad(Long profesionalId, DayOfWeek dia) {
        Profesional profesional = obtenerProfesional(profesionalId);
        Disponibilidad disponibilidad = disponibilidadRepository.findByProfesionalAndDia(profesional, dia)
                .orElseThrow(() -> new EntityNotFoundException("El profesional no tiene disponibilidad."));
        List<Turno> turnos = turnoRepository.findByDisponibilidad_Profesional(profesional);
        return obtenerTodaDisponibilidad(disponibilidad, turnos);
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

    private DisponibilidadDTO obtenerTodaDisponibilidad(Disponibilidad disponibilidad, List<Turno> turnosReservados) {
        Set<LocalTime> horasReservadas = turnosReservados.stream()
                .map(Turno::getHora)
                .collect(Collectors.toSet());

        List<TurnoDTO> turnosDisponibles = generarTurnosDisponibles(disponibilidad, horasReservadas);

        return DisponibilidadDTO.builder()
                .profesionalId(disponibilidad.getProfesional().getId())
                .turnosDisponibles(turnosDisponibles)
                .build();
    }

    private List<TurnoDTO> generarTurnosDisponibles(Disponibilidad disponibilidad, Set<LocalTime> horasReservadas) {
        List<TurnoDTO> turnos = new ArrayList<>();

        LocalTime hora = disponibilidad.getHoraInicio();
        while (!hora.plusHours(1).isAfter(disponibilidad.getHoraFin())) {
            if (!horasReservadas.contains(hora)) {
                TurnoDTO turno = TurnoDTO.builder()
                        .horaInicio(hora)
                        .horaFin(hora.plusHours(1))
                        .build();
                turnos.add(turno);
            }
            hora = hora.plusHours(1);
        }

        return turnos;
    }
}
