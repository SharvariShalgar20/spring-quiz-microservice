package com.Sharvari.quiz_service.repository;

import com.Sharvari.quiz_service.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {

}
