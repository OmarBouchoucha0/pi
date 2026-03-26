package com.pi.backend.model.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.pi.backend.model.Department;
import com.pi.backend.model.user.enums.NurseShift;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "nurses")
@Data
@SQLDelete(sql = "UPDATE nurses SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Nurse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NurseShift shift;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
