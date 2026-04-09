package tn.esprit.pi.service.medication;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.medication.MedicationCatalogRequest;
import tn.esprit.pi.dto.medication.MedicationCatalogResponse;
import tn.esprit.pi.entity.medication.MedicationCatalog;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.medication.MedicationCatalogRepository;

@Service
@RequiredArgsConstructor
public class MedicationCatalogServiceImpl implements MedicationCatalogService {

    private final MedicationCatalogRepository medicationCatalogRepository;

    // ─── Mapping ──────────────────────────────────────────────────

    private MedicationCatalogResponse toResponse(MedicationCatalog m) {
        return MedicationCatalogResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .description(m.getDescription())
                .molecule(m.getMolecule())
                .category(m.getCategory())
                .minDose(m.getMinDose())
                .maxDose(m.getMaxDose())
                .unit(m.getUnit())
                .frequencyPerDay(m.getFrequencyPerDay())
                .build();
    }

    // ─── Create ───────────────────────────────────────────────────
    // Vérifie que le médicament n'existe pas déjà (par nom)

    public MedicationCatalogResponse create(MedicationCatalogRequest request) {
        if (medicationCatalogRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Médicament déjà existant : " + request.getName());
        }
        MedicationCatalog med = MedicationCatalog.builder()
                .name(request.getName())
                .description(request.getDescription())
                .molecule(request.getMolecule())
                .category(request.getCategory())
                .minDose(request.getMinDose())
                .maxDose(request.getMaxDose())
                .unit(request.getUnit())
                .frequencyPerDay(request.getFrequencyPerDay())
                .build();
        return toResponse(medicationCatalogRepository.save(med));
    }

    // ─── Read ─────────────────────────────────────────────────────

    public MedicationCatalogResponse getById(Long id) {
        return toResponse(medicationCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médicament non trouvé : " + id)));
    }

    public List<MedicationCatalogResponse> getAll() {
        return medicationCatalogRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MedicationCatalogResponse> searchByName(String name) {
        return medicationCatalogRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MedicationCatalogResponse> getByCategory(String category) {
        return medicationCatalogRepository.findByCategory(category)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─── Update ───────────────────────────────────────────────────

    public MedicationCatalogResponse update(Long id, MedicationCatalogRequest request) {
        MedicationCatalog med = medicationCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médicament non trouvé : " + id));
        med.setName(request.getName());
        med.setDescription(request.getDescription());
        med.setMolecule(request.getMolecule());
        med.setCategory(request.getCategory());
        med.setMinDose(request.getMinDose());
        med.setMaxDose(request.getMaxDose());
        med.setUnit(request.getUnit());
        med.setFrequencyPerDay(request.getFrequencyPerDay());
        return toResponse(medicationCatalogRepository.save(med));
    }

    // ─── Delete ───────────────────────────────────────────────────

    public void delete(Long id) {
        if (!medicationCatalogRepository.existsById(id)) {
            throw new ResourceNotFoundException("Médicament non trouvé : " + id);
        }
        medicationCatalogRepository.deleteById(id);
    }
}
