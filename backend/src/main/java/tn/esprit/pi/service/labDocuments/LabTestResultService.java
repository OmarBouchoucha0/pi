package tn.esprit.pi.service.labDocuments;

import java.util.List;

import tn.esprit.pi.dto.labDocumentsDTO.LabTestResultDTO;

public interface LabTestResultService {
    LabTestResultDTO createTestResult(LabTestResultDTO dto);
    LabTestResultDTO getTestResultById(Long id);
    List<LabTestResultDTO> getAllTestResults();
    List<LabTestResultDTO> getTestResultsByPatientId(Long patientId);
    List<LabTestResultDTO> getTestResultsByDocumentId(Long documentId);
    LabTestResultDTO updateTestResult(Long id, LabTestResultDTO dto);
    void deleteTestResult(Long id);
}
