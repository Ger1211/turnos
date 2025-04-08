package com.german.cabrera.turnos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Profesional {
    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private String especialidad;

    @OneToOne
    private Usuario usuario;
}

