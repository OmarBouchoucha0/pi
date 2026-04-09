package tn.esprit.pi.dto.response;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.RecoveryStatus;

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

    // Per vital type: list of expected values indexed by day
    private Map<String, Double[]> expectedCurve;

    // Per vital type: list of actual values indexed by day (null if no check-in that day)
    private Map<String, Double[]> actualCurve;

    // Per day: composite deviation score (null if no check-in)
    private Double[] deviationByDay;

    // All individual check-ins
    private List<RecoveryCheckInResponse> checkIns;
}
