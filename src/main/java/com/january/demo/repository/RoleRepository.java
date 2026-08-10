package com.january.demo.repository;

import com.january.demo.entity.Role;
import com.january.demo.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Tìm quyền người dùng theo tên quyền.
     *
     * @param name tên quyền cần tìm
     * @return {@link Optional} chứa quyền nếu tìm thấy
     */
    Optional<Role> findByName(RoleName name);
}
