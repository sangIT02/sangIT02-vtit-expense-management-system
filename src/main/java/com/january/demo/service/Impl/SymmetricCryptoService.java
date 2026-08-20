package com.january.demo.service.Impl;


import com.january.demo.constant.EncryptionConstants;
import com.january.demo.exception.DecryptionException;
import com.january.demo.exception.EncryptionException;
import com.january.demo.service.ISymmetricCryptoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import static com.january.demo.constant.EncryptionConstants.*;

@Service
public class SymmetricCryptoService implements ISymmetricCryptoService {

    private final SecretKeySpec secretKeySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public SymmetricCryptoService(@Value("${app.security.aes-key}") String aesKey) {

        // chuyển secretkey từ string -> bytes
        String cleanKey = aesKey.replaceAll("[\\r\\n\\s]+", "");

        // 2. GIẢI MÃ BASE64 để lấy về mảng 32 bytes nhị phân thực sự
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("AES-256 key must be exactly 32 bytes");
        }

        this.secretKeySpec = new SecretKeySpec(keyBytes, EncryptionConstants.AES_ALGORITHM);
    }
    @Override
    public String encrypt(String plainText) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(EncryptionConstants.AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Lỗi mã hóa dữ liệu AES", e);
        }
    }

    @Override
    public String decrypt(String encryptedText) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            byte[] iv = Arrays.copyOfRange(combined, 0, IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(combined, IV_LENGTH, combined.length);

            Cipher cipher = Cipher.getInstance(EncryptionConstants.AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);

            byte[] plainBytes = cipher.doFinal(cipherText);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new DecryptionException("Lỗi giải mã dữ liệu AES", e);
        } catch (IllegalArgumentException e) {
            throw new DecryptionException("Dữ liệu mã hóa không đúng định dạng Base64", e);
        }
    }
}
