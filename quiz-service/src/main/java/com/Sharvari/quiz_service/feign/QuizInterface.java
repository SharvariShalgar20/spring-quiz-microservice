package com.Sharvari.quiz_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "QUESTION-SERVICE")
public interface QuizInterface {

    @GetMapping("question/generate/{category}/{numQuestion}")
    List<Integer> getQuestionsForQuiz(@PathVariable String category, @PathVariable int numQuestion);
}
