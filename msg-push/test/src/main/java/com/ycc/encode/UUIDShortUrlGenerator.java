package com.ycc.encode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// UUID哈希法
public class UUIDShortUrlGenerator {

    public static void main(String[] args) {
        System.out.println(generate("https://aps.xinyiglass.com/#/planSchedule/ProductionPlan/scheduling"));
    }
    public static String generate(String longUrl) {
        // 计算MD5哈希
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(longUrl.getBytes(StandardCharsets.UTF_8));
            
            // 生成4个短码候选（每个32位）
            String[] candidates = new String[4];
            for (int i = 0; i < 4; i++) {
                long hash = 0;
                for (int j = 0; j < 4; j++) {
                    hash <<= 8;
                    hash |= ((long) hashBytes[i * 4 + j]) & 0xFF;
                }
                candidates[i] = encode(hash);
            }
            
            // 返回第一个候选（实际应用中需检查冲突）
            return candidates[0];
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成短链接失败", e);
        }
    }
    
    private static String encode(long hash) {
        // 转为62进制
        StringBuilder sb = new StringBuilder();
        while (hash > 0) {
            sb.append(BASE62.charAt((int) (hash % 62)));
            hash /= 62;
        }
        // 不足6位时补零
        while (sb.length() < 6) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }
    
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
}