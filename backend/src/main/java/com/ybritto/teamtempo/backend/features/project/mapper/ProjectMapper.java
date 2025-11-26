package com.ybritto.teamtempo.backend.features.project.mapper;

import com.ybritto.teamtempo.backend.core.mapper.CommonsMapper;
import com.ybritto.teamtempo.backend.features.project.entity.ProjectEntity;
import com.ybritto.teamtempo.backend.gen.model.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {CommonsMapper.class})
public interface ProjectMapper {

    @Named("mapProjectWithoutTeam")
    @Mapping(target = "team", ignore = true)
    ProjectDto mapToDtoWithoutTeam(ProjectEntity entity);

}
