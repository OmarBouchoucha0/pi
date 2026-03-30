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

/**
 * Service for managing user accounts. Handles creation, retrieval, updates,
 * and soft-deletion of users across all roles within a tenant.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    /**
     * Creates a new user account for the specified tenant.
     *
     * @param tenantId       the ID of the tenant the user belongs to
     * @param email          the user's email address
     * @param passwordHash   the hashed password
     * @param firstName      the user's first name
     * @param lastName       the user's last name
     * @param role           the user's role (DOCTOR, PATIENT, NURSE, etc.)
     * @return the created User entity
     * @throws DuplicateResourceException if a user with the email already exists for the tenant
     * @throws ResourceNotFoundException  if the tenant does not exist
     */
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

    /**
     * Retrieves a user by their unique ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the User entity
     * @throws ResourceNotFoundException if no user with the given ID exists
     */
    public User getUserById(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    /**
     * Retrieves a user by their email address within a tenant.
     *
     * @param tenantId the ID of the tenant to search within
     * @param email    the email address to search for
     * @return the User entity matching the email
     * @throws ResourceNotFoundException if no user with the email exists for the tenant
     */
    public User getUserByEmail(Long tenantId, String email) {
        return userRepository.findByTenantIdAndEmailAndDeletedAtIsNull(tenantId, email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Retrieves all active users belonging to a tenant.
     *
     * @param tenantId the ID of the tenant
     * @return a list of users for the tenant
     */
    public List<User> getUsersByTenant(Long tenantId) {
        return userRepository.findByTenantIdAndDeletedAtIsNull(tenantId);
    }

    /**
     * Retrieves all active users of a specific role within a tenant.
     *
     * @param tenantId the ID of the tenant
     * @param role     the user role to filter by
     * @return a list of users matching the role
     */
    public List<User> getUsersByRole(Long tenantId, UserRole role) {
        return userRepository.findByTenantIdAndRoleAndDeletedAtIsNull(tenantId, role);
    }

    /**
     * Retrieves all users with a specific status within a tenant.
     *
     * @param tenantId the ID of the tenant
     * @param status   the user status to filter by
     * @return a list of users matching the status
     */
    public List<User> getUsersByStatus(Long tenantId, UserStatus status) {
        return userRepository.findByTenantIdAndStatusAndDeletedAtIsNull(tenantId, status);
    }

    /**
     * Checks whether a user with the given email exists within a tenant.
     *
     * @param tenantId the ID of the tenant
     * @param email    the email address to check
     * @return {@code true} if a user with the email exists, {@code false} otherwise
     */
    public boolean existsByEmail(Long tenantId, String email) {
        return userRepository.existsByTenantIdAndEmail(tenantId, email);
    }

    /**
     * Counts the number of users with a specific status within a tenant.
     *
     * @param tenantId the ID of the tenant
     * @param status   the user status to count
     * @return the number of users matching the status
     */
    public long countByStatus(Long tenantId, UserStatus status) {
        return userRepository.countByTenantIdAndStatus(tenantId, status);
    }

    /**
     * Updates the profile information of a user.
     *
     * @param userId      the ID of the user to update
     * @param firstName   the updated first name
     * @param lastName    the updated last name
     * @param phone       the updated phone number
     * @param dateOfBirth the updated date of birth
     * @param gender      the updated gender
     * @param address     the updated address
     * @return the updated User entity
     * @throws ResourceNotFoundException if no user with the given ID exists
     */
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

    /**
     * Updates the status of a user account.
     *
     * @param userId the ID of the user to update
     * @param status the new user status
     * @return the updated User entity
     * @throws ResourceNotFoundException if no user with the given ID exists
     */
    @Transactional
    public User updateUserStatus(Long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        return userRepository.save(user);
    }

    /**
     * Records a successful login by updating the last login timestamp
     * and resetting the failed attempt counter.
     *
     * @param userId the ID of the user who logged in
     * @throws ResourceNotFoundException if no user with the given ID exists
     */
    @Transactional
    public void recordLogin(Long userId) {
        User user = getUserById(userId);
        user.setLastLogin(LocalDateTime.now());
        user.setFailedAttempts(0);
        userRepository.save(user);
    }

    /**
     * Increments the failed login attempt counter for a user.
     *
     * @param userId the ID of the user
     * @throws ResourceNotFoundException if no user with the given ID exists
     */
    @Transactional
    public void incrementFailedAttempts(Long userId) {
        User user = getUserById(userId);
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        userRepository.save(user);
    }

    /**
     * Soft-deletes a user by setting the deletedAt timestamp.
     *
     * @param userId the ID of the user to delete
     * @throws ResourceNotFoundException if no user with the given ID exists
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
    user.setDeletedAt(LocalDateTime.now());
    userRepository.save(user);
    }
}
