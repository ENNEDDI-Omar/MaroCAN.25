package com.projects.server.domain.entities;

import com.projects.server.domain.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Représente un rôle dans le système.
 * Un rôle peut être associé à plusieurs utilisateurs.
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}