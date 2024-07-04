package com.shoggoth.paymentprovider.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;
import java.util.Set;

public class Alfa2CountryCodeConstraintValidator implements ConstraintValidator<Alfa2CountryCode, String> {
    private static final Set<String> countryCodes = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return countryCodes.contains(value);
    }
}
