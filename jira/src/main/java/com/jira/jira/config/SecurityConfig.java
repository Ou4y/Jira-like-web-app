package com.jira.jira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll() // Allow all requests without authentication
                );
        return http.build();
    }
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(
            "/webjars/**", 
            "/css/**", 
            "/js/**"
        );
    }
}
