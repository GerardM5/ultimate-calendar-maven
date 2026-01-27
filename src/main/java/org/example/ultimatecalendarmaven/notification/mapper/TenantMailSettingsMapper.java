package org.example.ultimatecalendarmaven.notification.mapper;

import org.example.ultimatecalendarmaven.notification.dto.TenantMailSettingsRequest;
import org.example.ultimatecalendarmaven.notification.dto.TenantMailSettingsResponse;
import org.example.ultimatecalendarmaven.notification.model.TenantMailSettings;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TenantMailSettingsMapper {

    TenantMailSettingsResponse toResponse(TenantMailSettings entity);

    /**
     * Usado para aplicar cambios parciales sobre la entidad existente.
     * Ignoramos nulls para permitir "PATCH-like" con PUT si quieres.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "providerIdentityId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(TenantMailSettingsRequest request, @MappingTarget TenantMailSettings entity);
}