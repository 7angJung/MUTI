package com.muti.domain.survey.service;

import com.muti.domain.survey.entity.Question;
import com.muti.domain.survey.entity.QuestionOption;
import com.muti.domain.survey.entity.Survey;
import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import com.muti.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 초기 설문 데이터 로더
 * 로컬 환경에서만 실행됩니다.
 */
@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class SurveyDataInitializer implements ApplicationRunner {

    private final SurveyRepository surveyRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 이미 데이터가 있으면 스킵
        if (surveyRepository.count() > 0) {
            log.info("Initial survey data already exists. Skipping initialization.");
            return;
        }

        log.info("Initializing survey data...");

        // Survey 생성
        Survey survey = Survey.builder()
                .title("MUTI 음악 성향 테스트")
                .description("당신의 음악 취향을 16가지 타입으로 분석합니다. 총 8개의 질문에 답해주세요.")
                .build();

        survey.activate(); // 활성화

        // ===== E_I 축 질문 =====
        // Q1: 음악 템포 선호도
        Question q1 = createQuestion(survey, "평소 즐겨 듣는 음악의 템포는?", MutiAxis.E_I, 1);
        addOption(q1, "빠르고 역동적인 음악 (댄스, EDM, 업템포 록)", AxisDirection.E, 5, 1);
        addOption(q1, "느리고 차분한 음악 (발라드, 앰비언트, 재즈)", AxisDirection.I, 5, 2);

        // Q2: 운동/활동 시 음악
        Question q2 = createQuestion(survey, "운동이나 활동할 때 선호하는 음악은?", MutiAxis.E_I, 2);
        addOption(q2, "에너지 넘치는 신나는 음악", AxisDirection.E, 4, 1);
        addOption(q2, "집중을 돕는 잔잔한 배경음악", AxisDirection.I, 4, 2);

        // ===== S_F 축 질문 =====
        // Q3: 음악 감상 포인트
        Question q3 = createQuestion(survey, "음악을 들을 때 가장 중요하게 생각하는 요소는?", MutiAxis.S_F, 3);
        addOption(q3, "강렬한 비트와 리듬감 (EDM, 힙합, 펑크)", AxisDirection.S, 5, 1);
        addOption(q3, "서정적인 멜로디와 감동적인 가사 (발라드, 포크)", AxisDirection.F, 5, 2);

        // Q4: 음악 선택 기준
        Question q4 = createQuestion(survey, "새로운 음악을 고를 때 더 끌리는 것은?", MutiAxis.S_F, 4);
        addOption(q4, "몸이 절로 움직이게 만드는 그루브", AxisDirection.S, 4, 1);
        addOption(q4, "마음을 울리는 감성적인 분위기", AxisDirection.F, 4, 2);

        // ===== A_D 축 질문 =====
        // Q5: 악기 선호도
        Question q5 = createQuestion(survey, "더 매력적으로 느껴지는 사운드는?", MutiAxis.A_D, 5);
        addOption(q5, "어쿠스틱 악기의 생생한 연주 (기타, 피아노, 드럼)", AxisDirection.A, 5, 1);
        addOption(q5, "전자 악기의 독특한 신스 사운드 (신시사이저, 일렉트로닉)", AxisDirection.D, 5, 2);

        // Q6: 음악 감상 방식
        Question q6 = createQuestion(survey, "라이브 공연과 스튜디오 음원 중 선호하는 것은?", MutiAxis.A_D, 6);
        addOption(q6, "라이브 공연의 날것 그대로의 에너지", AxisDirection.A, 4, 1);
        addOption(q6, "스튜디오 음원의 완벽하게 다듬어진 사운드", AxisDirection.D, 4, 2);

        // ===== P_U 축 질문 =====
        // Q7: 음악 발견 방식
        Question q7 = createQuestion(survey, "주로 어떤 방식으로 음악을 발견하나요?", MutiAxis.P_U, 7);
        addOption(q7, "음악 차트나 인기곡 플레이리스트", AxisDirection.P, 5, 1);
        addOption(q7, "숨겨진 인디 아티스트나 언더그라운드 씬", AxisDirection.U, 5, 2);

        // Q8: 음악 취향
        Question q8 = createQuestion(survey, "더 선호하는 음악 스타일은?", MutiAxis.P_U, 8);
        addOption(q8, "많은 사람들이 공감할 수 있는 대중적인 음악", AxisDirection.P, 4, 1);
        addOption(q8, "독특하고 실험적인 비주류 음악", AxisDirection.U, 4, 2);

        // 저장
        surveyRepository.save(survey);

        log.info("Initial survey data created successfully:");
        log.info("  - Survey: {}", survey.getTitle());
        log.info("  - Questions: {}", survey.getQuestions().size());
        log.info("  - Total Options: {}",
                survey.getQuestions().stream()
                        .mapToInt(q -> q.getOptions().size())
                        .sum());
    }

    private Question createQuestion(Survey survey, String content, MutiAxis axis, int orderIndex) {
        Question question = Question.builder()
                .content(content)
                .axis(axis)
                .orderIndex(orderIndex)
                .build();
        survey.addQuestion(question);
        return question;
    }

    private void addOption(Question question, String content, AxisDirection direction, int score, int orderIndex) {
        QuestionOption option = QuestionOption.builder()
                .content(content)
                .direction(direction)
                .score(score)
                .orderIndex(orderIndex)
                .build();
        question.addOption(option);
    }
}