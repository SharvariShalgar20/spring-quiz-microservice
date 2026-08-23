package com.sharvari.analytics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizStatsResponse {
    private Integer quizId;
    private String quizTitle;
    private Integer totalAttempts;
    private Integer highestScore;
    private Double averageScorePercentage;
}
