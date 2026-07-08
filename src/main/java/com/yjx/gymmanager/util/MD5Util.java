package com.yjx.gymmanager.util;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class MD5Util {

    /**
     * 普通MD5加密 32位小写
     */
    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    // 可选：加盐MD5
    public static String md5Salt(String str, String salt) {
        return md5(str + salt);
    }
}