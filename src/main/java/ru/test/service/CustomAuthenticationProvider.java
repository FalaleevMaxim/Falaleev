package ru.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.test.model.Storage;
import ru.test.model.User;
import ru.test.model.UserStorage;

import java.util.ArrayList;
@Service("provider")
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private Storage<User> userStorage;
    private  PasswordEncryptor encryptor;

    @Autowired
    public CustomAuthenticationProvider(Storage<User> userStorage, PasswordEncryptor encryptor) {
        this.userStorage = userStorage;
        this.encryptor = encryptor;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = userStorage.findByName(authentication.getName());
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
