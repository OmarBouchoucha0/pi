package com.pi.backend.controller.patient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi.backend.dto.patient.CreateEmptyPatientRequest;
import com.pi.backend.dto.patient.CreateFullPatientRequest;
import com.pi.backend.dto.patient.PatientResponse;
import com.pi.backend.dto.patient.UpdatePatientRequest;
import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.service.user.PatientService;

/**
 * Tests for {@link PatientController}. Uses MockMvc to test HTTP endpoints
 * and verify proper request/response handling and exception mapping.
 */
@SpringBootTest
class PatientControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Verifies that POST /api/patients/full creates a patient and returns 201.
     */
    @Test
    void createFullPatient_success() throws Exception {
        CreateFullPatientRequest request = new CreateFullPatientRequest(
            1L, "John", "Doe", "john@test.com", "hash",
            "MRN-001", "O+", "Peanuts", null, null, null, null
        );

        PatientResponse response = createMockResponse();
        when(patientService.createPatientWithUser(
            eq(1L), eq("John"), eq("Doe"), eq("john@test.com"), eq("hash"),
            eq("MRN-001"), eq("O+"), eq("Peanuts"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(response);

        mockMvc.perform(post("/api/patients/full")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    /**
     * Verifies that POST /api/patients/full with missing fields returns 400.
     */
    @Test
    void createFullPatient_validationError() throws Exception {
        CreateFullPatientRequest request = new CreateFullPatientRequest(
            null, "", "", "invalid-email", "",
            null, null, null, null, null, null, null
        );

        mockMvc.perform(post("/api/patients/full")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.errors.tenantId").value("Tenant ID is required"))
            .andExpect(jsonPath("$.errors.firstName").value("First name is required"))
            .andExpect(jsonPath("$.errors.lastName").value("Last name is required"))
            .andExpect(jsonPath("$.errors.email").value("Email must be a valid email address"));
    }

    /**
     * Verifies that POST /api/patients/full with duplicate email returns 409.
     */
    @Test
    void createFullPatient_duplicateEmail() throws Exception {
        CreateFullPatientRequest request = new CreateFullPatientRequest(
            1L, "John", "Doe", "existing@test.com", "hash",
            null, null, null, null, null, null, null
        );

        when(patientService.createPatientWithUser(
            eq(1L), eq("John"), eq("Doe"), eq("existing@test.com"), eq("hash"),
            isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        )).thenThrow(new DuplicateResourceException("User", "email", "existing@test.com"));

        mockMvc.perform(post("/api/patients/full")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("Conflict"))
            .andExpect(jsonPath("$.field").value("email"));
    }

    /**
     * Verifies that POST /api/patients/empty creates an empty patient and returns 201.
     */
    @Test
    void createEmptyPatient_success() throws Exception {
        CreateEmptyPatientRequest request = new CreateEmptyPatientRequest(
            1L, "John", "Doe", "john@test.com", "hash"
        );

        PatientResponse response = createMockResponse();
        when(patientService.createEmptyPatient(1L, "John", "Doe", "john@test.com", "hash"))
            .thenReturn(response);

        mockMvc.perform(post("/api/patients/empty")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    /**
     * Verifies that POST /api/patients/empty with missing fields returns 400.
     */
    @Test
    void createEmptyPatient_validationError() throws Exception {
        CreateEmptyPatientRequest request = new CreateEmptyPatientRequest(
            null, "", "", "invalid-email", ""
        );

        mockMvc.perform(post("/api/patients/empty")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    /**
     * Verifies that GET /api/patients returns a list of patients.
     */
    @Test
    void getAllPatients_success() throws Exception {
        when(patientService.getAllPatients()).thenReturn(List.of(createMockResponse()));

        mockMvc.perform(get("/api/patients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1));
    }

    /**
     * Verifies that GET /api/patients/{id} returns a patient.
     */
    @Test
    void getPatientById_success() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(createMockResponse());

        mockMvc.perform(get("/api/patients/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    /**
     * Verifies that GET /api/patients/{id} with non-existent ID returns 404.
     */
    @Test
    void getPatientById_notFound() throws Exception {
        when(patientService.getPatientById(999L))
            .thenThrow(new ResourceNotFoundException("Patient", 999L));

        mockMvc.perform(get("/api/patients/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.resource").value("Patient"));
    }

    /**
     * Verifies that PUT /api/patients/{id} updates a patient and returns 200.
     */
    @Test
    void updatePatient_success() throws Exception {
        UpdatePatientRequest request = new UpdatePatientRequest(
            "A+", "Peanuts", "Diabetes", "Jane Doe", "1234567890"
        );

        PatientResponse response = createMockResponse();
        response = new PatientResponse(
            response.id(), response.userId(), response.firstName(), response.lastName(),
            response.email(), response.medicalRecordNumber(), "A+",
            "Peanuts", "Diabetes", "Jane Doe", "1234567890",
            response.primaryDepartmentId(), response.createdAt()
        );
        when(patientService.updatePatient(1L, "A+", "Peanuts", "Diabetes", "Jane Doe", "1234567890"))
            .thenReturn(response);

        mockMvc.perform(put("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bloodType").value("A+"));
    }

    /**
     * Verifies that PUT /api/patients/{id} with non-existent ID returns 404.
     */
    @Test
    void updatePatient_notFound() throws Exception {
        UpdatePatientRequest request = new UpdatePatientRequest(
            "A+", null, null, null, null
        );

        when(patientService.updatePatient(999L, "A+", null, null, null, null))
            .thenThrow(new ResourceNotFoundException("Patient", 999L));

        mockMvc.perform(put("/api/patients/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    /**
     * Verifies that DELETE /api/patients/{id} soft deletes a patient and returns 204.
     */
    @Test
    void deletePatient_success() throws Exception {
        doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/api/patients/1"))
            .andExpect(status().isNoContent());
    }

    /**
     * Verifies that DELETE /api/patients/{id} with non-existent ID returns 404.
     */
    @Test
    void deletePatient_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Patient", 999L))
            .when(patientService).deletePatient(999L);

        mockMvc.perform(delete("/api/patients/999"))
            .andExpect(status().isNotFound());
    }

    private PatientResponse createMockResponse() {
        return new PatientResponse(
            1L, 1L, "John", "Doe", "john@test.com",
            "MRN-001", "O+", "Peanuts", null, null, null,
            null, LocalDateTime.now()
        );
    }
}
