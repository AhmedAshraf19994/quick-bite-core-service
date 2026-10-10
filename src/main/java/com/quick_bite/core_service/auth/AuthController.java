package com.quick_bite.core_service.auth;

import com.quick_bite.core_service.auth.dto.AuthResponse;
import com.quick_bite.core_service.auth.dto.LoginResponseDto;
import com.quick_bite.core_service.auth.dto.LoginUserDto;
import com.quick_bite.core_service.common.Response;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("${api.endpoint.base-url}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Response<LoginResponseDto> login(@Valid @RequestBody LoginUserDto dto, HttpServletResponse response) throws NoSuchAlgorithmException {
        AuthResponse authResponse = this.authService.login(dto);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());;

        // to exclude the refreshToken from the response
        LoginResponseDto loginResponseDto = new LoginResponseDto(
                authResponse.accessToken(),
                authResponse.user()
        );

        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Login successful",
                loginResponseDto
        );
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Response<?> logout  (
            @CookieValue("refreshToken") @NotBlank String refreshToken,
            HttpServletResponse response
    ) throws NoSuchAlgorithmException {
        this.authService.logout(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new Response<>(
                true,
                HttpStatus.NO_CONTENT.value(),
                "Logout successful",
                null
        );

    }

    @PostMapping("/rotate-token")
    public Response<LoginResponseDto> rotateToken(
            @CookieValue("refreshToken") @NotBlank String refreshToken,
            HttpServletResponse response
    ) throws NoSuchAlgorithmException {
        AuthResponse authResponse = this.authService.rotateToken(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // to exclude the refreshToken from the response
        LoginResponseDto loginResponseDto = new LoginResponseDto(
                authResponse.accessToken(),
                authResponse.user()
        );

        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Rotate token successful",
                loginResponseDto
        );
    }



}
