package com.ybritto.teamtempo.backend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface CommonsMapper {

    default LocalDateTime map(OffsetDateTime value) {
        if (value == null) {
            return null;
        }
        return value.toLocalDateTime();
    }

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }

        // Specify a ZoneId (e.g., UTC or your local time zone)
        ZoneId zoneId = ZoneId.systemDefault();

        // Create a ZonedDateTime from the LocalDateTime and ZoneId
        ZonedDateTime zonedDateTime = value.atZone(zoneId);

        // Convert ZonedDateTime to OffsetDateTime
        return zonedDateTime.toOffsetDateTime();
    }

}
