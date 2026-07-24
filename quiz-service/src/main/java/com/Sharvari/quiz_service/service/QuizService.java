package com.Sharvari.quiz_service.service;

import com.Sharvari.quiz_service.dto.QuestionWrapper;
import com.Sharvari.quiz_service.dto.Response;
import com.Sharvari.quiz_service.feign.QuizInterface;
import com.Sharvari.quiz_service.model.Quiz;
import com.Sharvari.quiz_service.model.QuizAttempt;
import com.Sharvari.quiz_service.model.QuizQuestion;
import com.Sharvari.quiz_service.repository.QuizAttemptRepository;
import com.Sharvari.quiz_service.repository.QuizRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QuizService {

    @Autowired
    private QuizInterface quizInterface;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    public Integer createQuiz(String category, int numQ, String title) {
        List<Integer> questionIds = quizInterface.getQuestionsForQuiz(category, numQ);
        log.info("Received question ids from QUESTION-SERVICE: {}", questionIds);
        Quiz quiz = new Quiz();
        quiz.setTitle(title);

        List<QuizQuestion> quizQuestions = new ArrayList<>();
        for (Integer qId : questionIds) {
            QuizQuestion qq = new QuizQuestion();
            qq.setQuestionId(qId);
            qq.setQuiz(quiz);
            quizQuestions.add(qq);
        }
        quiz.setQuestions(quizQuestions);

        Quiz saved = quizRepository.save(quiz);
        return saved.getId();
    }

    public List<QuestionWrapper> getQuizQuestions(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> {
            log.error("Quiz not found with id: {}", quizId);
            return new RuntimeException("Quiz not found with id: " + quizId);
        });
        List<Integer> ids = quiz.getQuestions().stream()
                .map(QuizQuestion::getQuestionId)
                .toList();
        return quizInterface.getQuestionsFromId(ids);
    }

    public Integer calculateResult(Integer quizId, List<Response> responses, String username) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));

        log.info("Calling QUESTION-SERVICE to score quiz id: {}", quizId);
        Integer score = quizInterface.getScore(responses);
        log.info("QUESTION-SERVICE returned score: {}", score);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUsername(username);
        attempt.setQuizId(quizId);
        attempt.setQuizTitle(quiz.getTitle());
        attempt.setScore(score);
        attempt.setTotalQuestions(responses.size());
        attempt.setSubmittedAt(LocalDateTime.now());

        quizAttemptRepository.save(attempt);
        log.info("Saved quiz attempt for user '{}' on quiz {}: {}/{}", username, quizId, score, responses.size());

        return score;
    }
}
