package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.AppointmentRequestDTO;
import org.example.ultimatecalendarmaven.mapper.AppointmentMapper;
import org.example.ultimatecalendarmaven.model.*;
import org.example.ultimatecalendarmaven.notification.service.EmailOutboxService;
import org.example.ultimatecalendarmaven.notification.service.EmailSenderService;
import org.example.ultimatecalendarmaven.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

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
    private final CustomerService customerService;
    private final EmailSenderService emailSenderService;
    private final EmailOutboxService emailOutboxService;

    @Transactional(readOnly = true)
    public List<Appointment> findByTenantAndRange(UUID tenantId, OffsetDateTime from, OffsetDateTime to) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        List<Appointment> appointments = appointmentRepository.findByTenantAndStartsAtBetweenOrderByStartsAtAsc(tenant,from, to);

        return appointments;
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> findByIdScoped(UUID tenantId, UUID id) {
        return appointmentRepository.findById(id)
                .filter(a -> a.getTenant().getId().equals(tenantId));
    }

    public Appointment create(AppointmentRequestDTO dto) throws Exception {
        // 1) Cargar entidades y validar pertenencia al tenant
        UUID tenantId = Objects.requireNonNull(dto.getTenantId(), "tenantId is required");
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + dto.getServiceId()));
        if (!service.getTenant().getId().equals(tenantId)) throw new IllegalArgumentException("Service not in tenant");

        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + dto.getStaffId()));
        if (!staff.getTenant().getId().equals(tenantId)) throw new IllegalArgumentException("Staff not in tenant");


        Customer customer = customerRepository.findByTenantAndPhone(tenant, dto.getCustomer().getPhone())
                .orElseGet(() -> {
                    // crear customer si no existe
                    Customer newCustomer = new Customer();
                    newCustomer.setTenant(tenant);
                    newCustomer.setName(dto.getCustomer().getName());
                    newCustomer.setEmail(dto.getCustomer().getEmail());
                    newCustomer.setPhone(dto.getCustomer().getPhone());
                    return customerRepository.save(newCustomer);
                });
        if (!customer.getTenant().getId().equals(tenantId)) throw new IllegalArgumentException("Customer not in tenant");

        // 2) Mapear DTO -> entidad y setear relaciones
        Appointment entity = appointmentMapper.toEntity(dto);
        entity.setTenant(tenant);
        entity.setService(service);
        entity.setStaff(staff);
        entity.setCustomer(customer);
        entity.setStartsAt(dto.getStartsAt());
        entity.setEndsAt(dto.getStartsAt().plusMinutes(service.getDurationMin()));
        entity.setPriceCents(service.getPriceCents());
        entity.setActive(true);
        entity.setStatus(AppointmentStatus.valueOf("PENDING"));

        // (Opcional) 3) Validación previa de solape en memoria (rápida/optimista)
        // Reutilizamos method del repo: any cita activa que solape el rango?
        boolean hasOverlap = appointmentRepository
                .existsByStaffAndStartsAtLessThanAndEndsAtGreaterThanAndActiveTrue(
                        staff, entity.getEndsAt(), entity.getStartsAt());

        if (hasOverlap) throw new ConflictException("Slot not available");

        // 4) Persistir; si hay carrera, el EXCLUDE en DB lanzará una excepción de integridad -> 409
        try {
            Appointment saved = appointmentRepository.save(entity);
            //emailSenderService.sendAppointmentConfirmationEmail(saved);
            emailOutboxService.sendAppointmentConfirmationEmail(saved);
            return saved;
        } catch (DataIntegrityViolationException ex) {
            // probablemente por constraint de solape (EXCLUDE)
            throw new ConflictException("Slot not available", ex);
        }
    }

    public Appointment updateStatus(UUID tenantId, UUID id, AppointmentStatus status) {
        Appointment appt = appointmentRepository.findById(id)
                .filter(a -> a.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + id));
        appt.setStatus(status);
        return appointmentRepository.save(appt);
    }

    public boolean deleteScoped(UUID tenantId, UUID id) {
        return appointmentRepository.findById(id)
                .filter(a -> a.getTenant().getId().equals(tenantId))
                .map(a -> { appointmentRepository.delete(a); return true; })
                .orElse(false);
    }

    // ---- Error de conflicto semántico (409)
    public static class ConflictException extends RuntimeException {
        public ConflictException(String msg) { super(msg); }
        public ConflictException(String msg, Throwable t) { super(msg, t); }
    }
}