package tn.esprit.pi.controller.appointments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.appointmentsDTO.DoctorAvailabilityDTO;
import tn.esprit.pi.service.appointments.IDoctorAvailabilityService;

@RestController
@RequestMapping("/doctor-availabilities")
@RequiredArgsConstructor
public class DoctorAvailabilityController {

    private final IDoctorAvailabilityService availabilityService;

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<DoctorAvailabilityDTO> createAvailability(@RequestBody DoctorAvailabilityDTO dto) {
        DoctorAvailabilityDTO created = availabilityService.createAvailability(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping("/list")
    public ResponseEntity<List<DoctorAvailabilityDTO>> getAllAvailabilities() {
        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
    }

    // READ BY ID
    @GetMapping("/getById/{id}")
    public ResponseEntity<DoctorAvailabilityDTO> getAvailabilityById(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityService.getAvailabilityById(id));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<DoctorAvailabilityDTO> updateAvailability(
            @PathVariable Long id,
            @RequestBody DoctorAvailabilityDTO dto) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, dto));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
