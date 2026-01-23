package org.example.ultimatecalendarmaven.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.CustomerRequestDTO;
import org.example.ultimatecalendarmaven.dto.CustomerResponseDTO;
import org.example.ultimatecalendarmaven.mapper.CustomerMapper;
import org.example.ultimatecalendarmaven.model.Customer;
import org.example.ultimatecalendarmaven.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Customer", description = "APIs for managing customers")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/{tenantId}/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @GetMapping
    public List<CustomerResponseDTO> list(@PathVariable UUID tenantId) {
        List<Customer> data = customerService.findByTenant(tenantId);
        return data.stream().map(customerMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> get(@PathVariable UUID tenantId,
                                                   @PathVariable UUID id) {
        Customer c = customerService.getScoped(tenantId, id);
        return ResponseEntity.ok(customerMapper.toResponse(c));
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@PathVariable UUID tenantId,
                                                      @Validated @RequestBody CustomerRequestDTO dto) {
        Customer saved = customerService.create(tenantId, dto);
        return ResponseEntity.created(URI.create("/api/v1/tenants/%s/customers/%s"
                        .formatted(tenantId, saved.getId())))
                .body(customerMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(@PathVariable UUID tenantId,
                                                      @PathVariable UUID id,
                                                      @Validated @RequestBody CustomerRequestDTO dto) {
        Customer updated = customerService.update(tenantId, id, dto);
        return ResponseEntity.ok(customerMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID tenantId, @PathVariable UUID id) {
        return customerService.delete(tenantId, id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}