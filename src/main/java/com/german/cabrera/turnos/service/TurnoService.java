package com.german.cabrera.turnos.service;

import com.german.cabrera.turnos.model.Cliente;
import com.german.cabrera.turnos.model.EstadoTurno;
import com.german.cabrera.turnos.model.Profesional;
import com.german.cabrera.turnos.model.Turno;
import com.german.cabrera.turnos.repository.ClienteRepository;
import com.german.cabrera.turnos.repository.ProfesionalRepository;
import com.german.cabrera.turnos.repository.TurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final ClienteRepository clienteRepository;
    private final ProfesionalRepository profesionalRepository;
    private final TurnoRepository turnoRepository;

    @Transactional
    public Turno reservar(Long clienteId, Long profesionalId, LocalDateTime fechaHora) {
        Profesional profesional = obtenerProfesional(profesionalId);
        Turno turno = obtenerTurnoDisponible(profesional, fechaHora);
        Cliente cliente = obtenerCliente(clienteId);

        turno.setCliente(cliente);
        turno.setEstado(EstadoTurno.RESERVADO);

        return turnoRepository.save(turno);
    }

    public void cancelar(Long turnoId, Long clienteId) {
        Turno turno = obtenerTurnoDeCliente(turnoId, clienteId);

        turno.setEstado(EstadoTurno.DISPONIBLE);
        turno.setCliente(null);

        turnoRepository.save(turno);
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

    private Turno obtenerTurnoDisponible(Profesional profesional, LocalDateTime fechaHora) {
        Turno turno = turnoRepository.findByProfesionalAndFechaHora(profesional, fechaHora)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado para ese profesional en esa fecha y hora"));

        if (turno.getEstado() != EstadoTurno.DISPONIBLE) {
            throw new IllegalStateException("El turno no está disponible");
        }

        return turno;
    }

    private Cliente obtenerCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

}
