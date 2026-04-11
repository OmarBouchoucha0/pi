package tn.esprit.pi.controller.appointments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.appointmentsDTO.DoctorAvailabilityDTO;
import tn.esprit.pi.service.appointments.IDoctorAvailabilityService;

@RestController
@RequestMapping("/doctor-availabilities")
@RequiredArgsConstructor
@Tag(name = "Doctor Availabilities", description = "APIs for managing doctor availability schedules")
public class DoctorAvailabilityController {

    private final IDoctorAvailabilityService availabilityService;

    // CREATE
    @PostMapping("/create")
    @Operation(summary = "Create doctor availability", description = "Creates a new doctor availability schedule")
    @ApiResponse(responseCode = "201", description = "Availability created successfully")
    public ResponseEntity<DoctorAvailabilityDTO> createAvailability(@RequestBody DoctorAvailabilityDTO dto) {
        DoctorAvailabilityDTO created = availabilityService.createAvailability(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping("/list")
    @Operation(summary = "Get all availabilities", description = "Retrieves all doctor availabilities")
    @ApiResponse(responseCode = "200", description = "Availabilities retrieved successfully")
    public ResponseEntity<List<DoctorAvailabilityDTO>> getAllAvailabilities() {
        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
    }

    // READ BY ID
    @GetMapping("/getById/{id}")
    @Operation(summary = "Get availability by ID", description = "Retrieves a specific availability by its ID")
    @ApiResponse(responseCode = "200", description = "Availability found")
    @ApiResponse(responseCode = "404", description = "Availability not found")
    public ResponseEntity<DoctorAvailabilityDTO> getAvailabilityById(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityService.getAvailabilityById(id));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    @Operation(summary = "Update availability", description = "Updates an existing doctor availability")
    @ApiResponse(responseCode = "200", description = "Availability updated successfully")
    @ApiResponse(responseCode = "404", description = "Availability not found")
    public ResponseEntity<DoctorAvailabilityDTO> updateAvailability(
            @PathVariable Long id,
            @RequestBody DoctorAvailabilityDTO dto) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, dto));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete availability", description = "Deletes a doctor availability")
    @ApiResponse(responseCode = "204", description = "Availability deleted successfully")
    @ApiResponse(responseCode = "404", description = "Availability not found")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
