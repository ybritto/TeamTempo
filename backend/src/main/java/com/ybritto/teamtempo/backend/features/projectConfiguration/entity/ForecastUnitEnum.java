package com.ybritto.teamtempo.backend.features.projectConfiguration.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ForecastUnitEnum {

    MAN_DAYS("Man Days", "MAN_DAYS");

    ForecastUnitEnum(String name, String type) {
        this.name = name;
        this.type = type;
    }

    private final String name;
    private final String type;

    public static ForecastUnitEnum findByType(String type) {
        if (type == null) {
            return null;
        }
        return Arrays.stream(ForecastUnitEnum.values())
                .filter(typeEnum -> typeEnum.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

}
