package com.shoggoth.paymentprovider.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;
import java.util.Set;

public class Alfa2LanguageCodeConstraintValidator implements ConstraintValidator<Alfa2LanguageCode, String> {
    private final Set<String> languages = Set.of(Locale.getISOLanguages());
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return languages.contains(value);
    }
}
