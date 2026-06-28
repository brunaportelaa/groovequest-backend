package com.groovequest.user;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class TokenService {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(1);

    private final String issuer;

    public TokenService(
            @ConfigProperty(name = "mp.jwt.verify.issuer") String issuer
    ) {
        this.issuer = issuer;
    }

    public String issueFor(User user) {
        return  Jwt.issuer(issuer).upn(user.getEmail())
                .subject(String.valueOf(user.getId()))
                .groups(Set.of(user.getRole()))
                .expiresIn(TOKEN_LIFETIME)
                .sign();
    }
}
