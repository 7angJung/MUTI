package com.muti.muti.survey.service;

import com.muti.muti.music.dto.MusicDto;
import com.muti.muti.music.service.MusicService;
import com.muti.muti.survey.domain.Choice;
import com.muti.muti.survey.domain.MutiTrait;
import com.muti.muti.survey.dto.MutiResultDto;
import com.muti.muti.survey.dto.QuestionDto;
import com.muti.muti.survey.dto.SurveyResultWithMusicDto;
import com.muti.muti.survey.dto.SurveySubmissionDto;
import com.muti.muti.survey.repository.ChoiceRepository;
import com.muti.muti.survey.repository.QuestionRepository;
import com.muti.muti.user.domain.User;
import com.muti.muti.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final UserRepository userRepository;
    private final MusicService musicService;

    @Transactional(readOnly = true)
    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public MutiResultDto calculateAndSaveMutiType(Long userId, SurveySubmissionDto submissionDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        StringBuilder mutiTypeBuilder = new StringBuilder();
        List<Choice> choices = choiceRepository.findAllById(submissionDto.getAnswers().values());

        // This logic assumes questions are always in the same order.
        // A more robust implementation would map questions to traits.
        choices.stream()
                .map(Choice::getTrait)
                .sorted()
                .forEach(mutiTypeBuilder::append);

        String mutiType = mutiTypeBuilder.toString();
        user.setMutiType(mutiType);
        userRepository.save(user);

        return new MutiResultDto(mutiType, "Your MUTI type description here.");
    }

    public SurveyResultWithMusicDto getSurveyResultWithMusic(String mutiType) {
        MutiResultDto mutiResultDto = new MutiResultDto(mutiType, "Description for " + mutiType);
        List<MusicDto> recommendations = musicService.getRecommendationsByMutiType(mutiType);
        return new SurveyResultWithMusicDto(mutiResultDto, recommendations);
    }
}