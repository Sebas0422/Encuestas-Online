package com.example.encuestas_api.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = NoWhitespaceValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface NoWhitespace {
    String message() default "No debe contener espacios en blanco al inicio o final";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

