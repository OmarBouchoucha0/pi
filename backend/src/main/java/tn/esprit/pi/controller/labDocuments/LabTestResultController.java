package tn.esprit.pi.controller.labDocuments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.labDocumentsDTO.LabTestResultDTO;
import tn.esprit.pi.service.labDocuments.LabTestResultService;

@RestController
@RequestMapping("/lab-test-results")
@RequiredArgsConstructor
@Tag(name = "Lab Test Results", description = "APIs for managing laboratory test results")
public class LabTestResultController {

    private final LabTestResultService testResultService;

    @PostMapping("/create")
    @Operation(summary = "Create lab test result", description = "Creates a new lab test result")
    @ApiResponse(responseCode = "201", description = "Test result created successfully")
    public ResponseEntity<LabTestResultDTO> createTestResult(@RequestBody LabTestResultDTO dto) {
        return new ResponseEntity<>(testResultService.createTestResult(dto), HttpStatus.CREATED);
    }

    @GetMapping("/getById/{id}")
    @Operation(summary = "Get test result by ID", description = "Retrieves a specific test result by its ID")
    @ApiResponse(responseCode = "200", description = "Test result found")
    @ApiResponse(responseCode = "404", description = "Test result not found")
    public ResponseEntity<LabTestResultDTO> getTestResultById(@PathVariable Long id) {
        return ResponseEntity.ok(testResultService.getTestResultById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "Get all test results", description = "Retrieves all lab test results")
    @ApiResponse(responseCode = "200", description = "Test results retrieved successfully")
    public ResponseEntity<List<LabTestResultDTO>> getAllTestResults() {
        return ResponseEntity.ok(testResultService.getAllTestResults());
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get test results by patient", description = "Retrieves all test results for a specific patient")
    @ApiResponse(responseCode = "200", description = "Test results retrieved successfully")
    public ResponseEntity<List<LabTestResultDTO>> getTestResultsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(testResultService.getTestResultsByPatientId(patientId));
    }

    @GetMapping("/document/{documentId}")
    @Operation(summary = "Get test results by document", description = "Retrieves all test results for a specific document")
    @ApiResponse(responseCode = "200", description = "Test results retrieved successfully")
    public ResponseEntity<List<LabTestResultDTO>> getTestResultsByDocumentId(@PathVariable Long documentId) {
        return ResponseEntity.ok(testResultService.getTestResultsByDocumentId(documentId));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update test result", description = "Updates an existing test result")
    @ApiResponse(responseCode = "200", description = "Test result updated successfully")
    @ApiResponse(responseCode = "404", description = "Test result not found")
    public ResponseEntity<LabTestResultDTO> updateTestResult(@PathVariable Long id, @RequestBody LabTestResultDTO dto) {
        return ResponseEntity.ok(testResultService.updateTestResult(id, dto));
    }

    @DeleteMapping("/update/{id}")
    @Operation(summary = "Delete test result", description = "Deletes a lab test result")
    @ApiResponse(responseCode = "204", description = "Test result deleted successfully")
    @ApiResponse(responseCode = "404", description = "Test result not found")
    public ResponseEntity<Void> deleteTestResult(@PathVariable Long id) {
        testResultService.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }
}
