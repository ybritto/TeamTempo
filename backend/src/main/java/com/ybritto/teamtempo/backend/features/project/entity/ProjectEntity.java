package com.ybritto.teamtempo.backend.features.project.entity;

import com.ybritto.teamtempo.backend.features.iteration.entity.IterationEntity;
import com.ybritto.teamtempo.backend.features.projectConfiguration.entity.ProjectConfigurationEntity;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true) // toBuilder is true to facilitate object copy/transformation to perform IT tests
@AllArgsConstructor // All Args constructor is needed for builder
@NoArgsConstructor // No args constructor is needed for JPA specification
@Getter
@EqualsAndHashCode(of = {"uuid"})
@Entity
@Table(
        name = "project",
        uniqueConstraints = {
                @UniqueConstraint(name = "proejct_uuid_unique", columnNames = {"uuid"})
        })
public class ProjectEntity {

    @Id
    @Column(name = "key_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projectSeqGen")
    @SequenceGenerator(name = "projectSeqGen", sequenceName = "project_key_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "name",  length = 200, nullable = false)
    @NotNull(message = "Name can not be null")
    @NotEmpty(message = "Name can not be empty")
    @Size(min = 1, max = 200)
    private String name;

    @Column(name = "description", length = 1000)
    @Size(max = 1000)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    @NotNull(message = "Team can not be null")
    private TeamEntity team;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date can not be null")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    @SQLRestriction("is_active = true")
    private List<ProjectConfigurationEntity> projectConfigurations;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<IterationEntity> iterations;

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
