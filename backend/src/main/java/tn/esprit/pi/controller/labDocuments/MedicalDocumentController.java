package tn.esprit.pi.controller.labDocuments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.labDocumentsDTO.MedicalDocumentDTO;
import tn.esprit.pi.service.labDocuments.MedicalDocumentService;

@RestController
@RequestMapping("/medical-documents")
@RequiredArgsConstructor
public class MedicalDocumentController {

    private final MedicalDocumentService documentService;

    @PostMapping("/create")
    public ResponseEntity<MedicalDocumentDTO> createDocument(@RequestBody MedicalDocumentDTO dto) {
        return new ResponseEntity<>(documentService.createDocument(dto), HttpStatus.CREATED);
    }

    @GetMapping("/GetById/{id}")
    public ResponseEntity<MedicalDocumentDTO> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<MedicalDocumentDTO>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<MedicalDocumentDTO>> getDocumentsByFolderId(@PathVariable Long folderId) {
        return ResponseEntity.ok(documentService.getDocumentsByFolderId(folderId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalDocumentDTO>> getDocumentsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(documentService.getDocumentsByPatientId(patientId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MedicalDocumentDTO> updateDocument(@PathVariable Long id, @RequestBody MedicalDocumentDTO dto) {
        return ResponseEntity.ok(documentService.updateDocument(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
