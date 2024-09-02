package com.snyck.asistenciaelectronica.config.crypto;

import android.util.Base64;

import com.snyck.asistenciaelectronica.BuildConfig;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class cryptAEs {

    public static String encryptAES(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(BuildConfig.PERSONAL_API_USER.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(BuildConfig.PERSONAL_IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptAES(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(BuildConfig.PERSONAL_API_USER.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(BuildConfig.PERSONAL_IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = Base64.decode(encryptedText, Base64.NO_WRAP);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
