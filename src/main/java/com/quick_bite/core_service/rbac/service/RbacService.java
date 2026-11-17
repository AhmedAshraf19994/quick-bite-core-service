package com.quick_bite.core_service.rbac.service;

import com.quick_bite.core_service.rbac.dto.RestaurantMemberInfo;
import com.quick_bite.core_service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RbacService {

    private final CurrentUserService currentUserService;

    private final RolePermissionCacheService rolePermissionCacheService;


    public boolean hasResourceAccess(String resource, String action) {
        if(this.currentUserService.isAdmin()) {
            return true;
        }
        String currentUserRestaurantRole = this.currentUserService.getRestaurantMemberInfo().restaurantRole();

        return this.rolePermissionCacheService.getRolePermissions(currentUserRestaurantRole)
                .stream()
                .anyMatch(permission -> permission.getResource().equals(resource) && permission.getAction().equals(action));
    }

    public boolean isRestaurantMember (Long restaurantId) {
        return this.currentUserService.isAdmin() || this.currentUserService.getRestaurantMemberInfo().restaurantId().equals(restaurantId) ;
    }

    public boolean isRestaurantOwner (Long restaurantId) {
        if(this.currentUserService.isAdmin()) {
            return true;
        }

        RestaurantMemberInfo memberInfo = this.currentUserService.getRestaurantMemberInfo();
        return Objects.equals(memberInfo.restaurantId(), restaurantId)
                &&  memberInfo.restaurantRole().equals("owner") ;
    }

}
