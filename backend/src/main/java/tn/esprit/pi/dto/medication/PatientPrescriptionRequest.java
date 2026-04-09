package tn.esprit.pi.dto.medication;

import java.time.LocalDate;

import lombok.Data;
import tn.esprit.pi.enums.medication.PrescriptionStatus;

@Data
public class PatientPrescriptionRequest {
    private Long patientId;
    private Long doctorId;
    private Long drugId;
    private Double dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean withFood;
    private PrescriptionStatus status;
}
