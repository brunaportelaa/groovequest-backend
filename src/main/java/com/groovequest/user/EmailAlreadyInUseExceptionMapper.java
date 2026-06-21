package com.groovequest.user;

import com.groovequest.common.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EmailAlreadyInUseExceptionMapper implements ExceptionMapper<EmailAlreadyInUseException> {

    @Override
    public Response toResponse(EmailAlreadyInUseException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(ErrorResponse.of(
                        e.getMessage(),
                        Response.Status.CONFLICT.getStatusCode()
                ))
                .build();
    }

}
