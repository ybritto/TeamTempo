package com.ybritto.teamtempo.backend.features.iteration.mapper;

import com.ybritto.teamtempo.backend.core.mapper.CommonsMapper;
import com.ybritto.teamtempo.backend.features.iteration.entity.IterationEntity;
import com.ybritto.teamtempo.backend.gen.model.IterationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {CommonsMapper.class})
public interface IterationMapper {

    @Named("mapIterationsToDtoWithoutProject")
    List<IterationDto> mapIterationsToDtoWithoutProject(List<IterationEntity> entity);



    @Mapping(target = "project", ignore = true)
    @Mapping(target = "plannedStartDate", source = "planned.startDate")
    @Mapping(target = "plannedEndDate", source = "planned.endDate")
    @Mapping(target = "plannedCapacity", source = "planned.capacity")
    @Mapping(target = "plannedForecast", source = "planned.forecast")
    @Mapping(target = "actualStartDate", source = "actual.startDate")
    @Mapping(target = "actualEndDate", source = "actual.endDate")
    @Mapping(target = "actualCapacity", source = "actual.capacity")
    @Mapping(target = "actualForecast", source = "actual.forecast")
    IterationDto mapToDtoWithoutProject(IterationEntity entity);

}
