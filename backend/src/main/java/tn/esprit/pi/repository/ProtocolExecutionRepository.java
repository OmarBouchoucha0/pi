// ProtocolExecutionRepository.java
package tn.esprit.pi.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.ProtocolExecution;
import tn.esprit.pi.enums.ExecutionStatus;

@Repository
public interface ProtocolExecutionRepository
        extends JpaRepository<ProtocolExecution, String> {

    List<ProtocolExecution> findByPatientProtocolId(String patientProtocolId);
    long countByPatientProtocolIdAndStatus(String patientProtocolId, ExecutionStatus status);
    long countByPatientProtocolId(String patientProtocolId);
}
