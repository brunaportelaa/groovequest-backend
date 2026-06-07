package com.groovequest.session;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TrainingSessionRepository implements PanacheRepository<TrainingSession> {

    public List<TrainingSession> listAllOrderedByDateDesc() {
        return list("ORDER BY date DESC, id DESC");
    }

    public List<Object[]> sumXpGroupedBySkill() {
        return getEntityManager()
                .createQuery("""
                        SELECT session.skill, SUM(session.xpGained)
                        FROM TrainingSession session
                        GROUP BY session.skill
                        ORDER BY SUM(session.xpGained) DESC
                        """, Object[].class)
                .getResultList();
    }

    public Long sumTotalXp() {
        Long totalXp = getEntityManager()
                .createQuery("""
                        SELECT COALESCE(SUM(session.xpGained), 0)
                        FROM TrainingSession session
                        """, Long.class)
                .getSingleResult();

        return totalXp;
    }

    public Long sumTotalTrainingMinutes() {
        Long totalMinutes = getEntityManager()
                .createQuery("""
                        SELECT COALESCE(SUM(session.durationMinutes), 0)
                        FROM TrainingSession session
                        """, Long.class)
                .getSingleResult();

        return totalMinutes;
    }

}
