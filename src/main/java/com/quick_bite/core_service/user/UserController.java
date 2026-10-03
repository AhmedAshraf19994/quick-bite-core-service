package com.quick_bite.core_service.user;

import com.quick_bite.core_service.common.Response;
import com.quick_bite.core_service.user.dto.CreateUserDto;
import com.quick_bite.core_service.user.dto.UpdateUserDto;
import com.quick_bite.core_service.user.dto.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<UserResponseDto> createUser(@Valid @RequestBody CreateUserDto dto) {
        UserResponseDto user =  this.userService.createUser(dto);
        return new Response<>(
                true,
                HttpStatus.CREATED.value(),
                "User created successfully",
                user
        );
    }

    @GetMapping
    public Response<List<UserResponseDto>> getAllUsers () {
        List<UserResponseDto> users = this.userService.getAllUsers();

        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Users find success",
                users
        );
    }

    @GetMapping("/{userId}")
    public Response<UserResponseDto> getUserById(@PathVariable("userId") long userId) {
        UserResponseDto  user = this.userService.getUserById(userId);
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "User find success",
                user
        );
    }

    @PatchMapping("/{userId}")
    public Response<UserResponseDto> updateUserById(
            @PathVariable("userId") Long userId ,
            @Valid @RequestBody UpdateUserDto dto
    ) {
        UserResponseDto user = this.userService.updateUserById(userId, dto);
        return new Response<>(
                true,
                    HttpStatus.OK.value(),
                    "User updated successfully",
                    user
        );
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Response<?> deleteUserById(@PathVariable("userId") Long userId) {
        this.userService.deleteUserById(userId);
        return new Response<>(
                true,
                HttpStatus.NO_CONTENT.value(),
                "User deleted successfully",
                null
        );
    }
}
