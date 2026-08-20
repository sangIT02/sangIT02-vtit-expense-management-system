package com.january.demo.service;

public interface ISymmetricCryptoService {

    /**
     * Mã hóa dữ liệu bằng thuật toán AES-256.
     *
     * <p>Quá trình mã hóa tự động sinh một IV (Initialization Vector) ngẫu nhiên
     * và kết hợp IV cùng bản mã (ciphertext) trước khi trả về kết quả dưới dạng
     * chuỗi Base64.</p>
     *
     * @param plainText dữ liệu dạng chữ thường (plaintext) cần mã hóa
     * @return chuỗi Base64 chứa IV và bản mã đã mã hóa
     * @throws com.january.demo.exception.EncryptionException nếu xảy ra lỗi trong quá trình mã hóa
     */
    String encrypt(String plainText);

    /**
     * Giải mã dữ liệu đã được mã hóa bằng thuật toán AES-256.
     *
     * <p>Chuỗi đầu vào phải là kết quả của {@link #encrypt(String)}, được mã hóa
     * dạng Base64 và bao gồm IV ở phần đầu của dữ liệu. IV này sẽ được tách ra
     * và dùng trong quá trình giải mã.</p>
     *
     * @param cipherText chuỗi Base64 chứa IV và bản mã cần giải mã
     * @return dữ liệu gốc (plaintext) sau khi giải mã
     * @throws com.january.demo.exception.DecryptionException nếu giải mã thất bại
     *         (chuỗi không hợp lệ, mã hóa không đúng định dạng hoặc key không khớp)
     */
    String decrypt(String cipherText);
}