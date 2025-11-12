package com.example.encuestas_api.auth.infrastructure.adapter.in.rest;

import com.example.encuestas_api.auth.application.dto.AuthResult;
import com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.AuthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthRestMapperTest {

    // ======================================================
    // CASO NORMAL
    // ======================================================
    @Test
    @DisplayName("Debería mapear correctamente un AuthResult completo a AuthResponse")
    void shouldMapAuthResultToAuthResponse() {
        // given
        AuthResult result = new AuthResult(
                "Bearer",
                "token123",
                3600L,
                10L,
                "user@example.com",
                "John Doe",
                true
        );

        // when
        AuthResponse response = AuthRestMapper.toResponse(result);

        // then
        assertNotNull(response);
        assertEquals("Bearer", response.tokenType());
        assertEquals("token123", response.accessToken());
        assertEquals(3600L, response.expiresIn());
        assertEquals(10L, response.userId());
        assertEquals("user@example.com", response.email());
        assertEquals("John Doe", response.fullName());
        assertTrue(response.systemAdmin());
    }

    // ======================================================
    // CAMPOS NULOS
    // ======================================================
    @Test
    @DisplayName("Debería manejar AuthResult con valores nulos sin lanzar excepciones")
    void shouldHandleNullValuesGracefully() {
        // given
        AuthResult result = new AuthResult(
                null,  // tokenType
                null,  // accessToken
                0L,    // expiresIn
                null,  // userId
                null,  // email
                null,  // fullName
                false  // systemAdmin
        );

        // when
        AuthResponse response = AuthRestMapper.toResponse(result);

        // then
        assertNotNull(response);
        assertNull(response.tokenType());
        assertNull(response.accessToken());
        assertEquals(0L, response.expiresIn());
        assertNull(response.userId());
        assertNull(response.email());
        assertNull(response.fullName());
        assertFalse(response.systemAdmin());
    }

    // ======================================================
    // VALIDACIÓN DE CONSISTENCIA DE DATOS
    // ======================================================
    @Test
    @DisplayName("Debería mantener consistencia de datos entre AuthResult y AuthResponse")
    void shouldPreserveDataConsistency() {
        AuthResult result = new AuthResult(
                "JWT",
                "super-secret-token",
                9999L,
                99L,
                "admin@domain.com",
                "Admin User",
                false
        );

        AuthResponse response = AuthRestMapper.toResponse(result);

        // Validar igualdad exacta de los datos
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

    // ======================================================
    //  CASO EXTRA: AuthResult con valores límite
    // ======================================================
    @Test
    @DisplayName("Debería mapear correctamente AuthResult con valores límite")
    void shouldHandleEdgeCaseValues() {
        AuthResult result = new AuthResult(
                "",                   // tokenType vacío
                "",                   // token vacío
                Long.MAX_VALUE,       // tiempo máximo
                Long.MIN_VALUE,       // userId negativo
                "",                   // email vacío
                "",                   // nombre vacío
                false                 // systemAdmin falso
        );

        AuthResponse response = AuthRestMapper.toResponse(result);

        assertNotNull(response);
        assertEquals("", response.tokenType());
        assertEquals("", response.accessToken());
        assertEquals(Long.MAX_VALUE, response.expiresIn());
        assertEquals(Long.MIN_VALUE, response.userId());
        assertEquals("", response.email());
        assertEquals("", response.fullName());
        assertFalse(response.systemAdmin());
    }
}
