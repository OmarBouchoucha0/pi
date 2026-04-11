package tn.esprit.pi.service.vital;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.pi.dto.vital.VitalParameterRequest;
import tn.esprit.pi.dto.vital.VitalParameterResponse;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.entity.vital.VitalParameter;
import tn.esprit.pi.enums.vital.VitalStatus;
import tn.esprit.pi.enums.vital.VitalType;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;
import tn.esprit.pi.repository.vital.ParameterThresholdRepository;
import tn.esprit.pi.repository.vital.VitalParameterRepository;

@ExtendWith(MockitoExtension.class)
class VitalParameterServiceTest {

    @Mock
    private VitalParameterRepository vitalRepo;

    @Mock
    private ParameterThresholdRepository thresholdRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private TenantRepository tenantRepo;

    @InjectMocks
    private VitalParameterServiceImpl vitalParameterService;

    private User patient;
    private Tenant tenant;
    private VitalParameter vitalParameter;

    @BeforeEach
    void setUp() {
        patient = User.builder().id(1L).firstName("John").lastName("Doe").build();
        tenant = Tenant.builder().id(1L).name("Test Tenant").build();

        vitalParameter = VitalParameter.builder()
                .id(1L)
                .patient(patient)
                .tenant(tenant)
                .type(VitalType.HEART_RATE)
                .value(75.0)
                .unit("bpm")
                .normalizedValue(75.0)
                .status(VitalStatus.NORMAL)
                .recordedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void addVital_success() {
        VitalParameterRequest request = new VitalParameterRequest();
        request.setPatientId(1L);
        request.setTenantId(1L);
        request.setType(VitalType.HEART_RATE);
        request.setValue(75.0);
        request.setUnit("bpm");

        when(userRepo.findById(1L)).thenReturn(Optional.of(patient));
        when(tenantRepo.findById(1L)).thenReturn(Optional.of(tenant));
        when(vitalRepo.save(any(VitalParameter.class))).thenReturn(vitalParameter);

        VitalParameterResponse result = vitalParameterService.addVital(request);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(75.0);
        verify(vitalRepo).save(any(VitalParameter.class));
    }

    @Test
    void addVital_patientNotFound() {
        VitalParameterRequest request = new VitalParameterRequest();
        request.setPatientId(99L);
        request.setTenantId(1L);
        request.setType(VitalType.HEART_RATE);
        request.setValue(75.0);

        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vitalParameterService.addVital(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void addVital_tenantNotFound() {
        VitalParameterRequest request = new VitalParameterRequest();
        request.setPatientId(1L);
        request.setTenantId(99L);
        request.setType(VitalType.HEART_RATE);
        request.setValue(75.0);

        when(userRepo.findById(1L)).thenReturn(Optional.of(patient));
        when(tenantRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vitalParameterService.addVital(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tenant not found");
    }

    @Test
    void getAll_shouldReturnAllVitals() {
        when(vitalRepo.findAll()).thenReturn(List.of(vitalParameter));

        List<VitalParameterResponse> result = vitalParameterService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValue()).isEqualTo(75.0);
    }

    @Test
    void getById_found() {
        when(vitalRepo.findById(1L)).thenReturn(Optional.of(vitalParameter));

        VitalParameterResponse result = vitalParameterService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_notFound() {
        when(vitalRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vitalParameterService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("VitalParameter not found");
    }

    @Test
    void getByPatient_shouldReturnPatientVitals() {
        when(vitalRepo.findByPatientIdAndTenantIdOrderByRecordedAtDesc(1L, 1L))
                .thenReturn(List.of(vitalParameter));

        List<VitalParameterResponse> result = vitalParameterService.getByPatient(1L, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId()).isEqualTo(1L);
    }

    @Test
    void getByPatientAndType_shouldReturnFilteredVitals() {
        when(vitalRepo.findByPatientIdAndTenantIdAndTypeOrderByRecordedAtDesc(1L, 1L, VitalType.HEART_RATE))
                .thenReturn(List.of(vitalParameter));

        List<VitalParameterResponse> result = vitalParameterService.getByPatientAndType(1L, 1L, VitalType.HEART_RATE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(VitalType.HEART_RATE);
    }

    @Test
    void update_success() {
        VitalParameterRequest request = new VitalParameterRequest();
        request.setPatientId(1L);
        request.setTenantId(1L);
        request.setType(VitalType.HEART_RATE);
        request.setValue(80.0);
        request.setUnit("bpm");

        when(vitalRepo.findById(1L)).thenReturn(Optional.of(vitalParameter));
        when(vitalRepo.save(any(VitalParameter.class))).thenReturn(vitalParameter);

        VitalParameterResponse result = vitalParameterService.update(1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    void delete_success() {
        when(vitalRepo.findById(1L)).thenReturn(Optional.of(vitalParameter));
        doNothing().when(vitalRepo).deleteById(1L);

        vitalParameterService.delete(1L);

        verify(vitalRepo).deleteById(1L);
    }

    @Test
    void delete_notFound() {
        when(vitalRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vitalParameterService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
