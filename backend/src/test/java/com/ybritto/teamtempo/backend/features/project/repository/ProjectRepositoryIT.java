package com.ybritto.teamtempo.backend.features.project.repository;

import com.ybritto.teamtempo.backend.authentication.entity.SecurityRoleEnum;
import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.features.project.entity.ProjectEntity;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProjectRepository Integration Tests")
class ProjectRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

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
    @DisplayName("Should use project_key_id_seq sequence and nothing else")
    void shouldUseProjectKeyIdSeqSequenceAndNothingElse() throws NoSuchFieldException {
        // Given - Get the id field from ProjectEntity
        java.lang.reflect.Field idField = ProjectEntity.class.getDeclaredField("id");

        // When - Check the SequenceGenerator annotation
        jakarta.persistence.SequenceGenerator sequenceGenerator = idField.getAnnotation(jakarta.persistence.SequenceGenerator.class);

        // Then - Verify the sequence name is exactly "project_key_id_seq"
        assertThat(sequenceGenerator)
                .as("ProjectEntity.id field must have @SequenceGenerator annotation")
                .isNotNull();

        String actualSequenceName = sequenceGenerator.sequenceName();
        assertThat(actualSequenceName)
                .as("ProjectEntity must use 'project_key_id_seq' sequence, not '%s'. " +
                        "Using the wrong sequence will cause primary key violations.",
                        actualSequenceName)
                .isEqualTo("project_key_id_seq");

        // Verify the generator name matches
        assertThat(sequenceGenerator.name())
                .as("Generator name should be 'projectSeqGen'")
                .isEqualTo("projectSeqGen");
    }

    @Test
    @DisplayName("Should save and find project by UUID")
    void shouldSaveAndFindProjectByUuid() {
        // Given
        UserEntity savedUser = entityManager.persistAndFlush(testUser);
        TeamEntity savedTeam = entityManager.persistAndFlush(testTeam.toBuilder().user(savedUser).build());

        ProjectEntity project = ProjectEntity.builder()
                .name("Test Project")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .active(true)
                .team(savedTeam)
                .build();

        ProjectEntity savedProject = entityManager.persistAndFlush(project);
        UUID projectUuid = savedProject.getUuid();

        // When
        Optional<ProjectEntity> foundProject = projectRepository.findByUuid(projectUuid);

        // Then
        assertThat(foundProject).isPresent();
        assertThat(foundProject.get().getName()).isEqualTo("Test Project");
        assertThat(foundProject.get().getUuid()).isEqualTo(projectUuid);
        assertThat(foundProject.get().getTeam().getName()).isEqualTo("Test Team");
    }

    @Test
    @DisplayName("Should find all projects by team")
    void shouldFindAllProjectsByTeam() {
        // Given
        UserEntity savedUser = entityManager.persistAndFlush(testUser);
        TeamEntity savedTeam = entityManager.persistAndFlush(testTeam.toBuilder().user(savedUser).build());

        ProjectEntity project1 = ProjectEntity.builder()
                .name("Project One")
                .description("Description One")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .active(true)
                .team(savedTeam)
                .build();

        ProjectEntity project2 = ProjectEntity.builder()
                .name("Project Two")
                .description("Description Two")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(60))
                .active(true)
                .team(savedTeam)
                .build();

        entityManager.persistAndFlush(project1);
        entityManager.persistAndFlush(project2);

        // When
        List<ProjectEntity> teamProjects = projectRepository.findAllByTeam(savedTeam);

        // Then
        assertThat(teamProjects).hasSize(2);
        assertThat(teamProjects).extracting(ProjectEntity::getName)
                .containsExactlyInAnyOrder("Project One", "Project Two");
    }
}

