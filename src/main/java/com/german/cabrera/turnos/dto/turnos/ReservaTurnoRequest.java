package com.german.cabrera.turnos.dto.turnos;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaTurnoRequest {
    private Long clienteId;
    private Long profesionalId;
    private LocalDate fecha;
    private LocalTime hora;
}

