package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.AvailabilityRequestDTO;
import org.example.ultimatecalendarmaven.dto.DayAvailabilityDTO;
import org.example.ultimatecalendarmaven.dto.SlotDTO;
import org.example.ultimatecalendarmaven.dto.StaffResponseDTO;
import org.example.ultimatecalendarmaven.model.*;
import org.example.ultimatecalendarmaven.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.LinkedHashSet;

import org.example.ultimatecalendarmaven.repository.StaffServiceRepository;
import org.example.ultimatecalendarmaven.model.StaffService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvailabilityService {

    private final TenantRepository tenantRepository;
    private final ServiceRepository serviceRepository;
    private final StaffRepository staffRepository;
    private final WorkingHoursRepository workingHoursRepository;
    private final TimeOffRepository timeOffRepository;
    private final ResourceLockRepository resourceLockRepository;
    private final AppointmentRepository appointmentRepository;
    private final StaffServiceRepository staffServiceRepository;

    public List<SlotDTO> getAvailability(AvailabilityRequestDTO req) {
        Tenant tenant = tenantRepository.findById(req.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + req.getTenantId()));

        ServiceEntity service = serviceRepository.findById(req.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + req.getServiceId()));

        // Validación de pertenencia del service al tenant
        if (!service.getTenant().getId().equals(tenant.getId())) {
            throw new IllegalArgumentException("Service not in tenant");
        }

        LocalDate day = req.getDay();
        ZoneId zone = ZoneId.of(tenant.getTimezone());

        List<Range> slotsAll = new ArrayList<>();

        if (req.getStaffId() != null) {
            // Modo: un staff concreto
            UUID staffId = req.getStaffId();
            Staff staff = staffRepository.findById(staffId)
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + staffId));
            if (!staff.getTenant().getId().equals(tenant.getId())) {
                throw new IllegalArgumentException("Staff not in tenant");
            }
            slotsAll.addAll(computeSlotsForStaff(tenant, service, staff, day));
        } else {
            // Modo: todos los staff que pueden hacer este servicio en este tenant
            List<Staff> candidates = staffServiceRepository.findByService(service).stream()
                    .map(StaffService::getStaff)
                    .filter(Objects::nonNull)
                    .filter(Staff::isActive)
                    .filter(s -> s.getTenant() != null && tenant.getId().equals(s.getTenant().getId()))
                    .toList();

            Map<UUID, String> staffNames = candidates.stream()
                    .collect(Collectors.toMap(Staff::getId, Staff::getName));

            for (Staff s : candidates) {
                slotsAll.addAll(computeSlotsForStaff(tenant, service, s, day));
            }

            // Agrupar por (start,end) y acumular el set de staff disponibles en ese slot
            Map<String, Range> slotTimes = new HashMap<>();
            Map<String, Set<UUID>> staffBySlot = new HashMap<>();
            for (Range r : slotsAll) {
                String key = r.start + ":" + r.end;
                slotTimes.putIfAbsent(key, new Range(r.start, r.end));
                staffBySlot.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(r.staffId);
            }

            // Ordenar por start luego end
            List<String> orderedKeys = slotTimes.entrySet().stream()
                    .sorted(Comparator.comparing((Map.Entry<String, Range> e) -> e.getValue().start)
                            .thenComparing(e -> e.getValue().end))
                    .map(Map.Entry::getKey)
                    .toList();

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            return orderedKeys.stream()
                    .map(key -> {
                        Range r = slotTimes.get(key);
                        ZonedDateTime sLocal = r.start.atZoneSameInstant(zone);
                        ZonedDateTime eLocal = r.end.atZoneSameInstant(zone);

                        Set<StaffResponseDTO> staffSet = staffBySlot.getOrDefault(key, Set.of()).stream()
                                .map(id -> StaffResponseDTO.builder()
                                        .id(id)
                                        .name(staffNames.get(id))
                                        .build())
                                .collect(Collectors.toCollection(LinkedHashSet::new));

                        return SlotDTO.builder()
                                .start(r.start)
                                .end(r.end)
                                .startLocal(sLocal.format(fmt))
                                .endLocal(eLocal.format(fmt))
                                .staff(staffSet)
                                .build();
                    })
                    .toList();
        }

        // Agrupar por (start,end) y acumular el set de staff disponibles en ese slot
        Map<String, Range> slotTimes = new HashMap<>();
        Map<String, Set<UUID>> staffBySlot = new HashMap<>();
        for (Range r : slotsAll) {
            String key = r.start + ":" + r.end;
            slotTimes.putIfAbsent(key, new Range(r.start, r.end));
            staffBySlot.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(r.staffId);
        }

        // Ordenar por start luego end
        List<String> orderedKeys = slotTimes.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, Range> e) -> e.getValue().start)
                        .thenComparing(e -> e.getValue().end))
                .map(Map.Entry::getKey)
                .toList();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return orderedKeys.stream()
                .map(key -> {
                    Range r = slotTimes.get(key);
                    ZonedDateTime sLocal = r.start.atZoneSameInstant(zone);
                    ZonedDateTime eLocal = r.end.atZoneSameInstant(zone);

                    Set<StaffResponseDTO> staffSet = staffBySlot.getOrDefault(key, Set.of()).stream()
                            .map(id -> StaffResponseDTO.builder().id(id).build())
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    return SlotDTO.builder()
                            .start(r.start)
                            .end(r.end)
                            .startLocal(sLocal.format(fmt))
                            .endLocal(eLocal.format(fmt))
                            .staff(staffSet)
                            .build();
                })
                .toList();
    }

    private List<Range> computeSlotsForStaff(Tenant tenant, ServiceEntity service, Staff staff, LocalDate day) {
        ZoneId zone = ZoneId.of(tenant.getTimezone());

        ZonedDateTime dayStartLocal = day.atStartOfDay(zone);
        ZonedDateTime dayEndLocal = day.plusDays(1).atStartOfDay(zone);
        OffsetDateTime fromUtc = dayStartLocal.toOffsetDateTime();
        OffsetDateTime toUtc = dayEndLocal.toOffsetDateTime();

        int weekdayCode = day.getDayOfWeek().getValue(); // 1=Monday ... 7=Sunday
        List<WorkingHours> wh = workingHoursRepository.findByStaffAndWeekdayOrderByStartTimeAsc(staff, weekdayCode);

        List<Range> base = wh.stream()
                .map(w -> {
                    ZonedDateTime s = ZonedDateTime.of(day, w.getStartTime(), zone);
                    ZonedDateTime e = ZonedDateTime.of(day, w.getEndTime(), zone);
                    return new Range(s.toOffsetDateTime(), e.toOffsetDateTime(), staff.getId());
                })
                .collect(Collectors.toList());

        List<Range> blockers = new ArrayList<>();
        timeOffRepository.findByStaffAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(staff, toUtc, fromUtc)
                .forEach(t -> blockers.add(new Range(t.getStartsAt(), t.getEndsAt())));
        resourceLockRepository.findByStaffAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(staff, toUtc, fromUtc)
                .forEach(r -> blockers.add(new Range(r.getStartsAt(), r.getEndsAt())));
        appointmentRepository.findByStaffAndStartsAtLessThanAndEndsAtGreaterThanAndActiveTrue(staff, toUtc, fromUtc)
                .forEach(a -> blockers.add(new Range(a.getStartsAt(), a.getEndsAt())));

        List<Range> free = subtractAll(base, merge(blockers));
        int minutes = service.getDurationMin() + service.getBufferBefore() + service.getBufferAfter();
        return splitBySize(free, Duration.ofMinutes(minutes));
    }

    @Transactional(readOnly = true)
    public List<DayAvailabilityDTO> getAvailabilityByDay(
            UUID tenantId, UUID serviceId, UUID staffId, OffsetDateTime from, OffsetDateTime to
    ) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from/to are required");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("'to' must be on or after 'from'");
        }

        // Reutilizamos validaciones básicas cargando tenant/service/staff solo una vez
        var tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceId));
        if (!service.getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Service not in tenant");
        }
        Staff staff = null;
        if (staffId != null) {
            staff = staffRepository.findById(staffId)
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + staffId));
            if (!staff.getTenant().getId().equals(tenantId)) {
                throw new IllegalArgumentException("Staff not in tenant");
            }
        }

        List<DayAvailabilityDTO> result = new ArrayList<>();

        for (OffsetDateTime d = from; !d.isAfter(to); d = d.plusDays(1)) {
            // construimos el request del día
            var req = AvailabilityRequestDTO.builder()
                    .tenantId(tenantId)
                    .serviceId(serviceId)
                    .staffId(staffId)
                    .day(d.toLocalDate())
                    .build();

            // Si hay al menos un slot -> available=true
            boolean available = !getAvailability(req).isEmpty();

            result.add(DayAvailabilityDTO.builder()
                    .date(d)
                    .available(available)
                    .build());
        }
        return result;
    }

    // ----- Helpers de rangos -----

    private static class Range {
        final OffsetDateTime start; // inclusivo
        final OffsetDateTime end;   // exclusivo idealmente
        final UUID staffId;         // null cuando no aplica
        Range(OffsetDateTime start, OffsetDateTime end) {
            if (!start.isBefore(end)) throw new IllegalArgumentException("Invalid range");
            this.start = start;
            this.end = end;
            this.staffId = null;
        }
        Range(OffsetDateTime start, OffsetDateTime end, UUID staffId) {
            if (!start.isBefore(end)) throw new IllegalArgumentException("Invalid range");
            this.start = start;
            this.end = end;
            this.staffId = staffId;
        }
        boolean overlaps(Range other) {
            return start.isBefore(other.end) && other.start.isBefore(end);
        }
        Range merge(Range other) {
            return new Range(min(start, other.start), max(end, other.end));
        }
    }

    private static OffsetDateTime min(OffsetDateTime a, OffsetDateTime b) {
        return a.isBefore(b) ? a : b;
    }
    private static OffsetDateTime max(OffsetDateTime a, OffsetDateTime b) {
        return a.isAfter(b) ? a : b;
    }

    /** Une rangos solapados */
    private static List<Range> merge(List<Range> ranges) {
        if (ranges.isEmpty()) return List.of();
        List<Range> sorted = ranges.stream()
                .sorted(Comparator.comparing(r -> r.start))
                .toList();
        List<Range> res = new ArrayList<>();
        Range cur = sorted.get(0);
        for (int i = 1; i < sorted.size(); i++) {
            Range nxt = sorted.get(i);
            if (cur.overlaps(nxt) || !cur.end.isBefore(nxt.start)) {
                cur = cur.merge(nxt);
            } else {
                res.add(cur);
                cur = nxt;
            }
        }
        res.add(cur);
        return res;
    }

    /** base - blockers (blockers ya unidos) */
    private static List<Range> subtractAll(List<Range> base, List<Range> blockers) {
        List<Range> res = new ArrayList<>(base);
        for (Range b : blockers) {
            res = res.stream().flatMap(r -> subtract(r, b).stream()).toList();
        }
        return res;
    }

    /** r - b */
    private static List<Range> subtract(Range r, Range b) {
        if (!r.overlaps(b)) return List.of(r);
        List<Range> out = new ArrayList<>();
        // izquierda
        if (r.start.isBefore(b.start)) {
            out.add(new Range(r.start, min(r.end, b.start), r.staffId));
        }
        // derecha
        if (r.end.isAfter(b.end)) {
            out.add(new Range(max(r.start, b.end), r.end, r.staffId));
        }
        return out;
    }

    /** Divide rangos libres en “slots” de tamaño fijo */
    private static List<Range> splitBySize(List<Range> free, Duration size) {
        List<Range> out = new ArrayList<>();
        for (Range r : free) {
            OffsetDateTime cur = r.start;
            while (!cur.plus(size).isAfter(r.end)) {
                out.add(new Range(cur, cur.plus(size), r.staffId));
                cur = cur.plus(size);
            }
        }
        return out;
    }
}