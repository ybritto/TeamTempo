package com.ybritto.teamtempo.backend.core.utils;

import com.ybritto.teamtempo.backend.core.exception.InvalidParameterException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDValidator {

    public static List<UUID> validateAndTransform(final List<String> uuidList) {
        if (CollectionUtils.isEmpty(uuidList)) {
            throw new InvalidParameterException("UUID list can not be empty or null");
        }
        try {
            return uuidList.stream()
                    .map(UUIDValidator::validateAndTransform)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            throw new InvalidParameterException(String.format("UUID is invalid: %s", ex.getMessage()));
        }

    }

    public static UUID validateAndTransform(final String uuid) {
        if (!StringUtils.hasText(uuid)) {
            throw new InvalidParameterException("UUID can not be empty or null");
        }

        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException ex) {
            throw new InvalidParameterException(String.format("UUID %s is invalid", uuid));
        }

    }

}
