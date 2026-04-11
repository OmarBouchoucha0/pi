package tn.esprit.pi.service.intake;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.intake.IntakeLogRequest;
import tn.esprit.pi.dto.intake.IntakeLogResponse;
import tn.esprit.pi.entity.intake.IntakeLog;
import tn.esprit.pi.entity.medication.PatientPrescription;
import tn.esprit.pi.enums.intake.IntakeStatus;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.intake.IntakeLogRepository;
import tn.esprit.pi.repository.medication.PatientPrescriptionRepository;

@Service
@RequiredArgsConstructor
public class IntakeLogServiceImpl implements IntakeLogService {

    private final IntakeLogRepository intakeLogRepository;
    private final PatientPrescriptionRepository prescriptionRepository;

    private IntakeLogResponse toResponse(IntakeLog log) {
        return IntakeLogResponse.builder()
                .id(log.getId())
                .prescriptionId(log.getPrescription().getId())
                .drugName(log.getPrescription().getDrug().getName())
                .patientName(log.getPrescription().getPatient().getFirstName()
                        + " " + log.getPrescription().getPatient().getLastName())
                .scheduledTime(log.getScheduledTime())
                .takenAt(log.getTakenAt())
                .status(log.getStatus())
                .delayMinutes(log.getDelayMinutes())
                .doseTaken(log.getDoseTaken())
                .build();
    }

    public IntakeLogResponse create(IntakeLogRequest request) {
        PatientPrescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance non trouvée : " + request.getPrescriptionId()));

        IntakeStatus status = request.getStatus();
        Integer delayMinutes = request.getDelayMinutes();

        if (request.getTakenAt() != null && request.getScheduledTime() != null) {
            long minutes = Duration.between(request.getScheduledTime(), request.getTakenAt()).toMinutes();
            delayMinutes = (int) minutes;
            if (minutes > 30) {
                status = IntakeStatus.LATE;
            }
        }

        IntakeLog log = IntakeLog.builder()
                .prescription(prescription)
                .scheduledTime(request.getScheduledTime())
                .takenAt(request.getTakenAt())
                .status(status != null ? status : IntakeStatus.TAKEN)
                .delayMinutes(delayMinutes)
                .doseTaken(request.getDoseTaken())
                .build();

        return toResponse(intakeLogRepository.save(log));
    }

    public IntakeLogResponse getById(Long id) {
        return toResponse(intakeLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IntakeLog non trouvé : " + id)));
    }

    public List<IntakeLogResponse> getAll() {
        return intakeLogRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<IntakeLogResponse> getByPrescription(Long prescriptionId) {
        return intakeLogRepository.findByPrescriptionId(prescriptionId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<IntakeLogResponse> getByStatus(IntakeStatus status) {
        return intakeLogRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Map<String, Object> getAdherenceStats(Long prescriptionId) {
        long taken  = intakeLogRepository.countByPrescriptionIdAndStatus(prescriptionId, IntakeStatus.TAKEN);
        long skipped= intakeLogRepository.countByPrescriptionIdAndStatus(prescriptionId, IntakeStatus.SKIPPED);
        long late   = intakeLogRepository.countByPrescriptionIdAndStatus(prescriptionId, IntakeStatus.LATE);
        long total  = taken + skipped + late;
        double rate = total > 0 ? (double)(taken + late) / total * 100 : 0;

        return Map.of(
                "taken",         taken,
                "skipped",       skipped,
                "late",          late,
                "total",         total,
                "adherenceRate", Math.round(rate * 10.0) / 10.0
        );
    }

    public void delete(Long id) {
        if (!intakeLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("IntakeLog non trouvé : " + id);
        }
        intakeLogRepository.deleteById(id);
    }

    public IntakeLogResponse update(Long id, IntakeLogRequest request) {
        IntakeLog log = intakeLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IntakeLog non trouvé : " + id));

        if (request.getScheduledTime() != null) {
            log.setScheduledTime(request.getScheduledTime());
        }
        if (request.getTakenAt() != null) {
            log.setTakenAt(request.getTakenAt());
        }
        if (request.getStatus() != null) {
            log.setStatus(request.getStatus());
        }
        if (request.getDoseTaken() != null) {
            log.setDoseTaken(request.getDoseTaken());
        }
        if (request.getDelayMinutes() != null) {
            log.setDelayMinutes(request.getDelayMinutes());
        }

        return toResponse(intakeLogRepository.save(log));
    }
}
