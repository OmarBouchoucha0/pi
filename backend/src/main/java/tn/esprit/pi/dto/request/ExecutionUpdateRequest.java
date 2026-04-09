// request/ExecutionUpdateRequest.java
package tn.esprit.pi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.pi.enums.ExecutionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionUpdateRequest {

    @NotNull
    private ExecutionStatus status;

    private Integer delayMinutes;
}
