package com.ybritto.teamtempo.backend.core.utils;

import com.ybritto.teamtempo.backend.core.exception.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDValidator {

    private static final Logger logger = LoggerFactory.getLogger(UUIDValidator.class);

    public static List<UUID> validateAndTransform(final List<String> uuidList) {
        if (CollectionUtils.isEmpty(uuidList)) {
            logger.warn("UUID list validation failed: list is empty or null");
            throw new InvalidParameterException("UUID list can not be empty or null");
        }
        try {
            logger.debug("Validating and transforming {} UUIDs", uuidList.size());
            List<UUID> result = uuidList.stream()
                    .map(UUIDValidator::validateAndTransform)
                    .collect(Collectors.toList());
            logger.debug("Successfully validated and transformed {} UUIDs", result.size());
            return result;
        } catch (IllegalArgumentException ex) {
            logger.error("UUID list validation failed: {}", ex.getMessage());
            throw new InvalidParameterException(String.format("UUID is invalid: %s", ex.getMessage()));
        }

    }

    public static UUID validateAndTransform(final String uuid) {
        if (!StringUtils.hasText(uuid)) {
            logger.warn("UUID validation failed: UUID is empty or null");
            throw new InvalidParameterException("UUID can not be empty or null");
        }

        try {
            UUID result = UUID.fromString(uuid);
            logger.debug("Successfully validated UUID: {}", uuid);
            return result;
        } catch (IllegalArgumentException ex) {
            logger.error("UUID validation failed: {} - {}", uuid, ex.getMessage());
            throw new InvalidParameterException(String.format("UUID %s is invalid", uuid));
        }

    }

}
