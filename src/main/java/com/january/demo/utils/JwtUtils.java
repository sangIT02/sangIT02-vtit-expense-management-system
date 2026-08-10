package com.january.demo.utils;

import com.january.demo.entity.Role;
import com.january.demo.entity.UserRole;
import com.january.demo.security.CustomUserDetails;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * Cung cấp các tiện ích tạo, đọc và kiểm tra JWT cho luồng xác thực của hệ thống.
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret-key}")
    private String SIGNER_KEY;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * Tạo access token cho người dùng đã xác thực.
     *
     * <p>Token được ký bằng thuật toán HS256 và chứa các thông tin chính như username,
     * danh sách role, scope, loại token, thời gian hết hạn và userId.</p>
     *
     * @param userDetails thông tin người dùng đã xác thực
     * @return access token đã được ký và serialize thành chuỗi JWT
     */
    public String generateToken(CustomUserDetails userDetails) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        // 2. Tạo Payload (Claims)
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(userDetails.getUser().getUsername())
                .claim("role", userDetails.getUser().getRoles().stream()
                        .map(UserRole::getRole)
                        .map(Role::getName)
                        .map(Enum::name)
                        .toList())
                .claim("type", "ACCESS")
                .issuer("http://financial-app.com") // Ai phát hành (Optional)
                .issueTime(new Date()) // Thời gian phát hành
                .expirationTime(new Date(System.currentTimeMillis() + accessTokenExpiration * 3))
                .jwtID(java.util.UUID.randomUUID().toString())
                .claim("userId", userDetails.getUser().getId());

        if (userDetails.getAuthorities() != null) {
            claimsBuilder.claim("scope", userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList()));
        }

        JWTClaimsSet claimsSet = claimsBuilder.build();

        // 3. Tạo đối tượng SignedJWT (Kết hợp Header + Payload)
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        // 4. Ký Token (Sign)
        try {
            JWSSigner signer = new MACSigner(SIGNER_KEY.getBytes());
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException("Lỗi khi ký Token: " + e.getMessage());
        }

        // 5. Serialize ra chuỗi String
        return signedJWT.serialize();
    }

    /**
     * Tạo refresh token cho người dùng đã xác thực.
     *
     * <p>Refresh token dùng để cấp lại cặp token mới khi access token hết hạn và được
     * phân biệt bằng claim {@code type = REFRESH}.</p>
     *
     * @param userDetails thông tin người dùng đã xác thực
     * @return refresh token đã được ký và serialize thành chuỗi JWT
     */
    public String generateRefreshToken(CustomUserDetails userDetails) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        // 2. Tạo Payload (Claims)
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(userDetails.getUser().getUsername())
                .claim("type", "REFRESH")   // phân biệt với access token
                .issuer("http://financial-app.com") // Ai phát hành (Optional)
                .issueTime(new Date()) // Thời gian phát hành
                .expirationTime(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .jwtID(java.util.UUID.randomUUID().toString())
                .claim("userId", userDetails.getUser().getId());

        JWTClaimsSet claimsSet = claimsBuilder.build();

        // 3. Tạo đối tượng SignedJWT (Kết hợp Header + Payload)
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        // 4. Ký Token (Sign)
        try {
            JWSSigner signer = new MACSigner(SIGNER_KEY.getBytes());
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException("Lỗi khi ký Token: " + e.getMessage());
        }

        // 5. Serialize ra chuỗi String
        return signedJWT.serialize();
    }

    /**
     * Phân tích token và lấy danh sách claim sau khi kiểm tra chữ ký.
     *
     * @param token JWT cần phân tích
     * @return claim của token nếu token hợp lệ, ngược lại trả về {@code null}
     */
    private JWTClaimsSet getClaimsFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Chữ ký token không hợp lệ");
            }
            return signedJWT.getJWTClaimsSet();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Lấy username từ subject của JWT.
     *
     * @param token JWT cần đọc username
     * @return username trong token nếu đọc được, ngược lại trả về {@code null}
     */
    public String extractUsername(String token) {
        JWTClaimsSet claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }


    /**
     * Kiểm tra access token có hợp lệ với thông tin người dùng hay không.
     *
     * <p>Token được xem là hợp lệ khi chữ ký đúng, token chưa hết hạn và username trong
     * token khớp với username của {@link UserDetails}.</p>
     *
     * @param token access token cần kiểm tra
     * @param userDetails thông tin người dùng dùng để đối chiếu username
     * @return {@code true} nếu token hợp lệ, ngược lại {@code false}
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            // Bước 1: Parse token
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Bước 2: Kiểm tra Chữ ký (Signature) xem có đúng là Server mình ký không
            // Đây là bước quan trọng để chống hack sửa token
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            boolean isVerified = signedJWT.verify(verifier);

            // Bước 3: Kiểm tra Hết hạn (Expiration)
            // new Date() lấy giờ hiện tại. before(expiration) nghĩa là hiện tại chưa vượt quá giờ hết hạn
            boolean isNotExpired = new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());

            // Bước 4: Kiểm tra Username có khớp với UserDetails trong DB không
            String usernameInToken = signedJWT.getJWTClaimsSet().getSubject();
            boolean isUsernameMatch = usernameInToken.equals(userDetails.getUsername());

            // Token hợp lệ khi thỏa mãn cả 3 điều kiện
            return isVerified && isNotExpired && isUsernameMatch;

        } catch (Exception e) {
            // Bất cứ lỗi gì (Sai chữ ký, sai format...) đều coi là không hợp lệ
            return false;
        }
    }

    /**
     * Kiểm tra refresh token có chữ ký hợp lệ và chưa hết hạn hay không.
     *
     * @param token refresh token cần kiểm tra
     * @return {@code true} nếu refresh token hợp lệ, ngược lại {@code false}
     */
    public boolean isRefrechTokenValid(String token) {
        try {
            // Bước 1: Parse token
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Bước 2: Kiểm tra Chữ ký (Signature) xem có đúng là Server mình ký không
            // Đây là bước quan trọng để chống hack sửa token
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            boolean isVerified = signedJWT.verify(verifier);

            // Bước 3: Kiểm tra Hết hạn (Expiration)
            // new Date() lấy giờ hiện tại. before(expiration) nghĩa là hiện tại chưa vượt quá giờ hết hạn
            boolean isNotExpired = new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());

            // Bước 4: Kiểm tra Username có khớp với UserDetails trong DB không
            String usernameInToken = signedJWT.getJWTClaimsSet().getSubject();

            // Token hợp lệ khi thỏa mãn cả 3 điều kiện
            return isVerified && isNotExpired;

        } catch (Exception e) {
            // Bất cứ lỗi gì (Sai chữ ký, sai format...) đều coi là không hợp lệ
            return false;
        }
    }

    public Date getExpirationFromToken(String token) {
        JWTClaimsSet claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpirationTime() : null;
    }

}
