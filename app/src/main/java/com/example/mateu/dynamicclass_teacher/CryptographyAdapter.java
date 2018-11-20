package com.example.mateu.dynamicclass_teacher;

import android.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.Cipher;

public class CryptographyAdapter {

    private static String IV = "AAAAAAAAAAAAAAAA";
    private static String keyComplement = "DNMC";


    public static String encryptText(String pureText, String cryptKey){
        try {
            return encrypt(pureText, cryptKey);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decryptText(String pureText, String cryptKey){
        try {
            return decrypt(pureText, cryptKey);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    private static String encrypt(String pureText, String cryptKey) throws Exception {
        String finalKey = cryptKey + keyComplement;
        Cipher encripta = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(finalKey.getBytes("UTF-8"), "AES");
        encripta.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        byte[] cryptoBytes = encripta.doFinal(pureText.getBytes("UTF-8"));
        return Base64.encodeToString(cryptoBytes, Base64.DEFAULT);
    }

    private static String decrypt(String cryptedText, String cryptKey) throws Exception{
        String finalKey = cryptKey + keyComplement;
        byte[] cryptedBytes = Base64.decode(cryptedText, Base64.DEFAULT);
        Cipher decripta = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(finalKey.getBytes("UTF-8"), "AES");
        decripta.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        return new String(decripta.doFinal(cryptedBytes),"UTF-8");
    }

}
