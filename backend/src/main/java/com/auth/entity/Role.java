package com.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.EqualsAndHashCode;

/**
 * Role entity representing user roles in the system.
 * Supports role-based access control (RBAC).
 */
@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private RoleName name;
}
