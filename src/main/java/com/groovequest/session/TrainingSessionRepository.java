package com.groovequest.session;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TrainingSessionRepository implements PanacheRepository<TrainingSession> {

    public List<TrainingSession> listAllOrderedByDateDesc() {
        return list("ORDER BY date DESC, id DESC");
    }

    
}
