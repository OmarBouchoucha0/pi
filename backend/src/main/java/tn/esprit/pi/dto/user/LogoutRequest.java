package tn.esprit.pi.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotBlank(message = "refreshToken is required")
    private String refreshToken;
}
