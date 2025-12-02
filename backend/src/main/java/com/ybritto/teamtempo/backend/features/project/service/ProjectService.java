package com.ybritto.teamtempo.backend.features.project.service;

import com.ybritto.teamtempo.backend.core.exception.BadRequestException;
import com.ybritto.teamtempo.backend.core.exception.NotFoundException;
import com.ybritto.teamtempo.backend.core.utils.UUIDValidator;
import com.ybritto.teamtempo.backend.features.project.entity.ProjectEntity;
import com.ybritto.teamtempo.backend.features.project.mapper.ProjectMapper;
import com.ybritto.teamtempo.backend.features.project.repository.ProjectRepository;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import com.ybritto.teamtempo.backend.features.team.repository.TeamRepository;
import com.ybritto.teamtempo.backend.gen.model.ProjectDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TeamRepository teamRepository;

    public List<ProjectDto> getProjectsByTeamUuid(String teamUuidAsString) {
        logger.debug("Retrieving projects for team UUID: {}", teamUuidAsString);
        UUID teamUuid = UUIDValidator.validateAndTransform(teamUuidAsString);
        TeamEntity team = teamRepository.findByUuid(teamUuid)
                .orElseThrow(() -> {
                    logger.warn("Team not found for retrieving projects with UUID: {}", teamUuidAsString);
                    return new NotFoundException(String.format("Team with uuid %s not found", teamUuidAsString));
                });
        List<ProjectDto> projects = projectMapper.mapToDtoListWithoutTeam(projectRepository.findAllByTeam(team));
        logger.info("Retrieved {} projects for team UUID: {}", projects.size(), teamUuidAsString);
        return projects;
    }

    public ProjectDto updateProject(String projectUuid, ProjectDto projectDto) {
        logger.debug("Entering method: updateProject with uuid: {}, projectName: {}", projectUuid, projectDto.getName());

        UUID uuid = UUIDValidator.validateAndTransform(projectUuid);
        ProjectEntity projectEntity = projectRepository.findByUuid(uuid).orElseThrow(() -> {
            logger.warn("Project not found for update with UUID: {}", projectUuid);
            return new NotFoundException("Project not found with uuid: " + projectUuid);
        });

        if (projectDto.getUuid() == null || !projectDto.getUuid().equals(uuid.toString())) {
            logger.warn("Project UUID mismatch: provided={}, team.uuid={}", projectUuid, projectDto.getUuid());
            throw new BadRequestException("Project uuid does not match the provided uuid");
        }

        ProjectEntity entityToUpdate = projectMapper.mapToEntity(projectDto,projectEntity.getId())
                .toBuilder()
                .team(projectEntity.getTeam())
                .build();
        ProjectDto result = projectMapper.mapToDtoWithoutTeam(projectRepository.save(entityToUpdate));

        logger.info("Successfully updated project: {} with UUID: {}", result.getName(), result.getUuid());
        logger.debug("Exiting method: updateProject with result: project: {}", result.getName());

        return result;
    }

    public void deleteProject(String projectUuid) {
        logger.debug("Entering method: deleteProject with UUID: {}", projectUuid);
        UUID uuid = UUIDValidator.validateAndTransform(projectUuid);
        ProjectEntity projectToDelete = projectRepository.findByUuid(uuid)
                .orElseThrow(() -> {
                    logger.warn("Project not found for deletion with UUID: {}", projectUuid);
                    return new NotFoundException("Project not found with uuid: " + projectUuid);
                });

        projectRepository.delete(projectToDelete);

        logger.info("Successfully deleted project: {} with UUID: {}", projectToDelete.getName(), projectUuid);
        logger.debug("Exiting method: deleteProject with result: project: {}", projectToDelete.getName());
    }

    public ProjectDto createProjectForTeam(String teamUuidAsString, ProjectDto projectDto) {
        logger.debug("Entering method: createProjectForTeam with team UUID: {}, projectName: {}", teamUuidAsString, projectDto.getName());

        TeamEntity team = teamRepository.findByUuid(UUIDValidator.validateAndTransform(teamUuidAsString))
                .orElseThrow(() -> {
                    logger.warn("Team not found for creating project with UUID: {}", teamUuidAsString);
                    return new NotFoundException("Team not found with uuid: " + teamUuidAsString);
                });

        ProjectEntity projectToCreate = projectMapper.mapToEntity(projectDto, null)
                .toBuilder()
                .team(team)
                .build();

        ProjectDto projectDtoCreated = projectMapper
                .mapToDtoWithoutTeam(projectRepository.save(projectToCreate));

        logger.info("Successfully created project: {} with UUID: {} for team: {}", 
                projectDtoCreated.getName(), projectDtoCreated.getUuid(), teamUuidAsString);
        logger.debug("Exiting method: createProjectForTeam with result: project: {}", projectDtoCreated.getName());

        return projectDtoCreated;
    }
}
