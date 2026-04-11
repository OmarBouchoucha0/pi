package tn.esprit.pi.controller.labDocuments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.labDocumentsDTO.DocumentFolderDTO;
import tn.esprit.pi.service.labDocuments.DocumentFolderService;

@RestController
@RequestMapping("/document-folders")
@RequiredArgsConstructor
@Tag(name = "Document Folders", description = "APIs for managing document folders")
public class DocumentFolderController {

    private final DocumentFolderService folderService;

    @PostMapping("/create")
    @Operation(summary = "Create document folder", description = "Creates a new document folder")
    @ApiResponse(responseCode = "201", description = "Folder created successfully")
    public ResponseEntity<DocumentFolderDTO> createFolder(@RequestBody DocumentFolderDTO dto) {
        return new ResponseEntity<>(folderService.createFolder(dto), HttpStatus.CREATED);
    }

    @GetMapping("/getById/{id}")
    @Operation(summary = "Get folder by ID", description = "Retrieves a specific folder by its ID")
    @ApiResponse(responseCode = "200", description = "Folder found")
    @ApiResponse(responseCode = "404", description = "Folder not found")
    public ResponseEntity<DocumentFolderDTO> getFolderById(@PathVariable Long id) {
        return ResponseEntity.ok(folderService.getFolderById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "Get all folders", description = "Retrieves all document folders")
    @ApiResponse(responseCode = "200", description = "Folders retrieved successfully")
    public ResponseEntity<List<DocumentFolderDTO>> getAllFolders() {
        return ResponseEntity.ok(folderService.getAllFolders());
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get folders by patient", description = "Retrieves all folders for a specific patient")
    @ApiResponse(responseCode = "200", description = "Folders retrieved successfully")
    public ResponseEntity<List<DocumentFolderDTO>> getFoldersByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(folderService.getFoldersByPatientId(patientId));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update folder", description = "Updates an existing folder")
    @ApiResponse(responseCode = "200", description = "Folder updated successfully")
    @ApiResponse(responseCode = "404", description = "Folder not found")
    public ResponseEntity<DocumentFolderDTO> updateFolder(@PathVariable Long id, @RequestBody DocumentFolderDTO dto) {
        return ResponseEntity.ok(folderService.updateFolder(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete folder", description = "Deletes a folder")
    @ApiResponse(responseCode = "204", description = "Folder deleted successfully")
    @ApiResponse(responseCode = "404", description = "Folder not found")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }
}
