package com.pi.backend.model.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.pi.backend.model.user.enums.AdminPrivilege;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents an admin profile linked to a user account.
 */
@Entity
@Table(name = "admins")
@Data
@SQLDelete(sql = "UPDATE admins SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "privilege_level", nullable = false)
    private AdminPrivilege privilegeLevel;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
