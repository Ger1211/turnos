package com.german.cabrera.turnos.builder;

import com.german.cabrera.turnos.model.Usuario;
import io.github.ger1211.builder.builder.AbstractPersistenceBuilder;

import java.util.Set;

public class UsuarioBuilder extends AbstractPersistenceBuilder<Usuario> {

    private UsuarioBuilder() {
        this.instance = new Usuario();
    }

    public static UsuarioBuilder basic() {
        return new UsuarioBuilder();
    }

    public UsuarioBuilder cliente() {
        instance.setRoles(Set.of("Cliente"));
        return this;
    }

    public UsuarioBuilder withEmail(String email) {
        instance.setEmail(email);
        return this;
    }

    public UsuarioBuilder withPassword(String password) {
        instance.setPassword(password);
        return this;
    }

}
