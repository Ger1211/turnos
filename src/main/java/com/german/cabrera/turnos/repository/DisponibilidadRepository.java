package com.german.cabrera.turnos.repository;

import com.german.cabrera.turnos.model.Disponibilidad;
import com.german.cabrera.turnos.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    @Query("""
        SELECT d FROM Disponibilidad d
        WHERE d.profesional = :profesional
          AND d.dia = :dia
          AND :hora BETWEEN d.horaInicio AND d.horaFin
    """)
    Optional<Disponibilidad> findByProfesionalAndDiaAndHora(@Param("profesional") Profesional profesional,
                                                            @Param("dia") DayOfWeek dia,
                                                            @Param("hora") LocalTime hora);
}
