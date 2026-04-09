package tn.esprit.pi.controller.labDocuments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.labDocumentsDTO.DocumentFolderDTO;
import tn.esprit.pi.service.labDocuments.DocumentFolderService;

@RestController
@RequestMapping("/document-folders")
@RequiredArgsConstructor
public class DocumentFolderController {

    private final DocumentFolderService folderService;

    @PostMapping("/create")
    public ResponseEntity<DocumentFolderDTO> createFolder(@RequestBody DocumentFolderDTO dto) {
        return new ResponseEntity<>(folderService.createFolder(dto), HttpStatus.CREATED);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<DocumentFolderDTO> getFolderById(@PathVariable Long id) {
        return ResponseEntity.ok(folderService.getFolderById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<DocumentFolderDTO>> getAllFolders() {
        return ResponseEntity.ok(folderService.getAllFolders());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DocumentFolderDTO>> getFoldersByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(folderService.getFoldersByPatientId(patientId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DocumentFolderDTO> updateFolder(@PathVariable Long id, @RequestBody DocumentFolderDTO dto) {
        return ResponseEntity.ok(folderService.updateFolder(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }
}
