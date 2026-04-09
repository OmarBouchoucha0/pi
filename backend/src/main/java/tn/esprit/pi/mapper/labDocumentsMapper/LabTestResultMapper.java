package tn.esprit.pi.mapper.labDocumentsMapper;

import org.springframework.stereotype.Component;

import tn.esprit.pi.dto.labDocumentsDTO.LabTestResultDTO;
import tn.esprit.pi.entity.labDocuments.LabTestResult;
import tn.esprit.pi.entity.labDocuments.MedicalDocument;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.repository.labDocuments.MedicalDocumentRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Component
public class LabTestResultMapper {

    private final UserRepository userRepository;
    private final MedicalDocumentRepository documentRepository;

    public LabTestResultMapper(UserRepository userRepository, MedicalDocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    public LabTestResultDTO toDto(LabTestResult entity) {
        if (entity == null) {
            return null;
        }

        return LabTestResultDTO.builder()
                .id(entity.getId())
                .patientId(entity.getPatient() != null ? entity.getPatient().getId() : null)
                .sourceDocumentId(entity.getSourceDocument() != null ? entity.getSourceDocument().getId() : null)
                .testName(entity.getTestName())
                .testValue(entity.getTestValue())
                .unit(entity.getUnit())
                .normalRangeMin(entity.getNormalRangeMin())
                .normalRangeMax(entity.getNormalRangeMax())
                .isAbnormal(entity.getIsAbnormal())
                .extractedAt(entity.getExtractedAt())
                .build();
    }

    public LabTestResult toEntity(LabTestResultDTO dto) {
        if (dto == null) {
            return null;
        }

        LabTestResult result = new LabTestResult();
        result.setId(dto.getId());
        result.setTestName(dto.getTestName());
        result.setTestValue(dto.getTestValue());
        result.setUnit(dto.getUnit());
        result.setNormalRangeMin(dto.getNormalRangeMin());
        result.setNormalRangeMax(dto.getNormalRangeMax());

        // Handle the default boolean value properly during updates/creation
        if (dto.getIsAbnormal() != null) {
            result.setIsAbnormal(dto.getIsAbnormal());
        }

        // Map Patient
        if (dto.getPatientId() != null) {
            User patient = userRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found with id: " + dto.getPatientId()));
            result.setPatient(patient);
        }

        // Map Source Document (Optional)
        if (dto.getSourceDocumentId() != null) {
            MedicalDocument document = documentRepository.findById(dto.getSourceDocumentId())
                    .orElseThrow(() -> new RuntimeException("Medical Document not found with id: " + dto.getSourceDocumentId()));
            result.setSourceDocument(document);
        }

        return result;
    }
}
