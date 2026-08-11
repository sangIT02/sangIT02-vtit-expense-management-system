package com.january.demo.repository;

import com.january.demo.dto.projection.PermissionNameProjection;
import com.january.demo.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    /**
     * Tim danh sach quan he role-permission theo role.
     *
     * @param roleId id cua role can tim
     * @return danh sach quan he role-permission cua role
     */
    List<RolePermission> findByRole_Id(Long roleId);

    /**
     * Tim danh sach quan he role-permission theo permission.
     *
     * @param permissionId id cua permission can tim
     * @return danh sach quan he role-permission cua permission
     */
    List<RolePermission> findByPermission_Id(Long permissionId);

    /**
     * Tim quan he role-permission theo role va permission.
     *
     * @param roleId id cua role can tim
     * @param permissionId id cua permission can tim
     * @return {@link Optional} chua quan he role-permission neu tim thay
     */
    Optional<RolePermission> findByRole_IdAndPermission_Id(Long roleId, Long permissionId);

    /**
     * Kiem tra quan he role-permission da ton tai hay chua.
     *
     * @param roleId id cua role can kiem tra
     * @param permissionId id cua permission can kiem tra
     * @return {@code true} neu quan he da ton tai, nguoc lai {@code false}
     */
    boolean existsByRole_IdAndPermission_Id(Long roleId, Long permissionId);


}
