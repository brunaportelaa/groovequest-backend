package com.groovequest.user;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@PermitAll
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthResource(UserService userService, AuthenticationService authService) {
        this.userService = userService;
        this.authenticationService = authService;
    }

    @POST
    @Path("/register")
    public Response register (@Valid RegisterRequest request) {
        UserResponse response = userService.register(request);

        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        LoginResponse response = authenticationService.login(request);
        return Response.ok(response).build();
    }
}
