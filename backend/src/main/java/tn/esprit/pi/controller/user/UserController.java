package tn.esprit.pi.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import tn.esprit.pi.dto.user.*;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.security.SecurityUtils;
import tn.esprit.pi.service.user.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = """
        APIs for managing users in the MeddiFollow hospital system.

        This controller provides endpoints for user authentication (login, refresh, logout),
        user registration (patients, doctors, admins), and user profile management.

        ## Authentication Flow
        1. Use `/api/users/auth/login` to authenticate with email and password
        2. Receive JWT access and refresh tokens in response
        3. Include access token in Authorization header for subsequent requests
        4. Use `/api/users/auth/refresh` to get new access token when expired
        5. Use `/api/users/auth/logout` to invalidate refresh token
        """)
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful. Returns JWT tokens.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid email or password. Authentication failed.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input format.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient registered successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input format.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email already registered.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tenant not found.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/register/patient")
    public ResponseEntity<UserResponse> registerPatient(@Valid @RequestBody PatientRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerPatient(request));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully. Returns new access token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input format.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/auth/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful. Token has been blacklisted.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token format.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input format.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        userService.logout(request);
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json"))
    })
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
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.toResponseList(userService.findAll()));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Patients can only view their own profile.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json"))
    })
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

    @Operation(summary = "Get User by Email", description = """
            Retrieves a user by their email address.

            This endpoint searches for a user with the specified email.
            Only accessible by ADMIN and DOCTOR roles.

            ## Path Parameters
            - email: The email address to search for

            ## Response Details
            - Returns user if found
            - Returns 404 if no user exists with that email
            """)
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

    @Operation(summary = "Get Users by Tenant", description = """
            Retrieves all users belonging to a specific tenant/organization.

            This endpoint returns all active users within a tenant.
            Only accessible by ADMIN role.

            ## Path Parameters
            - tenantId: The unique identifier of the tenant

            ## Response Details
            - Returns list of users in the tenant
            """)
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

    @Operation(summary = "Get Users by Role", description = """
            Retrieves all users with a specific role.

            This endpoint returns all users assigned to a specific role.
            Only accessible by ADMIN role.

            ## Path Parameters
            - roleId: The unique identifier of the role

            ## Response Details
            - Returns list of users with the specified role
            """)
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

    @Operation(summary = "Get Users by Department", description = """
            Retrieves all users belonging to a specific department.

            This endpoint returns all users (typically doctors) in a department.
            Accessible by ADMIN and DOCTOR roles.

            ## Path Parameters
            - departmentId: The unique identifier of the department

            ## Response Details
            - Returns list of users in the department
            """)
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

    @Operation(summary = "Get All Patients", description = """
            Retrieves all patients in the system.

            This endpoint returns patients based on the caller's role:
            - ADMIN: Returns all patients in the system
            - DOCTOR: Returns only patients in their hospital

            ## Response Details
            - Each patient includes medical record number and other patient-specific fields
            """)
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

    @Operation(summary = "Get All Doctors", description = """
            Retrieves all doctors in the system.

            This endpoint returns all users with doctor role.
            Accessible without authentication.

            ## Response Details
            - Each doctor includes license number and specialty
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/doctors")
    public ResponseEntity<List<UserResponse>> findAllDoctors() {
        return ResponseEntity.ok(userService.toResponseList(userService.findAllDoctors()));
    }

    @Operation(summary = "Get All Admins", description = """
            Retrieves all administrators in the system.

            This endpoint returns all users with admin role.
            Only accessible by ADMIN role.

            ## Response Details
            - Each admin includes their privilege level
            """)
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

    @Operation(summary = "Get Patient by Medical Record Number", description = """
            Retrieves a patient by their unique medical record number.

            This endpoint searches for a patient using their medical record number.
            Only accessible by ADMIN and DOCTOR roles.

            ## Path Parameters
            - medicalRecordNumber: The patient's unique medical record identifier

            ## Response Details
            - Returns patient if found
            - Returns 404 if no patient exists with that medical record number
            """)
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

    @Operation(summary = "Get Doctor by License Number", description = """
            Retrieves a doctor by their professional license number.

            This endpoint searches for a doctor using their license number.
            Only accessible by ADMIN and DOCTOR roles.

            ## Path Parameters
            - licenseNumber: The doctor's professional license identifier

            ## Response Details
            - Returns doctor if found
            - Returns 404 if no doctor exists with that license number
            """)
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

    @Operation(summary = "Check Email Availability", description = """
            Checks if an email address is already registered in the system.

            This endpoint verifies if the provided email is available for registration.
            Only accessible by ADMIN role.

            ## Path Parameters
            - email: The email address to check

            ## Response Details
            - Returns true if email is available (not registered)
            - Returns false if email is already in use
            """)
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

    @Operation(summary = "Create/Update User", description = """
            Creates a new user or updates an existing user.

            This endpoint creates a new user or updates an existing one.
            The request body should contain the complete user object.
            Only accessible by ADMIN role.

            ## Request Details
            - For new users: ID should be null
            - For updates: ID should be set to the existing user ID
            - Password should be plain text (will be hashed)

            ## Response Details
            - Returns the created/updated user
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "200", description = "User updated successfully.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only ADMIN role can access.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict. Email already in use.", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> save(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @Operation(summary = "Soft Delete User", description = """
            Soft deletes a user by setting their deletedAt timestamp.

            This endpoint performs a soft delete, marking the user as deleted
            without removing their record from the database. This preserves
            data integrity and allows for audit purposes.
            Only accessible by ADMIN role.

            ## Path Parameters
            - id: The unique identifier of the user to delete

            ## Response Details
            - Returns 204 No Content on success
            """)
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

    @Operation(summary = "Record Successful Login", description = """
            Records a successful login event for tracking purposes.

            This endpoint updates the user's last login timestamp and resets
            failed login attempts counter.

            ## Path Parameters
            - id: The unique identifier of the user
            """)
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

    @Operation(summary = "Record Failed Login Attempt", description = """
            Records a failed login attempt for security tracking.

            This endpoint increments the user's failed login attempts counter,
            which can be used for account lockout policies.

            ## Path Parameters
            - id: The unique identifier of the user
            """)
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

    @Operation(summary = "Create Patient", description = """
            Creates a new patient user account.

            This endpoint registers a new patient in the system with their
            personal information and medical details. Only accessible by ADMIN role.

            ## Request Details
            - Email must be unique across the system
            - Medical record number must be unique (if provided)
            - Password will be hashed before storage

            ## Patient-Specific Fields
            - medicalRecordNumber: Unique identifier for patient records
            - bloodType: Patient's blood type
            - allergies: Known allergies (comma-separated)
            - chronicConditions: Existing chronic conditions
            - emergencyContactName: Emergency contact name
            - emergencyContactPhone: Emergency contact phone
            """)
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

    @Operation(summary = "Update Patient", description = """
            Updates an existing patient's information.

            This endpoint allows partial update of a patient's information.
            Only accessible by ADMIN role.

            ## Path Parameters
            - id: The unique identifier of the patient

            ## Request Details
            - All fields are optional - only provided fields will be updated
            - To change password, provide the new password in plain text
            """)
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

    @Operation(summary = "Create Doctor", description = """
            Creates a new doctor user account.

            This endpoint registers a new doctor in the system with their
            professional information. Only accessible by ADMIN role.

            ## Request Details
            - Email must be unique across the system
            - License number must be unique (if provided)
            - Password will be hashed before storage
            - Department must exist

            ## Doctor-Specific Fields
            - licenseNumber: Professional medical license number
            - specialty: Medical specialty (e.g., Cardiology, Neurology)
            """)
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

    @Operation(summary = "Update Doctor", description = """
            Updates an existing doctor's information.

            This endpoint allows partial update of a doctor's information.
            Doctors can update their own profile, while ADMINs can update any doctor.

            ## Path Parameters
            - id: The unique identifier of the doctor

            ## Request Details
            - All fields are optional - only provided fields will be updated
            - To change department, provide the new department ID
            """)
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

    @Operation(summary = "Create Admin", description = """
            Creates a new administrator user account.

            This endpoint registers a new admin in the system with their
            privilege level. Only accessible by ADMIN role.

            ## Request Details
            - Email must be unique across the system
            - Password will be hashed before storage

            ## Admin-Specific Fields
            - privilegeLevel: The admin's access level (e.g., SUPER_ADMIN, ADMIN)
            """)
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

    @Operation(summary = "Update Admin", description = """
            Updates an existing administrator's information.

            This endpoint allows partial update of an admin's information.
            Only accessible by ADMIN role.

            ## Path Parameters
            - id: The unique identifier of the admin

            ## Request Details
            - All fields are optional - only provided fields will be updated
            - To change privilege level, provide the new level
            """)
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
