package com.muti.domain.music.repository;

import com.muti.config.TestJpaConfig;
import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.music.entity.Playlist;
import com.muti.domain.survey.enums.MutiType;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * PlaylistRepository 통합 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@DisplayName("PlaylistRepository 통합 테스트")
class PlaylistRepositoryTest {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TestEntityManager em;

    private User user;
    private Playlist playlist1;
    private Playlist playlist2;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        user = User.builder()
                .email("test@example.com")
                .password("password123")
                .username("테스터")
                .role(UserRole.USER)
                .build();
        em.persist(user);

        // 공개 플레이리스트
        playlist1 = Playlist.builder()
                .name("내 플레이리스트")
                .description("공개 플레이리스트")
                .user(user)
                .isPublic(true)
                .build();
        em.persist(playlist1);

        // 비공개 플레이리스트
        playlist2 = Playlist.builder()
                .name("비공개 플레이리스트")
                .description("비공개")
                .user(user)
                .isPublic(false)
                .mutiType(MutiType.ESAP)
                .build();
        em.persist(playlist2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("플레이리스트 저장 및 조회")
    void savePlaylist() {
        // given
        Playlist playlist = Playlist.builder()
                .name("새 플레이리스트")
                .description("설명")
                .user(user)
                .isPublic(true)
                .build();

        // when
        Playlist saved = playlistRepository.save(playlist);
        em.flush();
        em.clear();

        // then
        Optional<Playlist> found = playlistRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("새 플레이리스트");
    }

    @Test
    @DisplayName("사용자별 플레이리스트 조회")
    void findByUserId() {
        // when
        List<Playlist> results = playlistRepository.findByUserId(user.getId());

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("공개 플레이리스트 조회")
    void findPublicPlaylists() {
        // when
        Page<Playlist> results = playlistRepository.findPublicPlaylists(PageRequest.of(0, 10));

        // then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getIsPublic()).isTrue();
    }

    @Test
    @DisplayName("MUTI 타입별 플레이리스트 조회")
    void findByMutiType() {
        // when
        List<Playlist> results = playlistRepository.findByMutiType(MutiType.ESAP);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getMutiType()).isEqualTo(MutiType.ESAP);
    }

    @Test
    @DisplayName("MUTI 타입별 공개 플레이리스트 조회")
    void findPublicPlaylistsByMutiType() {
        // given - ESAP 타입 공개 플레이리스트 추가
        Playlist publicEsap = Playlist.builder()
                .name("ESAP 공개")
                .user(user)
                .isPublic(true)
                .mutiType(MutiType.ESAP)
                .build();
        em.persist(publicEsap);
        em.flush();

        // when
        List<Playlist> results = playlistRepository.findPublicPlaylistsByMutiType(MutiType.ESAP);

        // then
        assertThat(results).hasSize(1); // playlist2는 비공개이므로 제외
        assertThat(results.get(0).getIsPublic()).isTrue();
    }

    @Test
    @DisplayName("플레이리스트 이름으로 검색")
    void searchByName() {
        // when
        Page<Playlist> results = playlistRepository.searchByName("플레이리스트", PageRequest.of(0, 10));

        // then
        assertThat(results.getContent()).hasSize(1); // 공개만
        assertThat(results.getContent().get(0).getName()).contains("플레이리스트");
    }

    @Test
    @DisplayName("사용자의 플레이리스트 개수")
    void countByUserId() {
        // when
        long count = playlistRepository.countByUserId(user.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자의 플레이리스트 중 이름으로 찾기")
    void findByUserIdAndName() {
        // when
        Optional<Playlist> result = playlistRepository.findByUserIdAndName(user.getId(), "내 플레이리스트");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("내 플레이리스트");
    }

    @Test
    @DisplayName("플레이리스트 수정")
    void updatePlaylist() {
        // given
        Playlist playlist = playlistRepository.findById(playlist1.getId()).orElseThrow();

        // when
        playlist.update("수정된 이름", "수정된 설명", false);
        em.flush();
        em.clear();

        // then
        Playlist updated = playlistRepository.findById(playlist1.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("수정된 이름");
        assertThat(updated.getDescription()).isEqualTo("수정된 설명");
        assertThat(updated.getIsPublic()).isFalse();
    }

    @Test
    @DisplayName("플레이리스트 삭제")
    void deletePlaylist() {
        // when
        playlistRepository.deleteById(playlist1.getId());
        em.flush();

        // then
        Optional<Playlist> result = playlistRepository.findById(playlist1.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("소유자 확인")
    void isOwner() {
        // given
        Playlist playlist = playlistRepository.findById(playlist1.getId()).orElseThrow();

        // when & then
        assertThat(playlist.isOwner(user.getId())).isTrue();
        assertThat(playlist.isOwner(999L)).isFalse();
    }
}