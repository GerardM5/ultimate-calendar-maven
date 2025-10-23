package org.example.ultimatecalendarmaven.model;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
@Embeddable
public class StaffServiceId implements Serializable {
    private UUID staffId;
    private UUID serviceId;
}
