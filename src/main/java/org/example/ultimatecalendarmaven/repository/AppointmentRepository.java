package org.example.ultimatecalendarmaven.repository;
import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.model.Customer;
import org.example.ultimatecalendarmaven.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.UUID;
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByStaffAndStartsAtLessThanAndEndsAtGreaterThanAndActiveTrue(Staff staff, OffsetDateTime ends, OffsetDateTime starts);
    List<Appointment> findByCustomerOrderByStartsAtDesc(Customer customer);
}
