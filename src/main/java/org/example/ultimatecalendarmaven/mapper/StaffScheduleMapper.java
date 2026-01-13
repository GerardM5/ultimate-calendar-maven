package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleResponseDTO;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffScheduleMapper {

    StaffSchedule toEntity(StaffScheduleRequestDTO staffScheduleRequestDTO);

    List<StaffSchedule> toEntity(List<StaffScheduleRequestDTO> dtos);
    StaffScheduleResponseDTO toResponse(StaffSchedule staffSchedule);


}
