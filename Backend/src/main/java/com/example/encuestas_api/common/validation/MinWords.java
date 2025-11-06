package com.example.encuestas_api.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = MinWordsValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface MinWords {
    int value();
    String message() default "No cumple con el número mínimo de palabras";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

