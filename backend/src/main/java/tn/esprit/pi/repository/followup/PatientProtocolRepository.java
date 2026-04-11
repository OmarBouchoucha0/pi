// PatientProtocolRepository.java
package tn.esprit.pi.repository.followup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.followup.PatientProtocol;
@Repository
public interface PatientProtocolRepository
        extends JpaRepository<PatientProtocol, String> {

    List<PatientProtocol> findByPatientId(Long patientId);
    List<PatientProtocol> findByTenantId(Long tenantId);
    List<PatientProtocol> findByRiskFlagTrue();
}
