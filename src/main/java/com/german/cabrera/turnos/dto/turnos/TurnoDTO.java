package com.german.cabrera.turnos.dto.turnos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class TurnoDTO {
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
