package com.pi.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration providing password encoding and HTTP security rules.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Provides a BCrypt password encoder for hashing user passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the HTTP security filter chain.
     * Currently permits all requests to /api/**, Swagger UI, and API docs.
     * CSRF protection is disabled.
     *
     * @param http the HttpSecurity to configure
     * @return the built SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api/**")
                        .permitAll()
                        .anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
