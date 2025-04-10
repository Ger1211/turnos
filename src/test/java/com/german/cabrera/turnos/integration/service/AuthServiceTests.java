package com.german.cabrera.turnos.integration.service;


import com.german.cabrera.turnos.builder.UsuarioBuilder;
import com.german.cabrera.turnos.dto.auth.AuthRequest;
import com.german.cabrera.turnos.dto.auth.AuthResponse;
import com.german.cabrera.turnos.service.AuthService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTests extends IntegrationTests {
    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Test
    void login_DeberiaGenerarTokenConCredencialesValidas() {
        String email = "usuario@test.com";
        String password = "123456";
        UsuarioBuilder.basic().cliente().withEmail(email).withPassword(passwordEncoder.encode(password)).build(entityManager);
        AuthRequest request = new AuthRequest(email, password);
        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertTrue(response.getToken().startsWith("eyJ"));
    }

    @Test
    void login_DeberiaFallarConCredencialesInvalidas() {
        String email = "usuario@test.com";
        String password = "123456";
        UsuarioBuilder.basic().cliente().withEmail(email).withPassword(passwordEncoder.encode(password)).build(entityManager);
        AuthRequest request = new AuthRequest("usuario@test.com", "wrong");

        Exception exception = assertThrows(Exception.class, () -> authService.login(request));

        assertTrue(exception.getMessage().contains("Bad credentials"));
    }
}
