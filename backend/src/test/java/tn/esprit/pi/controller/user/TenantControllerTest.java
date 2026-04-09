package tn.esprit.pi.controller.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.enums.user.TenantStatus;
import tn.esprit.pi.service.user.TenantService;

@ExtendWith(MockitoExtension.class)
class TenantControllerTest {

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private TenantController tenantController;

    private Tenant tenant;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder()
                .id(1L)
                .name("Test Tenant")
                .status(TenantStatus.ACTIVE)
                .build();
    }

    @Test
    void findAll_shouldReturnTenants() {
        when(tenantService.findAll()).thenReturn(List.of(tenant));

        ResponseEntity<List<Tenant>> result = tenantController.findAll();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void findById_shouldReturnTenant() {
        when(tenantService.findById(1L)).thenReturn(Optional.of(tenant));

        ResponseEntity<Tenant> result = tenantController.findById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void findById_shouldReturnNotFound() {
        when(tenantService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Tenant> result = tenantController.findById(99L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findByName_shouldReturnTenant() {
        when(tenantService.findByName("Test Tenant")).thenReturn(Optional.of(tenant));

        ResponseEntity<Tenant> result = tenantController.findByName("Test Tenant");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void findByStatus_shouldReturnTenants() {
        when(tenantService.findByStatus(TenantStatus.ACTIVE)).thenReturn(List.of(tenant));

        ResponseEntity<List<Tenant>> result = tenantController.findByStatus(TenantStatus.ACTIVE);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void existsByName_shouldReturnTrue() {
        when(tenantService.existsByName("Test Tenant")).thenReturn(true);

        ResponseEntity<Boolean> result = tenantController.existsByName("Test Tenant");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isTrue();
    }

    @Test
    void save_shouldCreateTenant() {
        when(tenantService.save(any(Tenant.class))).thenReturn(tenant);

        ResponseEntity<Tenant> result = tenantController.save(tenant);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void softDelete_shouldReturnNoContent() {
        doNothing().when(tenantService).softDelete(1L);

        ResponseEntity<Void> result = tenantController.softDelete(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(tenantService).softDelete(1L);
    }

    @Test
    void update_shouldReturnUpdatedTenant() {
        tn.esprit.pi.dto.user.TenantUpdateRequest request = new tn.esprit.pi.dto.user.TenantUpdateRequest();
        request.setName("Updated Tenant");

        when(tenantService.update(1L, request)).thenReturn(tenant);

        ResponseEntity<Tenant> result = tenantController.update(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
