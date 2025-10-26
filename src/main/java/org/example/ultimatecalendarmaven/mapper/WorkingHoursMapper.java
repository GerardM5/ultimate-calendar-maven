package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.WorkingHoursRequestDTO;
import org.example.ultimatecalendarmaven.dto.WorkingHoursResponseDTO;
import org.example.ultimatecalendarmaven.model.WorkingHours;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WorkingHoursMapper {

    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "staffId", source = "staff.id")
    WorkingHoursResponseDTO toResponse(WorkingHours wh);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "staff", ignore = true)
    WorkingHours toEntity(WorkingHoursRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "staff", ignore = true)
    void update(@MappingTarget WorkingHours target, WorkingHoursRequestDTO source);
}