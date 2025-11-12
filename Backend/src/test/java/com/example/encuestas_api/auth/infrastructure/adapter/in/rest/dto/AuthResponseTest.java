package com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    // CASO NORMAL
    @Test
    @DisplayName("Debe crear AuthResponse correctamente con valores válidos")
    void shouldCreateAuthResponse() {
        AuthResponse response = new AuthResponse(
                "Bearer",
                "token123",
                3600L,
                1L,
                "user@example.com",
                "John Doe",
                true
        );

        assertEquals("Bearer", response.tokenType());
        assertEquals("token123", response.accessToken());
        assertEquals(3600L, response.expiresIn());
        assertEquals(1L, response.userId());
        assertEquals("user@example.com", response.email());
        assertEquals("John Doe", response.fullName());
        assertTrue(response.systemAdmin());
    }

    // CAMPOS NULOS
    @Test
    @DisplayName("Debe permitir campos nulos sin lanzar excepciones")
    void shouldAllowNullFields() {
        AuthResponse response = new AuthResponse(null, null, 0, null, null, null, false);
        assertNull(response.tokenType());
        assertNull(response.accessToken());
        assertNull(response.email());
        assertNull(response.fullName());
    }

    // CONSISTENCIA DE DATOS
    @Test
    @DisplayName("Debe mantener consistencia de datos entre los campos")
    void shouldMaintainDataConsistency() {
        AuthResponse response = new AuthResponse(
                "JWT", "abc123", 9999L, 42L,
                "admin@domain.com", "Admin", false
        );

        assertAll(
                () -> assertEquals("JWT", response.tokenType()),
                () -> assertEquals("abc123", response.accessToken()),
                () -> assertEquals(9999L, response.expiresIn()),
                () -> assertEquals(42L, response.userId()),
                () -> assertEquals("admin@domain.com", response.email()),
                () -> assertEquals("Admin", response.fullName()),
                () -> assertFalse(response.systemAdmin())
        );
    }
}
