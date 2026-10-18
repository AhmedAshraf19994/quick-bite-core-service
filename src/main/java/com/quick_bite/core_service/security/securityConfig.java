package com.quick_bite.core_service.security;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.SecureRandom;

@Configuration
@RequiredArgsConstructor
public class securityConfig {

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    private final CustomBearerAuthenticationEntryPoint customBearerAuthenticationEntryPoint;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return  http
                    .authorizeHttpRequests(auth ->
                            auth.requestMatchers(  baseUrl + "/auth/login").permitAll()
                                    .requestMatchers(  baseUrl + "/auth/rotate-token").permitAll()
                                    .requestMatchers(HttpMethod.POST,baseUrl + "/users").permitAll() // for user to sign up
                            .anyRequest().authenticated()
                    )
               .oauth2ResourceServer(oauth2 ->
                       oauth2.jwt(Customizer.withDefaults())
                               .authenticationEntryPoint(customBearerAuthenticationEntryPoint)
                               .accessDeniedHandler(customAccessDeniedHandler))
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                    .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
