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
import tn.esprit.pi.dto.user.DepartmentUpdateRequest;
import tn.esprit.pi.entity.user.Department;
import tn.esprit.pi.service.user.DepartmentService;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Department Management", description = "APIs for managing departments within hospitals")
@SecurityRequirement(name = "Bearer Authentication")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "Get All Departments", description = "Retrieves a list of all active departments in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<Department>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @Operation(summary = "Get Department by ID", description = "Retrieves a specific department by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Department not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Department> findById(
            @Parameter(description = "The unique identifier of the department", required = true)
            @PathVariable Long id) {
        return departmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get Departments by Tenant", description = "Retrieves all departments belonging to a specific tenant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Department>> findByTenantId(
            @Parameter(description = "The unique identifier of the tenant", required = true)
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(departmentService.findByTenantId(tenantId));
    }

    @Operation(summary = "Get Departments by Hospital", description = "Retrieves all departments within a specific hospital.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<Department>> findByHospitalId(
            @Parameter(description = "The unique identifier of the hospital", required = true)
            @PathVariable Long hospitalId) {
        return ResponseEntity.ok(departmentService.findByHospitalId(hospitalId));
    }

    @Operation(summary = "Create Department", description = "Creates a new department in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Department created successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Department name already exists in this hospital.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<Department> save(@RequestBody Department department) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.save(department));
    }

    @Operation(summary = "Soft Delete Department", description = "Soft deletes a department by setting their deletedAt timestamp.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Department soft deleted successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Department not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(
            @Parameter(description = "The unique identifier of the department to delete", required = true)
            @PathVariable Long id) {
        departmentService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update Department", description = "Updates an existing department's information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department updated successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Department not found.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Department name already in use.",
                    content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Department> update(
            @Parameter(description = "The unique identifier of the department", required = true)
            @PathVariable Long id,
            @Valid @RequestBody DepartmentUpdateRequest request) {
        return ResponseEntity.ok(departmentService.update(id, request));
    }
}
