package tn.esprit.pi.dto.user;

import lombok.Data;
import tn.esprit.pi.enums.user.TenantStatus;

@Data
public class TenantUpdateRequest {

    private String name;

    private TenantStatus status;
}
