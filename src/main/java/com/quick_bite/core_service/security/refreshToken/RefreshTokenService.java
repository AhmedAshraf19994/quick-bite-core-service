package com.quick_bite.core_service.security.refreshToken;

import com.quick_bite.core_service.auth.dto.AuthResponse;
import com.quick_bite.core_service.security.jwt.JwtService;
import com.quick_bite.core_service.security.refreshToken.exceptions.InvalidRefreshTokenException;
import com.quick_bite.core_service.user.User;
import com.quick_bite.core_service.user.UserMapper;
import com.quick_bite.core_service.user.dto.UserResponseDto;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final SecureRandom secureRandom;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    @Value("${security.refresh-token.expiration}")
    private  Duration refreshTokenDuration;

    public String createRefreshToken(User user) throws NoSuchAlgorithmException {
        // generate raw token
        String rawToken = generateRawToken();

        // hash token
        String hashedToken = hashToken(rawToken);

        LocalDateTime now = LocalDateTime.now();

        RefreshToken refreshToken = RefreshToken.builder()
                .hashedToken(hashedToken)
                .user(user)
                .expiresAt(now.plus(refreshTokenDuration))
                .build();

        this.refreshTokenRepository.save(refreshToken);

        //return token
        return rawToken;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        this.secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(token.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);

    }


    @Transactional
    public void revokeToken(String refreshToken) throws NoSuchAlgorithmException {
        //hash the token
        String hashedToken = hashToken(refreshToken);

        // get the hashed token
        Optional<RefreshToken> token = this.refreshTokenRepository.findByHashedToken(hashedToken);

        if(token.isEmpty()) {
            log.debug("Logout requested with unknown refresh token");
            return;
        }

        //revoke the token
        token.get().revokeToken();
        log.info("Token revoked successfully");

    }

    public AuthResponse rotateToken(String refreshToken) throws NoSuchAlgorithmException {
        // hash the token
        String hashedToken = hashToken(refreshToken);

        // find the token
        RefreshToken token = this.refreshTokenRepository.findByHashedToken(hashedToken)
                .orElseThrow(InvalidRefreshTokenException::new);

        // check if the token is expired
        if(token.isExpired() || token.isRevoked()) {
            log.debug("Rotate refresh token requested with expired or revoked token");
            throw new InvalidRefreshTokenException();
        }

        token.revokeToken();
        // create new token
        String rawToken = generateRawToken();

        // hash the token
        String newHashedToken = hashToken(rawToken);

        // build the entity
        LocalDateTime now = LocalDateTime.now();
        RefreshToken newToken = RefreshToken.builder()
                .hashedToken(newHashedToken)
                .user(token.getUser())
                .expiresAt(now.plus(refreshTokenDuration))
                .build();

        this.refreshTokenRepository.save(newToken);

        String accessToken = this.jwtService.generateToken(token.getUser());

        UserResponseDto userResponseDto = this.userMapper.toUserResponseDto(token.getUser());

        return new AuthResponse(accessToken, rawToken, userResponseDto);

    }
}
