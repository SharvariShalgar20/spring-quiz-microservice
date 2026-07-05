package com.Sharvari.quiz_service.controller;

import com.Sharvari.quiz_service.dto.QuestionWrapper;
import com.Sharvari.quiz_service.dto.Response;
import com.Sharvari.quiz_service.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return quizService.createQuiz(category, numQuestion, title);
    }

    @GetMapping("get/{quizId}")
    public List<QuestionWrapper> getQuizQuestions(@PathVariable Integer quizId) {
        return quizService.getQuizQuestions(quizId);
    }

    @PostMapping("submit/{quizId}")
    public Integer submitQuiz(@PathVariable Integer quizId, @RequestBody List<Response> responses) {
        return quizService.calculateResult(quizId, responses);
    }
}
