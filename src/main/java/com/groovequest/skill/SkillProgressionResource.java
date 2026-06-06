package com.groovequest.skill;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.awt.*;
import java.util.List;

@Path("/api/skills/progression")
@Produces(MediaType.APPLICATION_JSON)
public class SkillProgressionResource {

    private final SkillProgressionService skillProgressionService;

    public SkillProgressionResource(SkillProgressionService skillProgressionService) {
        this.skillProgressionService = skillProgressionService;
    }

    @GET
    public List<SkillProgressionResponse> getSkillProgression() {
        return skillProgressionService.getSkillProgression();
    }
}
