package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.StaffRequestDTO;
import org.example.ultimatecalendarmaven.dto.StaffResponseDTO;
import org.example.ultimatecalendarmaven.model.Staff;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StaffMapper {

    // entity -> response
    //@Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "services", ignore = true)
    StaffResponseDTO toResponse(Staff staff);

    // request -> entity (NO seteamos tenant aquí; lo resuelve el service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    Staff toEntity(StaffRequestDTO dto);

    // partial update ignorando nulls
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tenant", ignore = true)
    void update(@MappingTarget Staff target, StaffRequestDTO source);
}