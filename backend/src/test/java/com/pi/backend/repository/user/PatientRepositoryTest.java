package com.pi.backend.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.model.user.Patient;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void saveAndRetrievePatient() {
        User user = createUser();
        Patient patient = createPatient(user, "MRN-001");

        Patient saved = patientRepository.save(patient);

        assertNotNull(saved.getId());
        assertEquals("MRN-001", saved.getMedicalRecordNumber());
        assertEquals("O+", saved.getBloodType());
        assertEquals(user.getId(), saved.getUser().getId());
    }

    @Test
    void uniqueMedicalRecordNumber() {
        User u1 = createUser("user1@test.com");
        User u2 = createUser("user2@test.com");

        Patient p1 = createPatient(u1, "MRN-001");
        patientRepository.save(p1);

        Patient p2 = createPatient(u2, "MRN-001");

        assertThrows(DataIntegrityViolationException.class, () -> {
            patientRepository.saveAndFlush(p2);
        });
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        User user = createUser();
        Patient patient = createPatient(user, "MRN-001");
        Patient saved = patientRepository.save(patient);

        patientRepository.deleteById(saved.getId());

        List<Patient> all = patientRepository.findAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void allergiesAndChronicConditionsStored() {
        User user = createUser();
        Patient patient = createPatient(user, "MRN-001");
        patient.setAllergies("Penicillin, Peanuts");
        patient.setChronicConditions("Diabetes, Hypertension");
        patientRepository.save(patient);

        Patient found = patientRepository.findById(patient.getId()).orElseThrow();
        assertEquals("Penicillin, Peanuts", found.getAllergies());
        assertEquals("Diabetes, Hypertension", found.getChronicConditions());
    }

    private User createUser() {
        return createUser("test@test.com");
    }

    private User createUser(String email) {
        Tenant tenant = new Tenant();
        tenant.setName("Hospital A");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        user.setRole(UserRole.PATIENT);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private Patient createPatient(User user, String mrn) {
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setMedicalRecordNumber(mrn);
        patient.setBloodType("O+");
        patient.setEmergencyContactName("Jane Doe");
        patient.setEmergencyContactPhone("0987654321");
        return patient;
    }
}
