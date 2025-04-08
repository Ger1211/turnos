package com.german.cabrera.turnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
public class Disponibilidad {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    @ManyToOne
    private Profesional profesional;
}

