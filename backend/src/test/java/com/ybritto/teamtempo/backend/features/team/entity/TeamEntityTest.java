package com.ybritto.teamtempo.backend.features.team.entity;

import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("TeamEntity Unit Tests")
class TeamEntityTest {

    private UserEntity testUser;
    private LocalDate testStartDate;
    private LocalDate testEndDate;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        
        testUser = UserEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        testStartDate = LocalDate.of(2024, 1, 1);
        testEndDate = LocalDate.of(2024, 12, 31);
    }

    private void invokePrePersist(TeamEntity team) {
        try {
            Method method = TeamEntity.class.getDeclaredMethod("prePersist");
            method.setAccessible(true);
            method.invoke(team);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke prePersist", e);
        }
    }

    private void invokePreUpdate(TeamEntity team) {
        try {
            Method method = TeamEntity.class.getDeclaredMethod("preUpdate");
            method.setAccessible(true);
            method.invoke(team);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke preUpdate", e);
        }
    }

    @Test
    @DisplayName("Should create TeamEntity using builder with all fields")
    void shouldCreateTeamEntityWithBuilder() {
        // Given
        UUID expectedUuid = UUID.randomUUID();
        String expectedName = "Test Team";
        String expectedDescription = "Test Description";
        LocalDateTime expectedCreatedAt = LocalDateTime.now();
        LocalDateTime expectedUpdatedAt = LocalDateTime.now();

        // When
        TeamEntity team = TeamEntity.builder()
                .id(1L)
                .uuid(expectedUuid)
                .name(expectedName)
                .description(expectedDescription)
                .startDate(testStartDate)
                .endDate(testEndDate)
                .user(testUser)
                .createdAt(expectedCreatedAt)
                .updatedAt(expectedUpdatedAt)
                .build();

        // Then
        assertAll(
                () -> assertThat(team.getId()).isEqualTo(1L),
                () -> assertThat(team.getUuid()).isEqualTo(expectedUuid),
                () -> assertThat(team.getName()).isEqualTo(expectedName),
                () -> assertThat(team.getDescription()).isEqualTo(expectedDescription),
                () -> assertThat(team.getStartDate()).isEqualTo(testStartDate),
                () -> assertThat(team.getEndDate()).isEqualTo(testEndDate),
                () -> assertThat(team.getUser()).isEqualTo(testUser),
                () -> assertThat(team.getCreatedAt()).isEqualTo(expectedCreatedAt),
                () -> assertThat(team.getUpdatedAt()).isEqualTo(expectedUpdatedAt)
        );
    }

    @Test
    @DisplayName("Should create TeamEntity using builder with minimal required fields")
    void shouldCreateTeamEntityWithMinimalFields() {
        // When
        TeamEntity team = TeamEntity.builder()
                .name("Minimal Team")
                .startDate(testStartDate)
                .user(testUser)
                .build();

        // Then
        assertAll(
                () -> assertThat(team.getName()).isEqualTo("Minimal Team"),
                () -> assertThat(team.getStartDate()).isEqualTo(testStartDate),
                () -> assertThat(team.getUser()).isEqualTo(testUser),
                () -> assertThat(team.getDescription()).isNull(),
                () -> assertThat(team.getEndDate()).isNull()
        );
    }

    @Test
    @DisplayName("Should create TeamEntity using no-args constructor")
    void shouldCreateTeamEntityWithNoArgsConstructor() {
        // When
        TeamEntity team = new TeamEntity();

        // Then
        assertThat(team).isNotNull();
        assertThat(team.getName()).isNull();
        assertThat(team.getUuid()).isNull();
    }

    @Test
    @DisplayName("Should generate UUID automatically on prePersist when UUID is null")
    void shouldGenerateUuidOnPrePersistWhenNull() {
        // Given
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .startDate(testStartDate)
                .user(testUser)
                .build();

        // When
        invokePrePersist(team);

        // Then
        assertThat(team.getUuid()).isNotNull();
        assertThat(team.getUuid()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Should not override existing UUID on prePersist")
    void shouldNotOverrideExistingUuidOnPrePersist() {
        // Given
        UUID existingUuid = UUID.randomUUID();
        TeamEntity team = TeamEntity.builder()
                .uuid(existingUuid)
                .name("Test Team")
                .startDate(testStartDate)
                .user(testUser)
                .build();

        // When
        invokePrePersist(team);

        // Then
        assertThat(team.getUuid()).isEqualTo(existingUuid);
    }

    @Test
    @DisplayName("Should set createdAt and updatedAt on prePersist when null")
    void shouldSetTimestampsOnPrePersistWhenNull() {
        // Given
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .startDate(testStartDate)
                .user(testUser)
                .build();

        // When
        LocalDateTime beforePersist = LocalDateTime.now();
        invokePrePersist(team);
        LocalDateTime afterPersist = LocalDateTime.now();

        // Then
        assertAll(
                () -> assertThat(team.getCreatedAt()).isNotNull(),
                () -> assertThat(team.getUpdatedAt()).isNotNull(),
                () -> assertThat(team.getCreatedAt()).isAfterOrEqualTo(beforePersist),
                () -> assertThat(team.getCreatedAt()).isBeforeOrEqualTo(afterPersist),
                () -> assertThat(team.getUpdatedAt()).isAfterOrEqualTo(beforePersist),
                () -> assertThat(team.getUpdatedAt()).isBeforeOrEqualTo(afterPersist)
        );
    }

    @Test
    @DisplayName("Should not override existing createdAt on prePersist")
    void shouldNotOverrideExistingCreatedAtOnPrePersist() {
        // Given
        LocalDateTime existingCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .startDate(testStartDate)
                .user(testUser)
                .createdAt(existingCreatedAt)
                .build();

        // When
        invokePrePersist(team);

        // Then
        assertThat(team.getCreatedAt()).isEqualTo(existingCreatedAt);
    }

    @Test
    @DisplayName("Should update updatedAt on preUpdate")
    void shouldUpdateUpdatedAtOnPreUpdate() throws InterruptedException {
        // Given
        LocalDateTime originalUpdatedAt = LocalDateTime.now();
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .startDate(testStartDate)
                .user(testUser)
                .updatedAt(originalUpdatedAt)
                .build();

        // When
        Thread.sleep(10); // Small delay to ensure time difference
        invokePreUpdate(team);

        // Then
        assertThat(team.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should maintain createdAt on preUpdate")
    void shouldMaintainCreatedAtOnPreUpdate() {
        // Given
        LocalDateTime originalCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .startDate(testStartDate)
                .user(testUser)
                .createdAt(originalCreatedAt)
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        invokePreUpdate(team);

        // Then
        assertThat(team.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("Should be equal when UUIDs are the same")
    void shouldBeEqualWhenUuidsAreSame() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        TeamEntity team1 = TeamEntity.builder()
                .id(1L)
                .uuid(sharedUuid)
                .name("Team 1")
                .startDate(testStartDate)
                .user(testUser)
                .build();

        TeamEntity team2 = TeamEntity.builder()
                .id(2L)
                .uuid(sharedUuid)
                .name("Team 2")
                .startDate(testStartDate.plusDays(1))
                .user(testUser)
                .build();

        // Then
        assertThat(team1).isEqualTo(team2);
        assertThat(team1.hashCode()).isEqualTo(team2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when UUIDs are different")
    void shouldNotBeEqualWhenUuidsAreDifferent() {
        // Given
        TeamEntity team1 = TeamEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Team 1")
                .startDate(testStartDate)
                .user(testUser)
                .build();

        TeamEntity team2 = TeamEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Team 1")
                .startDate(testStartDate)
                .user(testUser)
                .build();

        // Then
        assertThat(team1).isNotEqualTo(team2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        // Given
        TeamEntity team = TeamEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Test Team")
                .startDate(testStartDate)
                .user(testUser)
                .build();

        // Then
        assertThat(team).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should use toBuilder to create modified copy")
    void shouldUseToBuilderToCreateModifiedCopy() {
        // Given
        TeamEntity original = TeamEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Original Team")
                .description("Original Description")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        TeamEntity modified = original.toBuilder()
                .name("Modified Team")
                .description("Modified Description")
                .build();

        // Then
        assertAll(
                () -> assertThat(modified.getId()).isEqualTo(original.getId()),
                () -> assertThat(modified.getUuid()).isEqualTo(original.getUuid()),
                () -> assertThat(modified.getName()).isEqualTo("Modified Team"),
                () -> assertThat(modified.getDescription()).isEqualTo("Modified Description"),
                () -> assertThat(modified.getStartDate()).isEqualTo(original.getStartDate()),
                () -> assertThat(modified.getEndDate()).isEqualTo(original.getEndDate()),
                () -> assertThat(modified.getUser()).isEqualTo(original.getUser()),
                () -> assertThat(modified.getCreatedAt()).isEqualTo(original.getCreatedAt()),
                () -> assertThat(modified.getUpdatedAt()).isEqualTo(original.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("Should handle null endDate")
    void shouldHandleNullEndDate() {
        // Given
        TeamEntity team = TeamEntity.builder()
                .name("Ongoing Team")
                .startDate(testStartDate)
                .user(testUser)
                .endDate(null)
                .build();

        // Then
        assertThat(team.getEndDate()).isNull();
    }

    @Test
    @DisplayName("Should handle null description")
    void shouldHandleNullDescription() {
        // Given
        TeamEntity team = TeamEntity.builder()
                .name("Team Without Description")
                .startDate(testStartDate)
                .user(testUser)
                .description(null)
                .build();

        // Then
        assertThat(team.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should handle endDate after startDate")
    void shouldHandleEndDateAfterStartDate() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        TeamEntity team = TeamEntity.builder()
                .name("Valid Date Range Team")
                .startDate(startDate)
                .endDate(endDate)
                .user(testUser)
                .build();

        // Then
        assertAll(
                () -> assertThat(team.getStartDate()).isEqualTo(startDate),
                () -> assertThat(team.getEndDate()).isEqualTo(endDate),
                () -> assertThat(team.getEndDate()).isAfter(team.getStartDate())
        );
    }

    @Test
    @DisplayName("Should accept name with maximum allowed length (200 characters)")
    void shouldAcceptNameWithMaxLength() {
        // Given
        String maxLengthName = "A".repeat(200);

        // When
        TeamEntity team = TeamEntity.builder()
                .name(maxLengthName)
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getName()).isEqualTo(maxLengthName),
                () -> assertThat(team.getName().length()).isEqualTo(200),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should reject name exceeding maximum length (201 characters)")
    void shouldRejectNameExceedingMaxLength() {
        // Given
        String tooLongName = "A".repeat(201);

        // When
        TeamEntity team = TeamEntity.builder()
                .name(tooLongName)
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getName().length()).isEqualTo(201),
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"))
        );
    }

    @Test
    @DisplayName("Should accept name with minimum allowed length (1 character)")
    void shouldAcceptNameWithMinLength() {
        // Given
        String minLengthName = "A";

        // When
        TeamEntity team = TeamEntity.builder()
                .name(minLengthName)
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getName()).isEqualTo(minLengthName),
                () -> assertThat(team.getName().length()).isEqualTo(1),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should reject empty name")
    void shouldRejectEmptyName() {
        // Given
        String emptyName = "";

        // When
        TeamEntity team = TeamEntity.builder()
                .name(emptyName)
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getName()).isEmpty(),
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"))
        );
    }

    @Test
    @DisplayName("Should accept description with maximum allowed length (1000 characters)")
    void shouldAcceptDescriptionWithMaxLength() {
        // Given
        String maxLengthDescription = "A".repeat(1000);

        // When
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .description(maxLengthDescription)
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getDescription()).isEqualTo(maxLengthDescription),
                () -> assertThat(team.getDescription().length()).isEqualTo(1000),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should reject description exceeding maximum length (1001 characters)")
    void shouldRejectDescriptionExceedingMaxLength() {
        // Given
        String tooLongDescription = "A".repeat(1001);

        // When
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .description(tooLongDescription)
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getDescription().length()).isEqualTo(1001),
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"))
        );
    }

    @Test
    @DisplayName("Should accept empty description (null is allowed)")
    void shouldAcceptNullDescription() {
        // When
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .description(null)
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getDescription()).isNull(),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should accept empty description string (empty string is allowed)")
    void shouldAcceptEmptyDescriptionString() {
        // When
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .description("")
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getDescription()).isEmpty(),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should accept name with exactly 100 characters")
    void shouldAcceptNameWith100Characters() {
        // Given
        String name100Chars = "A".repeat(100);

        // When
        TeamEntity team = TeamEntity.builder()
                .name(name100Chars)
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getName().length()).isEqualTo(100),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should accept description with exactly 500 characters")
    void shouldAcceptDescriptionWith500Characters() {
        // Given
        String description500Chars = "A".repeat(500);

        // When
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .description(description500Chars)
                .startDate(testStartDate)
                .user(testUser)
                .build();

        Set<ConstraintViolation<TeamEntity>> violations = validator.validate(team);

        // Then
        assertAll(
                () -> assertThat(team.getDescription().length()).isEqualTo(500),
                () -> assertThat(violations).isEmpty()
        );
    }
}

