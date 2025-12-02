package com.ybritto.teamtempo.backend.features.projectConfiguration.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CapacityUnitEnum {

    STORY_POINTS("Story Points", "STORY_POINTS"),
    T_SHIRT("T Shirt Size", "T_SHIRT");

    CapacityUnitEnum(String name, String type) {
        this.name = name;
        this.type = type;
    }

    private final String name;
    private final String type;

    public static CapacityUnitEnum findByType(String type) {
        if (type == null) {
            return null;
        }
        return Arrays.stream(CapacityUnitEnum.values())
                .filter(typeEnum -> typeEnum.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

}
