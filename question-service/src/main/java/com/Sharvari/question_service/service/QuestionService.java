package com.Sharvari.question_service.service;

import com.Sharvari.question_service.model.Question;
import com.Sharvari.question_service.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return questions.stream().map(Question::getId).toList();
    }
}
