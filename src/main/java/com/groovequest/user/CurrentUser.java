package com.groovequest.user;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CurrentUser {
    private final SecurityIdentity identity;
    private final UserRepository userRepository;

    public CurrentUser(SecurityIdentity identity, UserRepository userRepository) {
        this.identity = identity;
        this.userRepository = userRepository;
    }

    public User require() {
        String email = identity.getPrincipal().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated principal has no matching user: " + email));
    }

    public Long requireId() {
        return require().getId();
    }
}
