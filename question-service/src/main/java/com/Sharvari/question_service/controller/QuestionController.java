package com.Sharvari.question_service.controller;

import com.Sharvari.question_service.dto.QuestionWrapper;
import com.Sharvari.question_service.dto.Response;
import com.Sharvari.question_service.model.Question;
import com.Sharvari.question_service.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/all-Questions")
    public List<Question> getAllQuestion() {
        log.info("Fetching all questions");
        return questionService.getAllQuestion();
    }

    @GetMapping("/by-category/{category}")
    public List<Question> getQuestionsByCategory(@PathVariable String category) {
        log.info("Fetching questions for category: {}", category);
        return questionService.getQuestionsByCategory(category);
    }

    @GetMapping("/generate/{category}/{numQuestion}")
    public List<Integer> getQuestionsForQuiz(@PathVariable String category, @PathVariable int numQuestion) {
        log.info("Generating {} random question IDs for category: {}", numQuestion, category);
        return questionService.getQuestionsForQuiz(category, numQuestion);
    }

    @PostMapping("/create-question")
    public Question addQuestion(@RequestBody Question question) {
        log.info("Adding new question in category: {}", question.getCategory());
        return questionService.addQuestion(question);
    }

    @PostMapping("/get-Questions")
    public List<QuestionWrapper> getQuestionsFromId(@RequestBody List<Integer> ids) {
        log.info("Fetching question details for ids: {}", ids);
        return questionService.getQuestionsFromId(ids);
    }

    @PostMapping("/get-Score")
    public int getScore(@RequestBody List<Response> responses) {
        log.info("Calculating score for {} responses", responses.size());
        return questionService.getScore(responses);
    }
}
