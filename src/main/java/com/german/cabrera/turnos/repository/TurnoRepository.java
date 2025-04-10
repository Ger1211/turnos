package com.german.cabrera.turnos.repository;

import com.german.cabrera.turnos.model.Cliente;
import com.german.cabrera.turnos.model.Disponibilidad;
import com.german.cabrera.turnos.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    Optional<Turno> findByDisponibilidadAndFechaAndHora(Disponibilidad disponibilidad, LocalDate fecha, LocalTime hora);
    Optional<Turno> findByClienteAndFechaAndHora(Cliente cliente, LocalDate fecha, LocalTime hora);
}
