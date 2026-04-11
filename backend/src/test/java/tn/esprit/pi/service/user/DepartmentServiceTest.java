package tn.esprit.pi.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import tn.esprit.pi.dto.user.DepartmentUpdateRequest;
import tn.esprit.pi.entity.user.Department;
import tn.esprit.pi.entity.user.Hospital;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.DepartmentRepository;
import tn.esprit.pi.repository.user.HospitalRepository;
import tn.esprit.pi.repository.user.TenantRepository;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Tenant tenant;
    private Hospital hospital;
    private Department department;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder().id(1L).name("Test Tenant").build();
        hospital = Hospital.builder().id(1L).name("Test Hospital").tenant(tenant).build();
        department = Department.builder().id(1L).name("Cardiology").tenant(tenant).hospital(hospital).build();
    }

    @Test
    void findAll_shouldReturnNonDeletedDepartments() {
        when(departmentRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(department));

        List<Department> result = departmentService.findAll();

        assertThat(result).hasSize(1);
        verify(departmentRepository).findAllByDeletedAtIsNull();
    }

    @Test
    void findById_shouldReturnDepartmentWhenExists() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Optional<Department> result = departmentService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Cardiology");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Department> result = departmentService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByTenantId_shouldReturnDepartments() {
        when(departmentRepository.findByTenantIdAndDeletedAtIsNull(1L)).thenReturn(List.of(department));

        List<Department> result = departmentService.findByTenantId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByHospitalId_shouldReturnDepartments() {
        when(departmentRepository.findByHospitalIdAndDeletedAtIsNull(1L)).thenReturn(List.of(department));

        List<Department> result = departmentService.findByHospitalId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByNameAndTenantId_shouldReturnDepartmentWhenExists() {
        when(departmentRepository.findByNameAndTenantIdAndDeletedAtIsNull("Cardiology", 1L)).thenReturn(Optional.of(department));

        Optional<Department> result = departmentService.findByNameAndTenantId("Cardiology", 1L);

        assertThat(result).isPresent();
    }

    @Test
    void existsByNameAndTenantId_shouldReturnTrueWhenExists() {
        when(departmentRepository.existsByNameAndTenantIdAndDeletedAtIsNull("Cardiology", 1L)).thenReturn(true);

        boolean result = departmentService.existsByNameAndTenantId("Cardiology", 1L);

        assertThat(result).isTrue();
    }

    @Test
    void save_shouldPersistDepartment() {
        Department newDept = Department.builder().name("Cardiology").tenant(tenant).hospital(hospital).build();
        when(departmentRepository.findByNameAndTenantIdAndDeletedAtIsNull(anyString(), anyLong())).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        Department result = departmentService.save(newDept);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(newDept);
    }

    @Test
    void save_create_shouldThrowWhenDuplicateName() {
        Department newDept = Department.builder().name("Cardiology").tenant(tenant).hospital(hospital).build();
        when(departmentRepository.findByNameAndTenantIdAndDeletedAtIsNull("Cardiology", 1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> departmentService.save(newDept))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void save_update_shouldSucceedWhenNoDuplicate() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.findByNameAndTenantIdAndDeletedAtIsNull(anyString(), anyLong())).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        Department result = departmentService.save(department);

        assertThat(result).isNotNull();
    }

    @Test
    void save_update_shouldThrowWhenDuplicateName() {
        Department updateDept = Department.builder().id(1L).name("Cardiology").tenant(tenant).hospital(hospital).build();
        Department existingDept = Department.builder().id(2L).name("Cardiology").tenant(tenant).hospital(hospital).build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(updateDept));
        when(departmentRepository.findByNameAndTenantIdAndDeletedAtIsNull("Cardiology", 1L)).thenReturn(Optional.of(existingDept));

        assertThatThrownBy(() -> departmentService.save(updateDept))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void softDelete_shouldSetDeletedAt() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        departmentService.softDelete(1L);

        assertThat(department.getDeletedAt()).isNotNull();
    }

    @Test
    void softDelete_shouldThrowWhenNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.softDelete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateDepartment() {
        DepartmentUpdateRequest request = new DepartmentUpdateRequest();
        request.setName("Updated Cardiology");
        request.setDescription("Heart department");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        Department result = departmentService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Updated Cardiology");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        DepartmentUpdateRequest request = new DepartmentUpdateRequest();
        request.setName("Test");

        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
