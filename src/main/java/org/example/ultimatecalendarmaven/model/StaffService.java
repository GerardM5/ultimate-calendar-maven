package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "staff_service")
public class StaffService {
    @EmbeddedId
    private StaffServiceId id;

    public StaffService(Staff staff, ServiceEntity service) {
        this.staff = staff;
        this.service = service;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("staffId")
    @JoinColumn(name = "staff_id", nullable = false, foreignKey = @ForeignKey(name = "staff_service_staff_fk"))
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id", nullable = false, foreignKey = @ForeignKey(name = "staff_service_service_fk"))
    private ServiceEntity service;

    @PrePersist
    public void prePersist() {
        if (id == null && staff != null && service != null) {
            id = new StaffServiceId(staff.getId(), service.getId());
        }
    }
}
