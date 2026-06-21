package com.groovequest.user;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException();
        }

        String passwordHash = BcryptUtil.bcryptHash(request.getPassword());

        User user = new User(request.getEmail(), passwordHash, "user");

        repository.persist(user);

        return UserResponse.fromEntity(user);
    }
}
