package com.sinapipro.api.security.application;

import com.sinapipro.api.config.SecurityProperties;
import com.sinapipro.api.security.api.TokenResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityProperties properties;

    public JwtTokenService(JwtEncoder jwtEncoder,
                           JwtDecoder jwtDecoder,
                           AuthenticationManager authenticationManager,
                           SecurityProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.authenticationManager = authenticationManager;
        this.properties = properties;
    }

    public TokenResponse issueFromPassword(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BadCredentialsException("Username and password are required");
        }
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password));
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .toList();
        return issueTokens(authentication.getName(), roles);
    }

    public TokenResponse issueFromRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BadCredentialsException("Refresh token is required");
        }
        Jwt jwt = jwtDecoder.decode(refreshToken);
        if (!"refresh".equals(jwt.getClaimAsString("token_type"))) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        return issueTokens(jwt.getSubject(), jwt.getClaimAsStringList("roles"));
    }

    private TokenResponse issueTokens(String subject, List<String> roles) {
        Instant now = Instant.now();
        Instant accessTokenExpiresAt = now.plus(properties.accessToken().ttl());
        Instant refreshTokenExpiresAt = now.plus(properties.refreshToken().ttl());

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(header, accessClaims(subject, roles, now, accessTokenExpiresAt))).getTokenValue();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(header, refreshClaims(subject, roles, now, refreshTokenExpiresAt))).getTokenValue();

        return new TokenResponse(accessToken, refreshToken, "Bearer", accessTokenExpiresAt, refreshTokenExpiresAt);
    }

    private JwtClaimsSet accessClaims(String subject, List<String> roles, Instant issuedAt, Instant expiresAt) {
        return JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(subject)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("scope", properties.accessToken().scope())
                .claim("roles", roles)
                .claim("token_type", "access")
                .claim("tenant_id", properties.defaultTenantId())
                .build();
    }

    private JwtClaimsSet refreshClaims(String subject, List<String> roles, Instant issuedAt, Instant expiresAt) {
        return JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(subject)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .claim("token_type", "refresh")
                .build();
    }
}
