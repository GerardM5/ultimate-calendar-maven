package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.CustomerRequestDTO;
import org.example.ultimatecalendarmaven.dto.CustomerResponseDTO;
import org.example.ultimatecalendarmaven.model.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "tenantId", source = "tenant.id")
    CustomerResponseDTO toResponse(Customer entity);

    // request -> entity (tenant lo resuelve el service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "createdAt", ignore = true) // lo setea @PrePersist o el service
    Customer toEntity(CustomerRequestDTO dto);

    // patch/put ignorando nulls (tenant no tocamos aquí)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void update(@MappingTarget Customer target, CustomerRequestDTO source);
}