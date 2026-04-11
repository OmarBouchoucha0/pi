package tn.esprit.pi.controller.labDocuments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.labDocumentsDTO.MedicalDocumentDTO;
import tn.esprit.pi.service.labDocuments.MedicalDocumentService;

@RestController
@RequestMapping("/medical-documents")
@RequiredArgsConstructor
@Tag(name = "Medical Documents", description = "APIs for managing medical documents and files")
public class MedicalDocumentController {

    private final MedicalDocumentService documentService;

    @PostMapping("/create")
    @Operation(summary = "Create medical document", description = "Creates a new medical document")
    @ApiResponse(responseCode = "201", description = "Document created successfully")
    public ResponseEntity<MedicalDocumentDTO> createDocument(@RequestBody MedicalDocumentDTO dto) {
        return new ResponseEntity<>(documentService.createDocument(dto), HttpStatus.CREATED);
    }

    @GetMapping("/GetById/{id}")
    @Operation(summary = "Get document by ID", description = "Retrieves a specific document by its ID")
    @ApiResponse(responseCode = "200", description = "Document found")
    @ApiResponse(responseCode = "404", description = "Document not found")
    public ResponseEntity<MedicalDocumentDTO> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "Get all documents", description = "Retrieves all medical documents")
    @ApiResponse(responseCode = "200", description = "Documents retrieved successfully")
    public ResponseEntity<List<MedicalDocumentDTO>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/folder/{folderId}")
    @Operation(summary = "Get documents by folder", description = "Retrieves all documents in a specific folder")
    @ApiResponse(responseCode = "200", description = "Documents retrieved successfully")
    public ResponseEntity<List<MedicalDocumentDTO>> getDocumentsByFolderId(@PathVariable Long folderId) {
        return ResponseEntity.ok(documentService.getDocumentsByFolderId(folderId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get documents by patient", description = "Retrieves all documents for a specific patient")
    @ApiResponse(responseCode = "200", description = "Documents retrieved successfully")
    public ResponseEntity<List<MedicalDocumentDTO>> getDocumentsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(documentService.getDocumentsByPatientId(patientId));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update document", description = "Updates an existing medical document")
    @ApiResponse(responseCode = "200", description = "Document updated successfully")
    @ApiResponse(responseCode = "404", description = "Document not found")
    public ResponseEntity<MedicalDocumentDTO> updateDocument(@PathVariable Long id, @RequestBody MedicalDocumentDTO dto) {
        return ResponseEntity.ok(documentService.updateDocument(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete document", description = "Deletes a medical document")
    @ApiResponse(responseCode = "204", description = "Document deleted successfully")
    @ApiResponse(responseCode = "404", description = "Document not found")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
