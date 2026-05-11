package com.sinapipro.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties("sinapipro.security")
public record SecurityProperties(
        @DefaultValue("sinapipro") String issuer,
        @DefaultValue("0123456789abcdef0123456789abcdef0123456789abcdef") String secret,
        @DefaultValue("00000000-0000-0000-0000-000000000001") String defaultTenantId,
        DemoUser demoUser,
        AccessToken accessToken,
        RefreshToken refreshToken
) {

    public SecurityProperties {
        demoUser = demoUser == null
                ? new DemoUser("admin@sinapipro.dev", "SinapiPro#2026", List.of("ADMIN", "USER"))
                : demoUser;
        accessToken = accessToken == null
                ? new AccessToken(Duration.ofMinutes(20), "sinapipro.read sinapipro.write")
                : accessToken;
        refreshToken = refreshToken == null
                ? new RefreshToken(Duration.ofDays(14))
                : refreshToken;
    }

    public record DemoUser(
            @DefaultValue("admin@sinapipro.dev") String username,
            @DefaultValue("SinapiPro#2026") String password,
            List<String> roles
    ) {
        public DemoUser {
            roles = roles == null || roles.isEmpty() ? List.of("ADMIN", "USER") : List.copyOf(roles);
        }
    }

    public record AccessToken(
            @DefaultValue("PT20M") Duration ttl,
            @DefaultValue("sinapipro.read sinapipro.write") String scope
    ) {
    }

    public record RefreshToken(
            @DefaultValue("P14D") Duration ttl
    ) {
    }
}
