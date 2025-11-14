package com.ybritto.teamtempo.backend.core.utils;

import com.ybritto.teamtempo.backend.core.exception.EntityValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class EntityValidator {

    public static <T> void validateEntity(T entity) {
        Set<ConstraintViolation<T>> validate = validate(entity);
        if (validate.isEmpty()) {
            return;
        }

        StringBuilder validationMessage = new StringBuilder();
        validate.forEach(val -> validationMessage.append(val.getMessage()).append(";"));

        throw new EntityValidationException(validationMessage.toString());
    }

    private static <T> Set<ConstraintViolation<T>> validate(T entity) {

        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
            return validator.validate(entity);
        }
    }

}
