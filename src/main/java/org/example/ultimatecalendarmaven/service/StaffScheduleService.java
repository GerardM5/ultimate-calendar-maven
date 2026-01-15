package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestDTO;
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
    @Autowired StaffService staffService;


    public void assignSchedule(UUID tenantId, UUID staffId, List<StaffScheduleRequestDTO> staffScheduleRequestDTOList) {

        Staff staff = staffService.findById(staffId).orElseThrow();

        repository.saveAll(
                staffScheduleRequestDTOList
                        .stream()
                        .map(mapper::toEntity)
                        .peek(s -> s.setStaff(staff))
                        .toList()
        );

    }

    public List<StaffScheduleResponseDTO> listScheduleForStaff(UUID tenantId, UUID staffId) {
        Staff staff = Staff.builder()
                .id(staffId)
                .build();
        return repository.findByStaff(staff).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<StaffSchedule> getScheduleByStaffAndRangeDates(UUID staffId, OffsetDateTime startDate, OffsetDateTime endDate){
        return repository.findOverlapping(staffId, startDate, endDate);
    }
}
