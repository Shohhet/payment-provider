package com.shoggoth.paymentprovider.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class Alfa3CurrencyCodeConstraintValidator implements ConstraintValidator<Alfa3CurrencyCode, String> {
    private final Set<String> currencies = Currency.getAvailableCurrencies().stream().map(Currency::getCurrencyCode).collect(Collectors.toSet());

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return currencies.contains(value);
    }
}
