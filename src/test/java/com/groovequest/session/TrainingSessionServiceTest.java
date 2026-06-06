package com.groovequest.session;

import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingSessionServiceTest {

    @Mock
    TrainingSessionRepository trainingSessionRepository;

    @Mock
    XpCalculationService xpCalculationService;

    @InjectMocks
    TrainingSessionService trainingSessionService;

    @Test
    void shouldCreateTrainingSessionWithCalculatedXp(){
        CreateTrainingSessionRequest request = new CreateTrainingSessionRequest(
                LocalDate.of(2026, 6, 6),
                60,
                TrainingIntensity.HIGH,
                DanceSkill.PERFORMANCE,
                "Full-out choreography practice."
        );

        when(xpCalculationService
                .calculate(60, TrainingIntensity.HIGH))
                .thenReturn(120);

        TrainingSessionResponse response = trainingSessionService.create(request);

        ArgumentCaptor<TrainingSession> sessionCaptor = ArgumentCaptor.forClass(TrainingSession.class);
        verify(trainingSessionRepository).persist(sessionCaptor.capture());

        TrainingSession persistedSession = sessionCaptor.getValue();

        assertEquals(LocalDate.of(2026, 6, 6), persistedSession.getDate());
        assertEquals(60, persistedSession.getDurationMinutes());
        assertEquals(TrainingIntensity.HIGH, persistedSession.getIntensity());
        assertEquals(DanceSkill.PERFORMANCE, persistedSession.getSkill());
        assertEquals("Full-out choreography practice.", persistedSession.getNotes());
        assertEquals(120, persistedSession.getXpGained());
        assertEquals(120, response.getXpGained());
    }

    @Test
    void shouldListAllTrainingSessions() {
        TrainingSession session = new TrainingSession(
                LocalDate.of(2026, 6, 6),
                45,
                TrainingIntensity.MEDIUM,
                DanceSkill.FLEXIBILITY,
                "Mobility flow.",
                68
        );

        when(trainingSessionRepository.listAllOrderedByDateDesc()).thenReturn(List.of(session));

        List<TrainingSessionResponse> responses = trainingSessionService.listAll();

        assertEquals(1, responses.size());
        assertEquals(DanceSkill.FLEXIBILITY, responses.get(0).getSkill());
        assertEquals(68, responses.get(0).getXpGained());
    }

    @Test
    void shouldFindTrainingSessionById() {
        TrainingSession session = new TrainingSession(
                LocalDate.of(2026, 6, 6),
                75,
                TrainingIntensity.HIGH,
                DanceSkill.PERFORMANCE,
                "Stamina training.",
                150
        );

        when(trainingSessionRepository.findById(1L)).thenReturn(session);

        TrainingSessionResponse response = trainingSessionService.findById(1L);

        assertEquals(75, response.getDurationMinutes());
        assertEquals(TrainingIntensity.HIGH, response.getIntensity());
        assertEquals(150, response.getXpGained());
    }

    @Test
    void shouldThrowNotFoundWhenTrainingSessionDoesNotExist(){
        when(trainingSessionRepository.findById(999L)).thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> trainingSessionService.findById(999L)
        );
    }

    @Test
    void shouldUpdateTrainingSessionAndRecalculateXp() {
        TrainingSession existingSession = new TrainingSession(
                LocalDate.of(2026, 6, 6),
                75,
                TrainingIntensity.HIGH,
                DanceSkill.PERFORMANCE,
                "Original session.",
                150
        );

        UpdateTrainingSessionRequest request = new UpdateTrainingSessionRequest(
                LocalDate.of(2026, 6, 7),
                30,
                TrainingIntensity.LOW,
                DanceSkill.FOUNDATION,
                "Updated lighter session."
        );

        when(trainingSessionRepository.findById(1L)).thenReturn(existingSession);
        when(xpCalculationService.calculate(30, TrainingIntensity.LOW)).thenReturn(30);

        TrainingSessionResponse response = trainingSessionService.update(1L, request);

        assertEquals(LocalDate.of(2026, 6, 7), response.getDate());
        assertEquals(30, response.getDurationMinutes());
        assertEquals(TrainingIntensity.LOW, response.getIntensity());
        assertEquals(DanceSkill.FOUNDATION, response.getSkill());
        assertEquals("Updated lighter session.", response.getNotes());
        assertEquals(30, response.getXpGained());
    }

    @Test
    void shouldDeleteTrainingSession() {
        TrainingSession existingSession = new TrainingSession(
                LocalDate.of(2026, 6, 6),
                60,
                TrainingIntensity.MEDIUM,
                DanceSkill.HIP_HOP,
                "Groove practice.",
                90
        );

        when(trainingSessionRepository.findById(1L)).thenReturn(existingSession);

        trainingSessionService.delete(1L);

        verify((trainingSessionRepository)).delete(existingSession);
    }
}
