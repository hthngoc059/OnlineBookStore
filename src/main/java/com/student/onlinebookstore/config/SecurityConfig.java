package com.student.onlinebookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())         // Tắt CSRF để form JSP hoạt động
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()          // Cho phép tất cả request
            )
            .formLogin(form -> form.disable())     // Tắt form login mặc định của Spring
            .httpBasic(basic -> basic.disable());  // Tắt basic auth mặc định

        return http.build();
    }
}