package com.ybritto.teamtempo.backend.features.team.mapper;

import com.ybritto.teamtempo.backend.core.mapper.CommonsMapper;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import com.ybritto.teamtempo.backend.gen.model.TeamDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {CommonsMapper.class})
public interface TeamMapper {

    List<TeamDto> mapToDtoList(List<TeamEntity> teams);

}
