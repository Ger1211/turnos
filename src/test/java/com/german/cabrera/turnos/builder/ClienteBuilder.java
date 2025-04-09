package com.german.cabrera.turnos.builder;

import com.german.cabrera.turnos.model.Cliente;
import com.german.cabrera.turnos.model.Usuario;
import io.github.ger1211.builder.builder.AbstractPersistenceBuilder;

public class ClienteBuilder extends AbstractPersistenceBuilder<Cliente> {

    private ClienteBuilder(String nombre, Usuario usuario) {
        this.instance = new Cliente();
        this.instance.setNombre(nombre);
        this.instance.setUsuario(usuario);
    }

    public static ClienteBuilder basic(Usuario usuario) {
        return new ClienteBuilder("basico", usuario);
    }
}
