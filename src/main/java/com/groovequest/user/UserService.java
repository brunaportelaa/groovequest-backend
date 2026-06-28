package com.groovequest.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {

    private final UserRepository repository;
    private final PasswordHasher passwordHasher;

    public UserService(UserRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException();
        }

        String passwordHash = passwordHasher.hash(request.getPassword());

        User user = new User(request.getEmail(), passwordHash, "user");

        repository.persist(user);

        return UserResponse.fromEntity(user);
    }
}
