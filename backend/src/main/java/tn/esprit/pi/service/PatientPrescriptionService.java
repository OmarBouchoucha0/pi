package tn.esprit.pi.service;

import java.util.List;

import tn.esprit.pi.dto.PatientPrescriptionRequest;
import tn.esprit.pi.dto.PatientPrescriptionResponse;
import tn.esprit.pi.enums.PrescriptionStatus;

public interface PatientPrescriptionService {
    PatientPrescriptionResponse create(PatientPrescriptionRequest request);
    PatientPrescriptionResponse getById(Long id);
    List<PatientPrescriptionResponse> getAll();
    List<PatientPrescriptionResponse> getByPatient(Long patientId);
    List<PatientPrescriptionResponse> getActiveByPatient(Long patientId);
    List<PatientPrescriptionResponse> getByDoctor(Long doctorId);
    PatientPrescriptionResponse updateStatus(Long id, PrescriptionStatus status);
    void delete(Long id);
    PatientPrescriptionResponse update(Long id, PatientPrescriptionRequest request);
}
