package com.ybritto.teamtempo.backend.features.team.mapper;

import com.ybritto.teamtempo.backend.core.mapper.CommonsMapper;
import com.ybritto.teamtempo.backend.features.project.mapper.ProjectMapper;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import com.ybritto.teamtempo.backend.gen.model.TeamDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {CommonsMapper.class, ProjectMapper.class})
public interface TeamMapper {

    List<TeamDto> mapToDtoList(List<TeamEntity> entityList);

    @Mapping(target = "projects", qualifiedByName = "mapToDtoWithoutTeam")
    TeamDto mapToDto(TeamEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "projects", ignore = true)
    TeamEntity mapToEntity(TeamDto dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "projects", ignore = true)
    TeamEntity mapToEntity(TeamDto teamDto, Long id);
}
