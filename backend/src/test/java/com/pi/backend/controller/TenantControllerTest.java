package com.pi.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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
import com.pi.backend.dto.CreateTenantRequest;
import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.service.TenantService;

/**
 * Tests for {@link TenantController}. Uses MockMvc to test HTTP endpoints
 * and verify proper request/response handling and exception mapping.
 */
@SpringBootTest
class TenantControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private TenantService tenantService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Verifies that POST /api/tenants creates a tenant and returns 201.
     */
    @Test
    void createTenant_success() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest("City Hospital", TenantStatus.ACTIVE);

        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("City Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setCreatedAt(LocalDateTime.now());

        when(tenantService.createTenant("City Hospital", TenantStatus.ACTIVE)).thenReturn(tenant);

        mockMvc.perform(post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("City Hospital"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    /**
     * Verifies that POST /api/tenants with missing fields returns 400.
     */
    @Test
    void createTenant_validationError() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest(null, null);

        mockMvc.perform(post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.errors.name").value("Name is required"))
            .andExpect(jsonPath("$.errors.status").value("Status is required"));
    }

    /**
     * Verifies that POST /api/tenants with duplicate name returns 409.
     */
    @Test
    void createTenant_duplicateName() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest("Existing Hospital", TenantStatus.ACTIVE);

        when(tenantService.createTenant(eq("Existing Hospital"), eq(TenantStatus.ACTIVE)))
            .thenThrow(new DuplicateResourceException("Tenant", "name", "Existing Hospital"));

        mockMvc.perform(post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("Conflict"))
            .andExpect(jsonPath("$.field").value("name"));
    }

    /**
     * Verifies that POST /api/tenants with malformed JSON returns 400.
     */
    @Test
    void createTenant_malformedJson() throws Exception {
        mockMvc.perform(post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
