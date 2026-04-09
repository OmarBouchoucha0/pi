package tn.esprit.pi.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.user.AdminPrivilege;
import tn.esprit.pi.enums.user.Gender;
import tn.esprit.pi.enums.user.RolesEnum;
import tn.esprit.pi.enums.user.UserStatus;

@Data
@Builder
@Schema(description = "Response payload containing user information")
public class UserResponse {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's phone number", example = "+21612345678")
    private String phone;

    @Schema(description = "User's date of birth", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "User's gender", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"})
    private Gender gender;

    @Schema(description = "Current status of the user account", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private UserStatus status;

    @Schema(description = "Timestamp of the user's last login", example = "2024-01-15T10:30:00")
    private LocalDateTime lastLogin;

    @Schema(description = "Timestamp when the user account was created", example = "2024-01-01T08:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "ID of the tenant/organization the user belongs to", example = "1")
    private Long tenantId;

    @Schema(description = "Name of the tenant/organization", example = "City Hospital")
    private String tenantName;

    @Schema(description = "ID of the user's role", example = "1")
    private Long roleId;

    @Schema(description = "User's role in the system", example = "PATIENT", allowableValues = {"PATIENT", "DOCTOR", "ADMIN"})
    private RolesEnum role;

    @Schema(description = "Patient's unique medical record number", example = "MRN-2024-001")
    private String medicalRecordNumber;

    @Schema(description = "Patient's blood type", example = "A+", allowableValues = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"})
    private String bloodType;

    @Schema(description = "Known allergies (comma-separated)", example = "Penicillin, Pollen")
    private String allergies;

    @Schema(description = "Chronic medical conditions (comma-separated)", example = "Diabetes Type 2, Hypertension")
    private String chronicConditions;

    @Schema(description = "Emergency contact name", example = "Jane Doe")
    private String emergencyContactName;

    @Schema(description = "Emergency contact phone number", example = "+21698765432")
    private String emergencyContactPhone;

    @Schema(description = "ID of the department the user belongs to", example = "1")
    private Long departmentId;

    @Schema(description = "Name of the department", example = "Cardiology")
    private String departmentName;

    @Schema(description = "Doctor's professional license number", example = "MD-2024-001")
    private String licenseNumber;

    @Schema(description = "Doctor's medical specialty", example = "Cardiology")
    private String specialty;

    @Schema(description = "Admin's privilege level", example = "SUPER_ADMIN", allowableValues = {"SUPER_ADMIN", "ADMIN", "STAFF"})
    private AdminPrivilege privilegeLevel;
}
