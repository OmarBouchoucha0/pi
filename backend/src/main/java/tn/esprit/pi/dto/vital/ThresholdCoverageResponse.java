package tn.esprit.pi.dto.vital;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThresholdCoverageResponse {
    private Long tenantId;
    private int totalTypes;
    private int coveredTypes;
    private int coveragePercent;
    private List<?> missing;
    private List<?> covered;
}
