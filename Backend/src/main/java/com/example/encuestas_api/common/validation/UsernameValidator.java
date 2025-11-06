package com.example.encuestas_api.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private static final String USERNAME_PATTERN = "^[a-zA-Z_][a-zA-Z0-9_]{2,}$";
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.matches(USERNAME_PATTERN);
    }
}
