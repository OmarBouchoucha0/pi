package com.pi.backend.service.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.Gender;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;
import com.pi.backend.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public User createUser(Long tenantId, String email, String passwordHash,
                           String firstName, String lastName, UserRole role) {
        if (userRepository.existsByTenantIdAndEmail(tenantId, email)) {
            throw new DuplicateResourceException("User", "email", email);
        }

        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));

        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);

        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    public User getUserByEmail(Long tenantId, String email) {
        return userRepository.findByTenantIdAndEmail(tenantId, email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public List<User> getUsersByTenant(Long tenantId) {
        return userRepository.findByTenantIdAndDeletedAtIsNull(tenantId);
    }

    public List<User> getUsersByRole(Long tenantId, UserRole role) {
        return userRepository.findByTenantIdAndRole(tenantId, role);
    }

    public List<User> getUsersByStatus(Long tenantId, UserStatus status) {
        return userRepository.findByTenantIdAndStatus(tenantId, status);
    }

    public boolean existsByEmail(Long tenantId, String email) {
        return userRepository.existsByTenantIdAndEmail(tenantId, email);
    }

    public long countByStatus(Long tenantId, UserStatus status) {
        return userRepository.countByTenantIdAndStatus(tenantId, status);
    }

    @Transactional
    public User updateUserProfile(Long userId, String firstName, String lastName,
                                  String phone, LocalDate dateOfBirth,
                                  Gender gender, String address) {
        User user = getUserById(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setDateOfBirth(dateOfBirth);
        user.setGender(gender);
        user.setAddress(address);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserStatus(Long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Transactional
    public void recordLogin(Long userId) {
        User user = getUserById(userId);
        user.setLastLogin(LocalDateTime.now());
        user.setFailedAttempts(0);
        userRepository.save(user);
    }

    @Transactional
    public void incrementFailedAttempts(Long userId) {
        User user = getUserById(userId);
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }
}
