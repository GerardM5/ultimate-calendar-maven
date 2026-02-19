package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestUpdateDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleResponseDTO;
import org.example.ultimatecalendarmaven.model.StaffSchedule;
import org.example.ultimatecalendarmaven.utils.BusinessTimeService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = { StaffMapper.class, BusinessTimeService.class })
public interface StaffScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "startTime", expression = "java(businessTimeService.toInstant(dto.getStartTime()))")
    @Mapping(target = "endTime", expression = "java(businessTimeService.toInstant(dto.getEndTime()))")
    StaffSchedule toEntity(StaffScheduleRequestDTO dto, @Context BusinessTimeService businessTimeService);

    List<StaffSchedule> toEntity(List<StaffScheduleRequestDTO> dtos, @Context BusinessTimeService businessTimeService);

    @Mapping(target = "id", expression = "java(entity.getId() != null ? entity.getId().toString() : null)")
    @Mapping(target = "startTime", expression = "java(businessTimeService.toLocal(entity.getStartTime()))")
    @Mapping(target = "endTime", expression = "java(businessTimeService.toLocal(entity.getEndTime()))")
    StaffScheduleResponseDTO toResponse(StaffSchedule entity, @Context BusinessTimeService businessTimeService);
    List<StaffScheduleResponseDTO> toResponse(List<StaffSchedule> entities, @Context BusinessTimeService businessTimeService);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "startTime", expression = "java(businessTimeService.toInstant(dto.getStartTime()))")
    @Mapping(target = "endTime", expression = "java(businessTimeService.toInstant(dto.getEndTime()))")
    void updateEntityFromDto(StaffScheduleRequestUpdateDTO dto,
                             @MappingTarget StaffSchedule entity,
                             @Context BusinessTimeService businessTimeService);
}
