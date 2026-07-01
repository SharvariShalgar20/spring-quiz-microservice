package com.Sharvari.question_service.controller;

import com.Sharvari.question_service.model.Question;
import com.Sharvari.question_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/all-Questions")
    public List<Question> getAllQuestion() {
        return questionService.getAllQuestion();
    }

    @GetMapping("/by-category/{category}")
    public List<Question> getQuestionsByCategory(@PathVariable String category) {
        return questionService.getQuestionsByCategory(category);
    }

    @GetMapping("/generate/{category}/{numQuestion}")
    public List<Integer> getQuestionsForQuiz(@PathVariable String category, @PathVariable int numQuestion) {
        return questionService.getQuestionsForQuiz(category, numQuestion);
    }

    @PostMapping("/create-question")
    public Question addQuestion(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }
}
