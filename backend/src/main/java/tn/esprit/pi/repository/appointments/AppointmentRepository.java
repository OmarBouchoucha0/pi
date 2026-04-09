package tn.esprit.pi.repository.appointments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.appointments.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // You can add custom query methods here later, e.g.:
     List<Appointment> findByDoctorId(Long doctorId);
     List<Appointment> findByPatientId(Long patientId);
}
