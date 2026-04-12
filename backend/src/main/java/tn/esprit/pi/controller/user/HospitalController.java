package tn.esprit.pi.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.user.HospitalUpdateRequest;
import tn.esprit.pi.entity.user.Hospital;
import tn.esprit.pi.service.user.HospitalService;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
@Tag(name = "Hospital Management", description = "APIs for managing hospitals within the system")
@SecurityRequirement(name = "Bearer Authentication")
public class HospitalController {

    private final HospitalService hospitalService;

    @Operation(summary = "Get All Hospitals", description = "Retrieves a list of all active hospitals in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hospitals retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<Hospital>> findAll() {
        return ResponseEntity.ok(hospitalService.findAll());
    }

    @Operation(summary = "Get Hospital by ID", description = "Retrieves a specific hospital by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hospital retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Hospital not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Hospital> findById(
            @Parameter(description = "The unique identifier of the hospital", required = true)
            @PathVariable Long id) {
        return hospitalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get Hospitals by Tenant", description = "Retrieves all hospitals belonging to a specific tenant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hospitals retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Hospital>> findByTenantId(
            @Parameter(description = "The unique identifier of the tenant", required = true)
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(hospitalService.findByTenantId(tenantId));
    }

    @Operation(summary = "Create Hospital", description = "Creates a new hospital in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hospital created successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Hospital name already in use for this tenant.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<Hospital> save(@RequestBody Hospital hospital) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hospitalService.save(hospital));
    }

    @Operation(summary = "Soft Delete Hospital", description = "Soft deletes a hospital by setting their deletedAt timestamp.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hospital soft deleted successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Hospital not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(
            @Parameter(description = "The unique identifier of the hospital to delete", required = true)
            @PathVariable Long id) {
        hospitalService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update Hospital", description = "Updates an existing hospital's information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hospital updated successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Hospital not found.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Hospital name already in use.",
                    content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Hospital> update(
            @Parameter(description = "The unique identifier of the hospital", required = true)
            @PathVariable Long id,
            @Valid @RequestBody HospitalUpdateRequest request) {
        return ResponseEntity.ok(hospitalService.update(id, request));
    }
}
