package org.example.ultimatecalendarmaven.notification.mapper;

import org.example.ultimatecalendarmaven.notification.dto.OutboxEmailResponse;
import org.example.ultimatecalendarmaven.notification.model.OutboxEmail;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OutboxEmailMapper {
    OutboxEmailResponse toResponse(OutboxEmail entity);
}