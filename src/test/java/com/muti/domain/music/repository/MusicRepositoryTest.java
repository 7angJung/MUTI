package com.muti.domain.music.repository;

import com.muti.config.TestJpaConfig;
import com.muti.domain.music.entity.Music;
import com.muti.domain.music.enums.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * MusicRepository 통합 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@DisplayName("MusicRepository 통합 테스트")
class MusicRepositoryTest {

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private TestEntityManager em;

    private Music music1;
    private Music music2;

    @BeforeEach
    void setUp() {
        music1 = Music.builder()
                .title("Test Song 1")
                .artist("Test Artist")
                .album("Test Album")
                .genre(Genre.POP)
                .spotifyId("spotify1")
                .durationMs(180000)
                .releaseDate(LocalDate.of(2024, 1, 1))
                .build();
        em.persist(music1);

        music2 = Music.builder()
                .title("Another Song")
                .artist("Another Artist")
                .album("Another Album")
                .genre(Genre.ROCK)
                .spotifyId("spotify2")
                .durationMs(240000)
                .releaseDate(LocalDate.of(2024, 2, 1))
                .build();
        em.persist(music2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("음악 저장 및 조회")
    void saveMusic() {
        // given
        Music music = Music.builder()
                .title("새 음악")
                .artist("새 아티스트")
                .genre(Genre.JAZZ)
                .build();

        // when
        Music saved = musicRepository.save(music);
        em.flush();
        em.clear();

        // then
        Optional<Music> found = musicRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("새 음악");
        assertThat(found.get().getArtist()).isEqualTo("새 아티스트");
    }

    @Test
    @DisplayName("Spotify ID로 음악 찾기")
    void findBySpotifyId() {
        // when
        Optional<Music> result = musicRepository.findBySpotifyId("spotify1");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Song 1");
    }

    @Test
    @DisplayName("Spotify ID 존재 여부 확인")
    void existsBySpotifyId() {
        // when
        boolean exists = musicRepository.existsBySpotifyId("spotify1");
        boolean notExists = musicRepository.existsBySpotifyId("nonexistent");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("아티스트로 음악 검색")
    void findByArtistContaining() {
        // when
        List<Music> results = musicRepository.findByArtistContaining("Test");

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getArtist()).isEqualTo("Test Artist");
    }

    @Test
    @DisplayName("제목으로 음악 검색")
    void findByTitleContaining() {
        // when
        List<Music> results = musicRepository.findByTitleContaining("Another");

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Another Song");
    }

    @Test
    @DisplayName("장르별 음악 조회")
    void findByGenre() {
        // when
        Page<Music> results = musicRepository.findByGenre(Genre.POP, PageRequest.of(0, 10));

        // then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getGenre()).isEqualTo(Genre.POP);
    }

    @Test
    @DisplayName("제목 또는 아티스트로 검색")
    void searchByTitleOrArtist() {
        // when
        Page<Music> results = musicRepository.searchByTitleOrArtist("test", PageRequest.of(0, 10));

        // then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getTitle()).isEqualTo("Test Song 1");
    }

    @Test
    @DisplayName("최신 음악 조회")
    void findTop10ByOrderByReleaseDateDesc() {
        // when
        List<Music> results = musicRepository.findTop10ByOrderByReleaseDateDesc();

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getReleaseDate()).isAfter(results.get(1).getReleaseDate());
    }

    @Test
    @DisplayName("장르별 음악 개수")
    void countByGenre() {
        // when
        long count = musicRepository.countByGenre(Genre.POP);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("음악 수정")
    void updateMusic() {
        // given
        Music music = musicRepository.findById(music1.getId()).orElseThrow();

        // when
        music.update("Updated Title", "Updated Artist", "Updated Album", Genre.JAZZ, "Updated Description");
        em.flush();
        em.clear();

        // then
        Music updated = musicRepository.findById(music1.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getArtist()).isEqualTo("Updated Artist");
        assertThat(updated.getGenre()).isEqualTo(Genre.JAZZ);
    }

    @Test
    @DisplayName("음악 삭제")
    void deleteMusic() {
        // when
        musicRepository.deleteById(music1.getId());
        em.flush();

        // then
        Optional<Music> result = musicRepository.findById(music1.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("재생 시간 포맷 변환")
    void getFormattedDuration() {
        // given
        Music music = musicRepository.findById(music1.getId()).orElseThrow();

        // when
        String formatted = music.getFormattedDuration();

        // then
        assertThat(formatted).isEqualTo("3:00"); // 180000ms = 3분
    }
}