package com.example.Social_Media_WhichApp.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptPassword {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    // Khóa bí mật cho AES
    private static final String SECRET_KEY = "thuctaplaptrinht";

    // Hàm mã hóa mật khẩu
    public static String encrypt(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Hàm giải mã mật khẩu
    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }
    public static void main(String[] args) {
        try {
            String originalPassword = "12345";
            String encryptedPassword = EncryptPassword.encrypt(originalPassword);
            String decryptedPassword = EncryptPassword.decrypt("lEY8/FUyRdDjzoSpNiy95g==");

            System.out.println("Encrypted Password: " + encryptedPassword);
            System.out.println("Decrypted Password: " + decryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
