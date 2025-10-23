package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "customer",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_customer_tenant_email", columnNames = {"tenant_id", "email"}),
           @UniqueConstraint(name = "uk_customer_tenant_phone", columnNames = {"tenant_id", "phone"})
       })
public class Customer {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "customer_tenant_fk"))
    private Tenant tenant;

    @Column(nullable = false)
    private String name;

    private String email;
    private String phone;

    private String notes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
