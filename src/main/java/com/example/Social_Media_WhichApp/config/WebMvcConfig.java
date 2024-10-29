package com.example.Social_Media_WhichApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để ánh xạ thư mục uploads
        registry.addResourceHandler("/static/uploads/**")
                .addResourceLocations("file:src/main/resources/static/uploads");
    }
}