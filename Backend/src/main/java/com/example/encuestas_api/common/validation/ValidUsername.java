package com.example.encuestas_api.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidUsername {
    String message() default "El nombre de usuario no es válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
