package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "staff", uniqueConstraints = {
        @UniqueConstraint(name = "uk_staff_tenant_name", columnNames = {"tenant_id", "name"})
})
public class Staff {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "staff_tenant_fk"))
    private Tenant tenant;

    @Column(nullable = false)
    private String name;

    private String email;
    private String phone;

    private String color;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}
