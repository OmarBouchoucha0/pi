package tn.esprit.pi.repository.medication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.medication.PatientPrescription;
import tn.esprit.pi.enums.medication.PrescriptionStatus;

@Repository
public interface PatientPrescriptionRepository extends JpaRepository<PatientPrescription, Long> {
    List<PatientPrescription> findByPatientId(Long patientId);
    List<PatientPrescription> findByDoctorId(Long doctorId);
    List<PatientPrescription> findByPatientIdAndStatus(Long patientId, PrescriptionStatus status);
    List<PatientPrescription> findByDrugId(Long drugId);
    List<PatientPrescription> findByStatus(PrescriptionStatus status);
}
