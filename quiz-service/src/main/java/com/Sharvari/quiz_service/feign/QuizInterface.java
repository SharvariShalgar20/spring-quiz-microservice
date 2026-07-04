package com.Sharvari.quiz_service.feign;

import com.Sharvari.quiz_service.dto.QuestionWrapper;
import com.Sharvari.quiz_service.dto.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "QUESTION-SERVICE")
public interface QuizInterface {

    @GetMapping("question/generate/{category}/{numQuestion}")
    List<Integer> getQuestionsForQuiz(@PathVariable String category, @PathVariable int numQuestion);

    @PostMapping("question/getQuestions")
    List<QuestionWrapper> getQuestionsFromId(@RequestBody List<Integer> ids);

    @PostMapping("question/getScore")
    Integer getScore(@RequestBody List<Response> responses);
}
