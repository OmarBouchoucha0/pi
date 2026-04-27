package tn.esprit.pi.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.user.*;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.security.SecurityUtils;
import tn.esprit.pi.service.user.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user authentication, registration, and profile management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful. Returns JWT tokens.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid email or password. Authentication failed.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input format.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "User Login", description = "Authenticates a user with email and password and sets JWT cookies")
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.login(request, response));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient registered successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input format.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email already registered.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tenant not found.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "Register Patient", description = "Registers a new patient user with tenant association")
    @PostMapping("/register/patient")
    public ResponseEntity<UserResponse> registerPatient(@Valid @RequestBody PatientRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerPatient(request));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully. Sets new cookies.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "Refresh Access Token", description = "Exchanges a valid refresh token cookie for new access and refresh token cookies")
    @PostMapping("/auth/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            throw new BadCredentialsException("Refresh token cookie not found");
        }
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken(refreshToken);
        return ResponseEntity.ok(userService.refresh(refreshRequest, response));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful. Token has been blacklisted.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token format.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input format.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "User Logout", description = "Logs out the user by clearing cookies and blacklisting tokens")
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "Get Current User", description = "Retrieves the profile of the currently authenticated user")
    @GetMapping("/auth/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.toResponse(userId));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access this endpoint.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "Get All Users", description = "Retrieves all users in the system (ADMIN only)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.toResponseList(userService.findAll()));
    }

    @Operation(summary = "Update User", description = "Updates an existing user's information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email already in use.", content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "The unique identifier of the user", required = true) @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Patients can only view their own profile.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "Get User by ID", description = "Retrieves a user by their unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @Parameter(description = "The unique identifier of the user", required = true) @PathVariable Long id) {
        String role = SecurityUtils.getCurrentUserRole();
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if ("ROLE_PATIENT".equals(role) && !id.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userService.toResponse(id));
    }

    @Operation(summary = "Get User by Email", description = "Retrieves a user by their email address. Only accessible by ADMIN and DOCTOR roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN or DOCTOR roles can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found with specified email.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<UserResponse> findByEmail(
            @Parameter(description = "The email address to search for", required = true) @PathVariable String email) {
        return userService.findByEmail(email)
                .map(userService::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get Users by Tenant", description = "Retrieves all users belonging to a specific tenant/organization. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findByTenantId(
            @Parameter(description = "The unique identifier of the tenant", required = true) @PathVariable Long tenantId) {
        return ResponseEntity.ok(userService.toResponseList(userService.findByTenantId(tenantId)));
    }

    @Operation(summary = "Get Users by Role", description = "Retrieves all users with a specific role. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findByRoleId(
            @Parameter(description = "The unique identifier of the role", required = true) @PathVariable Long roleId) {
        return ResponseEntity.ok(userService.toResponseList(userService.findByRoleId(roleId)));
    }

    @Operation(summary = "Get Users by Department", description = "Retrieves all users belonging to a specific department. Accessible by ADMIN and DOCTOR roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN or DOCTOR roles can access.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<UserResponse>> findByDepartmentId(
            @Parameter(description = "The unique identifier of the department", required = true) @PathVariable Long departmentId) {
        return ResponseEntity.ok(userService.toResponseList(userService.findByDepartmentId(departmentId)));
    }

    @Operation(summary = "Get All Patients", description = "Retrieves all patients in the system. ADMIN sees all, DOCTOR sees only patients in their hospital.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN or DOCTOR roles can access.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/patients")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<UserResponse>> findAllPatients(Authentication authentication) {
        String role = SecurityUtils.getCurrentUserRole();

        if (role == null || "ROLE_ADMIN".equals(role)) {
            return ResponseEntity.ok(userService.toResponseList(userService.findAllPatients()));
        }

        Long doctorId = SecurityUtils.getCurrentUserId();
        if (doctorId == null) {
            return ResponseEntity.ok(userService.toResponseList(userService.findAllPatients()));
        }

        User doctor = userService.getCurrentUser(doctorId);
        if (doctor.getDepartment() != null && doctor.getDepartment().getHospital() != null) {
            Long hospitalId = doctor.getDepartment().getHospital().getId();
            return ResponseEntity.ok(userService.toResponseList(userService.getPatientsByHospitalId(hospitalId)));
        }

        return ResponseEntity.ok(List.of());
    }

    @Operation(summary = "Get All Doctors", description = "Retrieves all doctors in the system. Accessible without authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/doctors")
    public ResponseEntity<List<UserResponse>> findAllDoctors() {
        return ResponseEntity.ok(userService.toResponseList(userService.findAllDoctors()));
    }

    @Operation(summary = "Get All Admins", description = "Retrieves all administrators in the system. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admins retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAllAdmins() {
        return ResponseEntity.ok(userService.toResponseList(userService.findAllAdmins()));
    }

    @Operation(summary = "Get Patient by Medical Record Number", description = "Retrieves a patient by their unique medical record number. Only accessible by ADMIN and DOCTOR roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN or DOCTOR roles can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Patient not found with specified medical record number.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/medical-record/{medicalRecordNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<UserResponse> findByMedicalRecordNumber(
            @Parameter(description = "The patient's unique medical record number", required = true) @PathVariable String medicalRecordNumber) {
        return userService.findByMedicalRecordNumber(medicalRecordNumber)
                .map(userService::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get Doctor by License Number", description = "Retrieves a doctor by their professional license number. Only accessible by ADMIN and DOCTOR roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN or DOCTOR roles can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Doctor not found with specified license number.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/license/{licenseNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<UserResponse> findByLicenseNumber(
            @Parameter(description = "The doctor's professional license number", required = true) @PathVariable String licenseNumber) {
        return userService.findByLicenseNumber(licenseNumber)
                .map(userService::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Check Email Availability", description = "Checks if an email address is already registered. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email availability checked.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/check-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> existsByEmail(
            @Parameter(description = "The email address to check for availability", required = true) @PathVariable String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @Operation(summary = "Create/Update User", description = "Creates a new user or updates an existing user. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "200", description = "User updated successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email already in use.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> save(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @Operation(summary = "Soft Delete User", description = "Soft deletes a user by setting their deletedAt timestamp. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User soft deleted successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDelete(
            @Parameter(description = "The unique identifier of the user to delete", required = true) @PathVariable Long id) {
        userService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Record Successful Login", description = "Records a successful login event for tracking purposes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login recorded successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/{id}/login")
    public ResponseEntity<Void> recordLogin(
            @Parameter(description = "The unique identifier of the user", required = true) @PathVariable Long id) {
        userService.recordLogin(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Record Failed Login Attempt", description = "Records a failed login attempt for security tracking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Failed login recorded successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/{id}/failed-login")
    public ResponseEntity<Void> recordFailedLogin(
            @Parameter(description = "The unique identifier of the user", required = true) @PathVariable Long id) {
        userService.recordFailedLogin(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Create Patient", description = "Creates a new patient user account. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email or medical record number already in use.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tenant or role not found.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/patients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createPatient(@Valid @RequestBody PatientCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createPatient(request));
    }

    @Operation(summary = "Update Patient", description = "Updates an existing patient's information. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Patient not found.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email or medical record number already in use.", content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/patients/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updatePatient(
            @Parameter(description = "The unique identifier of the patient", required = true) @PathVariable Long id,
            @Valid @RequestBody PatientUpdateRequest request) {
        return ResponseEntity.ok(userService.updatePatient(id, request));
    }

    @Operation(summary = "Create Doctor", description = "Creates a new doctor user account. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email or license number already in use.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tenant, role, or department not found.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/doctors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createDoctor(@Valid @RequestBody DoctorCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createDoctor(request));
    }

    @Operation(summary = "Update Doctor", description = "Updates an existing doctor's information. Doctors can update their own profile, ADMINs can update any.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Doctors can only update their own profile.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Doctor not found.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email or license number already in use.", content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/doctors/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<UserResponse> updateDoctor(
            @Parameter(description = "The unique identifier of the doctor", required = true) @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateRequest request) {
        String role = SecurityUtils.getCurrentUserRole();
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if ("ROLE_DOCTOR".equals(role) && !id.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userService.updateDoctor(id, request));
    }

    @Operation(summary = "Create Admin", description = "Creates a new administrator user account. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin created successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email already in use.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tenant or role not found.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createAdmin(request));
    }

    @Operation(summary = "Update Admin", description = "Updates an existing administrator's information. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin updated successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Admin not found.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email already in use.", content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/admins/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateAdmin(
            @Parameter(description = "The unique identifier of the admin", required = true) @PathVariable Long id,
            @Valid @RequestBody AdminUpdateRequest request) {
        return ResponseEntity.ok(userService.updateAdmin(id, request));
    }
}
