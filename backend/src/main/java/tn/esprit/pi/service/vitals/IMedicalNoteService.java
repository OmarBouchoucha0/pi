package tn.esprit.pi.service.vitals;

import java.util.List;

import tn.esprit.pi.dto.request.MedicalNoteRequest;
import tn.esprit.pi.dto.response.MedicalNoteResponse;
import tn.esprit.pi.enums.NoteType;

public interface IMedicalNoteService {
    MedicalNoteResponse create(MedicalNoteRequest request);
    MedicalNoteResponse getById(Long id);
    List<MedicalNoteResponse> getAll();
    List<MedicalNoteResponse> getByPatient(Long patientId, Long tenantId);
    List<MedicalNoteResponse> getByPatientAndType(Long patientId, Long tenantId, NoteType type);
    List<MedicalNoteResponse> getByDoctor(Long doctorId, Long tenantId);
    MedicalNoteResponse update(Long id, MedicalNoteRequest request);
    void delete(Long id);
}
