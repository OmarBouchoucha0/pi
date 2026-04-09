package tn.esprit.pi.dto.recovery;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecoveryCheckInRequest {

    @NotNull(message = "planId is required")
    private Long planId;

    @NotNull(message = "patientId is required")
    private Long patientId;

    @NotNull(message = "actualVitals is required")
    private Map<String, Double> actualVitals;

    private String patientNotes;
}
