package com.january.demo.entity;

import com.january.demo.enums.RoleName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "role")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 20)
    @NotNull(message = "Tên role không được để trống")
    private RoleName name;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(
            mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL
    )
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(
            mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.ALL
    )
    private Set<RolePermission> rolePermissions = new HashSet<>();
}
