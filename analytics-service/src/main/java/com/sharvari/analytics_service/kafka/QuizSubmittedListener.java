package com.sharvari.analytics_service.kafka;

import com.sharvari.analytics_service.event.QuizSubmittedEvent;
import com.sharvari.analytics_service.model.QuizStats;
import com.sharvari.analytics_service.repository.QuizStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QuizSubmittedListener {

    @Autowired
    private QuizStatsRepository quizStatsRepository;

    @KafkaListener(topics = "${kafka.topic.quiz-submissions}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleQuizSubmitted(QuizSubmittedEvent event) {
        log.info("Analytics received QuizSubmittedEvent for quiz '{}' (user: {})", event.getQuizTitle(), event.getUsername());

        QuizStats stats = quizStatsRepository.findById(event.getQuizId())
                .orElse(new QuizStats(event.getQuizId(), event.getQuizTitle(), 0, 0, 0, 0));

        stats.setTotalAttempts(stats.getTotalAttempts() + 1);
        stats.setTotalScoreSum(stats.getTotalScoreSum() + event.getScore());
        stats.setTotalQuestionsSum(stats.getTotalQuestionsSum() + event.getTotalQuestions());
        stats.setHighestScore(Math.max(stats.getHighestScore(), event.getScore()));

        quizStatsRepository.save(stats);
        log.info("Updated stats for quiz '{}': {} attempts, avg score sum {}/{}",
                event.getQuizTitle(), stats.getTotalAttempts(), stats.getTotalScoreSum(), stats.getTotalQuestionsSum());
    }
}
