package tn.esprit.pi.service.appointments;

import java.util.List;

import tn.esprit.pi.dto.appointmentsDTO.AppointmentDTO;

public interface IAppointmentService {
    AppointmentDTO createAppointment(AppointmentDTO appointmentDTO);
    AppointmentDTO getAppointmentById(Long id);
    List<AppointmentDTO> getAllAppointments();
    AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO);
    void deleteAppointment(Long id);
}
