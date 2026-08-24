package com.Sharvari.question_service.controller;

import com.Sharvari.question_service.dto.QuestionWrapper;
import com.Sharvari.question_service.dto.Response;
import com.Sharvari.question_service.model.Question;
import com.Sharvari.question_service.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.Sharvari.question_service.service.MinioService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/question")
@Tag(name = "Question Service", description = "Manages quiz questions, categories, images, and scoring")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MinioService minioService;

    @Operation(summary = "Get all questions",
            description = "Returns every question stored in the database, across all categories")
    @GetMapping("/all-Questions")
    public List<Question> getAllQuestion() {
        log.info("Fetching all questions");
        return questionService.getAllQuestion();
    }

    @Operation(summary = "Get questions by category",
            description = "Returns all questions belonging to the given category")
    @GetMapping("/by-category/{category}")
    public List<Question> getQuestionsByCategory(@PathVariable String category) {
        log.info("Fetching questions for category: {}", category);
        return questionService.getQuestionsByCategory(category);
    }

    @Operation(summary = "Generate random question IDs for a quiz",
            description = "Returns a list of random question IDs from the given category, used by Quiz Service to build a quiz")
    @GetMapping("/generate/{category}/{numQuestion}")
    public List<Integer> getQuestionsForQuiz(@PathVariable String category, @PathVariable int numQuestion) {
        log.info("Generating {} random question IDs for category: {}", numQuestion, category);
        return questionService.getQuestionsForQuiz(category, numQuestion);
    }

    @Operation(summary = "Add a new question",
            description = "Creates and persists a new question with its options, right answer, difficulty, and category")
    @PostMapping("/create-question")
    public Question addQuestion(@RequestBody Question question) {
        log.info("Adding new question in category: {}", question.getCategory());
        return questionService.addQuestion(question);
    }

    @Operation(summary = "Get question details by IDs",
            description = "Returns question text, options, and image URL (without the right answer) for the given list of question IDs")
    @PostMapping("/get-Questions")
    public List<QuestionWrapper> getQuestionsFromId(@RequestBody List<Integer> ids) {
        log.info("Fetching question details for ids: {}", ids);
        return questionService.getQuestionsFromId(ids);
    }

    @Operation(summary = "Calculate quiz score",
            description = "Compares submitted answers against the correct answers stored in the database and returns the total score")
    @PostMapping("/get-Score")
    public int getScore(@RequestBody List<Response> responses) {
        log.info("Calculating score for {} responses", responses.size());
        return questionService.getScore(responses);
    }

    @Operation(summary = "Upload a question image",
            description = "Uploads an image file to object storage (ADMIN only) and returns its public URL")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Uploading image: {}", file.getOriginalFilename());
        String imageUrl = minioService.uploadFile(file);
        log.info("Image uploaded, URL: {}", imageUrl);
        return imageUrl;
    }

    @Operation(summary = "Create a question with an optional image",
            description = "Creates a new question (ADMIN only). If an image file is included, it's uploaded and linked automatically")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create-question-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Question addQuestionWithImage(
            @RequestPart("question") Question question,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {

        if (file != null && !file.isEmpty()) {
            log.info("Uploading image for new question: {}", file.getOriginalFilename());
            String imageUrl = minioService.uploadFile(file);
            question.setImageUrl(imageUrl);
        }

        log.info("Adding new question in category: {}", question.getCategory());
        Question saved = questionService.addQuestion(question);
        log.info("Saved question with id: {}, imageUrl: {}", saved.getId(), saved.getImageUrl());
        return saved;
    }
}
