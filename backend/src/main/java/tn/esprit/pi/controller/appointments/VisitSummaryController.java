package tn.esprit.pi.controller.appointments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.appointmentsDTO.VisitSummaryDTO;
import tn.esprit.pi.service.appointments.IVisitSummaryService;

@RestController
@RequestMapping("/visit-summaries")
@RequiredArgsConstructor
@Tag(name = "Visit Summaries", description = "APIs for managing visit summaries")
public class VisitSummaryController {

    private final IVisitSummaryService visitSummaryService;

    // CREATE
    @PostMapping("/create")
    @Operation(summary = "Create visit summary", description = "Creates a new visit summary")
    @ApiResponse(responseCode = "201", description = "Visit summary created successfully")
    public ResponseEntity<VisitSummaryDTO> createVisitSummary(@RequestBody VisitSummaryDTO dto) {
        VisitSummaryDTO created = visitSummaryService.createVisitSummary(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping("/list")
    @Operation(summary = "Get all visit summaries", description = "Retrieves all visit summaries")
    @ApiResponse(responseCode = "200", description = "Visit summaries retrieved successfully")
    public ResponseEntity<List<VisitSummaryDTO>> getAllVisitSummaries() {
        return ResponseEntity.ok(visitSummaryService.getAllVisitSummaries());
    }

    // READ BY ID
    @GetMapping("/getById/{id}")
    @Operation(summary = "Get visit summary by ID", description = "Retrieves a specific visit summary by its ID")
    @ApiResponse(responseCode = "200", description = "Visit summary found")
    @ApiResponse(responseCode = "404", description = "Visit summary not found")
    public ResponseEntity<VisitSummaryDTO> getVisitSummaryById(@PathVariable Long id) {
        return ResponseEntity.ok(visitSummaryService.getVisitSummaryById(id));
    }

    // READ BY APPOINTMENT ID
    @GetMapping("/getSummaryByAppointmentId/{appointmentId}")
    @Operation(summary = "Get visit summary by appointment", description = "Retrieves visit summary for a specific appointment")
    @ApiResponse(responseCode = "200", description = "Visit summary found")
    @ApiResponse(responseCode = "404", description = "Visit summary not found")
    public ResponseEntity<VisitSummaryDTO> getVisitSummaryByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(visitSummaryService.getVisitSummaryByAppointmentId(appointmentId));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    @Operation(summary = "Update visit summary", description = "Updates an existing visit summary")
    @ApiResponse(responseCode = "200", description = "Visit summary updated successfully")
    @ApiResponse(responseCode = "404", description = "Visit summary not found")
    public ResponseEntity<VisitSummaryDTO> updateVisitSummary(
            @PathVariable Long id,
            @RequestBody VisitSummaryDTO dto) {
        return ResponseEntity.ok(visitSummaryService.updateVisitSummary(id, dto));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete visit summary", description = "Deletes a visit summary")
    @ApiResponse(responseCode = "204", description = "Visit summary deleted successfully")
    @ApiResponse(responseCode = "404", description = "Visit summary not found")
    public ResponseEntity<Void> deleteVisitSummary(@PathVariable Long id) {
        visitSummaryService.deleteVisitSummary(id);
        return ResponseEntity.noContent().build();
    }
}
