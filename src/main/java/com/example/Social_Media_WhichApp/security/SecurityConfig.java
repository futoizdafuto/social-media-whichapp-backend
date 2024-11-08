package com.example.Social_Media_WhichApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF theo cách mới
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
<<<<<<< HEAD
                        .requestMatchers("/api/users/login", "/api/users/register" , "/uploads/**", "/api/**").permitAll()
                        // Cho phép truy cập vào đăng nhập, đăng ký
//                        .requestMatchers("/api/**").permitAll()
=======
                        .requestMatchers("/api/users/login","/api/users/reLogin",  "/api/users/register").permitAll() // Cho phép truy cập vào đăng nhập, đăng ký
>>>>>>> 863138a330a1516ee0a7b5c34d1d213c934189aa
                        .anyRequest().authenticated());

        // Thêm JwtAuthenticationFilter vào đây
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
