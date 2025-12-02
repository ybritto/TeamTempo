package com.ybritto.teamtempo.backend.features.team.controller;

import com.ybritto.teamtempo.backend.features.project.service.ProjectService;
import com.ybritto.teamtempo.backend.features.team.service.TeamService;
import com.ybritto.teamtempo.backend.gen.api.TeamsApi;
import com.ybritto.teamtempo.backend.gen.model.ProjectDto;
import com.ybritto.teamtempo.backend.gen.model.TeamDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class TeamController implements TeamsApi {

    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);
    private TeamService teamService;
    private ProjectService projectService;

    @Override
    public ResponseEntity<List<TeamDto>> myTeams() {
        logger.info("GET /teams/my-teams - Finding my teams");
        List<TeamDto> dtoList = teamService.getMyTeams();
        logger.info("GET /auth/my-teams - {} teams returned", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    @Override
    public ResponseEntity<TeamDto> createTeam(TeamDto teamDto) {
        logger.info("POST /teams - Creating team: {}", teamDto.getName());
        TeamDto createdTeam = teamService.createTeam(teamDto);
        logger.info("POST /teams - Successfully created team: {} with UUID: {}",
                createdTeam.getName(), createdTeam.getUuid());
        return ResponseEntity.ok(createdTeam);
    }

    @Override
    public ResponseEntity<TeamDto> updateTeam(String teamUuid, TeamDto teamDto) {
        logger.info("PUT /teams/{} - Updating team: {}", teamUuid, teamDto.getName());
        TeamDto updatedTeam = teamService.updateTeam(teamUuid, teamDto);
        logger.info("PUT /teams/{} - Successfully updated team: {} with UUID: {}",
                teamUuid, updatedTeam.getName(), updatedTeam.getUuid());
        return ResponseEntity.ok(updatedTeam);
    }


    @Override
    public ResponseEntity<Void> deleteTeam(String uuid) {
        logger.info("DELETE /teams/{} - Deleting team", uuid);
        teamService.deleteTeam(uuid);
        logger.info("DELETE /teams/{} - Successfully deleted team", uuid);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteSelectedTeams(List<String> teamUuids) {
        logger.info("DELETE /api/v1/teams - Deleting {} selected teams", teamUuids.size());
        teamService.deleteSelectedTeams(teamUuids);
        logger.info("DELETE /api/v1/teams - Successfully deleted {} selected teams", teamUuids.size());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ProjectDto>> getTeamProjects(String uuid) {
        logger.info("GET /teams/{}/projects - Projects for the team",  uuid);
        List<ProjectDto> dtoList = projectService.getProjectsByTeamUuid(uuid);
        logger.info("GET /teams/{}/projects - {} Projects found",  uuid, dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    @Override
    public ResponseEntity<ProjectDto> createProjectForTeam(String uuid, ProjectDto projectDto) {
        logger.info("GET /teams/{}/projects - Create projects {} for the team {}",  uuid,  projectDto.getName(), uuid);
        ProjectDto createdDto = projectService.createProjectForTeam(uuid, projectDto);
        logger.info("GET /teams/{}/projects - Project {} created for the team {}",  uuid,  createdDto.getName(), uuid);
        return ResponseEntity.ok(createdDto);
    }
}
