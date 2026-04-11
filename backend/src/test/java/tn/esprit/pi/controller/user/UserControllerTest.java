package tn.esprit.pi.controller.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tn.esprit.pi.dto.user.LoginRequest;
import tn.esprit.pi.dto.user.LoginResponse;
import tn.esprit.pi.dto.user.PatientRegisterRequest;
import tn.esprit.pi.dto.user.RefreshTokenRequest;
import tn.esprit.pi.dto.user.UserResponse;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.service.user.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserResponse userResponse;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        loginResponse = LoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .userId(1L)
                .email("test@test.com")
                .role(tn.esprit.pi.enums.user.RolesEnum.PATIENT)
                .build();
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        when(userService.login(request)).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> result = userController.login(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getAccessToken()).isEqualTo("access-token");
    }

    @Test
    void registerPatient_success() {
        PatientRegisterRequest request = new PatientRegisterRequest();
        request.setEmail("patient@test.com");
        request.setFirstName("Patient");
        request.setLastName("Test");
        request.setPassword("password");
        request.setTenantId(1L);

        when(userService.registerPatient(any(PatientRegisterRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> result = userController.registerPatient(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void refresh_success() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        when(userService.refresh(request)).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> result = userController.refresh(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void logout_success() {
        tn.esprit.pi.dto.user.LogoutRequest request = new tn.esprit.pi.dto.user.LogoutRequest();
        request.setRefreshToken("refresh-token");

        doNothing().when(userService).logout(any());

        ResponseEntity<Void> result = userController.logout(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).logout(request);
    }

    @Test
    void findAll_success() {
        when(userService.findAll()).thenReturn(List.of(user));
        when(userService.toResponseList(any())).thenReturn(List.of(userResponse));

        ResponseEntity<List<UserResponse>> result = userController.findAll();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }
}
