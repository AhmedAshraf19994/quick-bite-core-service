package com.quick_bite.core_service.user;

import com.quick_bite.core_service.user.dto.CreateUserDto;
import com.quick_bite.core_service.user.dto.UpdateUserDto;
import com.quick_bite.core_service.user.dto.UserResponseDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

//    @Mapping(target="role", source = "role", qualifiedByName = "setRole")
    User toUser(CreateUserDto dto);

    UserResponseDto toUserResponseDto(User user);


    void updateUser(UpdateUserDto dto,@MappingTarget User user);

    default UserRole map(String value) {
        return UserRole.setRole(value);
    }
}
