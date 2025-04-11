package com.german.cabrera.turnos.dto.turnos;

import lombok.Data;

@Data
public class CancelacionTurnoRequestDTO {
    private Long turnoId;
    private Long clienteId;
}
