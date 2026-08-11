package com.Sharvari.quiz_service.kafka;

import com.Sharvari.quiz_service.event.QuizSubmittedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, QuizSubmittedEvent> kafkaTemplate;

    @Value("${kafka.topic.quiz-submissions}")
    private String topic;

    public void publishQuizSubmitted(QuizSubmittedEvent event) {
        log.info("Publishing QuizSubmittedEvent to topic '{}': {}", topic, event);
        kafkaTemplate.send(topic, event.getUsername(), event);
    }
}
