package com.softuni.musichub.config;

import com.softuni.musichub.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final int TOKEN_VALIDITY_SECONDS = 864_000;

    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserService userService,
                          BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/bootstrap-4.0.0/**", "/css/**",
                        "/font-awesome/**", "/images/**",
                        "/jquery/**", "/theme/**", "/scripts/**", "/audiojs/**").permitAll()
                .antMatchers("/users/login", "/users/register").anonymous()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/comments/approve/{id}", "/comments/reject/{id}", "/comments/pending")
                .hasAnyRole("ADMIN", "MODERATOR")
                .antMatchers("/songs/upload", "/comments/post").authenticated()
                .anyRequest().permitAll()
                .and().exceptionHandling().accessDeniedPage("/songs/browse")
                .and().formLogin().defaultSuccessUrl("/songs/browse").loginPage("/users/login")
                .usernameParameter("username").passwordParameter("password")
                .and().rememberMe()
                .rememberMeParameter("rememberMe")
                .tokenValiditySeconds(TOKEN_VALIDITY_SECONDS)
                .and().logout().logoutUrl("/users/logout");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userService)
                .passwordEncoder(this.passwordEncoder);
    }
}