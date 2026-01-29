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
import java.util.List;
import java.util.UUID;

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


    public List<StaffScheduleResponseDTO> assignSchedule(UUID tenantId, UUID staffId, List<StaffScheduleRequestDTO> staffScheduleRequestDTOList) {

        Staff staff = staffService.findById(staffId).orElseThrow();

        return repository.saveAll(
                staffScheduleRequestDTOList
                        .stream()
                        .map(mapper::toEntity)
                        .peek(s -> s.setStaff(staff))
                        .toList()
        ).stream().map(mapper::toResponse).toList();

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




}
