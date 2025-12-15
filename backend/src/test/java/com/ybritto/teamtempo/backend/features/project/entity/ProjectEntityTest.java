package com.ybritto.teamtempo.backend.features.project.entity;

import com.ybritto.teamtempo.backend.authentication.entity.SecurityRoleEnum;
import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
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

@DisplayName("ProjectEntity Unit Tests")
class ProjectEntityTest {

    private UserEntity testUser;
    private TeamEntity testTeam;
    private LocalDate testStartDate;
    private LocalDate testEndDate;

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

        testStartDate = LocalDate.of(2024, 1, 1);
        testEndDate = LocalDate.of(2024, 12, 31);
    }

    private void invokePrePersist(ProjectEntity project) {
        try {
            Method method = ProjectEntity.class.getDeclaredMethod("prePersist");
            method.setAccessible(true);
            method.invoke(project);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke prePersist", e);
        }
    }

    private void invokePreUpdate(ProjectEntity project) {
        try {
            Method method = ProjectEntity.class.getDeclaredMethod("preUpdate");
            method.setAccessible(true);
            method.invoke(project);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke preUpdate", e);
        }
    }

    @Test
    @DisplayName("Should be equal when UUIDs are the same")
    void shouldBeEqualWhenUuidsAreSame() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectEntity project1 = ProjectEntity.builder()
                .id(1L)
                .uuid(sharedUuid)
                .name("Project 1")
                .description("Description 1")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .active(true)
                .team(testTeam)
                .build();

        ProjectEntity project2 = ProjectEntity.builder()
                .id(2L)
                .uuid(sharedUuid)
                .name("Project 2")
                .description("Description 2")
                .startDate(testStartDate.plusDays(1))
                .endDate(testEndDate.plusDays(1))
                .active(false)
                .team(testTeam)
                .build();

        // Then
        assertThat(project1).isEqualTo(project2);
        assertThat(project1.hashCode()).isEqualTo(project2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when UUIDs are different")
    void shouldNotBeEqualWhenUuidsAreDifferent() {
        // Given
        ProjectEntity project1 = ProjectEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Project 1")
                .description("Description 1")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        ProjectEntity project2 = ProjectEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Project 1")
                .description("Description 1")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        // Then
        assertThat(project1).isNotEqualTo(project2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        // Given
        ProjectEntity project = ProjectEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Test Project")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        // Then
        assertThat(project).isNotEqualTo(null);
        assertThat(project.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
        // Given
        ProjectEntity project = ProjectEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Test Project")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        String differentType = "Not a ProjectEntity";

        // Then
        assertThat(project).isNotEqualTo(differentType);
        assertThat(project.equals(differentType)).isFalse();
    }

    @Test
    @DisplayName("Should be equal to itself (reflexive)")
    void shouldBeEqualToItself() {
        // Given
        ProjectEntity project = ProjectEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Test Project")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        // Then
        assertThat(project).isEqualTo(project);
        assertThat(project.hashCode()).isEqualTo(project.hashCode());
    }

    @Test
    @DisplayName("Should have symmetric equals - if A equals B, then B equals A")
    void shouldHaveSymmetricEquals() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectEntity project1 = ProjectEntity.builder()
                .uuid(sharedUuid)
                .name("Project 1")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        ProjectEntity project2 = ProjectEntity.builder()
                .uuid(sharedUuid)
                .name("Project 2")
                .startDate(testStartDate.plusDays(1))
                .team(testTeam)
                .build();

        // Then
        assertThat(project1.equals(project2)).isTrue();
        assertThat(project2.equals(project1)).isTrue();
    }

    @Test
    @DisplayName("Should have transitive equals - if A equals B and B equals C, then A equals C")
    void shouldHaveTransitiveEquals() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectEntity project1 = ProjectEntity.builder()
                .uuid(sharedUuid)
                .name("Project 1")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        ProjectEntity project2 = ProjectEntity.builder()
                .uuid(sharedUuid)
                .name("Project 2")
                .startDate(testStartDate.plusDays(1))
                .team(testTeam)
                .build();

        ProjectEntity project3 = ProjectEntity.builder()
                .uuid(sharedUuid)
                .name("Project 3")
                .startDate(testStartDate.plusDays(2))
                .team(testTeam)
                .build();

        // Then
        assertThat(project1.equals(project2)).isTrue();
        assertThat(project2.equals(project3)).isTrue();
        assertThat(project1.equals(project3)).isTrue();
    }

    @Test
    @DisplayName("Should have consistent equals - multiple calls return same result")
    void shouldHaveConsistentEquals() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectEntity project1 = ProjectEntity.builder()
                .uuid(sharedUuid)
                .name("Project 1")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        ProjectEntity project2 = ProjectEntity.builder()
                .uuid(sharedUuid)
                .name("Project 2")
                .startDate(testStartDate.plusDays(1))
                .team(testTeam)
                .build();

        // Then
        assertThat(project1.equals(project2)).isTrue();
        assertThat(project1.equals(project2)).isTrue(); // Second call
        assertThat(project1.equals(project2)).isTrue(); // Third call
    }

    @Test
    @DisplayName("Should have consistent hashCode - multiple calls return same result")
    void shouldHaveConsistentHashCode() {
        // Given
        ProjectEntity project = ProjectEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Test Project")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        // When
        int hashCode1 = project.hashCode();
        int hashCode2 = project.hashCode();
        int hashCode3 = project.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
        assertThat(hashCode2).isEqualTo(hashCode3);
    }

    @Test
    @DisplayName("Should have equal hashCodes when UUIDs are equal")
    void shouldHaveEqualHashCodesWhenUuidsAreEqual() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        ProjectEntity project1 = ProjectEntity.builder()
                .id(1L)
                .uuid(sharedUuid)
                .name("Project 1")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        ProjectEntity project2 = ProjectEntity.builder()
                .id(2L)
                .uuid(sharedUuid)
                .name("Project 2")
                .startDate(testStartDate.plusDays(1))
                .team(testTeam)
                .build();

        // Then
        assertThat(project1.hashCode()).isEqualTo(project2.hashCode());
    }

    @Test
    @DisplayName("Should handle null UUID in equals")
    void shouldHandleNullUuidInEquals() {
        // Given
        ProjectEntity project1 = ProjectEntity.builder()
                .uuid(null)
                .name("Project 1")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        ProjectEntity project2 = ProjectEntity.builder()
                .uuid(null)
                .name("Project 2")
                .startDate(testStartDate.plusDays(1))
                .team(testTeam)
                .build();

        ProjectEntity project3 = ProjectEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Project 3")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        // Then
        assertThat(project1).isEqualTo(project2); // Both have null UUID
        assertThat(project1).isNotEqualTo(project3); // One has null, one has UUID
    }

    @Test
    @DisplayName("Should create ProjectEntity using builder with all fields")
    void shouldCreateProjectEntityWithBuilder() {
        // Given
        UUID expectedUuid = UUID.randomUUID();
        String expectedName = "Test Project";
        String expectedDescription = "Test Description";
        LocalDateTime expectedCreatedAt = LocalDateTime.now();
        LocalDateTime expectedUpdatedAt = LocalDateTime.now();

        // When
        ProjectEntity project = ProjectEntity.builder()
                .id(1L)
                .uuid(expectedUuid)
                .name(expectedName)
                .description(expectedDescription)
                .startDate(testStartDate)
                .endDate(testEndDate)
                .active(true)
                .team(testTeam)
                .createdAt(expectedCreatedAt)
                .updatedAt(expectedUpdatedAt)
                .build();

        // Then
        assertAll(
                () -> assertThat(project.getId()).isEqualTo(1L),
                () -> assertThat(project.getUuid()).isEqualTo(expectedUuid),
                () -> assertThat(project.getName()).isEqualTo(expectedName),
                () -> assertThat(project.getDescription()).isEqualTo(expectedDescription),
                () -> assertThat(project.getStartDate()).isEqualTo(testStartDate),
                () -> assertThat(project.getEndDate()).isEqualTo(testEndDate),
                () -> assertThat(project.isActive()).isTrue(),
                () -> assertThat(project.getTeam()).isEqualTo(testTeam),
                () -> assertThat(project.getCreatedAt()).isEqualTo(expectedCreatedAt),
                () -> assertThat(project.getUpdatedAt()).isEqualTo(expectedUpdatedAt)
        );
    }

    @Test
    @DisplayName("Should generate UUID automatically on prePersist when UUID is null")
    void shouldGenerateUuidOnPrePersistWhenNull() {
        // Given
        ProjectEntity project = ProjectEntity.builder()
                .name("Test Project")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        // When
        invokePrePersist(project);

        // Then
        assertThat(project.getUuid()).isNotNull();
        assertThat(project.getUuid()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Should not override existing UUID on prePersist")
    void shouldNotOverrideExistingUuidOnPrePersist() {
        // Given
        UUID existingUuid = UUID.randomUUID();
        ProjectEntity project = ProjectEntity.builder()
                .uuid(existingUuid)
                .name("Test Project")
                .startDate(testStartDate)
                .team(testTeam)
                .build();

        // When
        invokePrePersist(project);

        // Then
        assertThat(project.getUuid()).isEqualTo(existingUuid);
    }

    @Test
    @DisplayName("Should update updatedAt on preUpdate")
    void shouldUpdateUpdatedAtOnPreUpdate() throws InterruptedException {
        // Given
        LocalDateTime originalUpdatedAt = LocalDateTime.now();
        ProjectEntity project = ProjectEntity.builder()
                .name("Test Project")
                .startDate(testStartDate)
                .team(testTeam)
                .updatedAt(originalUpdatedAt)
                .build();

        // When
        Thread.sleep(10); // Small delay to ensure time difference
        invokePreUpdate(project);

        // Then
        assertThat(project.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
}

