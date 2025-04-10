package com.german.cabrera.turnos.builder;

import com.german.cabrera.turnos.model.*;
import io.github.ger1211.builder.builder.AbstractPersistenceBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

public class TurnoBuilder extends AbstractPersistenceBuilder<Turno> {

    private TurnoBuilder(Disponibilidad disponibilidad, LocalDate fecha, LocalTime hora) {
        this.instance = Turno.builder()
                .disponibilidad(disponibilidad)
                .fecha(fecha)
                .hora(hora)
                .build();
    }

    public static TurnoBuilder basic(Disponibilidad disponibilidad, LocalDate fecha, LocalTime hora) {
        return new TurnoBuilder(disponibilidad, fecha, hora);
    }

    public TurnoBuilder withCliente(Cliente cliente) {
        this.instance.setCliente(cliente);
        return this;
    }
}
