package org.example.ultimatecalendarmaven.repository;
import org.example.ultimatecalendarmaven.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface StaffServiceRepository extends JpaRepository<StaffService, StaffServiceId> {
    List<StaffService> findByStaff(Staff staff);
    List<StaffService> findByService(ServiceEntity service);
}
