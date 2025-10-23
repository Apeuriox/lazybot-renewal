package me.aloic.lazybot.util;

import java.security.SecureRandom;
import java.util.Base64;

public class BadgeKeyGenerator
{
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";



    public static String generateKeyBase64() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        // 输出格式：XXXX-XXXX-XXXX
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(bytes)
                .substring(0, 20)
                .replaceAll("(.{4})(?!$)", "$1-")
                .toUpperCase();
    }
    public static String generateKey(int segmentCount, int segmentLength) {
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < segmentCount; i++) {
            if (i > 0) key.append('-');
            for (int j = 0; j < segmentLength; j++) {
                key.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
            }
        }
        return key.toString();
    }



}
