package tn.esprit.pi.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import tn.esprit.pi.dto.user.HospitalUpdateRequest;
import tn.esprit.pi.entity.user.Hospital;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.enums.user.TenantStatus;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.HospitalRepository;
import tn.esprit.pi.repository.user.TenantRepository;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private HospitalService hospitalService;

    private Tenant tenant;
    private Hospital hospital;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder().id(1L).name("Test Tenant").status(TenantStatus.ACTIVE).build();
        hospital = Hospital.builder().id(1L).name("Test Hospital").tenant(tenant).status(TenantStatus.ACTIVE).build();
    }

    @Test
    void findAll_shouldReturnNonDeletedHospitals() {
        when(hospitalRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(hospital));

        List<Hospital> result = hospitalService.findAll();

        assertThat(result).hasSize(1);
        verify(hospitalRepository).findAllByDeletedAtIsNull();
    }

    @Test
    void findById_shouldReturnHospitalWhenExists() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));

        Optional<Hospital> result = hospitalService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Hospital");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(hospitalRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Hospital> result = hospitalService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByTenantId_shouldReturnHospitals() {
        when(hospitalRepository.findByTenantIdAndDeletedAtIsNull(1L)).thenReturn(List.of(hospital));

        List<Hospital> result = hospitalService.findByTenantId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByNameAndTenantId_shouldReturnHospitalWhenExists() {
        when(hospitalRepository.findByNameAndTenantIdAndDeletedAtIsNull("Test Hospital", 1L)).thenReturn(Optional.of(hospital));

        Optional<Hospital> result = hospitalService.findByNameAndTenantId("Test Hospital", 1L);

        assertThat(result).isPresent();
    }

    @Test
    void findByNameAndTenantId_shouldReturnEmptyWhenNotFound() {
        when(hospitalRepository.findByNameAndTenantIdAndDeletedAtIsNull("Non Existent", 1L)).thenReturn(Optional.empty());

        Optional<Hospital> result = hospitalService.findByNameAndTenantId("Non Existent", 1L);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByNameAndTenantId_shouldReturnTrueWhenExists() {
        when(hospitalRepository.existsByNameAndTenantIdAndDeletedAtIsNull("Test Hospital", 1L)).thenReturn(true);

        boolean result = hospitalService.existsByNameAndTenantId("Test Hospital", 1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsByNameAndTenantId_shouldReturnFalseWhenNotExists() {
        when(hospitalRepository.existsByNameAndTenantIdAndDeletedAtIsNull("Non Existent", 1L)).thenReturn(false);

        boolean result = hospitalService.existsByNameAndTenantId("Non Existent", 1L);

        assertThat(result).isFalse();
    }

    @Test
    void save_create_shouldPersistHospital() {
        Hospital newHospital = Hospital.builder().name("New Hospital").tenant(tenant).status(TenantStatus.ACTIVE).build();
        when(hospitalRepository.findByNameAndTenantIdAndDeletedAtIsNull(anyString(), anyLong())).thenReturn(Optional.empty());
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);

        Hospital result = hospitalService.save(newHospital);

        assertThat(result).isNotNull();
        verify(hospitalRepository).save(newHospital);
    }

    @Test
    void save_create_shouldThrowWhenDuplicateName() {
        Hospital newHospital = Hospital.builder().name("Test Hospital").tenant(tenant).status(TenantStatus.ACTIVE).build();
        when(hospitalRepository.findByNameAndTenantIdAndDeletedAtIsNull("Test Hospital", 1L)).thenReturn(Optional.of(hospital));

        assertThatThrownBy(() -> hospitalService.save(newHospital))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void save_update_shouldSucceedWhenNoDuplicate() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalRepository.findByNameAndTenantIdAndDeletedAtIsNull(anyString(), anyLong())).thenReturn(Optional.empty());
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);

        Hospital result = hospitalService.save(hospital);

        assertThat(result).isNotNull();
    }

    @Test
    void save_update_shouldThrowWhenDuplicateName() {
        Hospital updateHospital = Hospital.builder().id(1L).name("Test Hospital").tenant(tenant).status(TenantStatus.ACTIVE).build();
        Hospital existingHospital = Hospital.builder().id(2L).name("Test Hospital").tenant(tenant).status(TenantStatus.ACTIVE).build();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(updateHospital));
        when(hospitalRepository.findByNameAndTenantIdAndDeletedAtIsNull("Test Hospital", 1L)).thenReturn(Optional.of(existingHospital));

        assertThatThrownBy(() -> hospitalService.save(updateHospital))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void softDelete_shouldSetDeletedAt() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);

        hospitalService.softDelete(1L);

        assertThat(hospital.getDeletedAt()).isNotNull();
    }

    @Test
    void softDelete_shouldThrowWhenNotFound() {
        when(hospitalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalService.softDelete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateHospital() {
        HospitalUpdateRequest request = new HospitalUpdateRequest();
        request.setName("Updated Hospital");
        request.setStatus(TenantStatus.INACTIVE);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);

        Hospital result = hospitalService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Updated Hospital");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        HospitalUpdateRequest request = new HospitalUpdateRequest();
        request.setName("Test");

        when(hospitalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalService.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateTenant() {
        Tenant newTenant = Tenant.builder().id(2L).name("New Tenant").status(TenantStatus.ACTIVE).build();
        HospitalUpdateRequest request = new HospitalUpdateRequest();
        request.setTenantId(2L);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(tenantRepository.findById(2L)).thenReturn(Optional.of(newTenant));
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);

        Hospital result = hospitalService.update(1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    void update_shouldThrowWhenTenantNotFound() {
        HospitalUpdateRequest request = new HospitalUpdateRequest();
        request.setTenantId(99L);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(tenantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalService.update(1L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
