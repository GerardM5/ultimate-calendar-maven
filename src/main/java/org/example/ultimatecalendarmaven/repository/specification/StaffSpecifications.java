package org.example.ultimatecalendarmaven.repository.specification;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.ultimatecalendarmaven.dto.StaffFilter;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffService;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class StaffSpecifications {

    public static Specification<Staff> withFilter(StaffFilter filter) {
        return Specification.allOf(
                nameContains(filter.name()),
                activeEquals(filter.active()),
                hasService(filter.serviceId())
        );
    }

    private static Specification<Staff> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Staff> activeEquals(Boolean active) {
        if (active == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) ->
                cb.equal(root.get("active"), active);
    }

    private static Specification<Staff> hasService(UUID serviceId) {
        if (serviceId == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> {
            Subquery<Integer> sq = query.subquery(Integer.class);
            Root<StaffService> ss = sq.from(StaffService.class);

            sq.select(cb.literal(1))
                    .where(
                            cb.equal(ss.get("staff").get("id"), root.get("id")),
                            cb.equal(ss.get("service").get("id"), serviceId)
                    );

            return cb.exists(sq);
        };
    }
}