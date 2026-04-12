package tn.esprit.pi.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Request payload for admin user creation")
public class UserCreateRequest {

    @Schema(description = "User's email", example = "john.doe@example.com")
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @Schema(description = "User's password")
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    @Schema(description = "User's first name")
    @NotBlank(message = "firstName is required")
    private String firstName;

    @Schema(description = "User's last name")
    @NotBlank(message = "lastName is required")
    private String lastName;

    @Schema(description = "User's phone")
    @NotBlank(message = "phone is required")
    private String phone;

    @Schema(description = "Role name", example = "PATIENT", allowableValues = {"PATIENT", "DOCTOR", "ADMIN", "NURSE", "LAB"})
    @NotBlank(message = "role is required")
    private String role;

    @Schema(description = "Tenant ID")
    @NotNull(message = "tenantId is required")
    private Long tenantId;
}
