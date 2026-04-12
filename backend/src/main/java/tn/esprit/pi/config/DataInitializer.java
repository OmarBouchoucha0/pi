package tn.esprit.pi.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.pi.entity.user.Role;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.user.RolesEnum;
import tn.esprit.pi.enums.user.TenantStatus;
import tn.esprit.pi.enums.user.UserStatus;
import tn.esprit.pi.repository.user.RoleRepository;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeData();
    }

    private void initializeData() {
        log.info("Starting data initialization...");

        try {
            createRoles();
            Tenant defaultTenant = getOrCreateDefaultTenant();
            Role adminRole = roleRepository.findByRole(RolesEnum.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found after creation"));
            createDefaultAdminUser(adminRole, defaultTenant);

            log.info("Data initialization completed successfully!");
        } catch (Exception e) {
            log.error("Error during data initialization: {}", e.getMessage(), e);
        }
    }

    private void createRoles() {
        log.info("Initializing roles...");

        Arrays.asList(
                createRole(RolesEnum.ADMIN, "System Administrator"),
                createRole(RolesEnum.DOCTOR, "Medical Doctor"),
                createRole(RolesEnum.PATIENT, "Patient User"),
                createRole(RolesEnum.NURSE, "Nurse User"),
                createRole(RolesEnum.LAB, "Lab User")
        ).forEach(role -> {
            if (role != null) {
                log.info("Role ensured: {}", role.getRole());
            }
        });
    }

    private Role createRole(RolesEnum rolesEnum, String description) {
        return roleRepository.findByRole(rolesEnum)
                .orElseGet(() -> {
                    log.info("Creating role: {}", rolesEnum);
                    Role role = Role.builder()
                            .role(rolesEnum)
                            .description(description)
                            .build();
                    return roleRepository.save(role);
                });
    }

    private Tenant getOrCreateDefaultTenant() {
        return tenantRepository.findByNameAndDeletedAtIsNull("MeddiFollow")
                .orElseGet(() -> {
                    log.info("Creating default tenant: MeddiFollow");
                    Tenant tenant = Tenant.builder()
                            .name("MeddiFollow")
                            .status(TenantStatus.ACTIVE)
                            .build();
                    return tenantRepository.save(tenant);
                });
    }

    private void createDefaultAdminUser(Role adminRole, Tenant defaultTenant) {
        String adminEmail = "admin@example.com";

        if (userRepository.findByEmailAndDeletedAtIsNull(adminEmail).isPresent()) {
            log.info("Default admin user already exists, skipping creation");
            return;
        }

        log.info("Creating default admin user: {}", adminEmail);

        User adminUser = User.builder()
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode("admin"))
                .firstName("Admin")
                .lastName("User")
                .phone("+21600000000")
                .status(UserStatus.ACTIVE)
                .role(adminRole)
                .tenant(defaultTenant)
                .build();

        userRepository.save(adminUser);
        log.info("Default admin user created successfully with email: {}", adminEmail);
    }
}
