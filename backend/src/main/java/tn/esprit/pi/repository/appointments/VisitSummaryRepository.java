package tn.esprit.pi.repository.appointments;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.appointments.VisitSummary;

@Repository
public interface VisitSummaryRepository extends JpaRepository<VisitSummary, Long> {

    // Very useful for finding the summary linked to a specific appointment
    Optional<VisitSummary> findByAppointmentId(Long appointmentId);
}
