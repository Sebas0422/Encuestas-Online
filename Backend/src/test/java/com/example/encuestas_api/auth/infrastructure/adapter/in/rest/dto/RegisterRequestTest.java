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

class RegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe pasar validación con datos válidos")
    void shouldPassValidationWithValidFields() {
        RegisterRequest req = new RegisterRequest(
                "new@example.com",
                "New User",
                "StrongPass123!",
                true
        );
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Debe fallar si los campos obligatorios están vacíos")
    void shouldFailIfRequiredFieldsEmpty() {
        RegisterRequest req = new RegisterRequest("", "", "", false);
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Debe permitir systemAdmin nulo")
    void shouldAllowNullSystemAdmin() {
        RegisterRequest req = new RegisterRequest("email@ok.com", "Name", "Pass123", null);
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
        assertNull(req.systemAdmin());
    }

    @Test
    @DisplayName("Debe fallar si el fullName supera 255 caracteres")
    void shouldFailIfFullNameTooLong() {
        String longName = "A".repeat(256);
        RegisterRequest req = new RegisterRequest("user@domain.com", longName, "Pass123!", false);
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }
}
