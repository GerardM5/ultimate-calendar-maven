package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestUpdateDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleResponseDTO;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = { StaffMapper.class })
public interface StaffScheduleMapper {

    StaffSchedule toEntity(StaffScheduleRequestDTO staffScheduleRequestDTO);

    List<StaffSchedule> toEntity(List<StaffScheduleRequestDTO> dtos);
    @Mapping(target = "id", expression = "java(staffSchedule.getId() != null ? staffSchedule.getId().toString() : null)")
    StaffScheduleResponseDTO toResponse(StaffSchedule staffSchedule);

    List<StaffScheduleResponseDTO> toResponse(List<StaffSchedule> entities);

    void updateEntityFromDto(StaffScheduleRequestUpdateDTO dto, @MappingTarget StaffSchedule entity);
}
