package com.shoggoth.paymentprovider.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.time.YearMonth;

public class CardExpirationDateConstraintValidator implements ConstraintValidator<CardExpirationDate, YearMonth> {
    @Override
    public boolean isValid(YearMonth value, ConstraintValidatorContext context) {
        return !value.isBefore(YearMonth.now());
    }
}
