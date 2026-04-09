package tn.esprit.pi.dto.user;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.user.RolesEnum;

@Data
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private RolesEnum role;
}
