package tn.esprit.pi.dto.followup;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProtocolRequest {

    @NotNull
    private Long patientId;

    @NotNull
    private String protocolId;

    @NotNull
    private Long tenantId;

    @NotNull
    private LocalDate startDate;
}
