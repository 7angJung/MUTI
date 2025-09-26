package com.muti.muti.survey.service;

import com.muti.muti.survey.domain.Choice;
import com.muti.muti.survey.domain.MutiTrait;
import com.muti.muti.survey.dto.MutiResultDto;
import com.muti.muti.survey.dto.QuestionDto;
import com.muti.muti.survey.repository.ChoiceRepository;
import com.muti.muti.survey.repository.QuestionRepository;
import com.muti.muti.user.domain.User;
import com.muti.muti.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestions() {
        return questionRepository.findAllWithChoices().stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public MutiResultDto calculateAndSaveMutiType(String username, Map<Long, Long> answers) {
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Choice> chosenChoices = choiceRepository.findAllById(answers.values());

        Map<MutiTrait, Long> traitCounts = chosenChoices.stream()
                .map(Choice::getTrait)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        StringBuilder mutiType = new StringBuilder();
        mutiType.append(determineTrait(traitCounts, MutiTrait.E, MutiTrait.I));
        mutiType.append(determineTrait(traitCounts, MutiTrait.S, MutiTrait.F));
        mutiType.append(determineTrait(traitCounts, MutiTrait.A, MutiTrait.D));
        mutiType.append(determineTrait(traitCounts, MutiTrait.P, MutiTrait.U));

        String finalMutiType = mutiType.toString();
        user.setMutiType(finalMutiType);
        userRepository.save(user);

        String description = generateMutiDescription(finalMutiType);

        return new MutiResultDto(finalMutiType, description);
    }

    private String determineTrait(Map<MutiTrait, Long> counts, MutiTrait trait1, MutiTrait trait2) {
        long count1 = counts.getOrDefault(trait1, 0L);
        long count2 = counts.getOrDefault(trait2, 0L);
        // In case of a tie or if no choice was made for this dimension, default to the first one.
        return count1 >= count2 ? trait1.name() : trait2.name();
    }

    private String generateMutiDescription(String mutiType) {
        // This can be expanded with more detailed descriptions for each type
        return "Your MUTI type is " + mutiType;
    }
}