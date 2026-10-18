package com.quick_bite.core_service.security.jwt;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties (

        @NotEmpty
         String secretKey,

        @NotEmpty
        String issuer,

        @NotNull
        Duration expiration
){
}
