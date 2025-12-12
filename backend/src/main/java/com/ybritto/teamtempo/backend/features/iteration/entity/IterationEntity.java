package com.ybritto.teamtempo.backend.features.iteration.entity;

import com.ybritto.teamtempo.backend.features.project.entity.ProjectEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        name = "iteration",
        uniqueConstraints = {
                @UniqueConstraint(name = "iteration_uuid_unique", columnNames = {"uuid"})
        })
public class IterationEntity {

    @Id
    @Column(name = "key_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "iterationSeqGen")
    @SequenceGenerator(name = "iterationSeqGen", sequenceName = "iteration_key_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "name",  length = 200, nullable = false)
    @NotNull(message = "Name can not be null")
    @NotEmpty(message = "Name can not be empty")
    @Size(min = 1, max = 200)
    private String name;

    @ManyToOne
    @JoinColumn(name = "project_key_id")
    private ProjectEntity project;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "planned_start_date")),
            @AttributeOverride(name = "endDate", column = @Column(name = "planned_end_date")),
            @AttributeOverride(name = "capacity", column = @Column(name = "planned_capacity")),
            @AttributeOverride(name = "forecast", column = @Column(name = "planned_forecast"))
    })
    private IterationMetrics planned;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "actual_start_date")),
            @AttributeOverride(name = "endDate", column = @Column(name = "actual_end_date")),
            @AttributeOverride(name = "capacity", column = @Column(name = "actual_capacity")),
            @AttributeOverride(name = "forecast", column = @Column(name = "actual_forecast"))
    })
    private IterationMetrics actual;



    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

}
