package tn.esprit.pi.controller.appointments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.appointmentsDTO.AppointmentDTO;
import tn.esprit.pi.service.appointments.IAppointmentService;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "APIs for managing appointments")
public class AppointmentController {

    private final IAppointmentService appointmentService;

    // CREATE
    @PostMapping("/create")
    @Operation(summary = "Create appointment", description = "Creates a new appointment")
    @ApiResponse(responseCode = "201", description = "Appointment created successfully")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO created = appointmentService.createAppointment(appointmentDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping("/list")
    @Operation(summary = "Get all appointments", description = "Retrieves all appointments")
    @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    // READ BY ID
    @GetMapping("/getById/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieves a specific appointment by its ID")
    @ApiResponse(responseCode = "200", description = "Appointment found")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    // UPDATE
    @PutMapping("/update/{id}")
    @Operation(summary = "Update appointment", description = "Updates an existing appointment")
    @ApiResponse(responseCode = "200", description = "Appointment updated successfully")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    public ResponseEntity<AppointmentDTO> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO updated = appointmentService.updateAppointment(id, appointmentDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete appointment", description = "Deletes an appointment")
    @ApiResponse(responseCode = "204", description = "Appointment deleted successfully")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
