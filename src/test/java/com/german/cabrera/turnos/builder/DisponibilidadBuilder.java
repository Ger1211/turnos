package com.german.cabrera.turnos.builder;

import com.german.cabrera.turnos.model.Disponibilidad;
import com.german.cabrera.turnos.model.Profesional;
import io.github.ger1211.builder.builder.AbstractPersistenceBuilder;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class DisponibilidadBuilder extends AbstractPersistenceBuilder<Disponibilidad> {

    private DisponibilidadBuilder(Profesional profesional, DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin) {
        this.instance = Disponibilidad.builder()
                .profesional(profesional)
                .dia(dia)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .build();
    }

    public static DisponibilidadBuilder basic(Profesional profesional, DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin) {
        return new DisponibilidadBuilder(profesional, dia, horaInicio, horaFin);
    }
}
