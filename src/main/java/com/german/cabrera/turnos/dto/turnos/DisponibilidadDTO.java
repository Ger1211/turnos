package com.german.cabrera.turnos.dto.turnos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DisponibilidadDTO {
    private Long profesionalId;
    private List<TurnoDTO> turnosDisponibles;
}
