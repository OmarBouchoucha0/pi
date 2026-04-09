package tn.esprit.pi.service.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.entity.user.Role;
import tn.esprit.pi.enums.user.RolesEnum;
import tn.esprit.pi.repository.user.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  public List<Role> findAll() {
    return roleRepository.findAll();
  }

  public Optional<Role> findById(Long id) {
    return roleRepository.findById(id);
  }

  public Optional<Role> findByRole(RolesEnum role) {
    return roleRepository.findByRole(role);
  }

  public boolean existsByRole(RolesEnum role) {
    return roleRepository.existsByRole(role);
  }

  public Role save(Role role) {
    return roleRepository.save(role);
  }

  public void deleteById(Long id) {
    roleRepository.deleteById(id);
  }
}
