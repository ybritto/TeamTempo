package com.ybritto.teamtempo.backend.features.team.controller;

import com.ybritto.teamtempo.backend.features.team.service.TeamService;
import com.ybritto.teamtempo.backend.gen.api.TeamsApi;
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

    @Override
    public ResponseEntity<List<TeamDto>> myTeams() {
        logger.info("GET /teams/my-teams - Finding my teams");
        List<TeamDto> dtoList = teamService.getMyTeams();
        logger.info("GET /auth/my-teams - {} teams returned", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }
}
