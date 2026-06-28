package com.groovequest.session;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/sessions")
@RolesAllowed("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TrainingSessionResource {

    private final TrainingSessionService service;

    public TrainingSessionResource(TrainingSessionService service) {
        this.service = service;
    }

    @POST
    public Response create(@Valid CreateTrainingSessionRequest request) {
        TrainingSessionResponse response = service.create(request);

        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    public List<TrainingSessionResponse> listAll() {
        return service.listAll();
    }

    @GET
    @Path("/{id}")
    public TrainingSessionResponse findById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @PUT
    @Path("/{id}")
    public TrainingSessionResponse update(
            @PathParam("id") Long id,
            @Valid UpdateTrainingSessionRequest request){
        return service.update(id, request);
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }

}
