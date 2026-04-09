package tn.esprit.pi.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.user.DepartmentUpdateRequest;
import tn.esprit.pi.entity.user.Department;
import tn.esprit.pi.entity.user.Hospital;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.DepartmentRepository;
import tn.esprit.pi.repository.user.HospitalRepository;
import tn.esprit.pi.repository.user.TenantRepository;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final TenantRepository tenantRepository;
    private final HospitalRepository hospitalRepository;

    public List<Department> findAll() {
        return departmentRepository.findAllByDeletedAtIsNull();
    }

    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }

    public List<Department> findByTenantId(Long tenantId) {
        return departmentRepository.findByTenantIdAndDeletedAtIsNull(tenantId);
    }

    public List<Department> findByHospitalId(Long hospitalId) {
        return departmentRepository.findByHospitalIdAndDeletedAtIsNull(hospitalId);
    }

    public Optional<Department> findByNameAndTenantId(String name, Long tenantId) {
        return departmentRepository.findByNameAndTenantIdAndDeletedAtIsNull(name, tenantId);
    }

    public boolean existsByNameAndTenantId(String name, Long tenantId) {
        return departmentRepository.existsByNameAndTenantIdAndDeletedAtIsNull(name, tenantId);
    }

    public Department save(Department department) {
        if (department.getId() == null) {
            departmentRepository.findByNameAndTenantIdAndDeletedAtIsNull(department.getName(), department.getTenant().getId())
                    .ifPresent(existing -> {
                        throw new DuplicateResourceException("Department with name already exists in this tenant: " + department.getName());
                    });
        } else {
            departmentRepository.findById(department.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + department.getId()));

            departmentRepository.findByNameAndTenantIdAndDeletedAtIsNull(department.getName(), department.getTenant().getId())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(department.getId())) {
                            throw new DuplicateResourceException("Department with name already exists in this tenant: " + department.getName());
                        }
                    });
        }

        return departmentRepository.save(department);
    }

    public void softDelete(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        department.setDeletedAt(LocalDateTime.now());
        departmentRepository.save(department);
    }

    public Department update(Long id, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (request.getName() != null) department.setName(request.getName());
        if (request.getDescription() != null) department.setDescription(request.getDescription());
        if (request.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(request.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));
            department.setTenant(tenant);
        }
        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + request.getHospitalId()));
            department.setHospital(hospital);
        }

        return departmentRepository.save(department);
    }
}
