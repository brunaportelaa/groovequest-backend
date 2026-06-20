CREATE TABLE training_sessions (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    date             DATE,
    duration_minutes INT,
    intensity        ENUM('HIGH', 'LOW', 'MEDIUM'),
    skill            ENUM('CHOREO', 'COMPETITION', 'FLEXIBILITY', 'FOUNDATION',
                          'HIP_HOP', 'HOUSE', 'JAZZ_FUNK', 'MUSICALITY', 'PERFORMANCE'),
    notes            VARCHAR(255),
    xp_gained        INT,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;