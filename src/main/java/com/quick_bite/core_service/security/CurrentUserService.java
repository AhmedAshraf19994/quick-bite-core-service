package com.quick_bite.core_service.security;

import com.quick_bite.core_service.user.User;
import com.quick_bite.core_service.user.UserRole;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final EntityManager entityManager;

    public User getCurrentUser() {
        Jwt jwt = this.getJwt();
        Long userId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));
        return this.entityManager.getReference(User.class, userId);
    }

    public boolean isAdmin() {
        Jwt jwt = this.getJwt();
        return Objects.equals(jwt.getClaim("authorities"), UserRole.SYSTEM_ADMIN.getValue());
    }


    private Jwt getJwt () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);

        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new RuntimeException("User not authenticated");
        }
        return (Jwt) authentication.getPrincipal();

    }
}
