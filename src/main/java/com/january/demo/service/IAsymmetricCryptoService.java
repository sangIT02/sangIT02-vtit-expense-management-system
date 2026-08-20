package com.january.demo.service;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface IAsymmetricCryptoService {

    /**
     * Mã hóa dữ liệu bằng khóa công khai (Public Key) theo thuật toán RSA.
     *
     * @param plainText  dữ liệu cần mã hóa
     * @param publicKey  khóa công khai dùng để mã hóa, nếu {@code null} sẽ dùng
     *                   khóa công khai mặc định cấu hình trong hệ thống
     * @return chuỗi Base64 của dữ liệu đã mã hóa
     * @throws com.january.demo.exception.EncryptionException nếu xảy ra lỗi trong quá trình mã hóa
     */
    String encrypt(String plainText, PublicKey publicKey);

    /**
     * Giải mã dữ liệu bằng khóa bí mật (Private Key) theo thuật toán RSA.
     *
     * @param cipherText  chuỗi Base64 của dữ liệu đã mã hóa cần giải mã
     * @param privateKey  khóa bí mật dùng để giải mã, nếu {@code null} sẽ dùng
     *                    khóa bí mật mặc định cấu hình trong hệ thống
     * @return dữ liệu gốc (plaintext) sau khi giải mã
     * @throws com.january.demo.exception.DecryptionException nếu giải mã thất bại
     *         (sai key, dữ liệu hỏng hoặc định dạng Base64 không hợp lệ)
     */
    String decrypt(String cipherText, PrivateKey privateKey);

    /**
     * Ký số dữ liệu bằng khóa bí mật (Private Key) để chứng minh nguồn gốc và
     * tính toàn vẹn của dữ liệu.
     *
     * @param data       dữ liệu cần ký
     * @param privateKey khóa bí mật dùng để tạo chữ ký
     * @return chữ ký số dạng Base64
     * @throws com.january.demo.exception.EncryptionException nếu xảy ra lỗi trong quá trình tạo chữ ký
     */
    String sign(String data, PrivateKey privateKey);

    /**
     * Xác minh chữ ký số bằng khóa công khai (Public Key).
     *
     * @param data        dữ liệu gốc đã được ký
     * @param signature   chữ ký số dạng Base64 cần xác minh
     * @param publicKey   khóa công khai dùng để xác minh chữ ký
     * @return {@code true} nếu chữ ký hợp lệ và dữ liệu không bị thay đổi,
     *         {@code false} nếu chữ ký không hợp lệ hoặc dữ liệu bị sai lệch
     */
    boolean verify(String data, String signature, PublicKey publicKey);
}