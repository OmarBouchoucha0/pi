package tn.esprit.pi.service.medication;

import java.util.List;

import tn.esprit.pi.dto.medication.MedicationCatalogRequest;
import tn.esprit.pi.dto.medication.MedicationCatalogResponse;

public interface MedicationCatalogService {
    MedicationCatalogResponse create(MedicationCatalogRequest request);
    MedicationCatalogResponse getById(Long id);
    List<MedicationCatalogResponse> getAll();
    List<MedicationCatalogResponse> searchByName(String name);
    List<MedicationCatalogResponse> getByCategory(String category);
    MedicationCatalogResponse update(Long id, MedicationCatalogRequest request);
    void delete(Long id);
}
