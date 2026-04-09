package tn.esprit.pi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.RecoveryCheckIn;

@Repository
public interface RecoveryCheckInRepository extends JpaRepository<RecoveryCheckIn, Long> {

    // All check-ins for a plan ordered chronologically
    List<RecoveryCheckIn> findByPlanIdOrderByDayNumberAsc(Long planId);

    // Last N check-ins — for computing streak
    List<RecoveryCheckIn> findTop5ByPlanIdOrderByDayNumberDesc(Long planId);

    // Check-in for a specific day (should be unique per plan per day)
    Optional<RecoveryCheckIn> findByPlanIdAndDayNumber(Long planId, Integer dayNumber);

    // All check-ins for a patient across all plans
    List<RecoveryCheckIn> findByPatientIdOrderBySubmittedAtDesc(Long patientId);

    // Count consecutive bad check-ins (DETERIORATING status) for return score boost
    @Query("SELECT COUNT(rc) FROM RecoveryCheckIn rc WHERE rc.plan.id = :planId " +
           "AND rc.recoveryStatus = 'DETERIORATING' " +
           "AND rc.dayNumber >= (SELECT MAX(rc2.dayNumber) - 2 FROM RecoveryCheckIn rc2 WHERE rc2.plan.id = :planId)")
    long countRecentDeterioratingCheckIns(@Param("planId") Long planId);

    // Average composite deviation over all check-ins for a plan
    @Query("SELECT AVG(rc.compositeDeviation) FROM RecoveryCheckIn rc WHERE rc.plan.id = :planId")
    Optional<Double> findAvgDeviationByPlan(@Param("planId") Long planId);
}
