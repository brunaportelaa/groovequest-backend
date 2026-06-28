package com.groovequest.dashboard;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/dashboard")
@RolesAllowed("user")

@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    private final DashboardService service;

    public DashboardResource(DashboardService dashboardService) {
        this.service = dashboardService;
    }

    @GET
    public DashboardResponse getDashboard() {
        return service.getDashboard();
    }
}
