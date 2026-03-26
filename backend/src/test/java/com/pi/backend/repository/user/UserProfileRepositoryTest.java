package com.pi.backend.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.UserProfile;
import com.pi.backend.model.user.enums.Gender;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserProfileRepositoryTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void saveAndRetrieveProfile() {
        User user = createUser();
        UserProfile profile = createProfile(user);

        UserProfile saved = userProfileRepository.save(profile);

        assertNotNull(saved.getId());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals(Gender.MALE, saved.getGender());
        assertEquals(LocalDate.of(1990, 1, 1), saved.getDateOfBirth());
    }

    @Test
    void enumPersistence() {
        User user = createUser();
        UserProfile profile = createProfile(user);
        profile.setGender(Gender.FEMALE);
        userProfileRepository.save(profile);

        UserProfile found = userProfileRepository.findById(profile.getId()).orElseThrow();
        assertEquals(Gender.FEMALE, found.getGender());
    }

    @Test
    void profileLinkedToUser() {
        User user = createUser();
        UserProfile profile = createProfile(user);
        userProfileRepository.save(profile);

        UserProfile found = userProfileRepository.findById(profile.getId()).orElseThrow();
        assertEquals(user.getId(), found.getUser().getId());
        assertEquals("test@test.com", found.getUser().getEmail());
    }

    private User createUser() {
        Tenant tenant = new Tenant();
        tenant.setName("Hospital A");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        User user = new User();
        user.setTenant(tenant);
        user.setEmail("test@test.com");
        user.setPasswordHash("hashed");
        user.setRole(UserRole.PATIENT);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private UserProfile createProfile(User user) {
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setPhone("1234567890");
        profile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        profile.setGender(Gender.MALE);
        profile.setAddress("123 Main St");
        return profile;
    }
}
