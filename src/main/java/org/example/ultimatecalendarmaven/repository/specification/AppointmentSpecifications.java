package org.example.ultimatecalendarmaven.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.ultimatecalendarmaven.dto.AppointmentFilter;
import org.example.ultimatecalendarmaven.model.Appointment;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

public final class AppointmentSpecifications {

    private AppointmentSpecifications() {}

    public static Specification<Appointment> withFilter(UUID tenantId, AppointmentFilter filter) {
        return Specification.allOf(
                belongsToTenant(tenantId),
                startsAtFrom(filter.from()),
                startsAtTo(filter.to()),
                hasStaff(filter.staffId())
        );
    }

    private static Specification<Appointment> belongsToTenant(UUID tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<Appointment> startsAtFrom(OffsetDateTime from) {
        if (from == null) return Specification.unrestricted();
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startsAt"), from);
    }

    private static Specification<Appointment> startsAtTo(OffsetDateTime to) {
        if (to == null) return Specification.unrestricted();
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("startsAt"), to);
    }

    private static Specification<Appointment> hasStaff(UUID staffId) {
        if (staffId == null) return Specification.unrestricted();
        return (root, query, cb) -> cb.equal(root.get("staff").get("id"), staffId);
    }
}