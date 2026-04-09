package tn.esprit.pi.controller.user;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tn.esprit.pi.entity.user.Role;
import tn.esprit.pi.enums.user.RolesEnum;
import tn.esprit.pi.service.user.RoleService;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setRole(RolesEnum.PATIENT);
        role.setDescription("Patient role");
    }

    @Test
    void findAll_shouldReturnRoles() {
        when(roleService.findAll()).thenReturn(List.of(role));

        ResponseEntity<List<Role>> result = roleController.findAll();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void findById_shouldReturnRole() {
        when(roleService.findById(1L)).thenReturn(Optional.of(role));

        ResponseEntity<Role> result = roleController.findById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void findById_shouldReturnNotFound() {
        when(roleService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Role> result = roleController.findById(99L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findByRole_shouldReturnRole() {
        when(roleService.findByRole(RolesEnum.PATIENT)).thenReturn(Optional.of(role));

        ResponseEntity<Role> result = roleController.findByRole(RolesEnum.PATIENT);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void save_shouldCreateRole() {
        when(roleService.save(any(Role.class))).thenReturn(role);

        ResponseEntity<Role> result = roleController.save(role);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void deleteById_shouldReturnNoContent() {
        doNothing().when(roleService).deleteById(1L);

        ResponseEntity<Void> result = roleController.deleteById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(roleService).deleteById(1L);
    }
}
