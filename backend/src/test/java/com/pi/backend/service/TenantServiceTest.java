package com.pi.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.repository.TenantRepository;

/**
 * Unit tests for {@link TenantService}. Uses Mockito to mock repositories
 * and verify service logic for tenant CRUD operations and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    /**
     * Verifies that a tenant can be created successfully when the name is unique.
     */
    @Test
    void createTenant_success() {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("City Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);

        when(tenantRepository.findByNameAndDeletedAtIsNull("City Hospital")).thenReturn(Optional.empty());
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        Tenant result = tenantService.createTenant("City Hospital", TenantStatus.ACTIVE);

        assertNotNull(result);
        assertEquals("City Hospital", result.getName());
        assertEquals(TenantStatus.ACTIVE, result.getStatus());
        verify(tenantRepository).save(any(Tenant.class));
    }

    /**
     * Verifies that creating a tenant with a duplicate name throws a {@link DuplicateResourceException}.
     */
    @Test
    void createTenant_duplicateName() {
        Tenant existing = new Tenant();
        existing.setId(1L);
        existing.setName("City Hospital");

        when(tenantRepository.findByNameAndDeletedAtIsNull("City Hospital")).thenReturn(Optional.of(existing));

        assertThrows(DuplicateResourceException.class, () -> {
            tenantService.createTenant("City Hospital", TenantStatus.ACTIVE);
        });
    }

    /**
     * Verifies that a tenant can be retrieved by their ID when they exist.
     */
    @Test
    void getTenantById_success() {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("City Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);

        when(tenantRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(tenant));

        Tenant result = tenantService.getTenantById(1L);

        assertEquals(1L, result.getId());
        assertEquals("City Hospital", result.getName());
    }

    /**
     * Verifies that retrieving a non-existent tenant by ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getTenantById_notFound() {
        when(tenantRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tenantService.getTenantById(999L);
        });
    }
}
