package tn.esprit.pi.controller.labDocuments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.labDocumentsDTO.LabTestResultDTO;
import tn.esprit.pi.service.labDocuments.LabTestResultService;

@RestController
@RequestMapping("/lab-test-results")
@RequiredArgsConstructor
public class LabTestResultController {

    private final LabTestResultService testResultService;

    @PostMapping("/create")
    public ResponseEntity<LabTestResultDTO> createTestResult(@RequestBody LabTestResultDTO dto) {
        return new ResponseEntity<>(testResultService.createTestResult(dto), HttpStatus.CREATED);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<LabTestResultDTO> getTestResultById(@PathVariable Long id) {
        return ResponseEntity.ok(testResultService.getTestResultById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<LabTestResultDTO>> getAllTestResults() {
        return ResponseEntity.ok(testResultService.getAllTestResults());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabTestResultDTO>> getTestResultsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(testResultService.getTestResultsByPatientId(patientId));
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<LabTestResultDTO>> getTestResultsByDocumentId(@PathVariable Long documentId) {
        return ResponseEntity.ok(testResultService.getTestResultsByDocumentId(documentId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LabTestResultDTO> updateTestResult(@PathVariable Long id, @RequestBody LabTestResultDTO dto) {
        return ResponseEntity.ok(testResultService.updateTestResult(id, dto));
    }

    @DeleteMapping("/update/{id}")
    public ResponseEntity<Void> deleteTestResult(@PathVariable Long id) {
        testResultService.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }
}
