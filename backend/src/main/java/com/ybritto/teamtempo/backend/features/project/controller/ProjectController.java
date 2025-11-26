package com.ybritto.teamtempo.backend.features.project.controller;

import com.ybritto.teamtempo.backend.gen.api.ProjectsApi;
import com.ybritto.teamtempo.backend.gen.model.ProjectDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController implements ProjectsApi {

    @Override
    public ResponseEntity<ProjectDto> updateProject(String uuid, ProjectDto projectDto) {
        return ProjectsApi.super.updateProject(uuid, projectDto);
    }
}
