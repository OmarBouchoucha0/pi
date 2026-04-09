package tn.esprit.pi.service.vitals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.request.MedicalNoteRequest;
import tn.esprit.pi.dto.response.MedicalNoteResponse;
import tn.esprit.pi.entity.MedicalNote;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.NoteType;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.MedicalNoteRepository;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalNoteServiceImpl implements IMedicalNoteService {

    private final MedicalNoteRepository noteRepo;
    private final UserRepository userRepo;
    private final TenantRepository tenantRepo;

    @Override
    public MedicalNoteResponse create(MedicalNoteRequest req) {
        User patient = userRepo.findById(req.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + req.getPatientId()));
        User doctor = userRepo.findById(req.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + req.getDoctorId()));
        Tenant tenant = tenantRepo.findById(req.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + req.getTenantId()));

        MedicalNote note = MedicalNote.builder()
                .patient(patient).doctor(doctor).tenant(tenant)
                .type(req.getType()).content(req.getContent())
                .diagnosisLabel(req.getDiagnosisLabel())
                .build();

        return toResponse(noteRepo.save(note));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalNoteResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalNoteResponse> getAll() {
        return noteRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalNoteResponse> getByPatient(Long patientId, Long tenantId) {
        return noteRepo.findByPatientIdAndTenantIdOrderByCreatedAtDesc(patientId, tenantId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalNoteResponse> getByPatientAndType(Long patientId, Long tenantId, NoteType type) {
        return noteRepo.findByPatientIdAndTenantIdAndTypeOrderByCreatedAtDesc(patientId, tenantId, type)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalNoteResponse> getByDoctor(Long doctorId, Long tenantId) {
        return noteRepo.findByDoctorIdAndTenantIdOrderByCreatedAtDesc(doctorId, tenantId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public MedicalNoteResponse update(Long id, MedicalNoteRequest req) {
        MedicalNote note = findOrThrow(id);
        note.setContent(req.getContent());
        note.setType(req.getType());
        note.setDiagnosisLabel(req.getDiagnosisLabel());
        note.setUpdatedAt(LocalDateTime.now());
        return toResponse(noteRepo.save(note));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        noteRepo.deleteById(id);
    }

    private MedicalNote findOrThrow(Long id) {
        return noteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalNote not found: " + id));
    }

    private MedicalNoteResponse toResponse(MedicalNote n) {
        return MedicalNoteResponse.builder()
                .id(n.getId())
                .patientId(n.getPatient().getId())
                .doctorId(n.getDoctor().getId())
                .doctorName(n.getDoctor().getFirstName() + " " + n.getDoctor().getLastName())
                .tenantId(n.getTenant().getId())
                .type(n.getType()).content(n.getContent())
                .diagnosisLabel(n.getDiagnosisLabel())
                .createdAt(n.getCreatedAt()).updatedAt(n.getUpdatedAt())
                .build();
    }
}
