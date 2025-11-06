package com.example.encuestas_api.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinWordsValidator implements ConstraintValidator<MinWords, String> {
    private int minWords;
    @Override
    public void initialize(MinWords constraintAnnotation) {
        this.minWords = constraintAnnotation.value();
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        String[] words = value.trim().split("\\s+");
        return words.length >= minWords;
    }
}

