package com.sharvari.analytics_service.controller;

import com.sharvari.analytics_service.model.QuizStats;
import com.sharvari.analytics_service.repository.QuizStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private QuizStatsRepository quizStatsRepository;

    @GetMapping("/quiz/{quizId}")
    public QuizStats getQuizStats(@PathVariable Integer quizId) {
        return quizStatsRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("No stats available yet for quiz id: " + quizId));
    }
}
