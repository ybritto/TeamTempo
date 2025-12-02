package com.ybritto.teamtempo.backend.authentication.repository;

import com.ybritto.teamtempo.backend.authentication.entity.SecurityRoleEnum;
import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .build();
    }

    @Test
    @DisplayName("Should save and find user by email")
    void shouldSaveAndFindUserByEmail() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<UserEntity> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
        assertThat(foundUser.get().getUuid()).isNotNull();
    }

    @Test
    @DisplayName("Should return empty when user email does not exist")
    void shouldReturnEmptyWhenUserEmailDoesNotExist() {
        // When
        Optional<UserEntity> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should save user with generated UUID")
    void shouldSaveUserWithGeneratedUuid() {
        // Given
        UserEntity userWithoutUuid = UserEntity.builder()
                .name("User Without UUID")
                .email("nouuid@example.com")
                .password("password123")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .build();

        // When
        UserEntity savedUser = entityManager.persistAndFlush(userWithoutUuid);

        // Then
        assertThat(savedUser.getUuid()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("nouuid@example.com");
    }

    @Test
    @DisplayName("Should save user with ADMIN role")
    void shouldSaveUserWithAdminRole() {
        // Given
        UserEntity adminUser = UserEntity.builder()
                .name("Admin User")
                .email("admin@example.com")
                .password("password123")
                .enabled(true)
                .role(SecurityRoleEnum.ADMIN)
                .build();

        // When
        entityManager.persistAndFlush(adminUser);
        Optional<UserEntity> foundUser = userRepository.findByEmail("admin@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRole()).isEqualTo(SecurityRoleEnum.ADMIN);
    }

    @Test
    @DisplayName("Should save multiple users and find each by email")
    void shouldSaveMultipleUsersAndFindEachByEmail() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .name("User One")
                .email("user1@example.com")
                .password("password123")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .build();

        UserEntity user2 = UserEntity.builder()
                .name("User Two")
                .email("user2@example.com")
                .password("password123")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .build();

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // When
        Optional<UserEntity> foundUser1 = userRepository.findByEmail("user1@example.com");
        Optional<UserEntity> foundUser2 = userRepository.findByEmail("user2@example.com");

        // Then
        assertThat(foundUser1).isPresent();
        assertThat(foundUser1.get().getName()).isEqualTo("User One");
        assertThat(foundUser2).isPresent();
        assertThat(foundUser2.get().getName()).isEqualTo("User Two");
    }

    @Test
    @DisplayName("Should save user with disabled status")
    void shouldSaveUserWithDisabledStatus() {
        // Given
        UserEntity disabledUser = UserEntity.builder()
                .name("Disabled User")
                .email("disabled@example.com")
                .password("password123")
                .enabled(false)
                .role(SecurityRoleEnum.USER)
                .build();

        // When
        entityManager.persistAndFlush(disabledUser);
        Optional<UserEntity> foundUser = userRepository.findByEmail("disabled@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().isEnabled()).isFalse();
    }
}

