package com.quick_bite.core_service.auth;

import com.quick_bite.core_service.auth.dto.AuthResponse;
import com.quick_bite.core_service.auth.dto.LoginUserDto;
import com.quick_bite.core_service.common.exception.ObjectNotFoundException;
import com.quick_bite.core_service.rbac.dto.RestaurantMemberInfo;
import com.quick_bite.core_service.rbac.entity.RestaurantMember;
import com.quick_bite.core_service.rbac.repository.RestaurantMemberRepository;
import com.quick_bite.core_service.security.jwt.JwtService;
import com.quick_bite.core_service.security.refreshToken.RefreshTokenService;
import com.quick_bite.core_service.user.User;
import com.quick_bite.core_service.user.UserMapper;
import com.quick_bite.core_service.user.UserRepository;
import com.quick_bite.core_service.user.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService   refreshTokenService;

    private final RestaurantMemberRepository restaurantMemberRepository;

    @Transactional
    public AuthResponse login(LoginUserDto dto) throws NoSuchAlgorithmException {
        //verify user exists
        User user = this.userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        //verify password
        if (!this.passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        RestaurantMemberInfo memberInfo = null;
        // populate the jwt with user restaurant info if he is restaurant user
        if (user.getRole() == UserRole.RESTAURANT_USER) {
            RestaurantMember member = this.restaurantMemberRepository.findByUser(user)
                    .orElseThrow(() -> new ObjectNotFoundException("Restaurant member"));

             memberInfo = new RestaurantMemberInfo(
                    member.getRestaurant().getId(),
                    member.getRole().getName(),
                    member.getBranches().stream()
                            .map(memberBranch -> memberBranch.getBranch().getId()).collect(Collectors.toSet())
            );
        }

        String refreshToken = this.refreshTokenService.createRefreshToken(user);

        String accessToken = this.jwtService.generateToken(user, memberInfo);

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
