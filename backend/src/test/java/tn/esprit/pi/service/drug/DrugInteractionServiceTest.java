package tn.esprit.pi.service.drug;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.pi.dto.drug.DrugInteractionRequest;
import tn.esprit.pi.dto.drug.DrugInteractionResponse;
import tn.esprit.pi.entity.drug.DrugInteraction;
import tn.esprit.pi.entity.medication.MedicationCatalog;
import tn.esprit.pi.enums.drug.DrugSeverity;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.drug.DrugInteractionRepository;
import tn.esprit.pi.repository.medication.MedicationCatalogRepository;

@ExtendWith(MockitoExtension.class)
class DrugInteractionServiceTest {

    @Mock
    private DrugInteractionRepository drugInteractionRepository;

    @Mock
    private MedicationCatalogRepository medicationCatalogRepository;

    @InjectMocks
    private DrugInteractionServiceImpl drugInteractionService;

    private MedicationCatalog drugA;
    private MedicationCatalog drugB;
    private DrugInteraction drugInteraction;

    @BeforeEach
    void setUp() {
        drugA = MedicationCatalog.builder().id(1L).name("Aspirin").build();
        drugB = MedicationCatalog.builder().id(2L).name("Warfarin").build();

        drugInteraction = DrugInteraction.builder()
                .id(1L)
                .drugA(drugA)
                .drugB(drugB)
                .severity(DrugSeverity.DANGER)
                .interactionType("Anticoagulant effect")
                .description("Increased bleeding risk")
                .recommendation("Monitor closely")
                .build();
    }

    @Test
    void create_success() {
        DrugInteractionRequest request = new DrugInteractionRequest();
        request.setDrugAId(1L);
        request.setDrugBId(2L);
        request.setSeverity(DrugSeverity.DANGER);
        request.setInteractionType("Anticoagulant effect");
        request.setDescription("Increased bleeding risk");
        request.setRecommendation("Monitor closely");

        when(medicationCatalogRepository.findById(1L)).thenReturn(Optional.of(drugA));
        when(medicationCatalogRepository.findById(2L)).thenReturn(Optional.of(drugB));
        when(drugInteractionRepository.findInteractionBetween(1L, 2L)).thenReturn(Optional.empty());
        when(drugInteractionRepository.save(any(DrugInteraction.class))).thenReturn(drugInteraction);

        DrugInteractionResponse result = drugInteractionService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getSeverity()).isEqualTo(DrugSeverity.DANGER);
    }

    @Test
    void create_drugANotFound() {
        DrugInteractionRequest request = new DrugInteractionRequest();
        request.setDrugAId(99L);
        request.setDrugBId(2L);

        when(medicationCatalogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> drugInteractionService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Drug A non trouvé");
    }

    @Test
    void create_drugBNotFound() {
        DrugInteractionRequest request = new DrugInteractionRequest();
        request.setDrugAId(1L);
        request.setDrugBId(99L);

        when(medicationCatalogRepository.findById(1L)).thenReturn(Optional.of(drugA));
        when(medicationCatalogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> drugInteractionService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Drug B non trouvé");
    }

    @Test
    void create_sameDrugThrowsException() {
        DrugInteractionRequest request = new DrugInteractionRequest();
        request.setDrugAId(1L);
        request.setDrugBId(1L);

        when(medicationCatalogRepository.findById(1L)).thenReturn(Optional.of(drugA));

        assertThatThrownBy(() -> drugInteractionService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ne peut pas interagir avec lui-même");
    }

    @Test
    void create_duplicateThrowsException() {
        DrugInteractionRequest request = new DrugInteractionRequest();
        request.setDrugAId(1L);
        request.setDrugBId(2L);
        request.setSeverity(DrugSeverity.DANGER);

        when(medicationCatalogRepository.findById(1L)).thenReturn(Optional.of(drugA));
        when(medicationCatalogRepository.findById(2L)).thenReturn(Optional.of(drugB));
        when(drugInteractionRepository.findInteractionBetween(1L, 2L)).thenReturn(Optional.of(drugInteraction));

        assertThatThrownBy(() -> drugInteractionService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Interaction déjà définie");
    }

    @Test
    void getById_found() {
        when(drugInteractionRepository.findById(1L)).thenReturn(Optional.of(drugInteraction));

        DrugInteractionResponse result = drugInteractionService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_notFound() {
        when(drugInteractionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> drugInteractionService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_shouldReturnAll() {
        when(drugInteractionRepository.findAll()).thenReturn(List.of(drugInteraction));

        List<DrugInteractionResponse> result = drugInteractionService.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void delete_success() {
        when(drugInteractionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(drugInteractionRepository).deleteById(1L);

        drugInteractionService.delete(1L);

        verify(drugInteractionRepository).deleteById(1L);
    }

    @Test
    void delete_notFound() {
        when(drugInteractionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> drugInteractionService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Interaction non trouvée");
    }
}
