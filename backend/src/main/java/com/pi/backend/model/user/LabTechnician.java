package com.pi.backend.model.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.pi.backend.model.Department;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lab_technicians")
@Data
@SQLDelete(sql = "UPDATE lab_technicians SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class LabTechnician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false)
    private String certification;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
