package tn.esprit.pi.dto.recovery;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.recovery.RecoveryStatus;

@Data
@Builder
public class RecoveryTrajectoryResponse {
    private Long planId;
    private Long patientId;
    private String patientName;
    private Integer plannedDurationDays;
    private Integer daysSinceDischarge;
    private RecoveryStatus currentStatus;
    private Double currentReturnScore;
    private Map<String, Double[]> expectedCurve;
    private Map<String, Double[]> actualCurve;
    private Double[] deviationByDay;
    private List<RecoveryCheckInResponse> checkIns;
}
