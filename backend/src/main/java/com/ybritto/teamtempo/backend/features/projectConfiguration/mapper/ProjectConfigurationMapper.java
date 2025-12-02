package com.ybritto.teamtempo.backend.features.projectConfiguration.mapper;

import com.ybritto.teamtempo.backend.core.exception.NotFoundException;
import com.ybritto.teamtempo.backend.core.mapper.CommonsMapper;
import com.ybritto.teamtempo.backend.features.projectConfiguration.entity.ProjectConfigurationEntity;
import com.ybritto.teamtempo.backend.gen.model.ProjectConfigurationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {CommonsMapper.class})
public interface ProjectConfigurationMapper {

    @Named("mapToDtoFromList")
    default ProjectConfigurationDto mapToDtoFromList(List<ProjectConfigurationEntity> configurationEntityList) {
        ProjectConfigurationEntity entity = new ProjectConfigurationEntity();
        if (!CollectionUtils.isEmpty(configurationEntityList)) {
            entity = configurationEntityList.stream()
                    .filter(ProjectConfigurationEntity::isActive)
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Not active configuration found for the project"));
        }
        return mapToDtoFromEntity(entity);
    }

    ProjectConfigurationDto mapToDtoFromEntity(ProjectConfigurationEntity entity);

    @Named("mapToProjectConfigurationEntity")
    default List<ProjectConfigurationEntity> mapToEntityFromDto(ProjectConfigurationDto dto) {
        if (dto == null) {
            return new ArrayList<>();
        }
        return Collections.singletonList(mapToEntity(dto));
    }

    ProjectConfigurationEntity mapToEntity(ProjectConfigurationDto dto);


}
