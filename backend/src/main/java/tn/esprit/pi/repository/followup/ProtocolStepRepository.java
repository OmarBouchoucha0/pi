// ProtocolStepRepository.java
package tn.esprit.pi.repository.followup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.followup.ProtocolStep;

@Repository
public interface ProtocolStepRepository
        extends JpaRepository<ProtocolStep, String> {

    List<ProtocolStep> findByProtocolIdOrderByDayNumber(String protocolId);
    void deleteByProtocolId(String protocolId);
}
