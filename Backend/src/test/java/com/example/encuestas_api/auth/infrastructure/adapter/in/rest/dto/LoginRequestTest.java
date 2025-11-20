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

    @Test
    @DisplayName("Debe aceptar un LoginRequest válido")
    void shouldPassValidationWithValidFields() {
        LoginRequest req = new LoginRequest("user@example.com", "password123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Debe fallar si el email o password son vacíos o nulos")
    void shouldFailIfFieldsAreEmptyOrNull() {
        LoginRequest emptyEmail = new LoginRequest("", "pass");
        LoginRequest nullEmail = new LoginRequest(null, "pass");
        LoginRequest emptyPass = new LoginRequest("user@example.com", "");
        LoginRequest nullPass = new LoginRequest("user@example.com", null);

        assertFalse(validator.validate(emptyEmail).isEmpty());
        assertFalse(validator.validate(nullEmail).isEmpty());
        assertFalse(validator.validate(emptyPass).isEmpty());
        assertFalse(validator.validate(nullPass).isEmpty());
    }
}
