package com.shoggoth.paymentprovider.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class ConsistentBeginEndTimeIntervalValidator implements ConstraintValidator<ConsistentBeginEndTimeInterval, Object[]> {

    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        if (value[0] == null || value[1] == null) {
            return false;
        }
        if (value[0] instanceof Long && value[1] instanceof Long) {
            return (Long) value[0] < (Long) value[1];
        } else {
            throw new IllegalArgumentException("Illegal method signature, expected two parameters of type Long.");
        }
    }
}
