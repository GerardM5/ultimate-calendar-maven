package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.AppointmentRequestDTO;
import org.example.ultimatecalendarmaven.dto.AppointmentResponseDTO;
import org.example.ultimatecalendarmaven.dto.AppointmentSummaryDTO;
import org.example.ultimatecalendarmaven.model.Appointment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    // Convierte entidad → ResponseDTO
    AppointmentResponseDTO toResponse(Appointment appointment);

    // Convierte lista de entidades → lista de ResponseDTOs
    List<AppointmentResponseDTO> toResponseList(List<Appointment> appointments);

    // Convierte RequestDTO → entidad
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    @Mapping(target = "active", ignore = true)
    Appointment toEntity(AppointmentRequestDTO dto);

    // Convierte entidad → SummaryDTO
    @Mapping(target = "staffName", source = "staff.name")
    @Mapping(target = "customerName", source = "customer.name")
    AppointmentSummaryDTO toSummary(Appointment appointment);

    List<AppointmentSummaryDTO> toSummaryList(List<Appointment> appointments);
}