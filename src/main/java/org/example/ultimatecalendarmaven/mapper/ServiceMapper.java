package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.ServiceRequestDTO;
import org.example.ultimatecalendarmaven.dto.ServiceResponseDTO;
import org.example.ultimatecalendarmaven.model.ServiceEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    // entity -> response
    @Mapping(target = "tenantId", source = "tenant.id")
    ServiceResponseDTO toResponse(ServiceEntity entity);

    // request -> entity (tenant se resuelve en el service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "active", expression = "java(dto.getActive() != null ? dto.getActive() : Boolean.TRUE)")
    ServiceEntity toEntity(ServiceRequestDTO dto);

    // partial update (ignora nulls), no tocar tenant
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tenant", ignore = true)
    void update(@MappingTarget ServiceEntity target, ServiceRequestDTO source);
}