package tn.esprit.pi.dto.user;

import lombok.Data;
import tn.esprit.pi.enums.user.TenantStatus;

@Data
public class HospitalUpdateRequest {

    private String name;

    private TenantStatus status;

    private Long tenantId;
}
