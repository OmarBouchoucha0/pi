package tn.esprit.pi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload for user update")
public class UserUpdateRequest {

    @Schema(description = "User's first name")
    private String firstName;

    @Schema(description = "User's last name")
    private String lastName;

    @Schema(description = "User's phone")
    private String phone;

    @Schema(description = "Role name", allowableValues = {"PATIENT", "DOCTOR", "ADMIN", "NURSE", "LAB"})
    private String role;

    @Schema(description = "User's password (optional for update)")
    private String password;

    @Schema(description = "User status", allowableValues = {"ACTIVE", "LOCKED", "DISABLED"})
    private String status;
}
