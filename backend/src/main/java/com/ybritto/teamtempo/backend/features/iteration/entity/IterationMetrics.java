package com.ybritto.teamtempo.backend.features.iteration.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
public class IterationMetrics {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer capacity;
    private Integer forecast;
}
