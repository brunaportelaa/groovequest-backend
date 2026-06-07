package com.groovequest.coaching;

public class CoachingInsightResponse {

    private CoachingInsightType type;
    private String message;

    public CoachingInsightResponse(CoachingInsightType type, String message) {
        this.type = type;
        this.message = message;
    }

    public CoachingInsightType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}