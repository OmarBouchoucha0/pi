package tn.esprit.pi.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.esprit.pi.dto.user.*;
import tn.esprit.pi.entity.user.BlacklistedToken;
import tn.esprit.pi.entity.user.Department;
import tn.esprit.pi.entity.user.Hospital;
import tn.esprit.pi.entity.user.Role;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.*;
import tn.esprit.pi.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @InjectMocks
    private UserService userService;

    private Tenant tenant;
    private Role patientRole;
    private Role doctorRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder().id(1L).name("Test Tenant").build();

        patientRole = Role.builder().id(1L).role(tn.esprit.pi.enums.user.RolesEnum.PATIENT).build();
        doctorRole = Role.builder().id(2L).role(tn.esprit.pi.enums.user.RolesEnum.DOCTOR).build();
        adminRole = Role.builder().id(3L).role(tn.esprit.pi.enums.user.RolesEnum.ADMIN).build();
    }

    @Test
    void findAll_shouldReturnNonDeletedUsers() {
        User user = User.builder().id(1L).email("test@test.com").build();
        when(userRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
        verify(userRepository).findAllByDeletedAtIsNull();
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        User user = User.builder().id(1L).email("test@test.com").build();
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_shouldReturnUserWhenExists() {
        User user = User.builder().id(1L).email("test@test.com").build();
        when(userRepository.findByEmailAndDeletedAtIsNull("test@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@test.com");

        assertThat(result).isPresent();
    }

    @Test
    void findByMedicalRecordNumber_shouldReturnUserWhenExists() {
        User user = User.builder().id(1L).medicalRecordNumber("MRN001").build();
        when(userRepository.findByMedicalRecordNumberAndDeletedAtIsNull("MRN001")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByMedicalRecordNumber("MRN001");

        assertThat(result).isPresent();
    }

    @Test
    void findByLicenseNumber_shouldReturnUserWhenExists() {
        User user = User.builder().id(1L).licenseNumber("LN001").build();
        when(userRepository.findByLicenseNumberAndDeletedAtIsNull("LN001")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByLicenseNumber("LN001");

        assertThat(result).isPresent();
    }

    @Test
    void findByTenantId_shouldReturnUsers() {
        User user = User.builder().id(1L).build();
        when(userRepository.findByTenantIdAndDeletedAtIsNull(1L)).thenReturn(List.of(user));

        List<User> result = userService.findByTenantId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByRoleId_shouldReturnUsers() {
        User user = User.builder().id(1L).build();
        when(userRepository.findByRoleIdAndDeletedAtIsNull(1L)).thenReturn(List.of(user));

        List<User> result = userService.findByRoleId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllPatients_shouldReturnPatients() {
        User user = User.builder().id(1L).medicalRecordNumber("MRN001").build();
        when(userRepository.findByMedicalRecordNumberIsNotNullAndDeletedAtIsNull()).thenReturn(List.of(user));

        List<User> result = userService.findAllPatients();

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllDoctors_shouldReturnDoctors() {
        User user = User.builder().id(1L).licenseNumber("LN001").build();
        when(userRepository.findByLicenseNumberIsNotNullAndDeletedAtIsNull()).thenReturn(List.of(user));

        List<User> result = userService.findAllDoctors();

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllAdmins_shouldReturnAdmins() {
        User user = User.builder().id(1L).privilegeLevel(tn.esprit.pi.enums.user.AdminPrivilege.SUPER_ADMIN).build();
        when(userRepository.findByPrivilegeLevelIsNotNullAndDeletedAtIsNull()).thenReturn(List.of(user));

        List<User> result = userService.findAllAdmins();

        assertThat(result).hasSize(1);
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenExists() {
        when(userRepository.existsByEmailAndDeletedAtIsNull("test@test.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@test.com");

        assertThat(result).isTrue();
    }

    @Test
    void save_create_shouldSucceedWhenNoDuplicates() {
        User newUser = User.builder().email("new@test.com").build();
        when(userRepository.findByEmailAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.save(newUser);

        assertThat(result).isNotNull();
        verify(userRepository).save(newUser);
    }

    @Test
    void save_create_shouldThrowWhenEmailExists() {
        User existingUser = User.builder().id(1L).email("existing@test.com").build();
        User newUser = User.builder().email("existing@test.com").build();
        when(userRepository.findByEmailAndDeletedAtIsNull("existing@test.com")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.save(newUser))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void save_create_shouldThrowWhenMedicalRecordNumberExists() {
        User existingUser = User.builder().id(1L).medicalRecordNumber("MRN001").build();
        User newUser = User.builder().email("new@test.com").medicalRecordNumber("MRN001").build();
        when(userRepository.findByEmailAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByMedicalRecordNumberAndDeletedAtIsNull("MRN001")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.save(newUser))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void save_create_shouldThrowWhenLicenseNumberExists() {
        User existingUser = User.builder().id(1L).licenseNumber("LN001").build();
        User newUser = User.builder().email("new@test.com").licenseNumber("LN001").build();
        lenient().when(userRepository.findByEmailAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        lenient().when(userRepository.findByMedicalRecordNumberAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByLicenseNumberAndDeletedAtIsNull("LN001")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.save(newUser))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void softDelete_shouldSetDeletedAt() {
        User user = User.builder().id(1L).email("test@test.com").build();
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.softDelete(1L);

        assertThat(user.getDeletedAt()).isNotNull();
    }

    @Test
    void softDelete_shouldThrowWhenNotFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.softDelete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void recordLogin_shouldUpdateLastLoginAndResetFailedAttempts() {
        User user = User.builder().id(1L).failedAttempts(3).build();
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.recordLogin(1L);

        assertThat(user.getLastLogin()).isNotNull();
        assertThat(user.getFailedAttempts()).isEqualTo(0);
    }

    @Test
    void recordFailedLogin_shouldIncrementFailedAttempts() {
        User user = User.builder().id(1L).failedAttempts(0).build();
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.recordFailedLogin(1L);

        assertThat(user.getFailedAttempts()).isEqualTo(1);
    }

    @Test
    void createPatient_shouldHashPasswordAndReturnResponse() {
        PatientCreateRequest request = new PatientCreateRequest();
        request.setEmail("newpatient@test.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("Patient");
        request.setPhone("98765432");
        request.setTenantId(1L);

        User user = User.builder().id(1L).email("newpatient@test.com").build();

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(roleRepository.findByRole(tn.esprit.pi.enums.user.RolesEnum.PATIENT)).thenReturn(Optional.of(patientRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = userService.createPatient(request);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void createPatient_shouldThrowWhenTenantNotFound() {
        PatientCreateRequest request = new PatientCreateRequest();
        request.setEmail("newpatient@test.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("Patient");
        request.setPhone("98765432");
        request.setTenantId(99L);

        when(tenantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createPatient(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void toResponse_shouldMapUserToResponse() {
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .phone("12345678")
                .tenant(tenant)
                .role(patientRole)
                .build();

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.toResponse(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findByDepartmentId_shouldReturnUsers() {
        User user = User.builder().id(1L).build();
        when(userRepository.findByDepartmentIdAndDeletedAtIsNull(1L)).thenReturn(List.of(user));

        List<User> result = userService.findByDepartmentId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void createDoctor_shouldCreateAndReturnResponse() {
        DoctorCreateRequest request = new DoctorCreateRequest();
        request.setEmail("newdoctor@test.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("Doctor");
        request.setPhone("98765432");
        request.setTenantId(1L);
        request.setDepartmentId(1L);
        request.setLicenseNumber("LN001");
        request.setSpecialty("Cardiology");

        Department department = Department.builder().id(1L).name("Cardiology").build();
        User user = User.builder().id(1L).email("newdoctor@test.com").build();

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(roleRepository.findByRole(tn.esprit.pi.enums.user.RolesEnum.DOCTOR)).thenReturn(Optional.of(doctorRole));
        when(departmentRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(department));
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = userService.createDoctor(request);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void createDoctor_shouldThrowWhenDepartmentNotFound() {
        DoctorCreateRequest request = new DoctorCreateRequest();
        request.setEmail("newdoctor@test.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("Doctor");
        request.setPhone("98765432");
        request.setTenantId(1L);
        request.setDepartmentId(99L);

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(roleRepository.findByRole(tn.esprit.pi.enums.user.RolesEnum.DOCTOR)).thenReturn(Optional.of(doctorRole));
        when(departmentRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createDoctor(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createAdmin_shouldCreateAndReturnResponse() {
        AdminCreateRequest request = new AdminCreateRequest();
        request.setEmail("newadmin@test.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("Admin");
        request.setPhone("98765432");
        request.setTenantId(1L);
        request.setPrivilegeLevel(tn.esprit.pi.enums.user.AdminPrivilege.SUPER_ADMIN);

        User user = User.builder().id(1L).email("newadmin@test.com").build();

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(roleRepository.findByRole(tn.esprit.pi.enums.user.RolesEnum.ADMIN)).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = userService.createAdmin(request);

        assertThat(result).isNotNull();
    }

    @Test
    void updatePatient_shouldUpdateAndReturnResponse() {
        User existingUser = User.builder().id(1L).email("test@test.com").build();
        PatientUpdateRequest request = new PatientUpdateRequest();
        request.setFirstName("Updated");

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmailAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponse result = userService.updatePatient(1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    void updatePatient_shouldThrowWhenNotFound() {
        PatientUpdateRequest request = new PatientUpdateRequest();
        request.setFirstName("Updated");

        when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePatient(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateDoctor_shouldUpdateAndReturnResponse() {
        User existingUser = User.builder().id(1L).email("test@test.com").build();
        DoctorUpdateRequest request = new DoctorUpdateRequest();
        request.setFirstName("Updated");

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmailAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponse result = userService.updateDoctor(1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    void updateDoctor_shouldThrowWhenNotFound() {
        DoctorUpdateRequest request = new DoctorUpdateRequest();
        request.setFirstName("Updated");

        when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateDoctor(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateAdmin_shouldUpdateAndReturnResponse() {
        User existingUser = User.builder().id(1L).email("test@test.com").build();
        AdminUpdateRequest request = new AdminUpdateRequest();
        request.setFirstName("Updated");

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmailAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponse result = userService.updateAdmin(1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    void updateAdmin_shouldThrowWhenNotFound() {
        AdminUpdateRequest request = new AdminUpdateRequest();
        request.setFirstName("Updated");

        when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateAdmin(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void login_shouldReturnTokensOnSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .passwordHash("hashed_password")
                .role(patientRole)
                .build();

when(httpServletRequest.isSecure()).thenReturn(false);
        when(userRepository.findByEmailAndDeletedAtIsNull("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        LoginResponse result = userService.login(httpServletRequest, request, httpServletResponse);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getRole()).isEqualTo(patientRole.getRole());
    }

    @Test
    void login_shouldThrowWhenInvalidEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmailAndDeletedAtIsNull("invalid@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(httpServletRequest, request, httpServletResponse))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    void login_shouldThrowWhenInvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrong_password");

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .passwordHash("hashed_password")
                .role(patientRole)
                .build();

        when(userRepository.findByEmailAndDeletedAtIsNull("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(httpServletRequest, request, httpServletResponse))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    void refresh_shouldReturnNewAccessToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh_token");

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .role(patientRole)
                .build();

        when(jwtTokenProvider.validateToken("refresh_token")).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken("refresh_token")).thenReturn(true);
        when(blacklistedTokenRepository.existsByToken("refresh_token")).thenReturn(false);
        when(jwtTokenProvider.getUserIdFromToken("refresh_token")).thenReturn(1L);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(anyLong(), anyString(), any())).thenReturn("new_access_token");
        when(jwtTokenProvider.generateRefreshToken(anyLong())).thenReturn("new_refresh_token");
        when(jwtTokenProvider.getExpirationFromToken("refresh_token")).thenReturn(new Date(System.currentTimeMillis() + 604800000));

        LoginResponse result = userService.refresh(httpServletRequest, request, httpServletResponse);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void refresh_shouldThrowWhenTokenInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid_token");

        when(jwtTokenProvider.validateToken("invalid_token")).thenReturn(false);

        assertThatThrownBy(() -> userService.refresh(httpServletRequest, request, httpServletResponse))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    void refresh_shouldThrowWhenTokenBlacklisted() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("blacklisted_token");

        when(jwtTokenProvider.validateToken("blacklisted_token")).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken("blacklisted_token")).thenReturn(true);
        when(blacklistedTokenRepository.existsByToken("blacklisted_token")).thenReturn(true);

        assertThatThrownBy(() -> userService.refresh(httpServletRequest, request, httpServletResponse))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessageContaining("revoked");
    }

    @Test
    void logout_shouldBlacklistToken() {
        Cookie refreshCookie = new Cookie("refreshToken", "refresh_token");
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] { refreshCookie });
        when(jwtTokenProvider.validateToken("refresh_token")).thenReturn(true);
        when(blacklistedTokenRepository.existsByToken("refresh_token")).thenReturn(false);
        when(jwtTokenProvider.getExpirationFromToken("refresh_token")).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        userService.logout(httpServletRequest, httpServletResponse);

        verify(blacklistedTokenRepository).save(any(BlacklistedToken.class));
    }

    @Test
    void logout_shouldNotBlacklistWhenAlreadyBlacklisted() {
        Cookie refreshCookie = new Cookie("refreshToken", "refresh_token");
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] { refreshCookie });
        when(jwtTokenProvider.validateToken("refresh_token")).thenReturn(true);
        when(blacklistedTokenRepository.existsByToken("refresh_token")).thenReturn(true);

        userService.logout(httpServletRequest, httpServletResponse);

        verify(blacklistedTokenRepository, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void getPatientsByHospitalId_shouldReturnPatientsForHospital() {
        Hospital hospital = Hospital.builder().id(1L).tenant(tenant).build();
        User patient = User.builder().id(1L).medicalRecordNumber("MRN001").build();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(userRepository.findByTenantIdAndDeletedAtIsNull(1L)).thenReturn(List.of(patient));

        List<User> result = userService.getPatientsByHospitalId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getPatientsByHospitalId_shouldThrowWhenHospitalNotFound() {
        when(hospitalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getPatientsByHospitalId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getCurrentUser_shouldReturnUser() {
        User user = User.builder().id(1L).email("test@test.com").build();

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        User result = userService.getCurrentUser(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getCurrentUser_shouldThrowWhenNotFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
