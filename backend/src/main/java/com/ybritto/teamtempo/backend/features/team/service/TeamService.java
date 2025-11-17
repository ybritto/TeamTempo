package com.ybritto.teamtempo.backend.features.team.service;

import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.features.team.mapper.TeamMapper;
import com.ybritto.teamtempo.backend.features.team.repository.TeamRepository;
import com.ybritto.teamtempo.backend.gen.model.TeamDto;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public List<TeamDto> getMyTeams(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        // The principal is set to UserEntity in JwtAuthenticationFilter
        UserEntity user = (UserEntity) authentication.getPrincipal();
        
        return teamMapper.mapToDtoList(teamRepository.findByUser(user));
    }

}
