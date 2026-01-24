package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceIdsDTO {
    List<UUID> serviceIds;
}
