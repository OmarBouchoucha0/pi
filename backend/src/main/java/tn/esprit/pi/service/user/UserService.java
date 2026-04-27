package tn.esprit.pi.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.user.*;
import tn.esprit.pi.entity.user.BlacklistedToken;
import tn.esprit.pi.entity.user.Department;
import tn.esprit.pi.entity.user.Hospital;
import tn.esprit.pi.entity.user.Role;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.user.RolesEnum;
import tn.esprit.pi.enums.user.UserStatus;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.BlacklistedTokenRepository;
import tn.esprit.pi.repository.user.DepartmentRepository;
import tn.esprit.pi.repository.user.HospitalRepository;
import tn.esprit.pi.repository.user.RoleRepository;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;
import tn.esprit.pi.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public List<User> findAll() {
        return userRepository.findAllByDeletedAtIsNull();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email);
    }

    public Optional<User> findByMedicalRecordNumber(String medicalRecordNumber) {
        return userRepository.findByMedicalRecordNumberAndDeletedAtIsNull(medicalRecordNumber);
    }

    public Optional<User> findByLicenseNumber(String licenseNumber) {
        return userRepository.findByLicenseNumberAndDeletedAtIsNull(licenseNumber);
    }

    public List<User> findByTenantId(Long tenantId) {
        return userRepository.findByTenantIdAndDeletedAtIsNull(tenantId);
    }

    public List<User> findByRoleId(Long roleId) {
        return userRepository.findByRoleIdAndDeletedAtIsNull(roleId);
    }

    public List<User> findByDepartmentId(Long departmentId) {
        return userRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    public List<User> findAllPatients() {
        return userRepository.findByMedicalRecordNumberIsNotNullAndDeletedAtIsNull();
    }

    public List<User> findAllDoctors() {
        return userRepository.findByLicenseNumberIsNotNullAndDeletedAtIsNull();
    }

    public List<User> findAllAdmins() {
        return userRepository.findByPrivilegeLevelIsNotNullAndDeletedAtIsNull();
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    public UserResponse registerPatient(PatientRegisterRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        Tenant tenant = tenantRepository.findByIdAndDeletedAtIsNull(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));

        Role role = roleRepository.findByRole(RolesEnum.PATIENT)
                .orElseThrow(() -> new ResourceNotFoundException("Role PATIENT not found"));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .tenant(tenant)
                .role(role)
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .bloodType(request.getBloodType())
                .allergies(request.getAllergies())
                .chronicConditions(request.getChronicConditions())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .build();

        return toResponse(save(user));
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        Tenant tenant = tenantRepository.findByIdAndDeletedAtIsNull(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));

        Role role = roleRepository.findByRole(RolesEnum.valueOf(request.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRole()));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .tenant(tenant)
                .role(role)
                .build();

        return toResponse(save(user));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            Role role = roleRepository.findByRole(RolesEnum.valueOf(request.getRole()))
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRole()));
            user.setRole(role);
        }
        if (request.getStatus() != null) {
            user.setStatus(UserStatus.valueOf(request.getStatus()));
        }

        return toResponse(userRepository.save(user));
    }

    public User save(User user) {
        if (user.getId() == null) {
            userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())
                    .ifPresent(existing -> {
                        throw new DuplicateResourceException("Email already in use: " + user.getEmail());
                    });

            if (user.getMedicalRecordNumber() != null) {
                userRepository.findByMedicalRecordNumberAndDeletedAtIsNull(user.getMedicalRecordNumber())
                        .ifPresent(existing -> {
                            throw new DuplicateResourceException("Medical record number already in use: " + user.getMedicalRecordNumber());
                        });
            }

            if (user.getLicenseNumber() != null) {
                userRepository.findByLicenseNumberAndDeletedAtIsNull(user.getLicenseNumber())
                        .ifPresent(existing -> {
                            throw new DuplicateResourceException("License number already in use: " + user.getLicenseNumber());
                        });
            }
        } else {
            userRepository.findByIdAndDeletedAtIsNull(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + user.getId()));

            userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(user.getId())) {
                            throw new DuplicateResourceException("Email already in use: " + user.getEmail());
                        }
                    });

            if (user.getMedicalRecordNumber() != null) {
                userRepository.findByMedicalRecordNumberAndDeletedAtIsNull(user.getMedicalRecordNumber())
                        .ifPresent(existing -> {
                            if (!existing.getId().equals(user.getId())) {
                                throw new DuplicateResourceException("Medical record number already in use: " + user.getMedicalRecordNumber());
                            }
                        });
            }

            if (user.getLicenseNumber() != null) {
                userRepository.findByLicenseNumberAndDeletedAtIsNull(user.getLicenseNumber())
                        .ifPresent(existing -> {
                            if (!existing.getId().equals(user.getId())) {
                                throw new DuplicateResourceException("License number already in use: " + user.getLicenseNumber());
                            }
                        });
            }
        }

        return userRepository.save(user);
    }

    public void softDelete(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void recordLogin(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setLastLogin(LocalDateTime.now());
        user.setFailedAttempts(0);
        userRepository.save(user);
    }

    public void recordFailedLogin(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        userRepository.save(user);
    }

    public UserResponse createPatient(PatientCreateRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));
        Role role = roleRepository.findByRole(RolesEnum.PATIENT)
                .orElseThrow(() -> new ResourceNotFoundException("Role PATIENT not found"));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .tenant(tenant)
                .role(role)
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .medicalRecordNumber(request.getMedicalRecordNumber())
                .bloodType(request.getBloodType())
                .allergies(request.getAllergies())
                .chronicConditions(request.getChronicConditions())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .build();

        return toResponse(save(user));
    }

    public UserResponse updatePatient(Long id, PatientUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        applyPatientUpdate(user, request);
        return toResponse(save(user));
    }

    public UserResponse createDoctor(DoctorCreateRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));
        Role role = roleRepository.findByRole(RolesEnum.DOCTOR)
                .orElseThrow(() -> new ResourceNotFoundException("Role DOCTOR not found"));
        Department department = departmentRepository.findByIdAndDeletedAtIsNull(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .tenant(tenant)
                .role(role)
                .department(department)
                .licenseNumber(request.getLicenseNumber())
                .specialty(request.getSpecialty())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .build();

        return toResponse(save(user));
    }

    public UserResponse updateDoctor(Long id, DoctorUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        applyDoctorUpdate(user, request);
        return toResponse(save(user));
    }

    public UserResponse createAdmin(AdminCreateRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));
        Role role = roleRepository.findByRole(RolesEnum.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role ADMIN not found"));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .tenant(tenant)
                .role(role)
                .privilegeLevel(request.getPrivilegeLevel())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .build();

        return toResponse(save(user));
    }

    public UserResponse updateAdmin(Long id, AdminUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        applyAdminUpdate(user, request);
        return toResponse(save(user));
    }

    public UserResponse toResponse(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toResponse(user);
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse toResponse(User user) {
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .status(user.getStatus())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .tenantId(user.getTenant() != null ? user.getTenant().getId() : null)
                .tenantName(user.getTenant() != null ? user.getTenant().getName() : null)
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .role(user.getRole() != null ? user.getRole().getRole() : null)
                .medicalRecordNumber(user.getMedicalRecordNumber())
                .bloodType(user.getBloodType())
                .allergies(user.getAllergies())
                .chronicConditions(user.getChronicConditions())
                .emergencyContactName(user.getEmergencyContactName())
                .emergencyContactPhone(user.getEmergencyContactPhone())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .licenseNumber(user.getLicenseNumber())
                .specialty(user.getSpecialty())
                .privilegeLevel(user.getPrivilegeLevel());

        return builder.build();
    }

    private void applyPatientUpdate(User user, PatientUpdateRequest request) {
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (request.getMedicalRecordNumber() != null) user.setMedicalRecordNumber(request.getMedicalRecordNumber());
        if (request.getBloodType() != null) user.setBloodType(request.getBloodType());
        if (request.getAllergies() != null) user.setAllergies(request.getAllergies());
        if (request.getChronicConditions() != null) user.setChronicConditions(request.getChronicConditions());
        if (request.getEmergencyContactName() != null) user.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null) user.setEmergencyContactPhone(request.getEmergencyContactPhone());
    }

    private void applyDoctorUpdate(User user, DoctorUpdateRequest request) {
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (request.getLicenseNumber() != null) user.setLicenseNumber(request.getLicenseNumber());
        if (request.getSpecialty() != null) user.setSpecialty(request.getSpecialty());
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findByIdAndDeletedAtIsNull(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
            user.setDepartment(department);
        }
    }

    private void applyAdminUpdate(User user, AdminUpdateRequest request) {
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (request.getPrivilegeLevel() != null) user.setPrivilegeLevel(request.getPrivilegeLevel());
    }

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/users/auth/refresh");
        refreshCookie.setMaxAge(604800);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().getRole())
                .build();
    }

    public LoginResponse refresh(RefreshTokenRequest request, HttpServletResponse response) {
        String oldRefreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if (!jwtTokenProvider.isRefreshToken(oldRefreshToken)) {
            throw new BadCredentialsException("Invalid token type");
        }

        if (blacklistedTokenRepository.existsByToken(oldRefreshToken)) {
            throw new BadCredentialsException("Token has been revoked");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(oldRefreshToken);
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(oldRefreshToken)
                .expiresAt(jwtTokenProvider.getExpirationFromToken(oldRefreshToken).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .build();
        blacklistedTokenRepository.save(blacklistedToken);

        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);

        Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/users/auth/refresh");
        refreshCookie.setMaxAge(604800);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().getRole())
                .build();
    }

    public void logout(LogoutRequest request, HttpServletResponse response) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            if (!blacklistedTokenRepository.existsByToken(refreshToken)) {
                BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                        .token(refreshToken)
                        .expiresAt(jwtTokenProvider.getExpirationFromToken(refreshToken).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                        .build();
                blacklistedTokenRepository.save(blacklistedToken);
            }
        }

        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/users/auth/refresh");
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    public List<User> getPatientsByHospitalId(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));

        return userRepository.findByTenantIdAndDeletedAtIsNull(hospital.getTenant().getId())
                .stream()
                .filter(user -> user.getMedicalRecordNumber() != null)
                .collect(Collectors.toList());
    }

    public User getCurrentUser(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
