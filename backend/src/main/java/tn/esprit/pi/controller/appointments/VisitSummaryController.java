package tn.esprit.pi.controller.appointments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.appointmentsDTO.VisitSummaryDTO;
import tn.esprit.pi.service.appointments.IVisitSummaryService;

@RestController
@RequestMapping("/visit-summaries")
@RequiredArgsConstructor
public class VisitSummaryController {

    private final IVisitSummaryService visitSummaryService;

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<VisitSummaryDTO> createVisitSummary(@RequestBody VisitSummaryDTO dto) {
        VisitSummaryDTO created = visitSummaryService.createVisitSummary(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping("/list")
    public ResponseEntity<List<VisitSummaryDTO>> getAllVisitSummaries() {
        return ResponseEntity.ok(visitSummaryService.getAllVisitSummaries());
    }

    // READ BY ID
    @GetMapping("/getById/{id}")
    public ResponseEntity<VisitSummaryDTO> getVisitSummaryById(@PathVariable Long id) {
        return ResponseEntity.ok(visitSummaryService.getVisitSummaryById(id));
    }

    // READ BY APPOINTMENT ID
    @GetMapping("/getSummaryByAppointmentId/{appointmentId}")
    public ResponseEntity<VisitSummaryDTO> getVisitSummaryByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(visitSummaryService.getVisitSummaryByAppointmentId(appointmentId));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<VisitSummaryDTO> updateVisitSummary(
            @PathVariable Long id,
            @RequestBody VisitSummaryDTO dto) {
        return ResponseEntity.ok(visitSummaryService.updateVisitSummary(id, dto));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVisitSummary(@PathVariable Long id) {
        visitSummaryService.deleteVisitSummary(id);
        return ResponseEntity.noContent().build();
    }
}
