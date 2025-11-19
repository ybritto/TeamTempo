package com.ybritto.teamtempo.backend.features.team.repository;

import com.ybritto.teamtempo.backend.authentication.entity.SecurityRoleEnum;
import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TeamRepository Integration Tests")
class TeamRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TeamRepository teamRepository;

    private UserEntity testUser;
    private TeamEntity testTeam;

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
    }

    @Test
    @DisplayName("Should save and find team by UUID")
    void shouldSaveAndFindTeamByUuid() {
        // Given
        UserEntity savedUser = entityManager.persistAndFlush(testUser);
        TeamEntity teamToSave = testTeam.toBuilder().user(savedUser).build();
        TeamEntity savedTeam = entityManager.persistAndFlush(teamToSave);
        UUID teamUuid = savedTeam.getUuid();

        // When
        Optional<TeamEntity> foundTeam = teamRepository.findByUuid(teamUuid);

        // Then
        assertThat(foundTeam).isPresent();
        assertThat(foundTeam.get().getName()).isEqualTo("Test Team");
        assertThat(foundTeam.get().getUuid()).isEqualTo(teamUuid);
        assertThat(foundTeam.get().getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when team UUID does not exist")
    void shouldReturnEmptyWhenTeamUuidDoesNotExist() {
        // Given
        UUID nonExistentUuid = UUID.randomUUID();

        // When
        Optional<TeamEntity> foundTeam = teamRepository.findByUuid(nonExistentUuid);

        // Then
        assertThat(foundTeam).isEmpty();
    }

    @Test
    @DisplayName("Should find all teams by user")
    void shouldFindAllTeamsByUser() {
        // Given
        UserEntity savedUser = entityManager.persistAndFlush(testUser);

        TeamEntity team1 = TeamEntity.builder()
                .name("Team One")
                .description("Description One")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .user(savedUser)
                .build();

        TeamEntity team2 = TeamEntity.builder()
                .name("Team Two")
                .description("Description Two")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(60))
                .user(savedUser)
                .build();

        entityManager.persistAndFlush(team1);
        entityManager.persistAndFlush(team2);

        // When
        List<TeamEntity> userTeams = teamRepository.findByUser(savedUser);

        // Then
        assertThat(userTeams).hasSize(2);
        assertThat(userTeams).extracting(TeamEntity::getName)
                .containsExactlyInAnyOrder("Team One", "Team Two");
    }

    @Test
    @DisplayName("Should return empty list when user has no teams")
    void shouldReturnEmptyListWhenUserHasNoTeams() {
        // Given
        UserEntity savedUser = entityManager.persistAndFlush(testUser);

        // When
        List<TeamEntity> userTeams = teamRepository.findByUser(savedUser);

        // Then
        assertThat(userTeams).isEmpty();
    }

    @Test
    @DisplayName("Should find teams by multiple UUIDs")
    void shouldFindTeamsByMultipleUuids() {
        // Given
        UserEntity savedUser = entityManager.persistAndFlush(testUser);

        TeamEntity team1 = TeamEntity.builder()
                .name("Team One")
                .description("Description One")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .user(savedUser)
                .build();

        TeamEntity team2 = TeamEntity.builder()
                .name("Team Two")
                .description("Description Two")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(60))
                .user(savedUser)
                .build();

        TeamEntity team3 = TeamEntity.builder()
                .name("Team Three")
                .description("Description Three")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(90))
                .user(savedUser)
                .build();

        TeamEntity savedTeam1 = entityManager.persistAndFlush(team1);
        TeamEntity savedTeam2 = entityManager.persistAndFlush(team2);
        entityManager.persistAndFlush(team3);

        List<UUID> uuidsToFind = List.of(savedTeam1.getUuid(), savedTeam2.getUuid());

        // When
        List<TeamEntity> foundTeams = teamRepository.findByUuidIn(uuidsToFind);

        // Then
        assertThat(foundTeams).hasSize(2);
        assertThat(foundTeams).extracting(TeamEntity::getUuid)
                .containsExactlyInAnyOrder(savedTeam1.getUuid(), savedTeam2.getUuid());
    }

    @Test
    @DisplayName("Should return empty list when UUIDs do not exist")
    void shouldReturnEmptyListWhenUuidsDoNotExist() {
        // Given
        List<UUID> nonExistentUuids = List.of(UUID.randomUUID(), UUID.randomUUID());

        // When
        List<TeamEntity> foundTeams = teamRepository.findByUuidIn(nonExistentUuids);

        // Then
        assertThat(foundTeams).isEmpty();
    }

    @Test
    @DisplayName("Should save team with generated UUID")
    void shouldSaveTeamWithGeneratedUuid() {
        // Given
        UserEntity savedUser = entityManager.persistAndFlush(testUser);
        TeamEntity teamWithoutUuid = TeamEntity.builder()
                .name("Team Without UUID")
                .description("Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .user(savedUser)
                .build();

        // When
        TeamEntity savedTeam = entityManager.persistAndFlush(teamWithoutUuid);

        // Then
        assertThat(savedTeam.getUuid()).isNotNull();
    }

    @Test
    @DisplayName("Should save team without end date")
    void shouldSaveTeamWithoutEndDate() {
        // Given
        UserEntity savedUser = entityManager.persistAndFlush(testUser);
        TeamEntity teamWithoutEndDate = TeamEntity.builder()
                .name("Team Without End Date")
                .description("Description")
                .startDate(LocalDate.now())
                .user(savedUser)
                .build();

        // When
        TeamEntity savedTeam = entityManager.persistAndFlush(teamWithoutEndDate);
        Optional<TeamEntity> foundTeam = teamRepository.findByUuid(savedTeam.getUuid());

        // Then
        assertThat(foundTeam).isPresent();
        assertThat(foundTeam.get().getEndDate()).isNull();
    }

    @Test
    @DisplayName("Should find teams for different users separately")
    void shouldFindTeamsForDifferentUsersSeparately() {
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

        UserEntity savedUser1 = entityManager.persistAndFlush(user1);
        UserEntity savedUser2 = entityManager.persistAndFlush(user2);

        TeamEntity team1 = TeamEntity.builder()
                .name("User1 Team")
                .description("Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .user(savedUser1)
                .build();

        TeamEntity team2 = TeamEntity.builder()
                .name("User2 Team")
                .description("Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .user(savedUser2)
                .build();

        entityManager.persistAndFlush(team1);
        entityManager.persistAndFlush(team2);

        // When
        List<TeamEntity> user1Teams = teamRepository.findByUser(savedUser1);
        List<TeamEntity> user2Teams = teamRepository.findByUser(savedUser2);

        // Then
        assertThat(user1Teams).hasSize(1);
        assertThat(user1Teams.get(0).getName()).isEqualTo("User1 Team");
        assertThat(user2Teams).hasSize(1);
        assertThat(user2Teams.get(0).getName()).isEqualTo("User2 Team");
    }
}

