package com.ybritto.teamtempo.backend.authentication.mapper;

import com.ybritto.teamtempo.backend.authentication.entity.SecurityRoleEnum;
import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.gen.model.LoginResponseDto;
import com.ybritto.teamtempo.backend.gen.model.RegisterUserDto;
import com.ybritto.teamtempo.backend.gen.model.SecurityRoleEnumDto;
import com.ybritto.teamtempo.backend.gen.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("AuthenticationMapper Unit Tests")
class AuthenticationMapperTest {

    private AuthenticationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AuthenticationMapper.class);
    }

    @Test
    @DisplayName("Should map UserEntity to LoginResponseDto with all fields")
    void shouldMapUserEntityToLoginResponseDto() {
        // Given
        UUID userId = UUID.randomUUID();
        String token = "test-jwt-token";
        long expirationTime = 3600L;
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(userId)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .role(SecurityRoleEnum.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        LoginResponseDto result = mapper.mapDto(userEntity, token, expirationTime);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getEmail()).isEqualTo(userEntity.getEmail()),
                () -> assertThat(result.getToken()).isEqualTo(token),
                () -> assertThat(result.getExpiresIn()).isEqualTo(expirationTime),
                () -> assertThat(result.getRole()).isEqualTo(SecurityRoleEnumDto.ADMIN)
        );
    }

    @Test
    @DisplayName("Should map UserEntity with USER role to LoginResponseDto")
    void shouldMapUserEntityWithUserRoleToLoginResponseDto() {
        // Given
        UUID userId = UUID.randomUUID();
        String token = "test-jwt-token";
        long expirationTime = 7200L;
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(userId)
                .name("Test User")
                .email("user@example.com")
                .password("encodedPassword")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        LoginResponseDto result = mapper.mapDto(userEntity, token, expirationTime);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getEmail()).isEqualTo(userEntity.getEmail()),
                () -> assertThat(result.getToken()).isEqualTo(token),
                () -> assertThat(result.getExpiresIn()).isEqualTo(expirationTime),
                () -> assertThat(result.getRole()).isEqualTo(SecurityRoleEnumDto.USER)
        );
    }

    @Test
    @DisplayName("Should map UserEntity with null role to LoginResponseDto")
    void shouldMapUserEntityWithNullRoleToLoginResponseDto() {
        // Given
        UUID userId = UUID.randomUUID();
        String token = "test-jwt-token";
        long expirationTime = 3600L;
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(userId)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .role(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        LoginResponseDto result = mapper.mapDto(userEntity, token, expirationTime);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getEmail()).isEqualTo(userEntity.getEmail()),
                () -> assertThat(result.getToken()).isEqualTo(token),
                () -> assertThat(result.getExpiresIn()).isEqualTo(expirationTime),
                () -> assertThat(result.getRole()).isNull()
        );
    }

    @Test
    @DisplayName("Should convert SecurityRoleEnum.ADMIN to SecurityRoleEnumDto.ADMIN")
    void shouldConvertAdminRoleEnum() {
        // When
        SecurityRoleEnumDto result = mapper.getSecurityRoleEnum(SecurityRoleEnum.ADMIN);

        // Then
        assertThat(result).isEqualTo(SecurityRoleEnumDto.ADMIN);
    }

    @Test
    @DisplayName("Should convert SecurityRoleEnum.USER to SecurityRoleEnumDto.USER")
    void shouldConvertUserRoleEnum() {
        // When
        SecurityRoleEnumDto result = mapper.getSecurityRoleEnum(SecurityRoleEnum.USER);

        // Then
        assertThat(result).isEqualTo(SecurityRoleEnumDto.USER);
    }

    @Test
    @DisplayName("Should return null when SecurityRoleEnum is null")
    void shouldReturnNullWhenSecurityRoleEnumIsNull() {
        // When
        SecurityRoleEnumDto result = mapper.getSecurityRoleEnum(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should map RegisterUserDto to UserEntity with password")
    void shouldMapRegisterUserDtoToUserEntity() {
        // Given
        String encodedPassword = "encodedPassword123";
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setName("New User");
        registerUserDto.setEmail("newuser@example.com");
        registerUserDto.setPassword("plainPassword");

        // When
        UserEntity result = mapper.mapEntity(registerUserDto, encodedPassword);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(registerUserDto.getName()),
                () -> assertThat(result.getEmail()).isEqualTo(registerUserDto.getEmail()),
                () -> assertThat(result.getPassword()).isEqualTo(encodedPassword),
                () -> assertThat(result.getUuid()).isNull(), // Will be set in prePersist
                () -> assertThat(result.getId()).isNull()
        );
    }

    @Test
    @DisplayName("Should map RegisterUserDto with null fields to UserEntity")
    void shouldMapRegisterUserDtoWithNullFieldsToUserEntity() {
        // Given
        String encodedPassword = "encodedPassword123";
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setName(null);
        registerUserDto.setEmail(null);
        registerUserDto.setPassword(null);

        // When
        UserEntity result = mapper.mapEntity(registerUserDto, encodedPassword);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getName()).isNull(),
                () -> assertThat(result.getEmail()).isNull(),
                () -> assertThat(result.getPassword()).isEqualTo(encodedPassword)
        );
    }

    @Test
    @DisplayName("Should map UserEntity to UserDto with all fields")
    void shouldMapUserEntityToUserDto() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(userId)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .role(SecurityRoleEnum.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        UserDto result = mapper.mapDto(userEntity);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getUuid()).isEqualTo(userId.toString()),
                () -> assertThat(result.getName()).isEqualTo(userEntity.getName()),
                () -> assertThat(result.getEmail()).isEqualTo(userEntity.getEmail()),
                () -> assertThat(result.getEnabled()).isEqualTo(userEntity.isEnabled())
        );
    }

    @Test
    @DisplayName("Should map UserEntity with disabled status to UserDto")
    void shouldMapUserEntityWithDisabledStatusToUserDto() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(userId)
                .name("Disabled User")
                .email("disabled@example.com")
                .password("encodedPassword")
                .enabled(false)
                .role(SecurityRoleEnum.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        UserDto result = mapper.mapDto(userEntity);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getUuid()).isEqualTo(userId.toString()),
                () -> assertThat(result.getName()).isEqualTo(userEntity.getName()),
                () -> assertThat(result.getEmail()).isEqualTo(userEntity.getEmail()),
                () -> assertThat(result.getEnabled()).isFalse()
        );
    }

    @Test
    @DisplayName("Should map UserEntity with null UUID to UserDto")
    void shouldMapUserEntityWithNullUuidToUserDto() {
        // Given
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(null)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        UserDto result = mapper.mapDto(userEntity);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getUuid()).isNull(),
                () -> assertThat(result.getName()).isEqualTo(userEntity.getName()),
                () -> assertThat(result.getEmail()).isEqualTo(userEntity.getEmail())
        );
    }

    @Test
    @DisplayName("Should map UserEntity with null name to UserDto")
    void shouldMapUserEntityWithNullNameToUserDto() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(userId)
                .name(null)
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        UserDto result = mapper.mapDto(userEntity);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getUuid()).isEqualTo(userId.toString()),
                () -> assertThat(result.getName()).isNull(),
                () -> assertThat(result.getEmail()).isEqualTo(userEntity.getEmail())
        );
    }

    @Test
    @DisplayName("Should map UserEntity with null email to UserDto")
    void shouldMapUserEntityWithNullEmailToUserDto() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(userId)
                .name("Test User")
                .email(null)
                .password("encodedPassword")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        UserDto result = mapper.mapDto(userEntity);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getUuid()).isEqualTo(userId.toString()),
                () -> assertThat(result.getName()).isEqualTo(userEntity.getName()),
                () -> assertThat(result.getEmail()).isNull()
        );
    }

    @Test
    @DisplayName("Should handle different expiration times in LoginResponseDto")
    void shouldHandleDifferentExpirationTimes() {
        // Given
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .build();

        // When & Then
        assertAll(
                () -> {
                    LoginResponseDto result1 = mapper.mapDto(userEntity, "token1", 3600L);
                    assertThat(result1.getExpiresIn()).isEqualTo(3600L);
                },
                () -> {
                    LoginResponseDto result2 = mapper.mapDto(userEntity, "token2", 7200L);
                    assertThat(result2.getExpiresIn()).isEqualTo(7200L);
                },
                () -> {
                    LoginResponseDto result3 = mapper.mapDto(userEntity, "token3", 0L);
                    assertThat(result3.getExpiresIn()).isEqualTo(0L);
                }
        );
    }

    @Test
    @DisplayName("Should handle empty token in LoginResponseDto")
    void shouldHandleEmptyToken() {
        // Given
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .role(SecurityRoleEnum.USER)
                .build();

        // When
        LoginResponseDto result = mapper.mapDto(userEntity, "", 3600L);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getToken()).isEmpty(),
                () -> assertThat(result.getExpiresIn()).isEqualTo(3600L)
        );
    }
}

