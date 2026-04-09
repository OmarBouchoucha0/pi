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
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.entity.user.Role;
import tn.esprit.pi.enums.user.RolesEnum;
import tn.esprit.pi.service.user.RoleService;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = """
        APIs for managing user roles in the MeddiFollow system.

        Roles define the permissions and access levels of users within the system.
        Each user is assigned a single role that determines their capabilities.

        ## System Roles
        - **PATIENT**: Basic user role for patients. Can view their own profile.
        - **DOCTOR**: Medical staff role. Can view patient records within their hospital.
        - **ADMIN**: Administrator role. Full access to all system features.

        ## Role Properties
        - **role**: The role identifier (PATIENT, DOCTOR, ADMIN)
        - **description**: Human-readable description of the role's purpose

        ## Common Use Cases
        - View all available roles
        - Get role details by ID or role name
        - Create custom roles (if needed)
        - Delete roles (with caution - affects all users with that role)
        """)
@SecurityRequirement(name = "Bearer Authentication")
public class RoleController {

    private final RoleService roleService;

    @Operation(
            summary = "Get All Roles",
            description = """
                    Retrieves a list of all roles in the system.

                    This endpoint returns all available roles, including system-defined
                    roles like PATIENT, DOCTOR, and ADMIN.

                    ## Response Details
                    - Returns list of all roles
                    - Each role includes its identifier and description
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<Role>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @Operation(
            summary = "Get Role by ID",
            description = """
                    Retrieves a specific role by their unique identifier.

                    This endpoint returns role details if found.

                    ## Path Parameters
                    - id: The unique identifier of the role

                    ## Response Details
                    - Returns role details including role type and description
                    - Returns 404 if role not found
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Role not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Role> findById(
            @Parameter(description = "The unique identifier of the role", required = true)
            @PathVariable Long id) {
        return roleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get Role by Name",
            description = """
                    Retrieves a role by its name/enumeration value.

                    This endpoint searches for a role using its enum value.

                    ## Path Parameters
                    - role: The role name (PATIENT, DOCTOR, or ADMIN)

                    ## Response Details
                    - Returns role if found
                    - Returns 404 if no role exists with that name
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Role not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/name/{role}")
    public ResponseEntity<Role> findByRole(
            @Parameter(description = "The role name to search for", required = true,
                    schema = @Schema(allowableValues = {"PATIENT", "DOCTOR", "ADMIN"}))
            @PathVariable RolesEnum role) {
        return roleService.findByRole(role)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Role",
            description = """
                    Creates a new role in the system.

                    This endpoint registers a new role. Typically used for creating
                    custom roles beyond the system defaults.

                    ## Request Details
                    - role: Unique role identifier (enum value)
                    - description: Human-readable description

                    ## Response Details
                    - Returns the created role with generated ID
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Role already exists.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<Role> save(@RequestBody Role role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.save(role));
    }

    @Operation(
            summary = "Delete Role",
            description = """
                    Permanently deletes a role from the system.

                    This endpoint permanently removes a role. Use with caution as it
                    will affect all users assigned to this role.

                    Warning: Deleting a role that is assigned to users may cause
                    authentication and authorization issues.

                    ## Path Parameters
                    - id: The unique identifier of the role to delete

                    ## Response Details
                    - Returns 204 No Content on success
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role deleted successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Role not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "The unique identifier of the role to delete", required = true)
            @PathVariable Long id) {
        roleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
