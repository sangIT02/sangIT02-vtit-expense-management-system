package com.january.demo.repository;

import com.january.demo.entity.ApiPermission;
import com.january.demo.enums.PermissionName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiPermissionRepository extends JpaRepository<ApiPermission, Long> {

    List<ApiPermission> findByEnabledTrue();

    Optional<ApiPermission> findByHttpMethodAndUrlPattern(String httpMethod, String urlPattern);

    boolean existsByHttpMethodAndUrlPattern(String httpMethod, String urlPattern);

    boolean existsByPermissionName(PermissionName permissionName);
}
