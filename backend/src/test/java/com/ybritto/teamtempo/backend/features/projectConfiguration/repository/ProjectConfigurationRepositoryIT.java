package com.ybritto.teamtempo.backend.features.projectConfiguration.repository;

import com.ybritto.teamtempo.backend.authentication.entity.SecurityRoleEnum;
import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.features.project.entity.ProjectEntity;
import com.ybritto.teamtempo.backend.features.projectConfiguration.entity.CapacityUnitEnum;
import com.ybritto.teamtempo.backend.features.projectConfiguration.entity.DurationUnitEnum;
import com.ybritto.teamtempo.backend.features.projectConfiguration.entity.ForecastUnitEnum;
import com.ybritto.teamtempo.backend.features.projectConfiguration.entity.ProjectConfigurationEntity;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProjectConfigurationRepository Integration Tests")
class ProjectConfigurationRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    private UserEntity testUser;
    private TeamEntity testTeam;
    private ProjectEntity testProject;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .build();

        testTeam = TeamEntity.builder()
                .name("Test Team")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .user(testUser)
                .build();

        testProject = ProjectEntity.builder()
                .name("Test Project")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .active(true)
                .team(testTeam)
                .build();
    }

    @Test
    @DisplayName("Should use project_configuration_key_id_seq sequence and nothing else")
    void shouldUseProjectConfigurationKeyIdSeqSequenceAndNothingElse() throws NoSuchFieldException {
        // Given - Get the id field from ProjectConfigurationEntity
        java.lang.reflect.Field idField = ProjectConfigurationEntity.class.getDeclaredField("id");

        // When - Check the SequenceGenerator annotation
        jakarta.persistence.SequenceGenerator sequenceGenerator = idField.getAnnotation(jakarta.persistence.SequenceGenerator.class);

        // Then - Verify the sequence name is exactly "project_configuration_key_id_seq"
        assertThat(sequenceGenerator)
                .as("ProjectConfigurationEntity.id field must have @SequenceGenerator annotation")
                .isNotNull();

        String actualSequenceName = sequenceGenerator.sequenceName();
        assertThat(actualSequenceName)
                .as("ProjectConfigurationEntity must use 'project_configuration_key_id_seq' sequence, not '%s'. " +
                        "Using the wrong sequence will cause primary key violations.",
                        actualSequenceName)
                .isEqualTo("project_configuration_key_id_seq");

        // Verify the generator name matches
        assertThat(sequenceGenerator.name())
                .as("Generator name should be 'projectConfigurationSeqGen'")
                .isEqualTo("projectConfigurationSeqGen");
    }

    @Test
    @DisplayName("Should save project configuration with all required fields")
    void shouldSaveProjectConfigurationWithAllRequiredFields() {
        // Given
        UserEntity savedUser = entityManager.persistAndFlush(testUser);
        TeamEntity savedTeam = entityManager.persistAndFlush(testTeam.toBuilder().user(savedUser).build());
        ProjectEntity savedProject = entityManager.persistAndFlush(testProject.toBuilder().team(savedTeam).build());

        ProjectConfigurationEntity configuration = ProjectConfigurationEntity.builder()
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(savedProject)
                .build();

        // When
        ProjectConfigurationEntity savedConfiguration = entityManager.persistAndFlush(configuration);

        // Then
        assertThat(savedConfiguration.getId()).isNotNull();
        assertThat(savedConfiguration.getUuid()).isNotNull();
        assertThat(savedConfiguration.getIterationDuration()).isEqualTo(2);
        assertThat(savedConfiguration.getIterationDurationUnit()).isEqualTo(DurationUnitEnum.WEEKS);
        assertThat(savedConfiguration.getCapacityUnit()).isEqualTo(CapacityUnitEnum.STORY_POINTS);
        assertThat(savedConfiguration.getForecastUnit()).isEqualTo(ForecastUnitEnum.MAN_DAYS);
        assertThat(savedConfiguration.isActive()).isTrue();
        assertThat(savedConfiguration.getProject().getId()).isEqualTo(savedProject.getId());
    }
}

