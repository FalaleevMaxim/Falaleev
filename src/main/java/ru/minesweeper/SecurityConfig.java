package ru.minesweeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ru.minesweeper.service.security.CustomAuthenticationProvider;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationProvider provider;

    @Autowired
    public SecurityConfig(CustomAuthenticationProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/AuthGame/**").hasRole("USER")
                .mvcMatchers("/User/Profile").hasRole("USER")
                .and()
                .formLogin().loginPage("/User/Login");
    }
}
