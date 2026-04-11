package tn.esprit.pi.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.pi.entity.user.Role;
import tn.esprit.pi.enums.user.RolesEnum;
import tn.esprit.pi.repository.user.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder().id(1L).role(RolesEnum.PATIENT).description("Patient role").build();
    }

    @Test
    void findAll_shouldReturnAllRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(role));

        List<Role> result = roleService.findAll();

        assertThat(result).hasSize(1);
        verify(roleRepository).findAll();
    }

    @Test
    void findById_shouldReturnRoleWhenExists() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(RolesEnum.PATIENT);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByRole_shouldReturnRoleWhenExists() {
        when(roleRepository.findByRole(RolesEnum.PATIENT)).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.findByRole(RolesEnum.PATIENT);

        assertThat(result).isPresent();
    }

    @Test
    void findByRole_shouldReturnEmptyWhenNotFound() {
        when(roleRepository.findByRole(RolesEnum.ADMIN)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.findByRole(RolesEnum.ADMIN);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByRole_shouldReturnTrueWhenExists() {
        when(roleRepository.existsByRole(RolesEnum.PATIENT)).thenReturn(true);

        boolean result = roleService.existsByRole(RolesEnum.PATIENT);

        assertThat(result).isTrue();
    }

    @Test
    void existsByRole_shouldReturnFalseWhenNotExists() {
        when(roleRepository.existsByRole(RolesEnum.ADMIN)).thenReturn(false);

        boolean result = roleService.existsByRole(RolesEnum.ADMIN);

        assertThat(result).isFalse();
    }

    @Test
    void save_shouldPersistRole() {
        Role newRole = Role.builder().role(RolesEnum.DOCTOR).description("Doctor role").build();
        when(roleRepository.save(any(Role.class))).thenReturn(newRole);

        Role result = roleService.save(newRole);

        assertThat(result).isNotNull();
        verify(roleRepository).save(newRole);
    }

    @Test
    void deleteById_shouldCallRepository() {
        doNothing().when(roleRepository).deleteById(1L);

        roleService.deleteById(1L);

        verify(roleRepository).deleteById(1L);
    }
}
