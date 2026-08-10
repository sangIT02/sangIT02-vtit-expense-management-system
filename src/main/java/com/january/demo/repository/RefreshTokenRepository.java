package com.january.demo.repository;

import com.january.demo.entity.RefreshToken;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Tìm refresh token theo giá trị token.
     *
     * @param token giá trị refresh token cần tìm
     * @return refresh token nếu tìm thấy, ngược lại trả về {@code null}
     */
    RefreshToken findRefreshTokenByToken(String token);
}
