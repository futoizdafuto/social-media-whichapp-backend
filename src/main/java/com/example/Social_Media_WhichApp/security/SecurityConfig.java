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
//                        .requestMatchers("/api/users/login", "/api/users/register" , "/uploads/**", "/api/**" , "/api/users/reLogin").permitAll()
                        .requestMatchers("/api/users/login",
                                "/api/users/register" ,
                                "/api/users/reLogin" ,
                                "/api/users/oauth2/callback/google",
                                "/uploads/**").permitAll()
                        // Cho phép truy cập vào đăng nhập, đăng ký
//                        .requestMatchers("/api/**").permitAll()

                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/users/oauth2/callback/google", true) // Chuyển hướng sau khi đăng nhập thành công
                );

        // Thêm JwtAuthenticationFilter vào đây
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
