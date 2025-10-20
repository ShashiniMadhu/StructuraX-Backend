package com.structurax.root.structurax.root.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded employee profile images
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // Serve uploaded project documents
        registry.addResourceHandler("/project-documents/**")
                .addResourceLocations("file:uploads/project-documents/");

        // Serve payment receipts
        registry.addResourceHandler("/payment-receipts/**")
                .addResourceLocations("file:uploads/payment-receipts/");

        // Serve project images
        registry.addResourceHandler("/project-images/**")
                .addResourceLocations("file:uploads/project-images/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:5174", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}