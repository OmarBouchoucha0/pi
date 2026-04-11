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
@Tag(name = "Hospital Management", description = """
        APIs for managing hospitals within the MeddiFollow system.

        A hospital is a healthcare facility that belongs to a tenant (organization).
        Each hospital contains departments and serves patients and staff.

        ## Hospital Properties
        - **name**: The unique name of the hospital
        - **tenant**: The organization this hospital belongs to
        - **status**: Current operational status
        - **deletedAt**: Timestamp for soft-delete (null if active)

        ## Common Use Cases
        - Create new hospital under a tenant
        - List all hospitals or filter by tenant
        - Update hospital information
        - Soft-delete a hospital
        """)
@SecurityRequirement(name = "Bearer Authentication")
public class HospitalController {

    private final HospitalService hospitalService;

    @Operation(
            summary = "Get All Hospitals",
            description = """
                    Retrieves a list of all active hospitals in the system.

                    This endpoint returns all hospitals that have not been soft-deleted.

                    ## Response Details
                    - Returns list of all active hospitals
                    - Each hospital includes its tenant association
                    """)
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

    @Operation(
            summary = "Get Hospital by ID",
            description = """
                    Retrieves a specific hospital by their unique identifier.

                    This endpoint returns hospital details if found.

                    ## Path Parameters
                    - id: The unique identifier of the hospital

                    ## Response Details
                    - Returns hospital details including tenant association
                    - Returns 404 if hospital not found
                    """)
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

    @Operation(
            summary = "Get Hospitals by Tenant",
            description = """
                    Retrieves all hospitals belonging to a specific tenant.

                    This endpoint returns hospitals filtered by the tenant they belong to.

                    ## Path Parameters
                    - tenantId: The unique identifier of the tenant

                    ## Response Details
                    - Returns list of hospitals in the tenant
                    """)
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

    @Operation(
            summary = "Create Hospital",
            description = """
                    Creates a new hospital in the system.

                    This endpoint registers a new hospital under a tenant.
                    The hospital name must be unique within the tenant.

                    ## Request Details
                    - name: Unique name for the hospital within the tenant
                    - tenant: The tenant (organization) this hospital belongs to
                    - status: Initial operational status

                    ## Response Details
                    - Returns the created hospital with generated ID
                    """)
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

    @Operation(
            summary = "Soft Delete Hospital",
            description = """
                    Soft deletes a hospital by setting their deletedAt timestamp.

                    This endpoint performs a soft delete, marking the hospital as deleted
                    without removing their record from the database.

                    Important: This will affect all departments within the hospital.

                    ## Path Parameters
                    - id: The unique identifier of the hospital to delete

                    ## Response Details
                    - Returns 204 No Content on success
                    """)
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

    @Operation(
            summary = "Update Hospital",
            description = """
                    Updates an existing hospital's information.

                    This endpoint allows partial update of a hospital's information.

                    ## Path Parameters
                    - id: The unique identifier of the hospital

                    ## Request Details
                    - name: New name (must be unique within tenant if provided)
                    - status: New operational status
                    - tenantId: New tenant association

                    ## Response Details
                    - Returns the updated hospital
                    """)
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
