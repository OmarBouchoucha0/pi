package tn.esprit.pi.dto.request;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecoveryCheckInRequest {

    @NotNull(message = "planId is required")
    private Long planId;

    @NotNull(message = "patientId is required")
    private Long patientId;

    // Actual vitals submitted by patient from home: { "TEMPERATURE": 37.9, "HEART_RATE": 85 }
    @NotNull(message = "actualVitals is required")
    private Map<String, Double> actualVitals;

    // Patient's optional subjective note ("I feel dizzy", "Better than yesterday")
    private String patientNotes;
}
