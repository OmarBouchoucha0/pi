package tn.esprit.pi.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.user.TenantUpdateRequest;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.enums.user.TenantStatus;
import tn.esprit.pi.service.user.TenantService;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant Management", description = """
        APIs for managing tenants (organizations/hospitals) in the MeddiFollow system.

        A tenant represents an organization, typically a hospital or healthcare network.
        Each tenant operates as a separate entity with its own users, departments, and data.

        ## Tenant Properties
        - **name**: The unique name identifier of the tenant/organization
        - **status**: Current operational status (ACTIVE, INACTIVE, SUSPENDED)
        - **deletedAt**: Timestamp for soft-delete (null if active)

        ## Common Use Cases
        - Create new tenant for a new hospital/organization
        - Update tenant information (name, status)
        - List all active tenants
        - Check if a tenant name is available
        - Soft-delete a tenant (marks as deleted without removing data)
        """)
@SecurityRequirement(name = "Bearer Authentication")
public class TenantController {

    private final TenantService tenantService;

    @Operation(
            summary = "Get All Tenants",
            description = """
                    Retrieves a list of all active tenants in the system.

                    This endpoint returns all tenants that have not been soft-deleted.
                    Each tenant includes their name, status, and other properties.

                    ## Response Details
                    - Returns list of all active tenants
                    - Soft-deleted tenants are excluded
                    - Each tenant includes their current status
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenants retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<Tenant>> findAll() {
        return ResponseEntity.ok(tenantService.findAll());
    }

    @Operation(
            summary = "Get Tenant by ID",
            description = """
                    Retrieves a specific tenant by their unique identifier.

                    This endpoint returns the tenant details if found and not soft-deleted.

                    ## Path Parameters
                    - id: The unique identifier of the tenant

                    ## Response Details
                    - Returns tenant details including name and status
                    - Returns 404 if tenant not found or soft-deleted
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tenant not found or has been deleted.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> findById(
            @Parameter(description = "The unique identifier of the tenant", required = true)
            @PathVariable Long id) {
        return tenantService.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get Tenant by Name",
            description = """
                    Retrieves a tenant by their name.

                    This endpoint searches for a tenant using its name.

                    ## Path Parameters
                    - name: The name of the tenant to search for

                    ## Response Details
                    - Returns tenant if found
                    - Returns 404 if no tenant exists with that name
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tenant not found with specified name.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<Tenant> findByName(
            @Parameter(description = "The name of the tenant to search for", required = true)
            @PathVariable String name) {
        return tenantService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get Tenants by Status",
            description = """
                    Retrieves all tenants with a specific status.

                    This endpoint returns tenants filtered by their operational status.

                    ## Path Parameters
                    - status: The status to filter by (ACTIVE, INACTIVE, SUSPENDED)

                    ## Response Details
                    - Returns list of tenants with the specified status
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenants retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Tenant>> findByStatus(
            @Parameter(description = "The status to filter tenants by", required = true,
                    schema = @Schema(allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"}))
            @PathVariable TenantStatus status) {
        return ResponseEntity.ok(tenantService.findByStatus(status));
    }

    @Operation(
            summary = "Check Tenant Name Availability",
            description = """
                    Checks if a tenant name is already in use.

                    This endpoint verifies if the provided name is available for creating
                    a new tenant or if it already exists.

                    ## Path Parameters
                    - name: The name to check for availability

                    ## Response Details
                    - Returns true if name is available (not in use)
                    - Returns false if name is already taken
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Name availability checked.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/check/{name}")
    public ResponseEntity<Boolean> existsByName(
            @Parameter(description = "The name to check for availability", required = true)
            @PathVariable String name) {
        return ResponseEntity.ok(tenantService.existsByName(name));
    }

    @Operation(
            summary = "Create Tenant",
            description = """
                    Creates a new tenant in the system.

                    This endpoint registers a new tenant/organization.
                    The tenant name must be unique across the system.

                    ## Request Details
                    - name: Unique name identifier for the tenant
                    - status: Initial status (typically ACTIVE for new tenants)

                    ## Response Details
                    - Returns the created tenant with generated ID
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tenant created successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Tenant name already in use.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<Tenant> save(@RequestBody Tenant tenant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.save(tenant));
    }

    @Operation(
            summary = "Soft Delete Tenant",
            description = """
                    Soft deletes a tenant by setting their deletedAt timestamp.

                    This endpoint performs a soft delete, marking the tenant as deleted
                    without removing their record from the database. This preserves
                    data integrity and allows for audit purposes.

                    Important: Soft-deleting a tenant will also affect all associated
                    users, departments, and hospitals within that tenant.

                    ## Path Parameters
                    - id: The unique identifier of the tenant to delete

                    ## Response Details
                    - Returns 204 No Content on success
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tenant soft deleted successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tenant not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(
            @Parameter(description = "The unique identifier of the tenant to delete", required = true)
            @PathVariable Long id) {
        tenantService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update Tenant",
            description = """
                    Updates an existing tenant's information.

                    This endpoint allows partial update of a tenant's information.
                    Only provided fields will be updated.

                    ## Path Parameters
                    - id: The unique identifier of the tenant

                    ## Request Details
                    - name: New name (must be unique if provided)
                    - status: New status (ACTIVE, INACTIVE, SUSPENDED)

                    ## Response Details
                    - Returns the updated tenant
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant updated successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tenant not found.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Tenant name already in use.",
                    content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Tenant> update(
            @Parameter(description = "The unique identifier of the tenant", required = true)
            @PathVariable Long id,
            @Valid @RequestBody TenantUpdateRequest request) {
        return ResponseEntity.ok(tenantService.update(id, request));
    }
}
