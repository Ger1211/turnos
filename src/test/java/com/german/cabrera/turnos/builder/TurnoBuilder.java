package com.german.cabrera.turnos.builder;

import com.german.cabrera.turnos.model.EstadoTurno;
import com.german.cabrera.turnos.model.Profesional;
import com.german.cabrera.turnos.model.Turno;
import io.github.ger1211.builder.builder.AbstractPersistenceBuilder;

import java.time.LocalDateTime;

public class TurnoBuilder extends AbstractPersistenceBuilder<Turno> {

    private TurnoBuilder(Profesional profesional, LocalDateTime fechaHora) {
        this.instance = new Turno();
        this.instance.setProfesional(profesional);
        this.instance.setFechaHora(fechaHora);
    }

    public static TurnoBuilder basic(Profesional profesional, LocalDateTime fechaHora) {
        return new TurnoBuilder(profesional, fechaHora);
    }

    public TurnoBuilder withEstado(EstadoTurno estadoTurno) {
        this.instance.setEstado(estadoTurno);
        return this;
    }
}
