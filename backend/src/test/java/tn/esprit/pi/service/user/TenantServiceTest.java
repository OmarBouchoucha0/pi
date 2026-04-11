package tn.esprit.pi.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.pi.dto.user.TenantUpdateRequest;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.enums.user.TenantStatus;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.TenantRepository;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    private Tenant tenant;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder().id(1L).name("Test Tenant").status(TenantStatus.ACTIVE).build();
    }

    @Test
    void findAll_shouldReturnNonDeletedTenants() {
        when(tenantRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(tenant));

        List<Tenant> result = tenantService.findAll();

        assertThat(result).hasSize(1);
        verify(tenantRepository).findAllByDeletedAtIsNull();
    }

    @Test
    void findById_shouldReturnTenantWhenExists() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));

        Optional<Tenant> result = tenantService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Tenant");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(tenantRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Tenant> result = tenantService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByName_shouldReturnTenantWhenExists() {
        when(tenantRepository.findByNameAndDeletedAtIsNull("Test Tenant")).thenReturn(Optional.of(tenant));

        Optional<Tenant> result = tenantService.findByName("Test Tenant");

        assertThat(result).isPresent();
    }

    @Test
    void findByName_shouldReturnEmptyWhenNotFound() {
        when(tenantRepository.findByNameAndDeletedAtIsNull("Non Existent")).thenReturn(Optional.empty());

        Optional<Tenant> result = tenantService.findByName("Non Existent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByStatus_shouldReturnTenants() {
        when(tenantRepository.findByStatusAndDeletedAtIsNull(TenantStatus.ACTIVE)).thenReturn(List.of(tenant));

        List<Tenant> result = tenantService.findByStatus(TenantStatus.ACTIVE);

        assertThat(result).hasSize(1);
    }

    @Test
    void existsByName_shouldReturnTrueWhenExists() {
        when(tenantRepository.existsByNameAndDeletedAtIsNull("Test Tenant")).thenReturn(true);

        boolean result = tenantService.existsByName("Test Tenant");

        assertThat(result).isTrue();
    }

    @Test
    void existsByName_shouldReturnFalseWhenNotExists() {
        when(tenantRepository.existsByNameAndDeletedAtIsNull("Non Existent")).thenReturn(false);

        boolean result = tenantService.existsByName("Non Existent");

        assertThat(result).isFalse();
    }

    @Test
    void save_create_shouldPersistTenant() {
        Tenant newTenant = Tenant.builder().name("New Tenant").status(TenantStatus.ACTIVE).build();
        when(tenantRepository.findByNameAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        Tenant result = tenantService.save(newTenant);

        assertThat(result).isNotNull();
        verify(tenantRepository).save(newTenant);
    }

    @Test
    void save_create_shouldThrowWhenDuplicateName() {
        Tenant newTenant = Tenant.builder().name("Test Tenant").status(TenantStatus.ACTIVE).build();
        when(tenantRepository.findByNameAndDeletedAtIsNull("Test Tenant")).thenReturn(Optional.of(tenant));

        assertThatThrownBy(() -> tenantService.save(newTenant))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void save_update_shouldSucceedWhenNoDuplicate() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(tenantRepository.findByNameAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        Tenant result = tenantService.save(tenant);

        assertThat(result).isNotNull();
    }

    @Test
    void save_update_shouldThrowWhenDuplicateName() {
        Tenant updateTenant = Tenant.builder().id(1L).name("Test Tenant").status(TenantStatus.ACTIVE).build();
        Tenant existingTenant = Tenant.builder().id(2L).name("Test Tenant").status(TenantStatus.ACTIVE).build();

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(updateTenant));
        when(tenantRepository.findByNameAndDeletedAtIsNull("Test Tenant")).thenReturn(Optional.of(existingTenant));

        assertThatThrownBy(() -> tenantService.save(updateTenant))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void softDelete_shouldSetDeletedAt() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        tenantService.softDelete(1L);

        assertThat(tenant.getDeletedAt()).isNotNull();
    }

    @Test
    void softDelete_shouldThrowWhenNotFound() {
        when(tenantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tenantService.softDelete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateTenant() {
        TenantUpdateRequest request = new TenantUpdateRequest();
        request.setName("Updated Tenant");
        request.setStatus(TenantStatus.INACTIVE);

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        Tenant result = tenantService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Updated Tenant");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        TenantUpdateRequest request = new TenantUpdateRequest();
        request.setName("Test");

        when(tenantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tenantService.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
