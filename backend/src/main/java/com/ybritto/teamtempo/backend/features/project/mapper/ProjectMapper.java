package com.ybritto.teamtempo.backend.features.project.mapper;

import com.ybritto.teamtempo.backend.core.mapper.CommonsMapper;
import com.ybritto.teamtempo.backend.features.iteration.mapper.IterationMapper;
import com.ybritto.teamtempo.backend.features.project.entity.ProjectEntity;
import com.ybritto.teamtempo.backend.features.projectConfiguration.mapper.ProjectConfigurationMapper;
import com.ybritto.teamtempo.backend.gen.model.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {CommonsMapper.class, ProjectConfigurationMapper.class, IterationMapper.class})
public interface ProjectMapper {

    default List<ProjectDto> mapToDtoListWithoutTeam(List<ProjectEntity> allByTeam) {
        if ( allByTeam == null ) {
            return new ArrayList<>();
        }

        List<ProjectDto> list = new ArrayList<>( allByTeam.size() );
        for ( ProjectEntity projectEntity : allByTeam ) {
            list.add( mapToDtoWithoutTeam( projectEntity ) );
        }

        return list;
    }

    @Named("mapToDtoWithoutTeam")
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "projectConfiguration", source = "projectConfigurations", qualifiedByName = "mapToDtoFromList")
    @Mapping(target = "iterations", source = "iterations", qualifiedByName = "mapIterationsToDtoWithoutProject")
    ProjectDto mapToDtoWithoutTeam(ProjectEntity entity);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "projectConfigurations", source = "projectDto.projectConfiguration", qualifiedByName = "mapToProjectConfigurationEntity")
    ProjectEntity mapToEntity(ProjectDto projectDto, Long id);


}
