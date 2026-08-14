package com.Sharvari.notification_service.kafka;

import com.Sharvari.notification_service.event.QuizSubmittedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QuizSubmittedListener {

    @KafkaListener(topics = "${kafka.topic.quiz-submissions}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleQuizSubmitted(QuizSubmittedEvent event) {
        log.info("Received QuizSubmittedEvent for user '{}'", event.getUsername());
        sendNotification(event);
    }

    private void sendNotification(QuizSubmittedEvent event) {
        // Simulated notification - in a real app this would call an email/SMS/push provider
        String percentage = String.format("%.0f", (event.getScore() * 100.0) / event.getTotalQuestions());

        log.info("📧 NOTIFICATION → To: {} | Subject: Quiz Result | Body: You scored {}/{} ({}%) on '{}'",
                event.getUsername(), event.getScore(), event.getTotalQuestions(), percentage, event.getQuizTitle());
    }
}
