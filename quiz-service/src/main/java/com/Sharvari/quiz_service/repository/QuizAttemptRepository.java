package com.Sharvari.quiz_service.repository;

import com.Sharvari.quiz_service.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Integer> {

    // For "my history" - all attempts by a user, most recent first
    List<QuizAttempt> findByUsernameOrderBySubmittedAtDesc(String username);

    // For leaderboard - all attempts on a specific quiz, ordered by score
    List<QuizAttempt> findByQuizIdOrderByScoreDesc(Integer quizId);
}
