package com.shoggoth.paymentprovider.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.YearMonth;

public class CardExpirationDateConstraintValidator implements ConstraintValidator<CardExpirationDate, YearMonth> {
    @Override
    public boolean isValid(YearMonth value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.isBefore(YearMonth.now());
    }
}
