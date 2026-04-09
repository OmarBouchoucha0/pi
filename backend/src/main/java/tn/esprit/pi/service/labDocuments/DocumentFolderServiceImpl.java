package tn.esprit.pi.service.labDocuments;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.labDocumentsDTO.DocumentFolderDTO;
import tn.esprit.pi.entity.labDocuments.DocumentFolder;
import tn.esprit.pi.mapper.labDocumentsMapper.DocumentFolderMapper;
import tn.esprit.pi.repository.labDocuments.DocumentFolderRepository;

@Service
@RequiredArgsConstructor
public class DocumentFolderServiceImpl implements DocumentFolderService {

    private final DocumentFolderRepository folderRepository;
    private final DocumentFolderMapper folderMapper;

    @Override
    @Transactional
    public DocumentFolderDTO createFolder(DocumentFolderDTO dto) {
        DocumentFolder folder = folderMapper.toEntity(dto);
        DocumentFolder savedFolder = folderRepository.save(folder);
        return folderMapper.toDto(savedFolder);
    }

    @Override
    public DocumentFolderDTO getFolderById(Long id) {
        DocumentFolder folder = folderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document Folder not found with id: " + id));
        return folderMapper.toDto(folder);
    }

    @Override
    public List<DocumentFolderDTO> getAllFolders() {
        return folderRepository.findAll().stream()
                .map(folderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentFolderDTO> getFoldersByPatientId(Long patientId) {
        return folderRepository.findByPatientId(patientId).stream()
                .map(folderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DocumentFolderDTO updateFolder(Long id, DocumentFolderDTO dto) {
        DocumentFolder existingFolder = folderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document Folder not found with id: " + id));

        // Update fields (excluding patient/createdAt to maintain integrity)
        existingFolder.setCategoryName(dto.getCategoryName());
        existingFolder.setDescription(dto.getDescription());

        DocumentFolder updatedFolder = folderRepository.save(existingFolder);
        return folderMapper.toDto(updatedFolder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long id) {
        if (!folderRepository.existsById(id)) {
            throw new RuntimeException("Document Folder not found with id: " + id);
        }
        folderRepository.deleteById(id);
    }
}
