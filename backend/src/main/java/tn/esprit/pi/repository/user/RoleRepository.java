package tn.esprit.pi.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.pi.entity.user.Role;
import tn.esprit.pi.enums.user.RolesEnum;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(RolesEnum role);

    boolean existsByRole(RolesEnum role);
}
