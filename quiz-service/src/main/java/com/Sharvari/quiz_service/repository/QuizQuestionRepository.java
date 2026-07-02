package com.Sharvari.quiz_service.repository;

import com.Sharvari.quiz_service.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
}
