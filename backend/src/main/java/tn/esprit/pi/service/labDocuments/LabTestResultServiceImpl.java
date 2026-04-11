package tn.esprit.pi.service.labDocuments;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.labDocumentsDTO.LabTestResultDTO;
import tn.esprit.pi.entity.labDocuments.LabTestResult;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.mapper.labDocumentsMapper.LabTestResultMapper;
import tn.esprit.pi.repository.labDocuments.LabTestResultRepository;

@Service
@RequiredArgsConstructor
public class LabTestResultServiceImpl implements LabTestResultService {

    private final LabTestResultRepository testResultRepository;
    private final LabTestResultMapper testResultMapper;

    @Override
    @Transactional
    public LabTestResultDTO createTestResult(LabTestResultDTO dto) {
        LabTestResult testResult = testResultMapper.toEntity(dto);
        LabTestResult savedResult = testResultRepository.save(testResult);
        return testResultMapper.toDto(savedResult);
    }

    @Override
    public LabTestResultDTO getTestResultById(Long id) {
        LabTestResult testResult = testResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Test Result not found with id: " + id));
        return testResultMapper.toDto(testResult);
    }

    @Override
    public List<LabTestResultDTO> getAllTestResults() {
        return testResultRepository.findAll().stream()
                .map(testResultMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabTestResultDTO> getTestResultsByPatientId(Long patientId) {
        return testResultRepository.findByPatientId(patientId).stream()
                .map(testResultMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabTestResultDTO> getTestResultsByDocumentId(Long documentId) {
        return testResultRepository.findBySourceDocumentId(documentId).stream()
                .map(testResultMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LabTestResultDTO updateTestResult(Long id, LabTestResultDTO dto) {
        LabTestResult existingResult = testResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Test Result not found with id: " + id));

        // Update data fields
        existingResult.setTestName(dto.getTestName());
        existingResult.setTestValue(dto.getTestValue());
        existingResult.setUnit(dto.getUnit());
        existingResult.setNormalRangeMin(dto.getNormalRangeMin());
        existingResult.setNormalRangeMax(dto.getNormalRangeMax());

        if (dto.getIsAbnormal() != null) {
            existingResult.setIsAbnormal(dto.getIsAbnormal());
        }

        LabTestResult updatedResult = testResultRepository.save(existingResult);
        return testResultMapper.toDto(updatedResult);
    }

    @Override
    @Transactional
    public void deleteTestResult(Long id) {
        if (!testResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lab Test Result not found with id: " + id);
        }
        testResultRepository.deleteById(id);
    }
}
