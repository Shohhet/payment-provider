package com.shoggoth.paymentprovider.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Constraint(validatedBy = Alfa3CurrencyCodeConstraintValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Alfa3CurrencyCode {
    String message() default "must be Alfa-3 currency code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
