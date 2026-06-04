package com.groovequest.session;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class TrainingSessionService {

    private final TrainingSessionRepository repository;
    private final XpCalculationService xpCalculationService;

    public TrainingSessionService(
            TrainingSessionRepository repository,
            XpCalculationService xpCalculationService) {
        this.repository = repository;
        this.xpCalculationService = xpCalculationService;
    }

    @Transactional
    public TrainingSessionResponse create(CreateTrainingSessionRequest request) {
        int xpGained = xpCalculationService.calculate(
                request.getDurationMinutes(),
                request.getIntensity()
        );

        TrainingSession trainingSession = new TrainingSession(
                request.getDate(),
                request.getDurationMinutes(),
                request.getIntensity(),
                request.getSkill(),
                request.getNotes(),
                xpGained
        );

        repository.persist(trainingSession);

        return TrainingSessionResponse.fromEntity(trainingSession);
    }

    public List<TrainingSessionResponse> listAll() {
        return repository.listAllOrderedByDateDesc()
                .stream()
                .map(TrainingSessionResponse::fromEntity)
                .toList();
    }

    public TrainingSessionResponse findById(Long id) {
        TrainingSession trainingSession = getExistingTrainingSession(id);

        return TrainingSessionResponse.fromEntity(trainingSession);
    }

    @Transactional
    public TrainingSessionResponse update(Long id, UpdateTrainingSessionRequest request) {
        TrainingSession session = getExistingTrainingSession(id);

        int xpGained = xpCalculationService.calculate(
                request.getDurationMinutes(),
                request.getIntensity());

        session.update(request.getDate(),
                request.getDurationMinutes(),
                request.getIntensity(),
                request.getSkill(),
                request.getNotes(),
                xpGained);

        return TrainingSessionResponse.fromEntity(session);
    }

    @Transactional
    public void delete(Long id) {
        TrainingSession session = getExistingTrainingSession(id);
        repository.delete(session);
    }

    private TrainingSession getExistingTrainingSession(Long id) {
        TrainingSession session = repository.findById(id);

        if (session == null) {
            throw new NotFoundException("Training session not found");
        }

       return session;
    }
}
