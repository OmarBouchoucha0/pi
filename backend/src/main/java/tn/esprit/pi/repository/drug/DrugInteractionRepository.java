package tn.esprit.pi.repository.drug;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.drug.DrugInteraction;
import tn.esprit.pi.enums.drug.DrugSeverity;

@Repository
public interface DrugInteractionRepository extends JpaRepository<DrugInteraction, Long> {

    List<DrugInteraction> findByDrugAIdOrDrugBId(Long drugAId, Long drugBId);

    List<DrugInteraction> findBySeverity(DrugSeverity severity);

    // Vérifie si une interaction existe entre deux médicaments (dans les deux sens)
    @Query("SELECT d FROM DrugInteraction d WHERE " +
            "(d.drugA.id = :drugAId AND d.drugB.id = :drugBId) OR " +
            "(d.drugA.id = :drugBId AND d.drugB.id = :drugAId)")
    Optional<DrugInteraction> findInteractionBetween(
            @Param("drugAId") Long drugAId,
            @Param("drugBId") Long drugBId);

    // Vérifie interactions parmi une liste de médicaments (pour une ordonnance)
    @Query("SELECT d FROM DrugInteraction d WHERE " +
            "d.drugA.id IN :drugIds AND d.drugB.id IN :drugIds")
    List<DrugInteraction> findInteractionsAmong(@Param("drugIds") List<Long> drugIds);
}
