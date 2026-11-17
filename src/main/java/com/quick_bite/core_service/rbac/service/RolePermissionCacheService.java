package com.quick_bite.core_service.rbac.service;

import com.quick_bite.core_service.common.exception.ObjectNotFoundException;
import com.quick_bite.core_service.rbac.entity.Permission;
import com.quick_bite.core_service.rbac.entity.Role;
import com.quick_bite.core_service.rbac.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RolePermissionCacheService {

    private final RoleRepository roleRepository;

    private Map<String, Set<Permission>> rolePermissionsCache = new ConcurrentHashMap<>();

    public Set<Permission> getRolePermissions(String roleName) {

       Set<Permission> permissions = this.rolePermissionsCache.get(roleName);

        if (permissions == null) {
            Role role = this.roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ObjectNotFoundException(roleName));

            this.rolePermissionsCache.put(roleName, role.getPermissions());

            return role.getPermissions();
        }

        return permissions;
    }


}
