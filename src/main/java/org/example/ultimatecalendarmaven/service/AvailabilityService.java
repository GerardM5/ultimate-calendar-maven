package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.AvailabilityRequestDTO;
import org.example.ultimatecalendarmaven.dto.DayAvailabilityDTO;
import org.example.ultimatecalendarmaven.dto.SlotDTO;
import org.example.ultimatecalendarmaven.model.*;
import org.example.ultimatecalendarmaven.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<SlotDTO> getAvailability(AvailabilityRequestDTO req) {
        Tenant tenant = tenantRepository.findById(req.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + req.getTenantId()));

        ServiceEntity service = serviceRepository.findById(req.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + req.getServiceId()));

        UUID staffId = Objects.requireNonNull(req.getStaffId(), "staffId is required for now");

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + staffId));

        if (!staff.getTenant().getId().equals(tenant.getId()) || !service.getTenant().getId().equals(tenant.getId())) {
            throw new IllegalArgumentException("Staff/Service not in tenant");
        }

        // Zona horaria del tenant
        ZoneId zone = ZoneId.of(tenant.getTimezone());

        // Ventana del día en zona local del tenant [day 00:00, next day 00:00)
        LocalDate day = req.getDay();
        ZonedDateTime dayStartLocal = day.atStartOfDay(zone);
        ZonedDateTime dayEndLocal = day.plusDays(1).atStartOfDay(zone);

        OffsetDateTime fromUtc = dayStartLocal.toOffsetDateTime();
        OffsetDateTime toUtc = dayEndLocal.toOffsetDateTime();

        // 1) Ventanas base por working hours del staff para ese weekday
        int weekday = day.getDayOfWeek().getValue() % 7; // 0=domingo ... 6=sábado en tu esquema (pgsql: 0..6)
        // Corregimos: Java usa 1..7 (MON..SUN). Tu esquema 0..6 con 0=domingo:
        int weekdayPg = (day.getDayOfWeek() == DayOfWeek.SUNDAY) ? 0 : day.getDayOfWeek().getValue();

        List<WorkingHours> wh = workingHoursRepository.findByStaffAndWeekdayOrderByStartTimeAsc(staff, weekdayPg);

        // Convertimos cada wh (LocalTime) a rango en UTC dentro del día
        List<Range> base = wh.stream()
                .map(w -> {
                    ZonedDateTime s = ZonedDateTime.of(day, w.getStartTime(), zone);
                    ZonedDateTime e = ZonedDateTime.of(day, w.getEndTime(), zone);
                    return new Range(s.toOffsetDateTime(), e.toOffsetDateTime());
                })
                .collect(Collectors.toList());

        // 2) Restar ausencias y bloqueos del día
        List<Range> blockers = new ArrayList<>();
        timeOffRepository.findByStaffAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
                staff, toUtc, fromUtc
        ).forEach(t -> blockers.add(new Range(t.getStartsAt(), t.getEndsAt())));

        resourceLockRepository.findByStaffAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
                staff, toUtc, fromUtc
        ).forEach(r -> blockers.add(new Range(r.getStartsAt(), r.getEndsAt())));

        // 3) Restar citas activas (PENDING/CONFIRMED/COMPLETED) del día
        appointmentRepository.findByStaffAndStartsAtLessThanAndEndsAtGreaterThanAndActiveTrue(
                staff, toUtc, fromUtc
        ).forEach(a -> blockers.add(new Range(a.getStartsAt(), a.getEndsAt())));

        // 4) Calculamos free = base - blockers (unión de bloqueos)
        List<Range> mergedBlockers = merge(blockers);
        List<Range> free = subtractAll(base, mergedBlockers);

        // 5) Segmentar por tamaño = duración + buffers
        int minutes = service.getDurationMin() + service.getBufferBefore() + service.getBufferAfter();
        List<Range> slots = splitBySize(free, Duration.ofMinutes(minutes));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        // 6) Mapear a DTOs (UTC + strings en local)
        return slots.stream()
                .map(r -> {
                    ZonedDateTime sLocal = r.start.atZoneSameInstant(zone);
                    ZonedDateTime eLocal = r.end.atZoneSameInstant(zone);
                    return SlotDTO.builder()
                            .start(r.start)
                            .end(r.end)
                            .startLocal(sLocal.format(fmt))
                            .endLocal(eLocal.format(fmt))
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DayAvailabilityDTO> getAvailabilityByDay(
            UUID tenantId, UUID serviceId, UUID staffId, LocalDate from, LocalDate to
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

        ZoneId zone = ZoneId.of(tenant.getTimezone());

        List<DayAvailabilityDTO> result = new ArrayList<>();

        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            // construimos el request del día
            var req = org.example.ultimatecalendarmaven.dto.AvailabilityRequestDTO.builder()
                    .tenantId(tenantId)
                    .serviceId(serviceId)
                    .staffId(staffId)
                    .day(d)
                    .build();

            // Si hay al menos un slot -> available=true
            boolean available = !getAvailability(req).isEmpty();

            // Inicio del día en la zona del tenant -> UTC ISO
            OffsetDateTime dayStartUtc = ZonedDateTime.of(d, LocalTime.MIDNIGHT, zone)
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .toOffsetDateTime();

            result.add(DayAvailabilityDTO.builder()
                    .date(dayStartUtc)
                    .available(available)
                    .build());
        }
        return result;
    }

    // ----- Helpers de rangos -----

    private static class Range {
        final OffsetDateTime start; // inclusivo
        final OffsetDateTime end;   // exclusivo idealmente
        Range(OffsetDateTime start, OffsetDateTime end) {
            if (!start.isBefore(end)) throw new IllegalArgumentException("Invalid range");
            this.start = start;
            this.end = end;
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
            out.add(new Range(r.start, min(r.end, b.start)));
        }
        // derecha
        if (r.end.isAfter(b.end)) {
            out.add(new Range(max(r.start, b.end), r.end));
        }
        return out;
    }

    /** Divide rangos libres en “slots” de tamaño fijo */
    private static List<Range> splitBySize(List<Range> free, Duration size) {
        List<Range> out = new ArrayList<>();
        for (Range r : free) {
            OffsetDateTime cur = r.start;
            while (!cur.plus(size).isAfter(r.end)) {
                out.add(new Range(cur, cur.plus(size)));
                cur = cur.plus(size);
            }
        }
        return out;
    }
}