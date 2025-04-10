package com.german.cabrera.turnos.unit.service;

import com.german.cabrera.turnos.builder.UsuarioBuilder;
import com.german.cabrera.turnos.dto.auth.AuthRequest;
import com.german.cabrera.turnos.dto.auth.AuthResponse;
import com.german.cabrera.turnos.model.Usuario;
import com.german.cabrera.turnos.repository.UsuarioRepository;
import com.german.cabrera.turnos.security.service.JwtService;
import com.german.cabrera.turnos.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_DeberiaRetornarTokenSiCredencialesSonValidas() {
        String email = "test@example.com";
        String password = "password123";
        String token = "mocked-jwt-token";
        AuthRequest request = new AuthRequest(email, password);
        Usuario usuario = UsuarioBuilder.basic().withEmail(email).build();

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(usuario)).thenReturn(token);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(token, response.getToken());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        verify(jwtService).generarToken(usuario);
    }

    @Test
    void login_DeberiaLanzarExcepcionSiUsuarioNoExiste() {
        String email = "noexiste@example.com";
        String password = "123";
        AuthRequest request = new AuthRequest(email, password);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }
}

