package tn.esprit.pi.entity.user;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.enums.user.RolesEnum;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RolesEnum role;

    private String description;
}
