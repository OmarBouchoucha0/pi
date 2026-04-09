package tn.esprit.pi.repository.appointments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.appointments.DoctorAvailability;


@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    // A helpful custom query you will likely need later:
    // List<DoctorAvailability> findByDoctorId(Long doctorId);
}
