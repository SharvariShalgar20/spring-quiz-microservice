package com.Sharvari.question_service.service;

import com.Sharvari.question_service.dto.QuestionWrapper;
import com.Sharvari.question_service.dto.Response;
import com.Sharvari.question_service.model.Question;
import com.Sharvari.question_service.repository.QuestionRepository;
import com.thoughtworks.xstream.mapper.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getAllQuestion() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByCategory(String category) {
        return questionRepository.findByCategory(category);
    }

    public Question addQuestion(Question question) {
        return questionRepository.save(question);
    }

    public List<Integer> getQuestionsForQuiz(String category, int numQuestion) {
        List<Question> questions = questionRepository.findRandomQuestionsByCategory(category, numQuestion);
        if (questions.size() < numQuestion) {
            log.warn("Requested {} questions for category '{}' but only found {}", numQuestion, category, questions.size());
        }
        return questions.stream().map(Question::getId).toList();
    }

    public List<QuestionWrapper> getQuestionsFromId(List<Integer> ids) {
        List<Question> questions = questionRepository.findByIdIn(ids);
        List<QuestionWrapper> result = new ArrayList<>();

        for(Question q : questions) {
            result.add(new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4()));
        }

        return result;
    }

    public int getScore(List<Response> responses) {
        int score = 0;

        for(Response r : responses) {
            Question q = questionRepository.findById(r.getId()).orElse(null);
            if (q == null) {
                log.warn("No question found for id: {}", r.getId());
                continue;
            }
            if (q.getRightAnswer().equalsIgnoreCase(r.getResponse())) {
                score++;
            } else {
                log.debug("Wrong answer for question id {}: expected '{}', got '{}'", r.getId(), q.getRightAnswer(), r.getResponse());
            }
        }

        return score;
    }
}
