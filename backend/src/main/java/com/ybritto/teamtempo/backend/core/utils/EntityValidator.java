package com.ybritto.teamtempo.backend.core.utils;

import com.ybritto.teamtempo.backend.core.exception.EntityValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class EntityValidator {

    private static final Logger logger = LoggerFactory.getLogger(EntityValidator.class);

    public static <T> void validateEntity(T entity) {
        logger.debug("Validating entity: {}", entity.getClass().getSimpleName());
        Set<ConstraintViolation<T>> validate = validate(entity);
        if (validate.isEmpty()) {
            logger.debug("Entity validation passed: {}", entity.getClass().getSimpleName());
            return;
        }

        StringBuilder validationMessage = new StringBuilder();
        validate.forEach(val -> validationMessage.append(val.getMessage()).append(";"));

        String errorMessage = validationMessage.toString();
        logger.warn("Entity validation failed for {}: {}", entity.getClass().getSimpleName(), errorMessage);
        throw new EntityValidationException(errorMessage);
    }

    private static <T> Set<ConstraintViolation<T>> validate(T entity) {

        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
            return validator.validate(entity);
        }
    }

}
