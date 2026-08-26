package com.Sharvari.quiz_service.controller;

import com.Sharvari.quiz_service.dto.QuestionWrapper;
import com.Sharvari.quiz_service.dto.Response;
import com.Sharvari.quiz_service.model.QuizAttempt;
import com.Sharvari.quiz_service.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("quiz")
@Tag(name = "Quiz Service", description = "Creates quizzes, tracks attempts, and orchestrates calls to Question Service")
public class QuizController {

    @Autowired
    private QuizService quizService;

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    @Operation(summary = "Create a new quiz",
            description = "Fetches random question IDs from Question Service for the given category and creates a new quiz")
    @PostMapping("create/{category}/{numQuestion}/{title}")
    public Integer createQuiz(
            @PathVariable String category,
            @PathVariable int numQuestion,
            @PathVariable String title) {
        log.info("Quiz created.");
        return quizService.createQuiz(category, numQuestion, title);
    }

    @Operation(summary = "Get quiz questions",
            description = "Fetches full question details (without right answers) for a given quiz ID")
    @GetMapping("get/{quizId}")
    public List<QuestionWrapper> getQuizQuestions(@PathVariable Integer quizId) {
        log.info("Fetching questions for quiz id: {}", quizId);
        return quizService.getQuizQuestions(quizId);
    }

    @Operation(summary = "Submit quiz answers",
            description = "Submits the user's answers, saves the attempt, publishes a QuizSubmittedEvent, and returns the score")
    @PostMapping("submit/{quizId}")
    public Integer submitQuiz(@PathVariable Integer quizId, @RequestBody List<Response> responses) {
        String username = getCurrentUsername();
        log.info("User '{}' submitting {} answers for quiz id: {}", username, responses.size(), quizId);
        Integer score = quizService.calculateResult(quizId, responses, username);
        log.info("Quiz {} scored: {}", quizId, score);
        return score;
    }

    @Operation(summary = "Get my quiz history",
            description = "Returns all past quiz attempts for the currently logged-in user, most recent first")
    @GetMapping("history")
    public List<QuizAttempt> getMyHistory() {
        String username = getCurrentUsername();
        return quizService.getMyHistory(username);
    }

    @Operation(summary = "Get quiz leaderboard",
            description = "Returns all attempts for a given quiz, ranked by score (highest first)")
    @GetMapping("leaderboard/{quizId}")
    public List<QuizAttempt> getLeaderboard(@PathVariable Integer quizId) {
        return quizService.getLeaderboard(quizId);
    }
}
