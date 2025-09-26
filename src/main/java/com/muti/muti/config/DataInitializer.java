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

    private final MusicRepository musicRepository;
    private final QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception {
        createMusicData();
        createSurveyQuestions();
    }

    private void createMusicData() {
        if (musicRepository.count() == 0) {
            musicRepository.saveAll(Arrays.asList(
                new Music("Bohemian Rhapsody", "Queen", "Rock"),
                new Music("Stairway to Heaven", "Led Zeppelin", "Rock"),
                new Music("Hotel California", "Eagles", "Rock"),

                new Music("Shape of You", "Ed Sheeran", "Pop"),
                new Music("Uptown Funk", "Mark Ronson ft. Bruno Mars", "Pop"),
                new Music("Blinding Lights", "The Weeknd", "Pop"),

                new Music("So What", "Miles Davis", "Jazz"),
                new Music("Take Five", "The Dave Brubeck Quartet", "Jazz"),
                new Music("My Favorite Things", "John Coltrane", "Jazz"),

                new Music("Symphony No. 5", "Ludwig van Beethoven", "Classical"),
                new Music("The Four Seasons", "Antonio Vivaldi", "Classical"),
                new Music("Clair de Lune", "Claude Debussy", "Classical"),

                new Music("Lose Yourself", "Eminem", "Hip-Hop"),
                new Music("Juicy", "The Notorious B.I.G.", "Hip-Hop"),
                new Music("Nuthin' but a 'G' Thang", "Dr. Dre ft. Snoop Dogg", "Hip-Hop")
            ));
        }
    }

    private void createSurveyQuestions() {
        if (questionRepository.count() == 0) {
            // E vs I
            Question q1 = new Question("음악을 들을 때, 당신을 더 사로잡는 것은 무엇인가요?");
            q1.addChoice(new Choice("가슴을 울리는 멜로디와 가사", MutiTrait.E));
            q1.addChoice(new Choice("감탄을 자아내는 악기 연주와 곡의 구성", MutiTrait.I));

            // S vs F
            Question q2 = new Question("오늘 당신의 기분과 더 잘 맞는 플레이리스트는?");
            q2.addChoice(new Choice("차분한 저녁, 혼자만의 시간을 위한 느긋한 음악", MutiTrait.S));
            q2.addChoice(new Choice("활기찬 아침, 에너지가 필요한 순간을 위한 신나는 음악", MutiTrait.F));

            // A vs D
            Question q3 = new Question("새로운 음악을 발견하는 당신의 주된 방법은?");
            q3.addChoice(new Choice("직접 아티스트나 앨범을 찾아보고, 숨겨진 명곡을 발굴한다", MutiTrait.A));
            q3.addChoice(new Choice("유튜브, 스포티파이 등 앱이 추천해주는 음악을 편하게 듣는다", MutiTrait.D));

            // P vs U
            Question q4 = new Question("당신이 더 끌리는 콘서트는?");
            q4.addChoice(new Choice("수만 명과 함께 떼창하는 메가 히트곡 페스티벌", MutiTrait.P));
            q4.addChoice(new Choice("나만 알고 싶은 아티스트의 소규모 클럽 공연", MutiTrait.U));

            questionRepository.saveAll(Arrays.asList(q1, q2, q3, q4));
        }
    }
}