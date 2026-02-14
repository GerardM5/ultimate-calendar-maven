package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestUpdateDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleResponseDTO;
import org.example.ultimatecalendarmaven.mapper.StaffScheduleMapper;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffSchedule;
import org.example.ultimatecalendarmaven.repository.StaffScheduleRepository;
import org.example.ultimatecalendarmaven.utils.BusinessTimeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffScheduleService {

    private final StaffScheduleRepository repository;
    private final StaffScheduleMapper mapper;
    private final StaffService staffService;
    private final BusinessTimeService businessTimeService;


    public List<StaffScheduleResponseDTO> assignSchedule(
            UUID tenantId,
            List<StaffScheduleRequestDTO> staffScheduleRequestDTOList
    ) {
        // 1) Unique staffIds
        Set<UUID> staffIds = staffScheduleRequestDTOList.stream()
                .map(StaffScheduleRequestDTO::getStaffId)
                .collect(Collectors.toSet());

        // 2) Batch fetch
        List<Staff> staffList = staffService.findAllById(staffIds);

        // 3) Validate missing staff ids
        Set<UUID> foundIds = staffList.stream()
                .map(Staff::getId)
                .collect(Collectors.toSet());

        Set<UUID> missingIds = new HashSet<>(staffIds);
        missingIds.removeAll(foundIds);

        if (!missingIds.isEmpty()) {
            throw new IllegalArgumentException("Staff not found: " + missingIds);
        }

        // 4) Validate tenant + build lookup map
        Map<UUID, Staff> staffById = staffList.stream()
                .peek(staff -> {
                    if (!staff.getTenant().getId().equals(tenantId)) {
                        throw new IllegalArgumentException("Staff does not belong to tenant: " + staff.getId());
                    }
                })
                .collect(Collectors.toMap(Staff::getId, Function.identity()));

        // 5) Map dtos -> entities
        List<StaffSchedule> entities = staffScheduleRequestDTOList.stream()
                .map(dto -> {
                    Staff staff = staffById.get(dto.getStaffId()); // safe (validated above)
                    StaffSchedule entity = mapper.toEntity(dto,businessTimeService);
                    entity.setStaff(staff);
                    return entity;
                })
                .toList();

        List<StaffSchedule> saved = repository.saveAll(entities);
        return mapper.toResponse(saved, businessTimeService);
    }

    public List<StaffScheduleResponseDTO> getSchedules(UUID tenantId, List<UUID> staffIds, String from, String to) {
        if (staffIds == null || staffIds.isEmpty()) {
            staffIds = staffService.findByTenant(tenantId).stream()
                    .map(Staff::getId)
                    .toList();
        } else {
            List<Staff> staffList = staffService.findAllById(new HashSet<>(staffIds));
            Set<UUID> foundIds = staffList.stream().map(Staff::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(staffIds);
            missingIds.removeAll(foundIds);
            if (!missingIds.isEmpty()) {
                throw new IllegalArgumentException("Staff not found: " + missingIds);
            }
            boolean anyWrongTenant = staffList.stream()
                    .anyMatch(s -> !s.getTenant().getId().equals(tenantId));
            if (anyWrongTenant) {
                throw new IllegalArgumentException("One or more staff do not belong to tenant");
            }
        }
        //si no viene from y to, poner principio de este mes y fin de este mes
        List<StaffSchedule> staffSchedules = repository.findAllByStaffIdsAndDateRange(
                staffIds,
                parseRangeStart(from),
                parseRangeEnd(to)
        );
        return mapper.toResponse(staffSchedules, businessTimeService);
    }

    public List<StaffSchedule> getScheduleByStaffAndRangeDates(UUID staffId, Instant startDate, Instant endDate) {
        return repository.findOverlapping(staffId, startDate, endDate);

    }


    public List<StaffScheduleResponseDTO> updateSchedule(UUID tenantId, UUID staffId, List<StaffScheduleRequestUpdateDTO> staffScheduleRequestDTOList) {
        Staff staff = staffService.findById(staffId).orElseThrow();
        if (!staff.getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Staff does not belong to tenant: " + staffId);
        }

        // Extraer los IDs de los DTOs
        List<UUID> scheduleIds = staffScheduleRequestDTOList.stream()
                .map(StaffScheduleRequestUpdateDTO::getId)
                .toList();

        // Buscar los horarios existentes en el repositorio
        List<StaffSchedule> existingSchedules = repository.findAllById(scheduleIds);

        // Actualizar los horarios existentes con los datos del DTO
        existingSchedules.forEach(schedule -> {
            if (!schedule.getStaff().getId().equals(staffId)) {
                throw new IllegalArgumentException("Schedule does not belong to staff: " + schedule.getId());
            }
            if (!schedule.getStaff().getTenant().getId().equals(tenantId)) {
                throw new IllegalArgumentException("Schedule does not belong to tenant: " + schedule.getId());
            }
            StaffScheduleRequestUpdateDTO dto = staffScheduleRequestDTOList.stream()
                    .filter(d -> d.getId().equals(schedule.getId()))
                    .findFirst()
                    .orElseThrow();
            mapper.updateEntityFromDto(dto, schedule, businessTimeService);
        });

        // Guardar los horarios actualizados
        List<StaffSchedule> saved = repository.saveAll(existingSchedules);
        return mapper.toResponse(saved, businessTimeService);
    }

    public void deleteSchedule(UUID tenantId, UUID id) {
        StaffSchedule schedule = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + id));

        if (!schedule.getStaff().getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Schedule does not belong to tenant: " + id);
        }

        repository.delete(schedule);
    }

    private Instant parseRangeStart(String from) {
        if (from != null && !from.isBlank()) {
            return parseFlexibleInstant(from);
        }
        // default: start of current month in business timezone
        var now = java.time.ZonedDateTime.now(businessTimeService.getZone());
        var startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        return businessTimeService.toInstant(startOfMonth);
    }

    private Instant parseRangeEnd(String to) {
        if (to != null && !to.isBlank()) {
            return parseFlexibleInstant(to);
        }
        // default: start of next month in business timezone
        var now = java.time.ZonedDateTime.now(businessTimeService.getZone());
        var startOfNextMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay().plusMonths(1);
        return businessTimeService.toInstant(startOfNextMonth);
    }

    private Instant parseFlexibleInstant(String value) {
        // Accept: Instant (e.g. 2026-02-14T13:00:00Z), OffsetDateTime, or LocalDateTime (business local)
        try {
            return Instant.parse(value);
        } catch (Exception ignored) {
        }
        try {
            return java.time.OffsetDateTime.parse(value).toInstant();
        } catch (Exception ignored) {
        }
        return businessTimeService.toInstant(java.time.LocalDateTime.parse(value));
    }


}
