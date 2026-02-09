package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestUpdateDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleResponseDTO;
import org.example.ultimatecalendarmaven.mapper.StaffScheduleMapper;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffSchedule;
import org.example.ultimatecalendarmaven.repository.StaffScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffScheduleService {

    @Autowired
    private final StaffScheduleRepository repository;
    @Autowired
    final StaffScheduleMapper mapper;
    @Autowired
    StaffService staffService;


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
                    StaffSchedule entity = mapper.toEntity(dto);
                    entity.setStaff(staff);
                    return entity;
                })
                .toList();

        return repository.saveAll(entities).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<StaffScheduleResponseDTO> getSchedules(UUID tenantId, List<UUID> staffIds, String from, String to) {

        if (staffIds == null || staffIds.isEmpty()) {
            staffIds = staffService.findByTenant(tenantId).stream()
                    .map(Staff::getId)
                    .toList();
        }
        //si no viene from y to, poner principio de este mes y fin de este mes
        List<StaffSchedule> staffSchedules = repository.findAllByStaffIdsAndDateRange(
                staffIds,
                from != null ? OffsetDateTime.parse(from) : OffsetDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0),
                to != null ? OffsetDateTime.parse(to) : OffsetDateTime.now().withDayOfMonth(1).plusMonths(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        );
        return staffSchedules.stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<StaffSchedule> getScheduleByStaffAndRangeDates(UUID staffId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return repository.findOverlapping(staffId, startDate, endDate);

    }


    public List<StaffScheduleResponseDTO> updateSchedule(UUID tenantId, UUID staffId, List<StaffScheduleRequestUpdateDTO> staffScheduleRequestDTOList) {
        Staff staff = staffService.findById(staffId).orElseThrow();

        // Extraer los IDs de los DTOs
        List<UUID> scheduleIds = staffScheduleRequestDTOList.stream()
                .map(StaffScheduleRequestUpdateDTO::getId)
                .toList();

        // Buscar los horarios existentes en el repositorio
        List<StaffSchedule> existingSchedules = repository.findAllById(scheduleIds);

        // Actualizar los horarios existentes con los datos del DTO
        existingSchedules.forEach(schedule -> {
            StaffScheduleRequestUpdateDTO dto = staffScheduleRequestDTOList.stream()
                    .filter(d -> d.getId().equals(schedule.getId()))
                    .findFirst()
                    .orElseThrow();
            mapper.updateEntityFromDto(dto, schedule);
        });

        // Guardar los horarios actualizados
        repository.saveAll(existingSchedules);
        return mapper.toResponse(existingSchedules);
    }

    public void deleteSchedule(UUID tenantId, UUID id) {
        StaffSchedule schedule = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + id));

        if (!schedule.getStaff().getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Schedule does not belong to tenant: " + id);
        }

        repository.delete(schedule);
    }
}
