package com.macro.mall.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AES {

    private static String CHARSET_NAME = "UTF-8";

    //加密 加密模式ECB 填充方式PKCS5PADDING，这里的AES加密使用的是javax.crypto包下的
    public static String encrypt(String input, String key) {
        byte[] crypted = null;
        try {

            SecretKeySpec skey = new SecretKeySpec(key.getBytes(CHARSET_NAME), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes(CHARSET_NAME));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        Base64.Encoder encoder = Base64.getEncoder();

        return encoder.encodeToString(crypted);
    }

    //解密
    public static String decrypt(String input, String key) {
        byte[] output = null;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(CHARSET_NAME), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(decoder.decode(input.getBytes(CHARSET_NAME)));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return output.toString();
    }
}
