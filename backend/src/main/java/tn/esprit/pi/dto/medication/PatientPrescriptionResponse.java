package tn.esprit.pi.dto.medication;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.medication.PrescriptionStatus;

@Data
@Builder
public class PatientPrescriptionResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long drugId;
    private String drugName;
    private Double dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean withFood;
    private PrescriptionStatus status;
}
