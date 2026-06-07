package com.groovequest.session;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
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

    public List<Object[]> summarizeTrainingDistributionSince(LocalDate startDate) {
        return getEntityManager()
                .createQuery("""
                    SELECT session.skill, COUNT(session), SUM(session.durationMinutes)
                    FROM TrainingSession session
                    WHERE session.date >= :startDate
                    GROUP BY session.skill
                    ORDER BY SUM(session.durationMinutes) DESC
                    """, Object[].class)
                .setParameter("startDate", startDate)
                .getResultList();
    }

    public List<DanceSkill> findSkillsTrainedSince(LocalDate startDate) {
        return getEntityManager()
                .createQuery("""
                    SELECT DISTINCT session.skill
                    FROM TrainingSession session
                    WHERE session.date >= :startDate
                    """, DanceSkill.class)
                .setParameter("startDate", startDate)
                .getResultList();
    }

    public Long countDistinctTrainingDaysSince(LocalDate startDate) {
        return getEntityManager()
                .createQuery("""
                    SELECT COUNT(DISTINCT session.date)
                    FROM TrainingSession session
                    WHERE session.date >= :startDate
                    """, Long.class)
                .setParameter("startDate", startDate)
                .getSingleResult();
    }

}
