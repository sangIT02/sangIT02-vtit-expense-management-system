package com.january.demo.repository;

import com.january.demo.dto.projection.PermissionNameProjection;
import com.january.demo.entity.Permission;
import com.january.demo.enums.PermissionName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Tim permission theo ten permission.
     *
     * @param name ten permission can tim
     * @return {@link Optional} chua permission neu tim thay
     */
    Optional<Permission> findByName(PermissionName name);

    /**
     * Kiem tra permission da ton tai theo ten hay chua.
     *
     * @param name ten permission can kiem tra
     * @return {@code true} neu permission da ton tai, nguoc lai {@code false}
     */
    boolean existsByName(PermissionName name);

    /**
     * Tim danh sach ten permission cua nguoi dung theo username.
     *
     * @param username ten dang nhap cua nguoi dung can lay permission
     * @return danh sach projection chua ten permission cua nguoi dung
     */
    @Query(value = """
    SELECT DISTINCT p.name
    FROM users u
    JOIN user_role  ur ON u.id = ur.user_id
    JOIN role r ON r.id = ur.role_id
    JOIN role_permission  rp ON rp.role_id = r.id
    JOIN permission p ON p.id = rp.permission_id
    WHERE u.username = :username
""", nativeQuery = true)
    List<PermissionNameProjection> findPermissionNamesByUsername(@Param("username") String username);
}
