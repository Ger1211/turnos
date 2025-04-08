package com.german.cabrera.turnos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Cliente {
    @Id
    @GeneratedValue
    private Long id;

    private String nombre;

    @OneToOne
    private Usuario usuario;

}

