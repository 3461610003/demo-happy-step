package com.example.demo.happy.step.utils;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * <p>
 * Base64Utils
 * </p>
 *
 * @author zhenghao
 * @date 2020/9/24 14:48
 */
public class CryptUtils {

    /**
     * base64加密
     * @param content 加密内容
     * @return 加密结果
     */
    public static byte[] base64Encrypt(final byte[] content) {
        return Base64.getEncoder().encode(content);
    }

    public static String base64Encrypt(final String content, Charset charset) {
        return new String(base64Encrypt(content.getBytes(charset)), charset);
    }

    public static String base64Encrypt(final String content) {
        return new String(base64Encrypt(content.getBytes()));
    }

    /**
     * base64解密
     * @param encoderContent 已加密内容
     * @return 解密结果
     */
    public static byte[] base64Decrypt(final byte[] encoderContent) {
        return Base64.getDecoder().decode(encoderContent);
    }

    public static String base64Decrypt(final String encoderContent) {
        return new String(Base64.getDecoder().decode(encoderContent));
    }
    public static String base64Decrypt(final String encoderContent, Charset charset) {
        return new String(Base64.getDecoder().decode(encoderContent), charset);
    }

    public static void main(String[] args) {
        String hellos = base64Encrypt("salfkjslaflajfdklal");
        System.out.println(hellos);
        System.out.println("-------------------------------");
        String bytes = base64Decrypt(hellos);
        System.out.println(bytes);
        System.out.println(base64Decrypt("H++/vQgAAAAAAAAAS0xKNjQy77+977+907fvv71RdFBWcXR0BABK77+977+9SBIAAAA="));
    }

}
