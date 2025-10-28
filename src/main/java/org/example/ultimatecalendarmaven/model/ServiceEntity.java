package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "service", uniqueConstraints = {
        @UniqueConstraint(name = "uk_service_tenant_name", columnNames = {"tenant_id", "name"})
})
public class ServiceEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "service_tenant_fk"))
    private Tenant tenant;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(name = "duration_min", nullable = false)
    private int durationMin;

    @Builder.Default
    @Column(name = "price_cents", nullable = false)
    private int priceCents = 0;

    @Builder.Default
    @Column(name = "buffer_before", nullable = false)
    private int bufferBefore = 0;

    @Builder.Default
    @Column(name = "buffer_after", nullable = false)
    private int bufferAfter = 0;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}
