package com.january.demo.entity;

import com.january.demo.enums.PermissionName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "api_permission",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_api_permission_method_url",
                        columnNames = {"http_method", "url_pattern"}
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiPermission extends BaseEntity {

    @Column(name = "http_method", nullable = false, length = 10)
    @NotBlank(message = "HTTP method khong duoc de trong")
    @Size(max = 10, message = "HTTP method khong duoc vuot qua 10 ky tu")
    private String httpMethod;

    @Column(name = "url_pattern", nullable = false, length = 255)
    @NotBlank(message = "URL pattern khong duoc de trong")
    @Size(max = 255, message = "URL pattern khong duoc vuot qua 255 ky tu")
    private String urlPattern;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_name", nullable = false, length = 100)
    @NotNull(message = "Quyen han khong duoc de trong")
    private PermissionName permissionName;

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "Mo ta khong duoc vuot qua 255 ky tu")
    private String description;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;
}
