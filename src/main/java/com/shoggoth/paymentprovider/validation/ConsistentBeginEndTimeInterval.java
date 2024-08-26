package com.shoggoth.paymentprovider.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ConsistentBeginEndTimeIntervalValidator.class)
@Target({METHOD, CONSTRUCTOR})
@Retention(RUNTIME)
public @interface ConsistentBeginEndTimeInterval {
    String message() default
            "End date must be after begin date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
