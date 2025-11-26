package com.ybritto.teamtempo.backend.features.project;

import com.ybritto.teamtempo.backend.core.exception.NotFoundException;
import com.ybritto.teamtempo.backend.core.utils.UUIDValidator;
import com.ybritto.teamtempo.backend.features.project.mapper.ProjectMapper;
import com.ybritto.teamtempo.backend.features.project.repository.ProjectRepository;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import com.ybritto.teamtempo.backend.features.team.repository.TeamRepository;
import com.ybritto.teamtempo.backend.gen.model.ProjectDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final ProjectMapper projectMapper;

    public List<ProjectDto> getProejctsByTeamUuid(String teamUuidAsString) {
        UUID teamUuid = UUIDValidator.validateAndTransform(teamUuidAsString);
        TeamEntity team = teamRepository.findByUuid(teamUuid)
                .orElseThrow(() -> new NotFoundException(String.format("Team with uuid %s not found", teamUuidAsString)));
        return projectMapper.mapToDtoListWithoutTeam(projectRepository.findAllByTeam(team));
    }

}
