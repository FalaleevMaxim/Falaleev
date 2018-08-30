package ru.minesweeper.service.security;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class Sha1Encryptor implements PasswordEncryptor{
    private static int ITERATIONS = 3;
    String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte aResult : result) {
            sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public String encryptPassword(String password, String sault, int iterations){
        try {
            for(int i=0;i<iterations;i++){
                password = sha1(password+sault);
            }
        } catch (NoSuchAlgorithmException e) {
            password+=sault;
            e.printStackTrace();
        }
        return password;
    }


    @Override
    public String encryptPassword(String password, String sault) {
        return encryptPassword(password,sault,ITERATIONS);
    }
}
