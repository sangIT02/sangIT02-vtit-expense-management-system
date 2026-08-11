package com.january.demo.entity;

import com.january.demo.enums.PermissionName;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    @NotNull(message = "Ten quyen han khong duoc de trong")
    private PermissionName name;

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "Mo ta khong duoc vuot qua 255 ky tu")
    private String description;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<RolePermission> rolePermissions = new HashSet<>();
}
