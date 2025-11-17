package com.ybritto.teamtempo.backend.authentication.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("UserEntity Unit Tests")
class UserEntityTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    private void invokePrePersist(UserEntity user) {
        try {
            Method method = UserEntity.class.getDeclaredMethod("prePersist");
            method.setAccessible(true);
            method.invoke(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke prePersist", e);
        }
    }

    private void invokePreUpdate(UserEntity user) {
        try {
            Method method = UserEntity.class.getDeclaredMethod("preUpdate");
            method.setAccessible(true);
            method.invoke(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke preUpdate", e);
        }
    }

    @Test
    @DisplayName("Should create UserEntity using builder with all fields")
    void shouldCreateUserEntityWithBuilder() {
        // Given
        UUID expectedUuid = UUID.randomUUID();
        String expectedName = "Test User";
        String expectedEmail = "test@example.com";
        String expectedPassword = "password123";
        boolean expectedEnabled = true;
        SecurityRoleEnum expectedRole = SecurityRoleEnum.ADMIN;
        LocalDateTime expectedCreatedAt = LocalDateTime.now();
        LocalDateTime expectedUpdatedAt = LocalDateTime.now();

        // When
        UserEntity user = UserEntity.builder()
                .id(1L)
                .uuid(expectedUuid)
                .name(expectedName)
                .email(expectedEmail)
                .password(expectedPassword)
                .enabled(expectedEnabled)
                .role(expectedRole)
                .createdAt(expectedCreatedAt)
                .updatedAt(expectedUpdatedAt)
                .build();

        // Then
        assertAll(
                () -> assertThat(user.getId()).isEqualTo(1L),
                () -> assertThat(user.getUuid()).isEqualTo(expectedUuid),
                () -> assertThat(user.getName()).isEqualTo(expectedName),
                () -> assertThat(user.getEmail()).isEqualTo(expectedEmail),
                () -> assertThat(user.getPassword()).isEqualTo(expectedPassword),
                () -> assertThat(user.isEnabled()).isEqualTo(expectedEnabled),
                () -> assertThat(user.getRole()).isEqualTo(expectedRole),
                () -> assertThat(user.getCreatedAt()).isEqualTo(expectedCreatedAt),
                () -> assertThat(user.getUpdatedAt()).isEqualTo(expectedUpdatedAt)
        );
    }

    @Test
    @DisplayName("Should create UserEntity using builder with minimal required fields")
    void shouldCreateUserEntityWithMinimalFields() {
        // When
        UserEntity user = UserEntity.builder()
                .name("Minimal User")
                .email("minimal@example.com")
                .password("password123")
                .build();

        // Then
        assertAll(
                () -> assertThat(user.getName()).isEqualTo("Minimal User"),
                () -> assertThat(user.getEmail()).isEqualTo("minimal@example.com"),
                () -> assertThat(user.getPassword()).isEqualTo("password123"),
                () -> assertThat(user.getRole()).isNull() // Will be set in prePersist
                // Note: enabled defaults to false with builder (field initialization doesn't apply to builder)
        );
    }

    @Test
    @DisplayName("Should create UserEntity using no-args constructor")
    void shouldCreateUserEntityWithNoArgsConstructor() {
        // When
        UserEntity user = new UserEntity();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getUuid()).isNull();
        assertThat(user.isEnabled()).isTrue(); // Default value
    }

    @Test
    @DisplayName("Should generate UUID automatically on prePersist when UUID is null")
    void shouldGenerateUuidOnPrePersistWhenNull() {
        // Given
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        // When
        invokePrePersist(user);

        // Then
        assertThat(user.getUuid()).isNotNull();
        assertThat(user.getUuid()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Should not override existing UUID on prePersist")
    void shouldNotOverrideExistingUuidOnPrePersist() {
        // Given
        UUID existingUuid = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .uuid(existingUuid)
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        // When
        invokePrePersist(user);

        // Then
        assertThat(user.getUuid()).isEqualTo(existingUuid);
    }

    @Test
    @DisplayName("Should set createdAt and updatedAt on prePersist when null")
    void shouldSetTimestampsOnPrePersistWhenNull() {
        // Given
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        // When
        LocalDateTime beforePersist = LocalDateTime.now();
        invokePrePersist(user);
        LocalDateTime afterPersist = LocalDateTime.now();

        // Then
        assertAll(
                () -> assertThat(user.getCreatedAt()).isNotNull(),
                () -> assertThat(user.getUpdatedAt()).isNotNull(),
                () -> assertThat(user.getCreatedAt()).isAfterOrEqualTo(beforePersist),
                () -> assertThat(user.getCreatedAt()).isBeforeOrEqualTo(afterPersist),
                () -> assertThat(user.getUpdatedAt()).isAfterOrEqualTo(beforePersist),
                () -> assertThat(user.getUpdatedAt()).isBeforeOrEqualTo(afterPersist)
        );
    }

    @Test
    @DisplayName("Should not override existing createdAt on prePersist")
    void shouldNotOverrideExistingCreatedAtOnPrePersist() {
        // Given
        LocalDateTime existingCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .createdAt(existingCreatedAt)
                .build();

        // When
        invokePrePersist(user);

        // Then
        assertThat(user.getCreatedAt()).isEqualTo(existingCreatedAt);
    }

    @Test
    @DisplayName("Should set default role to USER on prePersist when role is null")
    void shouldSetDefaultRoleOnPrePersistWhenNull() {
        // Given
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .role(null)
                .build();

        // When
        invokePrePersist(user);

        // Then
        assertThat(user.getRole()).isEqualTo(SecurityRoleEnum.USER);
    }

    @Test
    @DisplayName("Should not override existing role on prePersist")
    void shouldNotOverrideExistingRoleOnPrePersist() {
        // Given
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .role(SecurityRoleEnum.ADMIN)
                .build();

        // When
        invokePrePersist(user);

        // Then
        assertThat(user.getRole()).isEqualTo(SecurityRoleEnum.ADMIN);
    }

    @Test
    @DisplayName("Should update updatedAt on preUpdate")
    void shouldUpdateUpdatedAtOnPreUpdate() throws InterruptedException {
        // Given
        LocalDateTime originalUpdatedAt = LocalDateTime.now();
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .updatedAt(originalUpdatedAt)
                .build();

        // When
        Thread.sleep(10); // Small delay to ensure time difference
        invokePreUpdate(user);

        // Then
        assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should maintain createdAt on preUpdate")
    void shouldMaintainCreatedAtOnPreUpdate() {
        // Given
        LocalDateTime originalCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .createdAt(originalCreatedAt)
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        invokePreUpdate(user);

        // Then
        assertThat(user.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("Should be equal when UUIDs are the same")
    void shouldBeEqualWhenUuidsAreSame() {
        // Given
        UUID sharedUuid = UUID.randomUUID();
        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .uuid(sharedUuid)
                .name("User 1")
                .email("user1@example.com")
                .password("password123")
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .uuid(sharedUuid)
                .name("User 2")
                .email("user2@example.com")
                .password("differentpassword")
                .build();

        // Then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when UUIDs are different")
    void shouldNotBeEqualWhenUuidsAreDifferent() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .uuid(UUID.randomUUID())
                .name("User 1")
                .email("user1@example.com")
                .password("password123")
                .build();

        UserEntity user2 = UserEntity.builder()
                .uuid(UUID.randomUUID())
                .name("User 1")
                .email("user1@example.com")
                .password("password123")
                .build();

        // Then
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        // Given
        UserEntity user = UserEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        // Then
        assertThat(user).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should use toBuilder to create modified copy")
    void shouldUseToBuilderToCreateModifiedCopy() {
        // Given
        UserEntity original = UserEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Original User")
                .email("original@example.com")
                .password("password123")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        UserEntity modified = original.toBuilder()
                .name("Modified User")
                .email("modified@example.com")
                .role(SecurityRoleEnum.ADMIN)
                .build();

        // Then
        assertAll(
                () -> assertThat(modified.getId()).isEqualTo(original.getId()),
                () -> assertThat(modified.getUuid()).isEqualTo(original.getUuid()),
                () -> assertThat(modified.getName()).isEqualTo("Modified User"),
                () -> assertThat(modified.getEmail()).isEqualTo("modified@example.com"),
                () -> assertThat(modified.getPassword()).isEqualTo(original.getPassword()),
                () -> assertThat(modified.getRole()).isEqualTo(SecurityRoleEnum.ADMIN),
                () -> assertThat(modified.getCreatedAt()).isEqualTo(original.getCreatedAt()),
                () -> assertThat(modified.getUpdatedAt()).isEqualTo(original.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("Should return email as username")
    void shouldReturnEmailAsUsername() {
        // Given
        String email = "test@example.com";
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email(email)
                .password("password123")
                .build();

        // When
        String username = user.getUsername();

        // Then
        assertThat(username).isEqualTo(email);
    }

    @Test
    @DisplayName("Should return authorities based on role")
    void shouldReturnAuthoritiesBasedOnRole() {
        // Given
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .role(SecurityRoleEnum.ADMIN)
                .build();

        // When
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Then
        assertAll(
                () -> assertThat(authorities).isNotEmpty(),
                () -> assertThat(authorities).hasSize(1),
                () -> assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN")
        );
    }

    @Test
    @DisplayName("Should return empty authorities when role is null")
    void shouldReturnEmptyAuthoritiesWhenRoleIsNull() {
        // Given
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .role(null)
                .build();

        // When
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Then
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Should return USER role authorities")
    void shouldReturnUserRoleAuthorities() {
        // Given
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .role(SecurityRoleEnum.USER)
                .build();

        // When
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Then
        assertAll(
                () -> assertThat(authorities).isNotEmpty(),
                () -> assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER")
        );
    }

    @Test
    @DisplayName("Should return enabled status")
    void shouldReturnEnabledStatus() {
        // Given
        UserEntity enabledUser = UserEntity.builder()
                .name("Enabled User")
                .email("enabled@example.com")
                .password("password123")
                .enabled(true)
                .build();

        UserEntity disabledUser = UserEntity.builder()
                .name("Disabled User")
                .email("disabled@example.com")
                .password("password123")
                .enabled(false)
                .build();

        // Then
        assertAll(
                () -> assertThat(enabledUser.isEnabled()).isTrue(),
                () -> assertThat(disabledUser.isEnabled()).isFalse()
        );
    }

    // Field Size Constraint Tests

    @Test
    @DisplayName("Should accept name with maximum allowed length (200 characters)")
    void shouldAcceptNameWithMaxLength() {
        // Given
        String maxLengthName = "A".repeat(200);

        // When
        UserEntity user = UserEntity.builder()
                .name(maxLengthName)
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getName()).isEqualTo(maxLengthName),
                () -> assertThat(user.getName().length()).isEqualTo(200),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should reject name exceeding maximum length (201 characters)")
    void shouldRejectNameExceedingMaxLength() {
        // Given
        String tooLongName = "A".repeat(201);

        // When
        UserEntity user = UserEntity.builder()
                .name(tooLongName)
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getName().length()).isEqualTo(201),
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
        UserEntity user = UserEntity.builder()
                .name(minLengthName)
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getName()).isEqualTo(minLengthName),
                () -> assertThat(user.getName().length()).isEqualTo(1),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should reject empty name")
    void shouldRejectEmptyName() {
        // Given
        String emptyName = "";

        // When
        UserEntity user = UserEntity.builder()
                .name(emptyName)
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getName()).isEmpty(),
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"))
        );
    }

    @Test
    @DisplayName("Should accept email with maximum allowed length (100 characters)")
    void shouldAcceptEmailWithMaxLength() {
        // Given - Create a valid email that's exactly 100 characters
        // Format: localPart + "@" + domainPart + ".com" = 100 chars
        // "a".repeat(50) + "@" + "b".repeat(45) + ".com" = 50 + 1 + 45 + 4 = 100
        String maxLengthEmail = "a".repeat(50) + "@" + "b".repeat(45) + ".com";

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email(maxLengthEmail)
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getEmail()).isEqualTo(maxLengthEmail),
                () -> assertThat(user.getEmail().length()).isEqualTo(100),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should reject email exceeding maximum length (101 characters)")
    void shouldRejectEmailExceedingMaxLength() {
        // Given - Create a valid email that's exactly 101 characters
        // "a".repeat(51) + "@" + "b".repeat(45) + ".com" = 51 + 1 + 45 + 4 = 101
        String tooLongEmail = "a".repeat(51) + "@" + "b".repeat(45) + ".com"; // 101 chars total

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email(tooLongEmail)
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getEmail().length()).isEqualTo(101),
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"))
        );
    }

    @Test
    @DisplayName("Should reject invalid email format")
    void shouldRejectInvalidEmailFormat() {
        // Given
        String invalidEmail = "not-an-email";

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email(invalidEmail)
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"))
        );
    }

    @Test
    @DisplayName("Should reject empty email")
    void shouldRejectEmptyEmail() {
        // Given
        String emptyEmail = "";

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email(emptyEmail)
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getEmail()).isEmpty(),
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"))
        );
    }

    @Test
    @DisplayName("Should accept valid email formats")
    void shouldAcceptValidEmailFormats() {
        // Given
        String[] validEmails = {
                "test@example.com",
                "user.name@example.com",
                "user+tag@example.co.uk",
                "user123@example-domain.com"
        };

        // When & Then
        for (String email : validEmails) {
            UserEntity user = UserEntity.builder()
                    .name("Test User")
                    .email(email)
                    .password("password123")
                    .build();

            Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

            assertThat(violations)
                    .as("Email '%s' should be valid", email)
                    .noneMatch(v -> v.getPropertyPath().toString().equals("email"));
        }
    }

    @Test
    @DisplayName("Should accept password with minimum allowed length (8 characters)")
    void shouldAcceptPasswordWithMinLength() {
        // Given
        String minLengthPassword = "12345678"; // 8 characters

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password(minLengthPassword)
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getPassword()).isEqualTo(minLengthPassword),
                () -> assertThat(user.getPassword().length()).isEqualTo(8),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should reject password below minimum length (7 characters)")
    void shouldRejectPasswordBelowMinLength() {
        // Given
        String tooShortPassword = "1234567"; // 7 characters

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password(tooShortPassword)
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getPassword().length()).isEqualTo(7),
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"))
        );
    }

    @Test
    @DisplayName("Should accept password with maximum allowed length (255 characters)")
    void shouldAcceptPasswordWithMaxLength() {
        // Given
        String maxLengthPassword = "A".repeat(255);

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password(maxLengthPassword)
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getPassword()).isEqualTo(maxLengthPassword),
                () -> assertThat(user.getPassword().length()).isEqualTo(255),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should reject password exceeding maximum length (256 characters)")
    void shouldRejectPasswordExceedingMaxLength() {
        // Given
        String tooLongPassword = "A".repeat(256);

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password(tooLongPassword)
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getPassword().length()).isEqualTo(256),
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"))
        );
    }

    @Test
    @DisplayName("Should reject empty password")
    void shouldRejectEmptyPassword() {
        // Given
        String emptyPassword = "";

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password(emptyPassword)
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getPassword()).isEmpty(),
                () -> assertThat(violations).isNotEmpty(),
                () -> assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"))
        );
    }

    @Test
    @DisplayName("Should accept password with exactly 16 characters")
    void shouldAcceptPasswordWith16Characters() {
        // Given
        String password16Chars = "A".repeat(16);

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password(password16Chars)
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getPassword().length()).isEqualTo(16),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should accept name with exactly 100 characters")
    void shouldAcceptNameWith100Characters() {
        // Given
        String name100Chars = "A".repeat(100);

        // When
        UserEntity user = UserEntity.builder()
                .name(name100Chars)
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getName().length()).isEqualTo(100),
                () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    @DisplayName("Should accept email with exactly 50 characters")
    void shouldAcceptEmailWith50Characters() {
        // Given - Create a valid email that's exactly 50 characters
        // "a".repeat(20) + "@" + "b".repeat(25) + ".com" = 20 + 1 + 25 + 4 = 50
        String email50Chars = "a".repeat(20) + "@" + "b".repeat(25) + ".com"; // 50 chars total

        // When
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email(email50Chars)
                .password("password123")
                .build();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        // Then
        assertAll(
                () -> assertThat(user.getEmail().length()).isEqualTo(50),
                () -> assertThat(violations).isEmpty()
        );
    }
}

