package tn.esprit.pi.service.medication;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.medication.PatientPrescriptionRequest;
import tn.esprit.pi.dto.medication.PatientPrescriptionResponse;
import tn.esprit.pi.entity.medication.MedicationCatalog;
import tn.esprit.pi.entity.medication.PatientPrescription;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.medication.PrescriptionStatus;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.drug.DrugInteractionRepository;
import tn.esprit.pi.repository.medication.MedicationCatalogRepository;
import tn.esprit.pi.repository.medication.PatientPrescriptionRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class PatientPrescriptionServiceImpl implements PatientPrescriptionService {


    private final PatientPrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;
    private final MedicationCatalogRepository medicationCatalogRepository;
    private final DrugInteractionRepository drugInteractionRepository;

    // ─── Mapping ──────────────────────────────────────────────────

    private PatientPrescriptionResponse toResponse(PatientPrescription p) {
        return PatientPrescriptionResponse.builder()
                .id(p.getId())
                .patientId(p.getPatient().getId())
                .patientName(p.getPatient().getFirstName() + " " + p.getPatient().getLastName())
                .doctorId(p.getDoctor().getId())
                .doctorName(p.getDoctor().getFirstName() + " " + p.getDoctor().getLastName())
                .drugId(p.getDrug().getId())
                .drugName(p.getDrug().getName())
                .dosage(p.getDosage())
                .frequency(p.getFrequency())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .withFood(p.getWithFood())
                .status(p.getStatus())
                .build();
    }

    // ─── Create ───────────────────────────────────────────────────
    // Vérifie la dose et les interactions avec les ordonnances actives du patient

    public PatientPrescriptionResponse create(PatientPrescriptionRequest request) {
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé : " + request.getPatientId()));
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé : " + request.getDoctorId()));
        MedicationCatalog drug = medicationCatalogRepository.findById(request.getDrugId())
                .orElseThrow(() -> new ResourceNotFoundException("Médicament non trouvé : " + request.getDrugId()));

        // Validation de la dose
        if (drug.getMinDose() != null && request.getDosage() < drug.getMinDose()) {
            throw new IllegalArgumentException("Dose trop faible. Minimum : " + drug.getMinDose() + " " + drug.getUnit());
        }
        if (drug.getMaxDose() != null && request.getDosage() > drug.getMaxDose()) {
            throw new IllegalArgumentException("Dose trop élevée. Maximum : " + drug.getMaxDose() + " " + drug.getUnit());
        }

        // Vérification des interactions avec les médicaments actifs du patient
        List<Long> activeDrugIds = prescriptionRepository
                .findByPatientIdAndStatus(patient.getId(), PrescriptionStatus.ACTIVE)
                .stream()
                .map(p -> p.getDrug().getId())
                .collect(Collectors.toList());

        if (!activeDrugIds.isEmpty()) {
            activeDrugIds.add(drug.getId());
            List<String> dangerousInteractions = drugInteractionRepository
                    .findInteractionsAmong(activeDrugIds)
                    .stream()
                    .filter(i -> i.getDrugA().getId().equals(drug.getId())
                            || i.getDrugB().getId().equals(drug.getId()))
                    .map(i -> i.getDrugA().getName() + " ↔ " + i.getDrugB().getName()
                            + " [" + i.getSeverity() + "]")
                    .collect(Collectors.toList());

            if (!dangerousInteractions.isEmpty()) {
                throw new IllegalArgumentException("Interactions détectées : " + dangerousInteractions);
            }
        }

        PatientPrescription prescription = PatientPrescription.builder()
                .patient(patient)
                .doctor(doctor)
                .drug(drug)
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .withFood(request.getWithFood() != null ? request.getWithFood() : false)
                .status(PrescriptionStatus.ACTIVE)
                .build();

        return toResponse(prescriptionRepository.save(prescription));
    }

    // ─── Read ─────────────────────────────────────────────────────

    public PatientPrescriptionResponse getById(Long id) {
        return toResponse(prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance non trouvée : " + id)));
    }

    public List<PatientPrescriptionResponse> getAll() {
        return prescriptionRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PatientPrescriptionResponse> getByPatient(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PatientPrescriptionResponse> getActiveByPatient(Long patientId) {
        return prescriptionRepository.findByPatientIdAndStatus(patientId, PrescriptionStatus.ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PatientPrescriptionResponse> getByDoctor(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─── Update status ────────────────────────────────────────────

    public PatientPrescriptionResponse updateStatus(Long id, PrescriptionStatus status) {
        PatientPrescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance non trouvée : " + id));
        prescription.setStatus(status);
        return toResponse(prescriptionRepository.save(prescription));
    }

    // ─── Delete ───────────────────────────────────────────────────

    public void delete(Long id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ordonnance non trouvée : " + id);
        }
        prescriptionRepository.deleteById(id);
    }
    @Override
    public PatientPrescriptionResponse update(Long id, PatientPrescriptionRequest request) {
        PatientPrescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance non trouvée : " + id));

        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé : " + request.getPatientId()));

        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé : " + request.getDoctorId()));

        MedicationCatalog drug = medicationCatalogRepository.findById(request.getDrugId())
                .orElseThrow(() -> new ResourceNotFoundException("Médicament non trouvé : " + request.getDrugId()));

        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setDrug(drug);
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setStartDate(request.getStartDate());
        prescription.setEndDate(request.getEndDate());
        prescription.setWithFood(request.getWithFood());
        if (request.getStatus() != null) {
            prescription.setStatus(request.getStatus());
        }

        return toResponse(prescriptionRepository.save(prescription));
    }
}
