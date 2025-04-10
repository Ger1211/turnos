package com.german.cabrera.turnos.controller;

import com.german.cabrera.turnos.dto.turnos.CancelarTurnoRequest;
import com.german.cabrera.turnos.dto.turnos.ReservaTurnoRequest;
import com.german.cabrera.turnos.dto.turnos.TurnoResponse;
import com.german.cabrera.turnos.model.Turno;
import com.german.cabrera.turnos.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @PostMapping("/reservaciones")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> reservar(@RequestBody ReservaTurnoRequest request) {
        Turno turno = turnoService.reservar(
                request.getClienteId(),
                request.getProfesionalId(),
                request.getFecha(),
                request.getHora()
        );

        return ResponseEntity.ok(TurnoResponse.from(turno));
    }

    @PostMapping("/cancelaciones")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<Void> cancelar(@RequestBody CancelarTurnoRequest request) {

        turnoService.cancelar(request.getTurnoId(), request.getClienteId());
        return ResponseEntity.noContent().build();
    }
}


