package ru.minesweeper.service.security;

public interface PasswordEncryptor {
    String encryptPassword(String password, String sault);
}
