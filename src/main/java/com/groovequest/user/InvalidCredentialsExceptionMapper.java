package com.groovequest.user;

import com.groovequest.common.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidCredentialsExceptionMapper implements ExceptionMapper<InvalidCredentialsException> {

    @Override
    public Response toResponse(InvalidCredentialsException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ErrorResponse.of(
                        exception.getMessage(),
                        Response.Status.UNAUTHORIZED.getStatusCode()))
                .build();
    }
}
