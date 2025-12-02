package com.ybritto.teamtempo.backend.features.project.service;

import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        UUID teamUuid = UUIDValidator.validateAndTransform(teamUuidAsString);
        TeamEntity team = teamRepository.findByUuid(teamUuid)
                .orElseThrow(() -> new NotFoundException(String.format("Team with uuid %s not found", teamUuidAsString)));
        return projectMapper.mapToDtoListWithoutTeam(projectRepository.findAllByTeam(team));
    }

    public ProjectDto updateProject(String projectUuid, ProjectDto projectDto) {
        logger.debug("Entering method: updateProject with uuid: {}, projectName: {}", projectUuid, projectDto.getName());

        UUID uuid = UUIDValidator.validateAndTransform(projectUuid);
        ProjectEntity projectEntity = projectRepository.findByUuid(uuid).orElseThrow(() -> {
            logger.warn("Pro not found for update with uuid: {}", projectUuid);
            return new NotFoundException("Team not found with uuid: " + projectUuid);
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

        logger.info("Successfully updated team: {} with UUID: {}", result.getName(), result.getUuid());
        logger.debug("Exiting method: updateTeam with result: team: {}", result.getName());

        return result;
    }

    public void deleteProject(String projectUuid) {
        logger.debug("Entering method: deleteProject with uuid: {}", projectUuid);
        UUID uuid = UUIDValidator.validateAndTransform(projectUuid);
        ProjectEntity projectToDelete = projectRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Project not found with uuid: " + projectUuid));

        projectRepository.delete(projectToDelete);

        logger.info("Successfully deleted project: {} with UUID: {}", projectToDelete.getName(), projectUuid);
        logger.debug("Exiting method: deleteProject with result: team: {}", projectToDelete.getName());
    }

    public ProjectDto createProjectForTeam(String teamUuidAsString, ProjectDto projectDto) {
        logger.debug("Entering method: createProjectForTeam with uuid: {}, projectName: {}", teamUuidAsString, projectDto.getName());

        TeamEntity team = teamRepository.findByUuid(UUIDValidator.validateAndTransform(teamUuidAsString))
                .orElseThrow(() -> new NotFoundException("Team not found with uuid: " + teamUuidAsString));

        ProjectEntity projectToCreate = projectMapper.mapToEntity(projectDto, null)
                .toBuilder()
                .team(team)
                .build();

        ProjectDto projectDtoCreated = projectMapper
                .mapToDtoWithoutTeam(projectRepository.save(projectToCreate));

        logger.info("Successfully created project: {} with UUID: {}", projectDtoCreated.getName(), projectDtoCreated.getUuid());
        logger.debug("Exiting method: createProjectForTeam with result: team: {}", projectDtoCreated.getName());

        return projectDtoCreated;
    }
}
