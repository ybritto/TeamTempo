package com.ybritto.teamtempo.backend.features.projectConfiguration.entity;

import com.ybritto.teamtempo.backend.authentication.entity.SecurityRoleEnum;
import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.features.project.entity.ProjectEntity;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ProjectConfigurationEntity Unit Tests")
class ProjectConfigurationEntityTest {

    private UserEntity testUser;
    private TeamEntity testTeam;
    private ProjectEntity testProject;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .build();

        testTeam = TeamEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Test Team")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .user(testUser)
                .build();

        testProject = ProjectEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Test Project")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .active(true)
                .team(testTeam)
                .build();
    }

    private void invokePrePersist(ProjectConfigurationEntity configuration) {
        try {
            Method method = ProjectConfigurationEntity.class.getDeclaredMethod("prePersist");
            method.setAccessible(true);
            method.invoke(configuration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke prePersist", e);
        }
    }

    private void invokePreUpdate(ProjectConfigurationEntity configuration) {
        try {
            Method method = ProjectConfigurationEntity.class.getDeclaredMethod("preUpdate");
            method.setAccessible(true);
            method.invoke(configuration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke preUpdate", e);
        }
    }

    @Test
    @DisplayName("Should be equal when UUIDs are the same")
    void shouldBeEqualWhenUuidsAreSame() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectConfigurationEntity config1 = ProjectConfigurationEntity.builder()
                .id(1L)
                .uuid(sharedUuid)
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        ProjectConfigurationEntity config2 = ProjectConfigurationEntity.builder()
                .id(2L)
                .uuid(sharedUuid)
                .iterationDuration(4)
                .iterationDurationUnit(DurationUnitEnum.DAYS)
                .capacityUnit(CapacityUnitEnum.T_SHIRT)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(false)
                .project(testProject)
                .build();

        // Then
        assertThat(config1).isEqualTo(config2);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when UUIDs are different")
    void shouldNotBeEqualWhenUuidsAreDifferent() {
        // Given
        ProjectConfigurationEntity config1 = ProjectConfigurationEntity.builder()
                .uuid(UUID.randomUUID())
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        ProjectConfigurationEntity config2 = ProjectConfigurationEntity.builder()
                .uuid(UUID.randomUUID())
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        // Then
        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        // Given
        ProjectConfigurationEntity config = ProjectConfigurationEntity.builder()
                .uuid(UUID.randomUUID())
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        // Then
        assertThat(config).isNotEqualTo(null);
        assertThat(config.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
        // Given
        ProjectConfigurationEntity config = ProjectConfigurationEntity.builder()
                .uuid(UUID.randomUUID())
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        String differentType = "Not a ProjectConfigurationEntity";

        // Then
        assertThat(config).isNotEqualTo(differentType);
        assertThat(config.equals(differentType)).isFalse();
    }

    @Test
    @DisplayName("Should be equal to itself (reflexive)")
    void shouldBeEqualToItself() {
        // Given
        ProjectConfigurationEntity config = ProjectConfigurationEntity.builder()
                .uuid(UUID.randomUUID())
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        // Then
        assertThat(config).isEqualTo(config);
        assertThat(config.hashCode()).isEqualTo(config.hashCode());
    }

    @Test
    @DisplayName("Should have symmetric equals - if A equals B, then B equals A")
    void shouldHaveSymmetricEquals() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectConfigurationEntity config1 = ProjectConfigurationEntity.builder()
                .uuid(sharedUuid)
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        ProjectConfigurationEntity config2 = ProjectConfigurationEntity.builder()
                .uuid(sharedUuid)
                .iterationDuration(4)
                .iterationDurationUnit(DurationUnitEnum.DAYS)
                .capacityUnit(CapacityUnitEnum.T_SHIRT)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(false)
                .project(testProject)
                .build();

        // Then
        assertThat(config1.equals(config2)).isTrue();
        assertThat(config2.equals(config1)).isTrue();
    }

    @Test
    @DisplayName("Should have transitive equals - if A equals B and B equals C, then A equals C")
    void shouldHaveTransitiveEquals() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectConfigurationEntity config1 = ProjectConfigurationEntity.builder()
                .uuid(sharedUuid)
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        ProjectConfigurationEntity config2 = ProjectConfigurationEntity.builder()
                .uuid(sharedUuid)
                .iterationDuration(4)
                .iterationDurationUnit(DurationUnitEnum.DAYS)
                .capacityUnit(CapacityUnitEnum.T_SHIRT)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(false)
                .project(testProject)
                .build();

        ProjectConfigurationEntity config3 = ProjectConfigurationEntity.builder()
                .uuid(sharedUuid)
                .iterationDuration(6)
                .iterationDurationUnit(DurationUnitEnum.MONTHS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        // Then
        assertThat(config1.equals(config2)).isTrue();
        assertThat(config2.equals(config3)).isTrue();
        assertThat(config1.equals(config3)).isTrue();
    }

    @Test
    @DisplayName("Should have consistent equals - multiple calls return same result")
    void shouldHaveConsistentEquals() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectConfigurationEntity config1 = ProjectConfigurationEntity.builder()
                .uuid(sharedUuid)
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        ProjectConfigurationEntity config2 = ProjectConfigurationEntity.builder()
                .uuid(sharedUuid)
                .iterationDuration(4)
                .iterationDurationUnit(DurationUnitEnum.DAYS)
                .capacityUnit(CapacityUnitEnum.T_SHIRT)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(false)
                .project(testProject)
                .build();

        // Then
        assertThat(config1.equals(config2)).isTrue();
        assertThat(config1.equals(config2)).isTrue(); // Second call
        assertThat(config1.equals(config2)).isTrue(); // Third call
    }

    @Test
    @DisplayName("Should have consistent hashCode - multiple calls return same result")
    void shouldHaveConsistentHashCode() {
        // Given
        ProjectConfigurationEntity config = ProjectConfigurationEntity.builder()
                .uuid(UUID.randomUUID())
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        // When
        int hashCode1 = config.hashCode();
        int hashCode2 = config.hashCode();
        int hashCode3 = config.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
        assertThat(hashCode2).isEqualTo(hashCode3);
    }

    @Test
    @DisplayName("Should have equal hashCodes when UUIDs are equal")
    void shouldHaveEqualHashCodesWhenUuidsAreEqual() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectConfigurationEntity config1 = ProjectConfigurationEntity.builder()
                .id(1L)
                .uuid(sharedUuid)
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        ProjectConfigurationEntity config2 = ProjectConfigurationEntity.builder()
                .id(2L)
                .uuid(sharedUuid)
                .iterationDuration(4)
                .iterationDurationUnit(DurationUnitEnum.DAYS)
                .capacityUnit(CapacityUnitEnum.T_SHIRT)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(false)
                .project(testProject)
                .build();

        // Then
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }

    @Test
    @DisplayName("Should handle null UUID in equals")
    void shouldHandleNullUuidInEquals() {
        // Given
        ProjectConfigurationEntity config1 = ProjectConfigurationEntity.builder()
                .uuid(null)
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        ProjectConfigurationEntity config2 = ProjectConfigurationEntity.builder()
                .uuid(null)
                .iterationDuration(4)
                .iterationDurationUnit(DurationUnitEnum.DAYS)
                .capacityUnit(CapacityUnitEnum.T_SHIRT)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(false)
                .project(testProject)
                .build();

        ProjectConfigurationEntity config3 = ProjectConfigurationEntity.builder()
                .uuid(UUID.randomUUID())
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        // Then
        assertThat(config1).isEqualTo(config2); // Both have null UUID
        assertThat(config1).isNotEqualTo(config3); // One has null, one has UUID
    }

    @Test
    @DisplayName("Should create ProjectConfigurationEntity using builder with all fields")
    void shouldCreateProjectConfigurationEntityWithBuilder() {
        // Given
        UUID expectedUuid = UUID.randomUUID();
        LocalDateTime expectedCreatedAt = LocalDateTime.now();
        LocalDateTime expectedUpdatedAt = LocalDateTime.now();

        // When
        ProjectConfigurationEntity config = ProjectConfigurationEntity.builder()
                .id(1L)
                .uuid(expectedUuid)
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .createdAt(expectedCreatedAt)
                .updatedAt(expectedUpdatedAt)
                .build();

        // Then
        assertAll(
                () -> assertThat(config.getId()).isEqualTo(1L),
                () -> assertThat(config.getUuid()).isEqualTo(expectedUuid),
                () -> assertThat(config.getIterationDuration()).isEqualTo(2),
                () -> assertThat(config.getIterationDurationUnit()).isEqualTo(DurationUnitEnum.WEEKS),
                () -> assertThat(config.getCapacityUnit()).isEqualTo(CapacityUnitEnum.STORY_POINTS),
                () -> assertThat(config.getForecastUnit()).isEqualTo(ForecastUnitEnum.MAN_DAYS),
                () -> assertThat(config.isActive()).isTrue(),
                () -> assertThat(config.getProject()).isEqualTo(testProject),
                () -> assertThat(config.getCreatedAt()).isEqualTo(expectedCreatedAt),
                () -> assertThat(config.getUpdatedAt()).isEqualTo(expectedUpdatedAt)
        );
    }

    @Test
    @DisplayName("Should generate UUID automatically on prePersist when UUID is null")
    void shouldGenerateUuidOnPrePersistWhenNull() {
        // Given
        ProjectConfigurationEntity config = ProjectConfigurationEntity.builder()
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        // When
        invokePrePersist(config);

        // Then
        assertThat(config.getUuid()).isNotNull();
        assertThat(config.getUuid()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Should not override existing UUID on prePersist")
    void shouldNotOverrideExistingUuidOnPrePersist() {
        // Given
        UUID existingUuid = UUID.randomUUID();
        ProjectConfigurationEntity config = ProjectConfigurationEntity.builder()
                .uuid(existingUuid)
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .build();

        // When
        invokePrePersist(config);

        // Then
        assertThat(config.getUuid()).isEqualTo(existingUuid);
    }

    @Test
    @DisplayName("Should update updatedAt on preUpdate")
    void shouldUpdateUpdatedAtOnPreUpdate() throws InterruptedException {
        // Given
        LocalDateTime originalUpdatedAt = LocalDateTime.now();
        ProjectConfigurationEntity config = ProjectConfigurationEntity.builder()
                .iterationDuration(2)
                .iterationDurationUnit(DurationUnitEnum.WEEKS)
                .capacityUnit(CapacityUnitEnum.STORY_POINTS)
                .forecastUnit(ForecastUnitEnum.MAN_DAYS)
                .active(true)
                .project(testProject)
                .updatedAt(originalUpdatedAt)
                .build();

        // When
        Thread.sleep(10); // Small delay to ensure time difference
        invokePreUpdate(config);

        // Then
        assertThat(config.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
}

