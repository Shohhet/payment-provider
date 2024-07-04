package com.shoggoth.paymentprovider.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CardExpirationDateConstraintValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CardExpirationDate {
    String message() default "Payment card was expired";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
