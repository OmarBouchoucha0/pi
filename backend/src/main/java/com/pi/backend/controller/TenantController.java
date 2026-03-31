package com.pi.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.backend.dto.CreateTenantRequest;
import com.pi.backend.model.Tenant;
import com.pi.backend.service.TenantService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing tenants.
 */
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant", description = "Tenant management APIs")
public class TenantController {

    private final TenantService tenantService;

    /**
     * Creates a new tenant.
     *
     * @param request the tenant creation request
     * @return the created tenant
     */
    @PostMapping
    public ResponseEntity<Tenant> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        Tenant tenant = tenantService.createTenant(request.name(), request.status());
        return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
    }
}
