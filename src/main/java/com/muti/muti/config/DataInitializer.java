package com.muti.muti.config;

import com.muti.muti.music.domain.Music;
import com.muti.muti.music.repository.MusicRepository;
import com.muti.muti.survey.domain.Choice;
import com.muti.muti.survey.domain.MutiTrait;
import com.muti.muti.survey.domain.Question;
import com.muti.muti.survey.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final MusicRepository musicRepository;

    @Override
    public void run(String... args) {
        if (questionRepository.count() == 0) {
            initializeQuestions();
        }
        if (musicRepository.count() == 0) {
            initializeMusicData();
        }
    }

    private void initializeQuestions() {
        Question q1 = new Question("가사, 멜로디, 감정선이 중요하다 vs 악기, 사운드, 구조가 중요하다");
        q1.addChoice(new Choice("전자", MutiTrait.E));
        q1.addChoice(new Choice("후자", MutiTrait.I));

        Question q2 = new Question("차분하고 느린 템포 vs 빠르고 리드미컬한 템포");
        q2.addChoice(new Choice("전자", MutiTrait.S));
        q2.addChoice(new Choice("후자", MutiTrait.F));

        Question q3 = new Question("새로운 음악을 적극적으로 탐색 vs 알고리즘/추천에 의존");
        q3.addChoice(new Choice("전자", MutiTrait.A));
        q3.addChoice(new Choice("후자", MutiTrait.D));

        Question q4 = new Question("대중적이고 익숙한 음악 vs 독특하고 실험적인 음악");
        q4.addChoice(new Choice("전자", MutiTrait.P));
        q4.addChoice(new Choice("후자", MutiTrait.U));

        questionRepository.saveAll(Arrays.asList(q1, q2, q3, q4));
    }

    private void initializeMusicData() {
        // 감성(E), 느림(S), 수동(D), 대중(P) -> 발라드
        musicRepository.save(new Music("첫 눈처럼 너에게 가겠다", "에일리", "발라드", "ESDP"));
        // 감성(E), 빠름(F), 적극(A), 비주류(U) -> 인디 팝
        musicRepository.save(new Music("그대의 우주", "백예린", "인디 팝", "EFAU"));
        // 악기(I), 빠름(F), 적극(A), 대중(P) -> 록
        musicRepository.save(new Music("Bohemian Rhapsody", "Queen", "록", "IFAP"));
        // 악기(I), 느림(S), 수동(D), 비주류(U) -> 재즈
        musicRepository.save(new Music("Misty", "Ella Fitzgerald", "재즈", "ISDU"));
    }
}