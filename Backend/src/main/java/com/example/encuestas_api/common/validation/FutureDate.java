package com.example.encuestas_api.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = FutureDateValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface FutureDate {
    String message() default "La fecha debe ser futura";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

