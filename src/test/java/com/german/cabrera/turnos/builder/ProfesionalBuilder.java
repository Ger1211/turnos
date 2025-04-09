package com.german.cabrera.turnos.builder;

import com.german.cabrera.turnos.model.Profesional;
import com.german.cabrera.turnos.model.Usuario;
import io.github.ger1211.builder.builder.AbstractPersistenceBuilder;

public class ProfesionalBuilder extends AbstractPersistenceBuilder<Profesional> {
    private ProfesionalBuilder(String nombre, Usuario usuario) {
        this.instance = new Profesional();
        this.instance.setNombre(nombre);
        this.instance.setUsuario(usuario);
    }

    public static ProfesionalBuilder basic(Usuario usuario) {
        return new ProfesionalBuilder("basico", usuario);
    }
}
