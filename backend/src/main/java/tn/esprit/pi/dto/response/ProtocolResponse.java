// response/ProtocolResponse.java
package tn.esprit.pi.dto.response;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolResponse {
    private String id;
    private String name;
    private Integer durationDays;
    private Integer version;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<ProtocolStepResponse> steps;
}
