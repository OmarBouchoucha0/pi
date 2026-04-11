package tn.esprit.pi.service.intake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.pi.dto.intake.IntakeLogRequest;
import tn.esprit.pi.dto.intake.IntakeLogResponse;
import tn.esprit.pi.entity.intake.IntakeLog;
import tn.esprit.pi.entity.medication.MedicationCatalog;
import tn.esprit.pi.entity.medication.PatientPrescription;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.intake.IntakeStatus;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.intake.IntakeLogRepository;
import tn.esprit.pi.repository.medication.PatientPrescriptionRepository;

@ExtendWith(MockitoExtension.class)
class IntakeLogServiceTest {

    @Mock
    private IntakeLogRepository intakeLogRepository;

    @Mock
    private PatientPrescriptionRepository prescriptionRepository;

    @InjectMocks
    private IntakeLogServiceImpl intakeLogService;

    private PatientPrescription prescription;
    private IntakeLog intakeLog;

    @BeforeEach
    void setUp() {
        User patient = User.builder().id(1L).firstName("John").lastName("Doe").build();
        MedicationCatalog drug = MedicationCatalog.builder().id(1L).name("Aspirin").build();

        prescription = PatientPrescription.builder()
                .id(1L)
                .patient(patient)
                .drug(drug)
                .build();

        intakeLog = IntakeLog.builder()
                .id(1L)
                .prescription(prescription)
                .scheduledTime(LocalDateTime.of(2024, 6, 10, 8, 0))
                .takenAt(LocalDateTime.of(2024, 6, 10, 8, 15))
                .status(IntakeStatus.TAKEN)
                .delayMinutes(15)
                .doseTaken(1.0)
                .build();
    }

    @Test
    void create_success() {
        IntakeLogRequest request = new IntakeLogRequest();
        request.setPrescriptionId(1L);
        request.setScheduledTime(LocalDateTime.of(2024, 6, 10, 8, 0));
        request.setTakenAt(LocalDateTime.of(2024, 6, 10, 8, 15));
        request.setStatus(IntakeStatus.TAKEN);
        request.setDoseTaken(1.0);

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(intakeLogRepository.save(any(IntakeLog.class))).thenReturn(intakeLog);

        IntakeLogResponse result = intakeLogService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(IntakeStatus.TAKEN);
    }

    @Test
    void create_prescriptionNotFound() {
        IntakeLogRequest request = new IntakeLogRequest();
        request.setPrescriptionId(99L);

        when(prescriptionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> intakeLogService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ordonnance non trouvée");
    }

    @Test
    void getById_found() {
        when(intakeLogRepository.findById(1L)).thenReturn(Optional.of(intakeLog));

        IntakeLogResponse result = intakeLogService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_notFound() {
        when(intakeLogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> intakeLogService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_success() {
        when(intakeLogRepository.existsById(1L)).thenReturn(true);
        doNothing().when(intakeLogRepository).deleteById(1L);

        intakeLogService.delete(1L);

        verify(intakeLogRepository).deleteById(1L);
    }
}
