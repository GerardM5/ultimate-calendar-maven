package org.example.ultimatecalendarmaven.mapper;

import org.example.ultimatecalendarmaven.dto.AppointmentConfirmed;
import org.example.ultimatecalendarmaven.dto.AppointmentRequestDTO;
import org.example.ultimatecalendarmaven.dto.AppointmentResponseDTO;
import org.example.ultimatecalendarmaven.dto.AppointmentSummaryDTO;
import org.example.ultimatecalendarmaven.model.Appointment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring",
        config = MapStructConfig.class,
        uses = { CustomerMapper.class, ServiceMapper.class, StaffMapper.class })
public interface AppointmentMapper {

    // Convierte entidad → ResponseDTO
    @Mapping(target = "tenantId",  source = "tenant.id")
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "staffId",   source = "staff.id")
    @Mapping(target = "customerId",source = "customer.id")
    AppointmentResponseDTO toResponse(Appointment appointment);

    // Convierte lista de entidades → lista de ResponseDTOs
    List<AppointmentResponseDTO> toResponseList(List<Appointment> appointments);

    // Convierte RequestDTO → entidad
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenant.id",  source = "tenantId")
    @Mapping(target = "service.id", source = "serviceId")
    @Mapping(target = "staff.id",   source = "staffId")
@Mapping(target = "customer",   source = "customer")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "endsAt", ignore = true)
    @Mapping(target = "priceCents", ignore = true)
    @Mapping(target = "confirmationToken", ignore = true)
    Appointment toEntity(AppointmentRequestDTO dto);

    // Convierte entidad → SummaryDTO
    AppointmentSummaryDTO toSummary(Appointment appointment);


    List<AppointmentSummaryDTO> toSummaryList(List<Appointment> appointments);



    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "staffName", source = "staff.name")
    @Mapping(target = "date", source = "startsAt", qualifiedByName = "startsAtToDate")
    @Mapping(target = "time", source = "startsAt", qualifiedByName = "startsAtToTime")
    AppointmentConfirmed toConfirmed(Appointment appointment);

    @Named("startsAtToDate")
    default String startsAtToDate(OffsetDateTime startsAt) {
        if (startsAt == null) return null;

        return startsAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Named("startsAtToTime")
    default String startsAtToTime(OffsetDateTime startsAt) {
        if (startsAt == null) return null;
        // Ejemplo: 19:30
        return startsAt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}