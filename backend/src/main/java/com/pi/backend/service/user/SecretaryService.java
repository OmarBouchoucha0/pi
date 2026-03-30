package com.pi.backend.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Department;
import com.pi.backend.model.user.Secretary;
import com.pi.backend.model.user.User;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.SecretaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecretaryService {

    private final SecretaryRepository secretaryRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Secretary createSecretary(Long userId, Long departmentId) {
        User user = userService.getUserById(userId);
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));

        Secretary secretary = new Secretary();
        secretary.setUser(user);
        secretary.setDepartment(department);

        return secretaryRepository.save(secretary);
    }

    public Secretary getSecretaryById(Long secretaryId) {
        return secretaryRepository.findByIdAndDeletedAtIsNull(secretaryId)
            .orElseThrow(() -> new ResourceNotFoundException("Secretary", secretaryId));
    }

    public Secretary getSecretaryByUserId(Long userId) {
        return secretaryRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Secretary", "userId", userId));
    }

    public List<Secretary> getSecretariesByDepartment(Long departmentId) {
        return secretaryRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    @Transactional
    public Secretary updateSecretaryDepartment(Long secretaryId, Long departmentId) {
        Secretary secretary = getSecretaryById(secretaryId);
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
        secretary.setDepartment(department);
        return secretaryRepository.save(secretary);
    }

    @Transactional
    public void deleteSecretary(Long secretaryId) {
        Secretary secretary = getSecretaryById(secretaryId);
        secretaryRepository.delete(secretary);
    }
}
