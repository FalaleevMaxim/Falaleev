package ru.test.service;

import java.security.NoSuchAlgorithmException;

public interface PasswordEncryptor {
    String encryptPassword(String password, String sault);
}
