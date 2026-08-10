package com.january.demo.repository;

import com.january.demo.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Kiểm tra tên đăng nhập đã tồn tại trong hệ thống hay chưa.
     *
     * @param username tên đăng nhập cần kiểm tra
     * @return {@code true} nếu tên đăng nhập đã tồn tại, ngược lại {@code false}
     */
    boolean existsByUsername(String username);

    /**
     * Kiểm tra email đã tồn tại trong hệ thống hay chưa.
     *
     * @param email email cần kiểm tra
     * @return {@code true} nếu email đã tồn tại, ngược lại {@code false}
     */
    boolean existsByEmail(String email);

    /**
     * Tìm người dùng theo tên đăng nhập.
     *
     * @param username tên đăng nhập cần tìm
     * @return {@link Optional} chứa người dùng nếu tìm thấy
     */
    Optional<User> findByUsername(String username);


}

