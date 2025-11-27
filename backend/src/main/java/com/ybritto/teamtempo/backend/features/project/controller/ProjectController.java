package com.ybritto.teamtempo.backend.features.project.controller;

import com.ybritto.teamtempo.backend.features.project.service.ProjectService;
import com.ybritto.teamtempo.backend.gen.api.ProjectsApi;
import com.ybritto.teamtempo.backend.gen.model.ProjectDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ProjectController implements ProjectsApi {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;

    @Override
    public ResponseEntity<ProjectDto> updateProject(String projectUuid, ProjectDto projectDto) {
        logger.info("PUT /projects/{} - Updating project: {}", projectUuid, projectDto.getName());
        ProjectDto updatedProject = projectService.updateProject(projectUuid, projectDto);
        logger.info("PUT /projects/{} - Successfully updated team: {} with UUID: {}",
                projectUuid, updatedProject.getName(), updatedProject.getUuid());
        return ResponseEntity.ok(updatedProject);
    }
}
