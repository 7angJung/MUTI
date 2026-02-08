package com.muti.domain.music.repository;

import com.muti.config.TestJpaConfig;
import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.music.entity.Music;
import com.muti.domain.music.entity.Playlist;
import com.muti.domain.music.entity.PlaylistMusic;
import com.muti.domain.music.enums.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * PlaylistMusicRepository 통합 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@DisplayName("PlaylistMusicRepository 통합 테스트")
class PlaylistMusicRepositoryTest {

    @Autowired
    private PlaylistMusicRepository playlistMusicRepository;

    @Autowired
    private TestEntityManager em;

    private User user;
    private Playlist playlist;
    private Music music1;
    private Music music2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .password("password123")
                .username("테스터")
                .role(UserRole.USER)
                .build();
        em.persist(user);

        playlist = Playlist.builder()
                .name("테스트 플레이리스트")
                .user(user)
                .isPublic(true)
                .build();
        em.persist(playlist);

        music1 = Music.builder()
                .title("음악 1")
                .artist("아티스트 1")
                .genre(Genre.POP)
                .build();
        em.persist(music1);

        music2 = Music.builder()
                .title("음악 2")
                .artist("아티스트 2")
                .genre(Genre.ROCK)
                .build();
        em.persist(music2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("플레이리스트에 음악 추가")
    void addMusicToPlaylist() {
        // given
        PlaylistMusic pm = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(0)
                .build();

        // when
        PlaylistMusic saved = playlistMusicRepository.save(pm);
        em.flush();
        em.clear();

        // then
        Optional<PlaylistMusic> found = playlistMusicRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getMusic().getTitle()).isEqualTo("음악 1");
    }

    @Test
    @DisplayName("플레이리스트의 음악 목록 조회 (순서대로)")
    void findByPlaylistIdOrderByOrderIndex() {
        // given
        PlaylistMusic pm1 = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(1)
                .build();
        PlaylistMusic pm2 = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music2)
                .orderIndex(0)
                .build();
        playlistMusicRepository.save(pm1);
        playlistMusicRepository.save(pm2);
        em.flush();
        em.clear();

        // when
        List<PlaylistMusic> results = playlistMusicRepository
                .findByPlaylistIdOrderByOrderIndex(playlist.getId());

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getOrderIndex()).isEqualTo(0);
        assertThat(results.get(1).getOrderIndex()).isEqualTo(1);
    }

    @Test
    @DisplayName("플레이리스트에 음악 존재 여부 확인")
    void existsByPlaylistIdAndMusicId() {
        // given
        PlaylistMusic pm = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(0)
                .build();
        playlistMusicRepository.save(pm);
        em.flush();

        // when
        boolean exists = playlistMusicRepository.existsByPlaylistIdAndMusicId(
                playlist.getId(), music1.getId());
        boolean notExists = playlistMusicRepository.existsByPlaylistIdAndMusicId(
                playlist.getId(), 999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("플레이리스트에서 음악 찾기")
    void findByPlaylistIdAndMusicId() {
        // given
        PlaylistMusic pm = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(0)
                .build();
        playlistMusicRepository.save(pm);
        em.flush();

        // when
        Optional<PlaylistMusic> result = playlistMusicRepository
                .findByPlaylistIdAndMusicId(playlist.getId(), music1.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMusic().getId()).isEqualTo(music1.getId());
    }

    @Test
    @DisplayName("플레이리스트의 음악 개수")
    void countByPlaylistId() {
        // given
        PlaylistMusic pm1 = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(0)
                .build();
        PlaylistMusic pm2 = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music2)
                .orderIndex(1)
                .build();
        playlistMusicRepository.save(pm1);
        playlistMusicRepository.save(pm2);
        em.flush();

        // when
        long count = playlistMusicRepository.countByPlaylistId(playlist.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("플레이리스트의 최대 orderIndex 찾기")
    void findMaxOrderIndexByPlaylistId() {
        // given
        PlaylistMusic pm1 = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(0)
                .build();
        PlaylistMusic pm2 = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music2)
                .orderIndex(5)
                .build();
        playlistMusicRepository.save(pm1);
        playlistMusicRepository.save(pm2);
        em.flush();

        // when
        Integer maxOrder = playlistMusicRepository.findMaxOrderIndexByPlaylistId(playlist.getId());

        // then
        assertThat(maxOrder).isEqualTo(5);
    }

    @Test
    @DisplayName("빈 플레이리스트의 최대 orderIndex는 -1")
    void findMaxOrderIndexByPlaylistId_Empty() {
        // when
        Integer maxOrder = playlistMusicRepository.findMaxOrderIndexByPlaylistId(playlist.getId());

        // then
        assertThat(maxOrder).isEqualTo(-1);
    }

    @Test
    @DisplayName("플레이리스트에서 음악 삭제")
    void deleteByPlaylistIdAndMusicId() {
        // given
        PlaylistMusic pm = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(0)
                .build();
        playlistMusicRepository.save(pm);
        em.flush();

        // when
        playlistMusicRepository.deleteByPlaylistIdAndMusicId(playlist.getId(), music1.getId());
        em.flush();

        // then
        boolean exists = playlistMusicRepository.existsByPlaylistIdAndMusicId(
                playlist.getId(), music1.getId());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("특정 음악이 포함된 플레이리스트 개수")
    void countByMusicId() {
        // given
        Playlist playlist2 = Playlist.builder()
                .name("플레이리스트 2")
                .user(user)
                .isPublic(true)
                .build();
        em.persist(playlist2);

        PlaylistMusic pm1 = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(0)
                .build();
        PlaylistMusic pm2 = PlaylistMusic.builder()
                .playlist(playlist2)
                .music(music1)
                .orderIndex(0)
                .build();
        playlistMusicRepository.save(pm1);
        playlistMusicRepository.save(pm2);
        em.flush();

        // when
        long count = playlistMusicRepository.countByMusicId(music1.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자의 플레이리스트에 음악 존재 여부")
    void existsByMusicIdAndUserId() {
        // given
        PlaylistMusic pm = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(0)
                .build();
        playlistMusicRepository.save(pm);
        em.flush();

        // when
        boolean exists = playlistMusicRepository.existsByMusicIdAndUserId(
                music1.getId(), user.getId());
        boolean notExists = playlistMusicRepository.existsByMusicIdAndUserId(
                music1.getId(), 999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("순서 변경")
    void updateOrder() {
        // given
        PlaylistMusic pm = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music1)
                .orderIndex(0)
                .build();
        PlaylistMusic saved = playlistMusicRepository.save(pm);
        em.flush();
        em.clear();

        // when
        PlaylistMusic found = playlistMusicRepository.findById(saved.getId()).orElseThrow();
        found.updateOrder(5);
        em.flush();
        em.clear();

        // then
        PlaylistMusic updated = playlistMusicRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getOrderIndex()).isEqualTo(5);
    }
}