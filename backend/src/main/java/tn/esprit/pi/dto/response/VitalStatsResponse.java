package tn.esprit.pi.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.VitalType;

@Data
@Builder
public class VitalStatsResponse {
    private Long patientId;
    private VitalType type;
    private Double min;
    private Double max;
    private Double avg;
    private Long count;
    private LocalDateTime from;
    private LocalDateTime to;
}
