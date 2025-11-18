package com.ybritto.teamtempo.backend.features.team.service;

import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.core.exception.BadRequestException;
import com.ybritto.teamtempo.backend.core.exception.InvalidParameterException;
import com.ybritto.teamtempo.backend.core.exception.NotFoundException;
import com.ybritto.teamtempo.backend.core.utils.UUIDValidator;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import com.ybritto.teamtempo.backend.features.team.mapper.TeamMapper;
import com.ybritto.teamtempo.backend.features.team.repository.TeamRepository;
import com.ybritto.teamtempo.backend.gen.model.TeamDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public List<TeamDto> getMyTeams() {
        logger.info("Retrieving teams for authenticated user");

        UserEntity user = getAuthenticatedUser();

        List<TeamDto> teams = teamMapper.mapToDtoList(teamRepository.findByUser(user));
        logger.info("Retrieved {} teams for user: {}", teams.size(), user.getEmail());
        return teams;
    }


    public TeamDto createTeam(TeamDto teamDto) {
        logger.info("Creating new team: {}", teamDto.getName());

        if (StringUtils.hasText(teamDto.getUuid())) {
            logger.warn("Attempted to create team with existing UUID: {}", teamDto.getUuid());
            throw new InvalidParameterException("New Team should not contain UUID");
        }

        TeamEntity entityToPersist = teamMapper.mapToEntity(teamDto)
                .toBuilder()
                .user(getAuthenticatedUser())
                .build();

        TeamDto savedTeamDto = teamMapper.mapToDto(
                teamRepository.save(entityToPersist));
        logger.info("Team {} created successfully with UUID: {}", savedTeamDto.getName(), savedTeamDto.getUuid());
        return savedTeamDto;
    }

    public TeamDto updateTeam(String teamUuid, TeamDto teamDto) {
        logger.debug("Entering method: updateTeam with uuid: {}, teamName: {}", teamUuid, teamDto.getName());

        UUID uuid = UUIDValidator.validateAndTransform(teamUuid);
        Optional<TeamEntity> teamEntity = teamRepository.findByUuid(uuid);
        if (teamEntity.isEmpty()) {
            logger.warn("Team not found for update with uuid: {}", teamUuid);
            throw new NotFoundException("Team not found with uuid: " + teamUuid);
        }

        if (teamDto.getUuid() == null || !teamDto.getUuid().equals(uuid.toString())) {
            logger.warn("Team UUID mismatch: provided={}, team.uuid={}", teamUuid, teamDto.getUuid());
            throw new BadRequestException("Team uuid does not match the provided uuid");
        }

        TeamEntity savedTeam = teamRepository.save(teamMapper.mapToEntity(teamDto, teamEntity.get().getId()));
        TeamDto result = teamMapper.mapToDto(savedTeam);

        logger.info("Successfully updated team: {} with UUID: {}", savedTeam.getName(), savedTeam.getUuid());
        logger.debug("Exiting method: updateTeam with result: team: {}", savedTeam.getName());

        return result;
    }

    private UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        // The principal is set to UserEntity in JwtAuthenticationFilter
        return (UserEntity) authentication.getPrincipal();
    }
}
