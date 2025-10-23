package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.AppointmentRequestDTO;
import org.example.ultimatecalendarmaven.mapper.AppointmentMapper;
import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TenantRepository tenantRepository;
    private final ServiceRepository serviceRepository;
    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentMapper appointmentMapper;

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findById(UUID id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Creates an Appointment from a DTO, resolving all foreign keys by UUID.
     */
    public Appointment create(AppointmentRequestDTO dto) {
        var entity = appointmentMapper.toEntity(dto);
        entity.setTenant(tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + dto.getTenantId())));
        entity.setService(serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + dto.getServiceId())));
        entity.setStaff(staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + dto.getStaffId())));
        entity.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + dto.getCustomerId())));
        return appointmentRepository.save(entity);
    }

    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public boolean deleteById(UUID id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
