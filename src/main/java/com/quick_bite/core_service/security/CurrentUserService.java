package com.quick_bite.core_service.security;

import com.quick_bite.core_service.common.exception.ForbiddenActionException;
import com.quick_bite.core_service.common.exception.UnauthenticatedException;
import com.quick_bite.core_service.rbac.dto.RestaurantMemberInfo;
import com.quick_bite.core_service.rbac.service.RolePermissionCacheService;
import com.quick_bite.core_service.user.User;
import com.quick_bite.core_service.user.UserRole;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final EntityManager entityManager;

    private final ObjectMapper  objectMapper;

    public User getCurrentUser() {

        Long userId = Long.parseLong(Objects.requireNonNull(this.getJwt().getSubject()));
        return this.entityManager.getReference(User.class, userId);
    }

    public boolean isAdmin() {
        return Objects.equals(this.getJwt().getClaim("authorities"), UserRole.SYSTEM_ADMIN.getValue());
    }

    public Long getCurrentUserId() {
        return Long.parseLong(Objects.requireNonNull(getJwt().getSubject()));
    }


    private Jwt getJwt () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthenticatedException();
        }
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new UnauthenticatedException();
        }
        return (Jwt) authentication.getPrincipal();
    }

    public RestaurantMemberInfo getRestaurantMemberInfo () {
        Object claim =  getJwt().getClaim("restaurantMemberInfo");
        if (claim == null) {
            throw new ForbiddenActionException();
        }
        return this.objectMapper.convertValue(claim, RestaurantMemberInfo.class);
    }




}
