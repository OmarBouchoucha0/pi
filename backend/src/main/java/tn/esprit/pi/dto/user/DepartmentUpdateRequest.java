package tn.esprit.pi.dto.user;

import lombok.Data;

@Data
public class DepartmentUpdateRequest {

    private String name;

    private String description;

    private Long tenantId;

    private Long hospitalId;
}
