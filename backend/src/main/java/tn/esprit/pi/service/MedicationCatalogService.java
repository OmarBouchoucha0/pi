package tn.esprit.pi.service;

import java.util.List;

import tn.esprit.pi.dto.MedicationCatalogRequest;
import tn.esprit.pi.dto.MedicationCatalogResponse;

public interface MedicationCatalogService {
    MedicationCatalogResponse create(MedicationCatalogRequest request);
    MedicationCatalogResponse getById(Long id);
    List<MedicationCatalogResponse> getAll();
    List<MedicationCatalogResponse> searchByName(String name);
    List<MedicationCatalogResponse> getByCategory(String category);
    MedicationCatalogResponse update(Long id, MedicationCatalogRequest request);
    void delete(Long id);
}
