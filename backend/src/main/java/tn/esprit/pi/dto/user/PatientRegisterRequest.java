package tn.esprit.pi.dto.user;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tn.esprit.pi.enums.user.Gender;

@Data
@Schema(description = "Request payload for patient self-registration")
public class PatientRegisterRequest {

    @Schema(description = "User's email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @Schema(description = "User's password", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    @Schema(description = "User's first name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "firstName is required")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "lastName is required")
    private String lastName;

    @Schema(description = "User's phone number", example = "+21612345678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "phone is required")
    private String phone;

    @Schema(description = "ID of the tenant/organization to register under", example = "1")
    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @Schema(description = "User's date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Schema(description = "User's gender", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"})
    private Gender gender;

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
}
