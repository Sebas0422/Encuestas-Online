package com.example.encuestas_api.auth.infrastructure.adapter.in.rest;

import com.example.encuestas_api.auth.application.dto.AuthResult;
import com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.AuthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthRestMapperTest {

    @Test
    @DisplayName("Debería mapear correctamente un AuthResult completo a AuthResponse")
    void shouldMapAuthResultToAuthResponse() {
        AuthResult result = new AuthResult(
                "Bearer",
                "token123",
                3600L,
                10L,
                "user@example.com",
                "John Doe",
                true
        );

        AuthResponse response = AuthRestMapper.toResponse(result);

        assertNotNull(response);
        assertEquals("Bearer", response.tokenType());
        assertEquals("token123", response.accessToken());
        assertEquals(3600L, response.expiresIn());
        assertEquals(10L, response.userId());
        assertEquals("user@example.com", response.email());
        assertEquals("John Doe", response.fullName());
        assertTrue(response.systemAdmin());
    }

    @Test
    @DisplayName("Debería manejar valores nulos sin lanzar excepciones")
    void shouldHandleNullValuesGracefully() {
        AuthResult result = new AuthResult(null, null, 0L, null, null, null, false);
        AuthResponse response = AuthRestMapper.toResponse(result);

        assertNotNull(response);
        assertNull(response.tokenType());
        assertNull(response.accessToken());
        assertEquals(0L, response.expiresIn());
        assertNull(response.userId());
        assertNull(response.email());
        assertNull(response.fullName());
        assertFalse(response.systemAdmin());
    }

    @Test
    @DisplayName("Debería mantener consistencia de datos entre AuthResult y AuthResponse")
    void shouldPreserveDataConsistency() {
        AuthResult result = new AuthResult(
                "JWT", "super-token", 9999L, 99L,
                "admin@domain.com", "Admin User", false
        );
        AuthResponse response = AuthRestMapper.toResponse(result);

        assertAll(
                () -> assertEquals(result.tokenType(), response.tokenType()),
                () -> assertEquals(result.accessToken(), response.accessToken()),
                () -> assertEquals(result.expiresIn(), response.expiresIn()),
                () -> assertEquals(result.userId(), response.userId()),
                () -> assertEquals(result.email(), response.email()),
                () -> assertEquals(result.fullName(), response.fullName()),
                () -> assertEquals(result.systemAdmin(), response.systemAdmin())
        );
    }
}
