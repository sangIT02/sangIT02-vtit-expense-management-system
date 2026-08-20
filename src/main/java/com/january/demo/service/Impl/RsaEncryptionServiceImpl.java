package com.january.demo.service.Impl;


import com.january.demo.constant.EncryptionConstants;
import com.january.demo.exception.DecryptionException;
import com.january.demo.exception.EncryptionException;
import com.january.demo.service.IAsymmetricCryptoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class RsaEncryptionServiceImpl implements IAsymmetricCryptoService {
    @Value("${app.security.rsa.public-key}")
    private String publicKeyString;

    @Value("${app.security.rsa.private-key}")
    private String privateKeyString;


    @Override
    public String encrypt(String plainText, PublicKey publicKey) {
        try {
            if(publicKey == null) {
                publicKey = getPublicKeyFromString(publicKeyString);
            }
            Cipher cipher = Cipher.getInstance(EncryptionConstants.RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Lỗi mã hóa RSA", e);
        }
    }

    @Override
    public String decrypt(String cipherText, PrivateKey privateKey) {
        try {
            if(privateKey == null) {
                privateKey = getPrivateKeyFromString(privateKeyString);
            }
            Cipher cipher = Cipher.getInstance(EncryptionConstants.RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new DecryptionException("Lỗi giải mã RSA (Sai key hoặc dữ liệu hỏng)", e);
        } catch (IllegalArgumentException e) {
            throw new DecryptionException("Lỗi giải mã RSA: dữ liệu không đúng định dạng Base64", e);
        }
    }

    @Override
    public String sign(String data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(EncryptionConstants.RSA_SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Lỗi tạo chữ ký điện tử", e);
        }
    }

    @Override
    public boolean verify(String data, String signatureBase64, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(EncryptionConstants.RSA_SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            return signature.verify(signatureBytes);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            // Lỗi parse Base64 hoặc format sai -> Chữ ký không hợp lệ
            return false;
        }
    }

    // =====================================================================
    // CÁC HÀM TIỆN ÍCH (HELPER METHODS) HỖ TRỢ CHUYỂN ĐỔI CHUỖI THÀNH KEY
    // Rất cần thiết vì dữ liệu lấy từ DB thường ở dạng String (Base64)
    // =====================================================================

    public PublicKey getPublicKeyFromString(String keyBase64) {
        try {
            String cleanKey = keyBase64.replaceAll("[\\r\\n\\s]+", "");
            byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
            return KeyFactory.getInstance(EncryptionConstants.RSA_ALGORITHM).generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Lỗi parse Public Key", e);
        } catch (IllegalArgumentException e) {
            throw new EncryptionException("Lỗi parse Public Key: không đúng định dạng Base64", e);
        }
    }

    public PrivateKey getPrivateKeyFromString(String keyBase64) {
        try {
            String cleanKey = keyBase64.replaceAll("[\\r\\n\\s]+", "");
            byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
            return KeyFactory.getInstance(EncryptionConstants.RSA_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Lỗi parse Private Key", e);
        } catch (IllegalArgumentException e) {
            throw new EncryptionException("Lỗi parse Private Key: không đúng định dạng Base64", e);
        }
    }
}
