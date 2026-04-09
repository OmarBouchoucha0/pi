package tn.esprit.pi.service.labDocuments;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.labDocumentsDTO.MedicalDocumentDTO;
import tn.esprit.pi.entity.labDocuments.MedicalDocument;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.mapper.labDocumentsMapper.MedicalDocumentMapper;
import tn.esprit.pi.repository.labDocuments.MedicalDocumentRepository;

@Service
@RequiredArgsConstructor
public class MedicalDocumentServiceImpl implements MedicalDocumentService {

    private final MedicalDocumentRepository documentRepository;
    private final MedicalDocumentMapper documentMapper;

    @Override
    @Transactional
    public MedicalDocumentDTO createDocument(MedicalDocumentDTO dto) {
        MedicalDocument document = documentMapper.toEntity(dto);
        MedicalDocument savedDocument = documentRepository.save(document);
        return documentMapper.toDto(savedDocument);
    }

    @Override
    public MedicalDocumentDTO getDocumentById(Long id) {
        MedicalDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical Document not found with id: " + id));
        return documentMapper.toDto(document);
    }

    @Override
    public List<MedicalDocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalDocumentDTO> getDocumentsByFolderId(Long folderId) {
        return documentRepository.findByFolderId(folderId).stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalDocumentDTO> getDocumentsByPatientId(Long patientId) {
        return documentRepository.findByPatientId(patientId).stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MedicalDocumentDTO updateDocument(Long id, MedicalDocumentDTO dto) {
        MedicalDocument existingDocument = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical Document not found with id: " + id));

        // Update fields (excluding folder, patient, and uploadDate for safety)
        existingDocument.setFileName(dto.getFileName());
        existingDocument.setFileUrl(dto.getFileUrl());
        existingDocument.setDocumentType(dto.getDocumentType());

        MedicalDocument updatedDocument = documentRepository.save(existingDocument);
        return documentMapper.toDto(updatedDocument);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medical Document not found with id: " + id);
        }
        documentRepository.deleteById(id);
    }
}
