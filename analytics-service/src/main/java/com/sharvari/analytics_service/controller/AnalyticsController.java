package com.sharvari.analytics_service.controller;

import com.sharvari.analytics_service.dto.QuizStatsResponse;
import com.sharvari.analytics_service.model.QuizStats;
import com.sharvari.analytics_service.repository.QuizStatsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics Service", description = "Aggregated quiz statistics, built from Kafka events")
public class AnalyticsController {

    @Autowired
    private QuizStatsRepository quizStatsRepository;

    @Operation(summary = "Get stats for a specific quiz",
            description = "Returns attempt count, highest score, and average score percentage for a given quiz")
    @GetMapping("/quiz/{quizId}")
    public QuizStats getQuizStats(@PathVariable Integer quizId) {
        return quizStatsRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("No stats available yet for quiz id: " + quizId));
    }

    @Operation(summary = "Get stats for all quizzes",
            description = "Returns aggregated stats across every quiz that has at least one attempt")
    @GetMapping("/all")
    public List<QuizStatsResponse> getAllStats() {
        return quizStatsRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private QuizStatsResponse toResponse(QuizStats stats) {
        double avgPercentage = stats.getTotalQuestionsSum() == 0 ? 0.0 :
                (stats.getTotalScoreSum() * 100.0) / stats.getTotalQuestionsSum();

        return new QuizStatsResponse(
                stats.getQuizId(),
                stats.getQuizTitle(),
                stats.getTotalAttempts(),
                stats.getHighestScore(),
                Math.round(avgPercentage * 10.0) / 10.0 // round to 1 decimal place
        );
    }
}
