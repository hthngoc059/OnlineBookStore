package com.student.onlinebookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
            )
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()          // Cho phép tất cả request
            )
            .formLogin(form -> form.disable())     // Tắt form login mặc định của Spring
            .httpBasic(basic -> basic.disable());  // Tắt basic auth mặc định

        return http.build();
    }
    @Bean
    public HttpFirewall allowDoubleSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);   // Cho phép "//"
        firewall.setAllowSemicolon(true);               
        firewall.setAllowBackSlash(true);               
        firewall.setAllowUrlEncodedSlash(true);         
        return firewall;
    }
}