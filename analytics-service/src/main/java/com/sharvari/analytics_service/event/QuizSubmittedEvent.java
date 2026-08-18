package com.sharvari.analytics_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmittedEvent {
    private String username;
    private Integer quizId;
    private String quizTitle;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime submittedAt;
}
