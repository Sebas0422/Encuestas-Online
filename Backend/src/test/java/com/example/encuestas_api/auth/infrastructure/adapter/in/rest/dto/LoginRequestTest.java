package com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ======================================================
    // CASO CORRECTO
    // ======================================================
    @Test
    @DisplayName("Debe aceptar un LoginRequest válido")
    void shouldPassValidationWithValidFields() {
        LoginRequest req = new LoginRequest("user@example.com", "password123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
        assertEquals("user@example.com", req.email());
        assertEquals("password123", req.password());
    }

    // ======================================================
    // EMAIL VACÍO O NULO
    // ======================================================
    @Test
    @DisplayName("Debe fallar si el email está vacío o es nulo")
    void shouldFailIfEmailIsEmptyOrNull() {
        LoginRequest emptyEmail = new LoginRequest("", "pass");
        Set<ConstraintViolation<LoginRequest>> v1 = validator.validate(emptyEmail);
        assertFalse(v1.isEmpty());

        LoginRequest nullEmail = new LoginRequest(null, "pass");
        Set<ConstraintViolation<LoginRequest>> v2 = validator.validate(nullEmail);
        assertFalse(v2.isEmpty());
    }

    // ======================================================
    // PASSWORD VACÍA O NULA
    // ======================================================
    @Test
    @DisplayName("Debe fallar si la contraseña está vacía o es nula")
    void shouldFailIfPasswordIsEmptyOrNull() {
        LoginRequest emptyPass = new LoginRequest("user@example.com", "");
        Set<ConstraintViolation<LoginRequest>> v1 = validator.validate(emptyPass);
        assertFalse(v1.isEmpty());

        LoginRequest nullPass = new LoginRequest("user@example.com", null);
        Set<ConstraintViolation<LoginRequest>> v2 = validator.validate(nullPass);
        assertFalse(v2.isEmpty());
    }
}
