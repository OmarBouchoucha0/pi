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
@Tag(name = "Tenant Management", description = "APIs for managing tenants (organizations/hospitals)")
@SecurityRequirement(name = "Bearer Authentication")
public class TenantController {

    private final TenantService tenantService;

    @Operation(summary = "Get All Tenants", description = "Retrieves a list of all active tenants in the system.")
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

    @Operation(summary = "Get Tenant by ID", description = "Retrieves a specific tenant by their unique identifier.")
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

    @Operation(summary = "Get Tenant by Name", description = "Retrieves a tenant by their name.")
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

    @Operation(summary = "Get Tenants by Status", description = "Retrieves all tenants with a specific status.")
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

    @Operation(summary = "Check Tenant Name Availability", description = "Checks if a tenant name is already in use.")
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

    @Operation(summary = "Create Tenant", description = "Creates a new tenant in the system.")
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

    @Operation(summary = "Soft Delete Tenant", description = "Soft deletes a tenant by setting their deletedAt timestamp.")
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

    @Operation(summary = "Update Tenant", description = "Updates an existing tenant's information.")
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
