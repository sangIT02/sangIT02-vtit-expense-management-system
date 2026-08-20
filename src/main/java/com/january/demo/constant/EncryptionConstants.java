package com.january.demo.constant;

public class EncryptionConstants {
    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    public static final int IV_LENGTH = 12;          // 12 bytes là chuẩn hay dùng cho GCM
    public static final int TAG_LENGTH_BIT = 128;    // auth tag 128-bit

    public static final String RSA_ALGORITHM = "RSA";
    public static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    public static final String RSA_SIGNATURE_ALGORITHM = "SHA256withRSA";
}
