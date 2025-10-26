package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.TimeOffRequestDTO;
import org.example.ultimatecalendarmaven.dto.TimeOffResponseDTO;
import org.example.ultimatecalendarmaven.model.TimeOff;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TimeOffMapper {

    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "staffId", source = "staff.id")
    TimeOffResponseDTO toResponse(TimeOff entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "staff", ignore = true)
    TimeOff toEntity(TimeOffRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "staff", ignore = true)
    void update(@MappingTarget TimeOff target, TimeOffRequestDTO source);
}