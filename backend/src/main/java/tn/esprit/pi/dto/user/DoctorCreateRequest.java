package tn.esprit.pi.dto.user;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.esprit.pi.enums.user.Gender;

@Data
public class DoctorCreateRequest {

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

    @NotNull(message = "departmentId is required")
    private Long departmentId;

    @NotBlank(message = "licenseNumber is required")
    private String licenseNumber;

    private String specialty;

    private LocalDate dateOfBirth;

    private Gender gender;
}
