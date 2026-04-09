// FollowupProtocolRepository.java
package tn.esprit.pi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.FollowupProtocol;

@Repository
public interface FollowupProtocolRepository
        extends JpaRepository<FollowupProtocol, String> {

    List<FollowupProtocol> findByIsActiveTrue();
    boolean existsByNameAndVersion(String name, Integer version);
}
