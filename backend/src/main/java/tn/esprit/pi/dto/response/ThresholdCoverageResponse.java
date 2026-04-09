package tn.esprit.pi.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.VitalType;

@Data
@Builder
public class ThresholdCoverageResponse {
    private Long tenantId;
    private int totalTypes;
    private int coveredTypes;
    private int coveragePercent;
    private List<VitalType> missing;
    private List<VitalType> covered;
}
