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

import tn.esprit.pi.entity.user.Hospital;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.enums.user.TenantStatus;
import tn.esprit.pi.service.user.HospitalService;

@ExtendWith(MockitoExtension.class)
class HospitalControllerTest {

    @Mock
    private HospitalService hospitalService;

    @InjectMocks
    private HospitalController hospitalController;

    private Tenant tenant;
    private Hospital hospital;

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Test Tenant");
        tenant.setStatus(TenantStatus.ACTIVE);

        hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setTenant(tenant);
        hospital.setStatus(TenantStatus.ACTIVE);
    }

    @Test
    void findAll_shouldReturnHospitals() {
        when(hospitalService.findAll()).thenReturn(List.of(hospital));

        ResponseEntity<List<Hospital>> result = hospitalController.findAll();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void findById_shouldReturnHospital() {
        when(hospitalService.findById(1L)).thenReturn(Optional.of(hospital));

        ResponseEntity<Hospital> result = hospitalController.findById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void findById_shouldReturnNotFound() {
        when(hospitalService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Hospital> result = hospitalController.findById(99L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findByTenantId_shouldReturnHospitals() {
        when(hospitalService.findByTenantId(1L)).thenReturn(List.of(hospital));

        ResponseEntity<List<Hospital>> result = hospitalController.findByTenantId(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void save_shouldCreateHospital() {
        when(hospitalService.save(any(Hospital.class))).thenReturn(hospital);

        ResponseEntity<Hospital> result = hospitalController.save(hospital);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void softDelete_shouldReturnNoContent() {
        doNothing().when(hospitalService).softDelete(1L);

        ResponseEntity<Void> result = hospitalController.softDelete(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(hospitalService).softDelete(1L);
    }

    @Test
    void update_shouldReturnUpdatedHospital() {
        tn.esprit.pi.dto.user.HospitalUpdateRequest request = new tn.esprit.pi.dto.user.HospitalUpdateRequest();
        request.setName("Updated Hospital");

        when(hospitalService.update(1L, request)).thenReturn(hospital);

        ResponseEntity<Hospital> result = hospitalController.update(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
