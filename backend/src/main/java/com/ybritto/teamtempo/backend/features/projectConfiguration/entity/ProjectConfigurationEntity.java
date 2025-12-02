package com.ybritto.teamtempo.backend.features.projectConfiguration.entity;

import com.ybritto.teamtempo.backend.features.project.entity.ProjectEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true) // toBuilder is true to facilitate object copy/transformation to perform IT tests
@AllArgsConstructor // All Args constructor is needed for builder
@NoArgsConstructor // No args constructor is needed for JPA specification
@Getter
@EqualsAndHashCode(of = {"uuid"})
@Entity
@Table(
        name = "project_configuration",
        uniqueConstraints = {
                @UniqueConstraint(name = "project_configuration_uuid_unique", columnNames = {"uuid"})
        })
public class ProjectConfigurationEntity {

    @Id
    @Column(name = "key_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projectConfigurationSeqGen")
    @SequenceGenerator(name = "projectConfigurationSeqGen", sequenceName = "project_configuration_key_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "iteration_duration", nullable = false)
    private Integer iterationDuration;

    @Column(name = "iteration_duration_unit", nullable = false, length = 20)
    @NotNull(message = "Iteration Duration can not be null")
    @Enumerated(EnumType.STRING)
    private DurationUnitEnum iterationDurationUnit;

    @Column(name = "capacity_unit", nullable = false, length = 20)
    @NotNull(message = "Capacity Unit can not be null")
    @Enumerated(EnumType.STRING)
    private CapacityUnitEnum capacityUnit;

    @Column(name = "forcast_unit", nullable = false, length = 20)
    @NotNull(message = "Forecast Unit can not be null")
    @Enumerated(EnumType.STRING)
    private ForecastUnitEnum forecastUnit;

    @Column(name = "is_active", nullable = false)
    private boolean active = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull(message = "Project can not be null")
    private ProjectEntity project;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    private void prePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
