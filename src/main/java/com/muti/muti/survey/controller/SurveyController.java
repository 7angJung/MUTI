package com.muti.muti.survey.controller;

import com.muti.muti.survey.dto.MutiResultDto;
import com.muti.muti.survey.dto.QuestionDto;
import com.muti.muti.survey.dto.SurveyResultWithMusicDto;
import com.muti.muti.survey.dto.SurveySubmissionDto;
import com.muti.muti.survey.service.SurveyService;
import com.muti.muti.user.domain.User;
import com.muti.muti.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final UserRepository userRepository; // For fetching user ID

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDto>> getQuestions() {
        return ResponseEntity.ok(surveyService.getAllQuestions());
    }

    @PostMapping("/submit")
    public ResponseEntity<SurveyResultWithMusicDto> submitSurvey(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SurveySubmissionDto submission) {
        User user = userRepository.findByUserId(userDetails.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MutiResultDto mutiResult = surveyService.calculateAndSaveMutiType(user.getId(), submission);
        SurveyResultWithMusicDto resultWithMusic = surveyService.getSurveyResultWithMusic(mutiResult.getMutiType());

        return ResponseEntity.ok(resultWithMusic);
    }
}
