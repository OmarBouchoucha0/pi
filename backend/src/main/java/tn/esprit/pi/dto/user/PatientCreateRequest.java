package tn.esprit.pi.dto.user;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.esprit.pi.enums.user.Gender;

@Data
public class PatientCreateRequest {

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "firstName is required")
    private String firstName;

    @NotBlank(message = "lastName is required")
    private String lastName;

    @NotBlank(message = "phone is required")
    private String phone;

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String medicalRecordNumber;

    private String bloodType;

    private String allergies;

    private String chronicConditions;

    private String emergencyContactName;

    private String emergencyContactPhone;
}
