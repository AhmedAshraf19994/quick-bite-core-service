package com.quick_bite.core_service.security.jwt;

import com.quick_bite.core_service.rbac.dto.RestaurantMemberInfo;
import com.quick_bite.core_service.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    private final JwtEncoder jwtEncoder;

    public String generateToken (User user, RestaurantMemberInfo restaurantMemberInfo) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(this.jwtProperties.issuer())
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(this.jwtProperties.expiration()))
                .claim("authorities", user.getRole().getValue())
                .claim("restaurantMemberInfo", restaurantMemberInfo)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

    }
}
