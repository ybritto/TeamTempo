package com.ybritto.teamtempo.backend.features.team.mapper;

import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import com.ybritto.teamtempo.backend.gen.model.TeamDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("TeamMapper Unit Tests")
class TeamMapperTest {

    private TeamMapper mapper;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(TeamMapper.class);
        
        testUser = UserEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Should map list of TeamEntity to list of TeamDto")
    void shouldMapListOfTeamEntityToTeamDto() {
        // Given
        UUID team1Uuid = UUID.randomUUID();
        UUID team2Uuid = UUID.randomUUID();
        LocalDate startDate1 = LocalDate.of(2024, 1, 1);
        LocalDate endDate1 = LocalDate.of(2024, 12, 31);
        LocalDate startDate2 = LocalDate.of(2024, 6, 1);
        LocalDate endDate2 = LocalDate.of(2024, 12, 31);

        TeamEntity team1 = TeamEntity.builder()
                .id(1L)
                .uuid(team1Uuid)
                .name("Team 1")
                .description("Description 1")
                .startDate(startDate1)
                .endDate(endDate1)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TeamEntity team2 = TeamEntity.builder()
                .id(2L)
                .uuid(team2Uuid)
                .name("Team 2")
                .description("Description 2")
                .startDate(startDate2)
                .endDate(endDate2)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TeamEntity> teams = List.of(team1, team2);

        // When
        List<TeamDto> result = mapper.mapToDtoList(teams);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).getUuid()).isEqualTo(team1Uuid.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("Team 1"),
                () -> assertThat(result.get(0).getDescription()).isEqualTo("Description 1"),
                () -> assertThat(result.get(0).getStartDate()).isEqualTo(startDate1),
                () -> assertThat(result.get(0).getEndDate()).isEqualTo(endDate1),
                () -> assertThat(result.get(1).getUuid()).isEqualTo(team2Uuid.toString()),
                () -> assertThat(result.get(1).getName()).isEqualTo("Team 2"),
                () -> assertThat(result.get(1).getDescription()).isEqualTo("Description 2"),
                () -> assertThat(result.get(1).getStartDate()).isEqualTo(startDate2),
                () -> assertThat(result.get(1).getEndDate()).isEqualTo(endDate2)
        );
    }

    @Test
    @DisplayName("Should map empty list of TeamEntity to empty list of TeamDto")
    void shouldMapEmptyListToEmptyList() {
        // Given
        List<TeamEntity> teams = Collections.emptyList();

        // When
        List<TeamDto> result = mapper.mapToDtoList(teams);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).isEmpty()
        );
    }

    @Test
    @DisplayName("Should map list with single TeamEntity to list with single TeamDto")
    void shouldMapSingleTeamEntityToList() {
        // Given
        UUID teamUuid = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        TeamEntity team = TeamEntity.builder()
                .id(1L)
                .uuid(teamUuid)
                .name("Single Team")
                .description("Single Description")
                .startDate(startDate)
                .endDate(endDate)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TeamEntity> teams = List.of(team);

        // When
        List<TeamDto> result = mapper.mapToDtoList(teams);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getUuid()).isEqualTo(teamUuid.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("Single Team"),
                () -> assertThat(result.get(0).getDescription()).isEqualTo("Single Description"),
                () -> assertThat(result.get(0).getStartDate()).isEqualTo(startDate),
                () -> assertThat(result.get(0).getEndDate()).isEqualTo(endDate)
        );
    }

    @Test
    @DisplayName("Should map TeamEntity with null description to TeamDto")
    void shouldMapTeamEntityWithNullDescription() {
        // Given
        UUID teamUuid = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2024, 1, 1);

        TeamEntity team = TeamEntity.builder()
                .id(1L)
                .uuid(teamUuid)
                .name("Team Without Description")
                .description(null)
                .startDate(startDate)
                .endDate(null)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TeamEntity> teams = List.of(team);

        // When
        List<TeamDto> result = mapper.mapToDtoList(teams);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getUuid()).isEqualTo(teamUuid.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("Team Without Description"),
                () -> assertThat(result.get(0).getDescription()).isNull(),
                () -> assertThat(result.get(0).getStartDate()).isEqualTo(startDate),
                () -> assertThat(result.get(0).getEndDate()).isNull()
        );
    }

    @Test
    @DisplayName("Should map TeamEntity with null endDate to TeamDto")
    void shouldMapTeamEntityWithNullEndDate() {
        // Given
        UUID teamUuid = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2024, 1, 1);

        TeamEntity team = TeamEntity.builder()
                .id(1L)
                .uuid(teamUuid)
                .name("Ongoing Team")
                .description("Team without end date")
                .startDate(startDate)
                .endDate(null)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TeamEntity> teams = List.of(team);

        // When
        List<TeamDto> result = mapper.mapToDtoList(teams);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getUuid()).isEqualTo(teamUuid.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("Ongoing Team"),
                () -> assertThat(result.get(0).getDescription()).isEqualTo("Team without end date"),
                () -> assertThat(result.get(0).getStartDate()).isEqualTo(startDate),
                () -> assertThat(result.get(0).getEndDate()).isNull()
        );
    }

    @Test
    @DisplayName("Should map TeamEntity with null UUID to TeamDto")
    void shouldMapTeamEntityWithNullUuid() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        TeamEntity team = TeamEntity.builder()
                .id(1L)
                .uuid(null)
                .name("Team Without UUID")
                .description("Description")
                .startDate(startDate)
                .endDate(endDate)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TeamEntity> teams = List.of(team);

        // When
        List<TeamDto> result = mapper.mapToDtoList(teams);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getUuid()).isNull(),
                () -> assertThat(result.get(0).getName()).isEqualTo("Team Without UUID"),
                () -> assertThat(result.get(0).getDescription()).isEqualTo("Description"),
                () -> assertThat(result.get(0).getStartDate()).isEqualTo(startDate),
                () -> assertThat(result.get(0).getEndDate()).isEqualTo(endDate)
        );
    }

    @Test
    @DisplayName("Should map large list of TeamEntity to list of TeamDto")
    void shouldMapLargeListOfTeamEntity() {
        // Given
        List<TeamEntity> teams = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        for (int i = 0; i < 10; i++) {
            TeamEntity team = TeamEntity.builder()
                    .id((long) i)
                    .uuid(UUID.randomUUID())
                    .name("Team " + i)
                    .description("Description " + i)
                    .startDate(startDate.plusDays(i))
                    .endDate(endDate)
                    .user(testUser)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            teams.add(team);
        }

        // When
        List<TeamDto> result = mapper.mapToDtoList(teams);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(10),
                () -> assertThat(result.get(0).getName()).isEqualTo("Team 0"),
                () -> assertThat(result.get(9).getName()).isEqualTo("Team 9")
        );
    }

    @Test
    @DisplayName("Should preserve order when mapping list of TeamEntity")
    void shouldPreserveOrderWhenMappingList() {
        // Given
        UUID team1Uuid = UUID.randomUUID();
        UUID team2Uuid = UUID.randomUUID();
        UUID team3Uuid = UUID.randomUUID();

        TeamEntity team1 = TeamEntity.builder()
                .id(1L)
                .uuid(team1Uuid)
                .name("First Team")
                .startDate(LocalDate.of(2024, 1, 1))
                .user(testUser)
                .build();

        TeamEntity team2 = TeamEntity.builder()
                .id(2L)
                .uuid(team2Uuid)
                .name("Second Team")
                .startDate(LocalDate.of(2024, 2, 1))
                .user(testUser)
                .build();

        TeamEntity team3 = TeamEntity.builder()
                .id(3L)
                .uuid(team3Uuid)
                .name("Third Team")
                .startDate(LocalDate.of(2024, 3, 1))
                .user(testUser)
                .build();

        List<TeamEntity> teams = List.of(team1, team2, team3);

        // When
        List<TeamDto> result = mapper.mapToDtoList(teams);

        // Then
        assertAll(
                () -> assertThat(result).hasSize(3),
                () -> assertThat(result.get(0).getName()).isEqualTo("First Team"),
                () -> assertThat(result.get(1).getName()).isEqualTo("Second Team"),
                () -> assertThat(result.get(2).getName()).isEqualTo("Third Team")
        );
    }

    @Test
    @DisplayName("Should handle null list input by returning empty list")
    void shouldHandleNullListInput() {
        // When - MapStruct with RETURN_DEFAULT returns empty list for null input
        List<TeamDto> result = mapper.mapToDtoList(null);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).isEmpty()
        );
    }

    @Test
    @DisplayName("Should map TeamEntity with all fields populated to TeamDto")
    void shouldMapTeamEntityWithAllFields() {
        // Given
        UUID teamUuid = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 10, 0);

        TeamEntity team = TeamEntity.builder()
                .id(1L)
                .uuid(teamUuid)
                .name("Complete Team")
                .description("Complete Description")
                .startDate(startDate)
                .endDate(endDate)
                .user(testUser)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        List<TeamEntity> teams = List.of(team);

        // When
        List<TeamDto> result = mapper.mapToDtoList(teams);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getUuid()).isEqualTo(teamUuid.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("Complete Team"),
                () -> assertThat(result.get(0).getDescription()).isEqualTo("Complete Description"),
                () -> assertThat(result.get(0).getStartDate()).isEqualTo(startDate),
                () -> assertThat(result.get(0).getEndDate()).isEqualTo(endDate)
        );
    }
}

