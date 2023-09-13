package com.panera.cmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CMTConfig {

    private static final int LOG_ROUNDS = 6;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(LOG_ROUNDS);
    }
}
