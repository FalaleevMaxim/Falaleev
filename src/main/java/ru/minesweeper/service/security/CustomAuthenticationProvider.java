package ru.minesweeper.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.minesweeper.storage.JpaUserStorage;
import ru.minesweeper.storage.Storage;
import ru.minesweeper.model.User;

import java.util.ArrayList;
@Service("provider")
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private JpaUserStorage userStorage;
    private  PasswordEncryptor encryptor;

    @Autowired
    public CustomAuthenticationProvider(JpaUserStorage userStorage, PasswordEncryptor encryptor) {
        this.userStorage = userStorage;
        this.encryptor = encryptor;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = userStorage.findUserByUserName(authentication.getName());
        if(user==null) return null;
        if(encryptor.encryptPassword(authentication.getCredentials().toString(),authentication.getName()).equals(user.getPassword())){
            ArrayList<GrantedAuthority> roles = new ArrayList<>();
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(authentication.getName(),authentication.getCredentials(),roles);
        }else return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.equals(aClass);
    }
}
