package com.quick_bite.core_service.auth;

import com.quick_bite.core_service.auth.dto.AuthResponse;
import com.quick_bite.core_service.auth.dto.LoginUserDto;
import com.quick_bite.core_service.security.jwt.JwtService;
import com.quick_bite.core_service.security.refreshToken.RefreshTokenService;
import com.quick_bite.core_service.user.User;
import com.quick_bite.core_service.user.UserMapper;
import com.quick_bite.core_service.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService   refreshTokenService;

    public AuthResponse login(LoginUserDto dto) throws NoSuchAlgorithmException {
        //verify user exists
        User user = this.userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        //verify password
        if (!this.passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        //to do create refresh token
        String refreshToken = this.refreshTokenService.createRefreshToken(user);

        String accessToken = this.jwtService.generateToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                this.userMapper.toUserResponseDto(user)
        );
    }

    public void logout(String refreshToken) throws NoSuchAlgorithmException {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        this.refreshTokenService.revokeToken(refreshToken);
    }

    public AuthResponse rotateToken(String refreshToken) throws NoSuchAlgorithmException {
        return this.refreshTokenService.rotateToken(refreshToken);
    }


}
