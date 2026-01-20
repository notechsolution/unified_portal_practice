package com.demo.util;

import java.security.SecureRandom;
import java.util.Base64;

public class AESKeyGenerator {
    
    public static void main(String[] args) {
        // 生成256位（32字节）的AES密钥
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        String base64Key = Base64.getEncoder().encodeToString(key);
        
        System.out.println("生成的AES密钥 (Base64编码): " + base64Key);
        System.out.println("密钥长度: " + key.length + " 字节");
        System.out.println("请将这个密钥添加到 application.properties 文件中:");
        System.out.println("credential.encryption.key=" + base64Key);
    }
}