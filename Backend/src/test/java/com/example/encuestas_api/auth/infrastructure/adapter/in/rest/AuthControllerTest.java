package com.example.encuestas_api.auth.infrastructure.adapter.in.rest;

import com.example.encuestas_api.auth.application.dto.AuthResult;
import com.example.encuestas_api.auth.application.port.in.LoginUseCase;
import com.example.encuestas_api.auth.application.port.in.RegisterUseCase;
import com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.AuthResponse;
import com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.RegisterRequest;
import com.example.encuestas_api.users.domain.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private RegisterUseCase registerUC;
    private LoginUseCase loginUC;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        registerUC = mock(RegisterUseCase.class);
        loginUC = mock(LoginUseCase.class);
        controller = new AuthController(registerUC, loginUC);
    }

    // ===================================================================================
    // CASOS EXITOSOS
    // ===================================================================================

    @Test
    @DisplayName("Debería registrar un usuario exitosamente")
    void shouldRegisterUserSuccessfully() {
        RegisterRequest req = new RegisterRequest("user@example.com", "User Name", "pass123", false);

        AuthResult result = new AuthResult("Bearer", "token123", 3600L, 1L,
                "user@example.com", "User Name", false);
        when(registerUC.handle(req.email(), req.fullName(), req.password(), false))
                .thenReturn(result);

        ResponseEntity<AuthResponse> response = controller.register(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("token123", response.getBody().accessToken());
        assertEquals("Bearer", response.getBody().tokenType());
        verify(registerUC).handle(req.email(), req.fullName(), req.password(), false);
    }

    @Test
    @DisplayName("Debería loguear un usuario exitosamente")
    void shouldLoginSuccessfully() {
        LoginRequest req = new LoginRequest("user@example.com", "password123");

        AuthResult result = new AuthResult("Bearer", "tokenXYZ", 3600L, 99L,
                "user@example.com", "User", false);
        when(loginUC.handle(req.email(), req.password())).thenReturn(result);

        ResponseEntity<AuthResponse> response = controller.login(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("tokenXYZ", response.getBody().accessToken());
        assertEquals("user@example.com", response.getBody().email());
        verify(loginUC).handle(req.email(), req.password());
    }

    // ===================================================================================
    // CASOS DE ERROR / EXCEPCIONES
    // ===================================================================================

    @Test
    @DisplayName("Debería lanzar excepción al registrar usuario que ya existe")
    void shouldHandleUserAlreadyExistsExceptionOnRegister() {
        RegisterRequest req = new RegisterRequest("exists@example.com", "Existing User", "pass123", false);

        when(registerUC.handle(anyString(), anyString(), anyString(), anyBoolean()))
                .thenThrow(new UserAlreadyExistsException("exists@example.com"));

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> controller.register(req));

        assertTrue(ex.getMessage().contains("exists@example.com"));
        verify(registerUC).handle(req.email(), req.fullName(), req.password(), false);
    }

    @Test
    @DisplayName("Debería lanzar BadCredentialsException si el login falla")
    void shouldHandleBadCredentialsExceptionOnLogin() {
        LoginRequest req = new LoginRequest("user@example.com", "wrongPass");

        when(loginUC.handle(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> controller.login(req));

        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(loginUC).handle(req.email(), req.password());
    }

    @Test
    @DisplayName("Debería propagar excepciones inesperadas al registrar")
    void shouldPropagateUnexpectedExceptionsOnRegister() {
        RegisterRequest req = new RegisterRequest("fail@example.com", "User", "pass", false);

        when(registerUC.handle(anyString(), anyString(), anyString(), anyBoolean()))
                .thenThrow(new RuntimeException("Error inesperado"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.register(req));

        assertEquals("Error inesperado", ex.getMessage());
    }

    @Test
    @DisplayName("Debería propagar excepciones inesperadas al hacer login")
    void shouldPropagateUnexpectedExceptionsOnLogin() {
        LoginRequest req = new LoginRequest("user@example.com", "password");

        when(loginUC.handle(anyString(), anyString()))
                .thenThrow(new RuntimeException("Fallo interno"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.login(req));

        assertEquals("Fallo interno", ex.getMessage());
    }
}
