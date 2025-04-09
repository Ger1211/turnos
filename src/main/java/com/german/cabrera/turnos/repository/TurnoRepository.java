package com.german.cabrera.turnos.repository;

import com.german.cabrera.turnos.model.Profesional;
import com.german.cabrera.turnos.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    Optional<Turno> findByProfesionalAndFechaHora(Profesional profesional, LocalDateTime fechaHora);
}
