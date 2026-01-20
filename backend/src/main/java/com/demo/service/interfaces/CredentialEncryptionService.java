package com.demo.service.interfaces;

public interface CredentialEncryptionService {
    String encrypt(String plainText);
    String decrypt(String encryptedText);
}