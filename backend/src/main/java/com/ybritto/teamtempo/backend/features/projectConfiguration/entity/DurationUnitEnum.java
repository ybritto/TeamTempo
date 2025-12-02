package com.ybritto.teamtempo.backend.features.projectConfiguration.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DurationUnitEnum {

    DAYS("Days", "DAYS"),
    WEEKS("Weeks", "WEEKS"),
    MONTHS("Months", "MONTHS");

    DurationUnitEnum(String name, String type) {
        this.name = name;
        this.type = type;
    }

    private final String name;
    private final String type;

    public static DurationUnitEnum findByType(String type) {
        if (type == null) {
            return null;
        }
        return Arrays.stream(DurationUnitEnum.values())
                .filter(typeEnum -> typeEnum.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

}
