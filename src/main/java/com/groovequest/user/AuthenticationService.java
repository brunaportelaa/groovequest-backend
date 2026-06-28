package com.groovequest.user;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    public AuthenticationService(
            UserRepository repository,
            PasswordHasher passwordHasher,
            TokenService tokenService) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest request){
        User user = repository.findByEmail(request.getEmail()).orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(request.getPassword(), user.getPasswordHash())){
            throw new InvalidCredentialsException();
        };

        String token = tokenService.issueFor(user);

        return new LoginResponse(token);

    }
}
