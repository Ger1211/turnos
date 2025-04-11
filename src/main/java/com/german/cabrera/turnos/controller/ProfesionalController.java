package com.german.cabrera.turnos.controller;

import com.german.cabrera.turnos.dto.turnos.ProfesionalDTO;
import com.german.cabrera.turnos.service.ProfesionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profesionales")
public class ProfesionalController {

    private final ProfesionalService profesionalService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> obtener(@RequestParam(required = false) Long profesionalId) {
        if (profesionalId != null) {
            return ResponseEntity.ok(ProfesionalDTO.from(profesionalService.obtener(profesionalId)));
        }
        return ResponseEntity.ok(profesionalService.obtenerTodos());
    }

    @GetMapping("/disponibilidades")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> consultarDisponilibilidad(@RequestParam Long profesionalId, @RequestParam DayOfWeek dia) {
        return ResponseEntity.ok(profesionalService.consultarDisponibilidad(profesionalId, dia));
    }
}
