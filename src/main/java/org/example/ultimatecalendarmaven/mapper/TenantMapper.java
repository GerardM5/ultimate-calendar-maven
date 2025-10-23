package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.*;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    TenantResponseDTO toResponse(Tenant tenant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    Tenant toEntity(TenantRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Tenant target, TenantRequestDTO source);
}