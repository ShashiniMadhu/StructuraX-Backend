package com.structurax.root.structurax.root.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.PUT, "/api/supplier/payments/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/designer/**").permitAll()
                        .requestMatchers("/transactions/**").permitAll()
                        .requestMatchers("/qs/**").permitAll()
                        .requestMatchers("/sqs/**").permitAll()
                        .requestMatchers("/wbs/**").permitAll()
                        .requestMatchers("/supplier/**").permitAll()
                        .requestMatchers("/site_supervisor/**").permitAll()
                        .requestMatchers("/pdf/**").permitAll()
                        .requestMatchers("/quotation/**").permitAll()
                        .requestMatchers("/purchase-order-pdf/**").permitAll()
                        .requestMatchers("/purchase-order/**").permitAll()
                        .requestMatchers("/legal_officer/**").permitAll()
                        .requestMatchers("/project_manager/**").permitAll()
                        .requestMatchers("/director/**").permitAll()
                        .requestMatchers("/boq/**").permitAll()
                        .requestMatchers("/financial_officer/**").permitAll()

                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
