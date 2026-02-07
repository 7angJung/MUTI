package com.muti.domain.survey.service;

import com.muti.domain.survey.dto.request.SubmitSurveyRequest;
import com.muti.domain.survey.dto.request.SurveyAnswerDto;
import com.muti.domain.survey.dto.response.SurveyResultDto;
import com.muti.domain.survey.entity.*;
import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import com.muti.domain.survey.enums.MutiType;
import com.muti.domain.survey.mapper.SurveyMapper;
import com.muti.domain.survey.repository.QuestionOptionRepository;
import com.muti.domain.survey.repository.QuestionRepository;
import com.muti.domain.survey.repository.SurveyResultRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 설문 응답 처리 및 MUTI 타입 산출 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SurveyResponseService {

    private final SurveyService surveyService;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final SurveyMapper surveyMapper;

    /**
     * 설문 응답 제출 및 MUTI 타입 산출
     */
    public SurveyResultDto submitSurvey(SubmitSurveyRequest request) {
        log.info("Submitting survey: surveyId={}, answerCount={}",
                request.getSurveyId(), request.getAnswers().size());

        // 1. 설문 검증
        Survey survey = surveyService.getSurveyEntity(request.getSurveyId());
        validateSurveyActive(survey);

        // 2. 응답 검증
        List<Question> questions = questionRepository.findBySurveyIdWithOptions(request.getSurveyId());
        validateAnswers(request.getAnswers(), questions);

        // 3. 각 축별 점수 계산
        Map<MutiAxis, Integer> axisScores = calculateAxisScores(request.getAnswers());

        // 4. MUTI 타입 결정
        MutiType mutiType = determineMutiType(axisScores);

        // 5. SurveyResult 생성
        SurveyResult result = SurveyResult.builder()
                .survey(survey)
                .mutiType(mutiType)
                .eiScore(axisScores.get(MutiAxis.E_I))
                .sfScore(axisScores.get(MutiAxis.S_F))
                .adScore(axisScores.get(MutiAxis.A_D))
                .puScore(axisScores.get(MutiAxis.P_U))
                .sessionId(request.getSessionId())
                .userId(request.getUserId())
                .build();

        // 6. SurveyResponse 생성 및 연결
        for (SurveyAnswerDto answer : request.getAnswers()) {
            Question question = findQuestion(questions, answer.getQuestionId());
            QuestionOption option = findOption(question, answer.getOptionId());

            SurveyResponse response = SurveyResponse.builder()
                    .question(question)
                    .selectedOption(option)
                    .build();

            result.addResponse(response);
        }

        // 7. 저장
        SurveyResult saved = surveyResultRepository.save(result);
        log.info("Survey submitted successfully: resultId={}, mutiType={}",
                saved.getId(), mutiType);

        return surveyMapper.toResultResponse(saved);
    }

    /**
     * 설문 활성화 상태 검증
     */
    private void validateSurveyActive(Survey survey) {
        if (!survey.getActive()) {
            log.warn("Survey is not active: surveyId={}", survey.getId());
            throw new BusinessException(ErrorCode.INVALID_SURVEY_RESPONSE, "비활성화된 설문입니다.");
        }
    }

    /**
     * 응답 유효성 검증
     */
    private void validateAnswers(List<SurveyAnswerDto> answers, List<Question> questions) {
        if (answers.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_SURVEY_RESPONSE, "응답이 없습니다.");
        }

        // 모든 질문에 답변했는지 확인
        if (answers.size() != questions.size()) {
            log.warn("Answer count mismatch: expected={}, actual={}",
                    questions.size(), answers.size());
            throw new BusinessException(ErrorCode.INVALID_SURVEY_RESPONSE,
                    "모든 질문에 답변해야 합니다.");
        }
    }

    /**
     * 각 축별 점수 계산
     */
    private Map<MutiAxis, Integer> calculateAxisScores(List<SurveyAnswerDto> answers) {
        Map<MutiAxis, Integer> scores = new HashMap<>();
        scores.put(MutiAxis.E_I, 0);
        scores.put(MutiAxis.S_F, 0);
        scores.put(MutiAxis.A_D, 0);
        scores.put(MutiAxis.P_U, 0);

        for (SurveyAnswerDto answer : answers) {
            QuestionOption option = questionOptionRepository.findById(answer.getOptionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_SURVEY_RESPONSE,
                            "잘못된 선택지입니다."));

            Question question = option.getQuestion();
            MutiAxis axis = question.getAxis();

            // 방향에 따라 점수 누적
            // E, S, A, P는 양수, I, F, D, U는 음수로 처리
            int score = calculateDirectionalScore(option);
            scores.put(axis, scores.get(axis) + score);
        }

        log.debug("Calculated axis scores: {}", scores);
        return scores;
    }

    /**
     * 방향을 고려한 점수 계산
     * E, S, A, P 방향: 양수
     * I, F, D, U 방향: 음수
     */
    private int calculateDirectionalScore(QuestionOption option) {
        AxisDirection direction = option.getDirection();
        int baseScore = option.getScore();

        // 두 번째 방향(I, F, D, U)이면 음수로 변환
        boolean isSecondDirection = direction == AxisDirection.I ||
                                   direction == AxisDirection.F ||
                                   direction == AxisDirection.D ||
                                   direction == AxisDirection.U;

        return isSecondDirection ? -Math.abs(baseScore) : Math.abs(baseScore);
    }

    /**
     * MUTI 타입 결정
     * 각 축의 점수가 양수면 첫 번째 방향, 음수면 두 번째 방향
     */
    private MutiType determineMutiType(Map<MutiAxis, Integer> scores) {
        String ei = scores.get(MutiAxis.E_I) >= 0 ? "E" : "I";
        String sf = scores.get(MutiAxis.S_F) >= 0 ? "S" : "F";
        String ad = scores.get(MutiAxis.A_D) >= 0 ? "A" : "D";
        String pu = scores.get(MutiAxis.P_U) >= 0 ? "P" : "U";

        MutiType type = MutiType.fromAxisValues(ei, sf, ad, pu);
        log.debug("Determined MUTI type: {} (E_I={}, S_F={}, A_D={}, P_U={})",
                type, ei, sf, ad, pu);

        return type;
    }

    /**
     * 질문 찾기
     */
    private Question findQuestion(List<Question> questions, Long questionId) {
        return questions.stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_SURVEY_RESPONSE,
                        "잘못된 질문입니다."));
    }

    /**
     * 선택지 찾기
     */
    private QuestionOption findOption(Question question, Long optionId) {
        return question.getOptions().stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_SURVEY_RESPONSE,
                        "잘못된 선택지입니다."));
    }
}