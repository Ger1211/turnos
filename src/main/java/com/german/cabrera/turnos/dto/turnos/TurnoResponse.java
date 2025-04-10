package com.german.cabrera.turnos.dto.turnos;

import com.german.cabrera.turnos.model.Turno;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoResponse(
        Long id,
        String nombreProfesional,
        String nombreCliente,
        LocalDate fecha,
        LocalTime hora
) {

    public static TurnoResponse from(Turno turno) {
        return new TurnoResponse(
                turno.getId(),
                turno.getDisponibilidad().getProfesional().getNombre(),
                turno.getCliente().getNombre(),
                turno.getFecha(),
                turno.getHora()
        );
    }
}

