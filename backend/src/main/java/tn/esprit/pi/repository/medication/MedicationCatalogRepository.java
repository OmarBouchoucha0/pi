package tn.esprit.pi.repository.medication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.medication.MedicationCatalog;

@Repository
public interface MedicationCatalogRepository extends JpaRepository<MedicationCatalog, Long> {
    List<MedicationCatalog> findByCategory(String category);
    List<MedicationCatalog> findByNameContainingIgnoreCase(String name);
    List<MedicationCatalog> findByMolecule(String molecule);
    boolean existsByNameIgnoreCase(String name);
}
