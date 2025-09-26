package com.muti.muti.survey.controller;

import com.muti.muti.survey.dto.MutiResultDto;
import com.muti.muti.survey.dto.QuestionDto;
import com.muti.muti.survey.dto.SurveySubmissionDto;
import com.muti.muti.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDto>> getSurveyQuestions() {
        return ResponseEntity.ok(surveyService.getQuestions());
    }

    @PostMapping("/submit")
    public ResponseEntity<MutiResultDto> submitSurvey(@RequestBody SurveySubmissionDto submission, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        MutiResultDto result = surveyService.calculateAndSaveMutiType(principal.getName(), submission.getAnswers());
        return ResponseEntity.ok(result);
    }
}