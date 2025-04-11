package com.german.cabrera.turnos.dto.turnos;

import com.german.cabrera.turnos.model.Turno;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoResponseDTO(
        Long id,
        String nombreProfesional,
        String nombreCliente,
        LocalDate fecha,
        LocalTime hora
) {

    public static TurnoResponseDTO from(Turno turno) {
        return new TurnoResponseDTO(
                turno.getId(),
                turno.getDisponibilidad().getProfesional().getNombre(),
                turno.getCliente().getNombre(),
                turno.getFecha(),
                turno.getHora()
        );
    }
}

