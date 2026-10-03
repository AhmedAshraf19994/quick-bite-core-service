package com.quick_bite.core_service.user;

import com.quick_bite.core_service.user.exceptions.UseRoleNotValidException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum UserRole {

    USER("user"),
    SYSTEM_ADMIN("system_admin"),
    DELIVERY_AGENT("delivery_agent"),
    RESTAURANT_USER("restaurant_user");

    private final String value;

    public static UserRole setRole (String roleAsString) {
        return Stream.of(values())
                .filter(role -> role.value.equalsIgnoreCase(roleAsString))
                .findFirst()
                .orElseThrow(UseRoleNotValidException::new);
    }

}
