package org.example.ultimatecalendarmaven.repository.specification;

import jakarta.persistence.criteria.JoinType;
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
        return (root, query, cb) -> {

            // Solo hacemos fetch en la query "real"
            // (en count queries de paginación, fetch rompe cosas)
            if (!Long.class.equals(query.getResultType())) {
                root.fetch("staff", JoinType.LEFT);
                root.fetch("service", JoinType.LEFT);
                root.fetch("customer", JoinType.LEFT);
                query.distinct(true);
            }

            var predicates = new ArrayList<Predicate>();

            predicates.add(cb.equal(root.get("tenant").get("id"), tenantId));

            if (filter.from() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startsAt"), filter.from()));
            }
            if (filter.to() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startsAt"), filter.to()));
            }
            if (filter.staffId() != null) {
                predicates.add(cb.equal(root.get("staff").get("id"), filter.staffId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}