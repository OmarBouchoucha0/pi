package tn.esprit.pi.dto.vital;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VitalStatsResponse {
    private Long patientId;
    private Object type;
    private Double min;
    private Double max;
    private Double avg;
    private Long count;
    private LocalDateTime from;
    private LocalDateTime to;
}
