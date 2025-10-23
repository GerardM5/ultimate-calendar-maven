package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "resource_lock")
public class ResourceLock {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "rlock_tenant_fk"))
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", foreignKey = @ForeignKey(name = "rlock_staff_fk"))
    private Staff staff; // optional

    @Column(nullable = false)
    private String label;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}
