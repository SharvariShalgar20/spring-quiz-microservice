package com.Sharvari.quiz_service.controller;

import com.Sharvari.quiz_service.dto.QuestionWrapper;
import com.Sharvari.quiz_service.dto.Response;
import com.Sharvari.quiz_service.service.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

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
        log.info("Submitting {} answers for quiz id: {}", responses.size(), quizId);
        return quizService.calculateResult(quizId, responses);
    }
}
