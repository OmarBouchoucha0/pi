package com.pi.backend.repository.user;

import com.pi.backend.model.Department;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.TenantRepository;

public class TestHelper {

    public static Tenant createTenant(TenantRepository tenantRepository, String name) {
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(tenant);
    }

    public static User createUser(UserRepository userRepository, Tenant tenant, String email, UserRole role) {
        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    public static Department createDepartment(DepartmentRepository departmentRepository, Tenant tenant, String name) {
        Department dept = new Department();
        dept.setTenant(tenant);
        dept.setName(name);
        return departmentRepository.save(dept);
    }
}
