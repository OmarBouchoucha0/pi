package tn.esprit.pi.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.IntakeLogRequest;
import tn.esprit.pi.dto.IntakeLogResponse;
import tn.esprit.pi.entity.IntakeLog;
import tn.esprit.pi.entity.PatientPrescription;
import tn.esprit.pi.enums.IntakeStatus;
import tn.esprit.pi.repository.IntakeLogRepository;
import tn.esprit.pi.repository.PatientPrescriptionRepository;

@Service
@RequiredArgsConstructor
public class IntakeLogServiceImpl implements IntakeLogService {

    private final IntakeLogRepository intakeLogRepository;
    private final PatientPrescriptionRepository prescriptionRepository;

    // ─── Mapping ──────────────────────────────────────────────────

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

    // ─── Create ───────────────────────────────────────────────────
    // Calcule automatiquement le delayMinutes et le statut LATE si retard > 30 min

    public IntakeLogResponse create(IntakeLogRequest request) {
        PatientPrescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new RuntimeException("Ordonnance non trouvée : " + request.getPrescriptionId()));

        IntakeStatus status = request.getStatus();
        Integer delayMinutes = request.getDelayMinutes();

        // Calcul automatique du délai si takenAt est fourni
        if (request.getTakenAt() != null && request.getScheduledTime() != null) {
            long minutes = Duration.between(request.getScheduledTime(), request.getTakenAt()).toMinutes();
            delayMinutes = (int) minutes;
            // Si retard > 30 min → statut LATE automatiquement
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

    // ─── Read ─────────────────────────────────────────────────────

    public IntakeLogResponse getById(Long id) {
        return toResponse(intakeLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IntakeLog non trouvé : " + id)));
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

    // ─── Taux d'adhérence ─────────────────────────────────────────
    // Retourne : { taken: 8, skipped: 1, late: 1, adherenceRate: 80.0 }

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

    // ─── Delete ───────────────────────────────────────────────────

    public void delete(Long id) {
        if (!intakeLogRepository.existsById(id)) {
            throw new RuntimeException("IntakeLog non trouvé : " + id);
        }
        intakeLogRepository.deleteById(id);
    }
    @Override
    public IntakeLogResponse update(Long id, IntakeLogRequest request) {
        IntakeLog log = intakeLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IntakeLog non trouvé : " + id));

        PatientPrescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new RuntimeException("Ordonnance non trouvée : " + request.getPrescriptionId()));

        IntakeStatus status = request.getStatus();
        Integer delayMinutes = request.getDelayMinutes();

        if (request.getTakenAt() != null && request.getScheduledTime() != null) {
            long minutes = Duration.between(request.getScheduledTime(), request.getTakenAt()).toMinutes();
            delayMinutes = (int) minutes;
            if (minutes > 30) {
                status = IntakeStatus.LATE;
            }
        }

        log.setPrescription(prescription);
        log.setScheduledTime(request.getScheduledTime());
        log.setTakenAt(request.getTakenAt());
        log.setStatus(status != null ? status : IntakeStatus.TAKEN);
        log.setDelayMinutes(delayMinutes);
        log.setDoseTaken(request.getDoseTaken());

        return toResponse(intakeLogRepository.save(log));
    }
}
