package com.sharvari.analytics_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizStats {

    @Id
    private Integer quizId; // one row per quiz, quizId is the natural key

    private String quizTitle;

    private Integer totalAttempts;

    private Integer totalScoreSum; // running sum, used to compute average

    private Integer totalQuestionsSum; // running sum, for percentage calc across varying quiz lengths

    private Integer highestScore;
}
