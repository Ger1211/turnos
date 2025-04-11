package com.german.cabrera.turnos.dto.turnos;

import com.german.cabrera.turnos.model.Profesional;

public record ProfesionalDTO(
        Long id,
        String nombre,
        String especialidad
) {
    public static ProfesionalDTO from(Profesional profesional) {
        return new ProfesionalDTO(
                profesional.getId(),
                profesional.getNombre(),
                profesional.getEspecialidad()
        );
    }
}
