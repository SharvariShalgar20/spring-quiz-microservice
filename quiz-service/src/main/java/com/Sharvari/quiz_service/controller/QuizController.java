package com.Sharvari.quiz_service.controller;

import com.Sharvari.quiz_service.dto.QuestionWrapper;
import com.Sharvari.quiz_service.dto.Response;
import com.Sharvari.quiz_service.service.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    @PostMapping("create/{category}/{numQuestion}/{title}")
    public Integer createQuiz(
            @PathVariable String category,
            @PathVariable int numQuestion,
            @PathVariable String title) {
        log.info("Quiz created.");
        return quizService.createQuiz(category, numQuestion, title);
    }

    @GetMapping("get/{quizId}")
    public List<QuestionWrapper> getQuizQuestions(@PathVariable Integer quizId) {
        log.info("Fetching questions for quiz id: {}", quizId);
        return quizService.getQuizQuestions(quizId);
    }

    @PostMapping("submit/{quizId}")
    public Integer submitQuiz(@PathVariable Integer quizId, @RequestBody List<Response> responses) {
        String username = getCurrentUsername();
        log.info("User '{}' submitting {} answers for quiz id: {}", username, responses.size(), quizId);
        Integer score = quizService.calculateResult(quizId, responses, username);
        log.info("Quiz {} scored: {}", quizId, score);
        return score;
    }
}
