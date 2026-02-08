package com.muti.domain.music.service;

import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.auth.repository.UserRepository;
import com.muti.domain.music.dto.request.AddMusicToPlaylistRequest;
import com.muti.domain.music.dto.request.CreatePlaylistRequest;
import com.muti.domain.music.dto.request.UpdatePlaylistRequest;
import com.muti.domain.music.dto.response.PlaylistDetailDto;
import com.muti.domain.music.dto.response.PlaylistDto;
import com.muti.domain.music.entity.Music;
import com.muti.domain.music.entity.Playlist;
import com.muti.domain.music.entity.PlaylistMusic;
import com.muti.domain.music.enums.Genre;
import com.muti.domain.music.repository.PlaylistMusicRepository;
import com.muti.domain.music.repository.PlaylistRepository;
import com.muti.domain.survey.enums.MutiType;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * PlaylistService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PlaylistService 테스트")
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PlaylistMusicRepository playlistMusicRepository;

    @Mock
    private MusicService musicService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlaylistService playlistService;

    @Test
    @DisplayName("플레이리스트 생성 - 성공")
    void createPlaylist_Success() {
        // given
        Long userId = 1L;
        CreatePlaylistRequest request = CreatePlaylistRequest.builder()
                .name("내 플레이리스트")
                .description("설명")
                .isPublic(true)
                .build();

        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .username("테스터")
                .role(UserRole.USER)
                .build();

        Playlist savedPlaylist = Playlist.builder()
                .name("내 플레이리스트")
                .description("설명")
                .user(user)
                .isPublic(true)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(playlistRepository.save(any(Playlist.class))).willReturn(savedPlaylist);

        // when
        PlaylistDto result = playlistService.createPlaylist(request, userId);

        // then
        assertThat(result.getName()).isEqualTo("내 플레이리스트");
        assertThat(result.getIsOwner()).isTrue();
        verify(playlistRepository).save(any(Playlist.class));
    }

    @Test
    @DisplayName("플레이리스트 조회 - 공개 플레이리스트")
    void getPlaylist_Public() {
        // given
        Long playlistId = 1L;
        Long userId = 1L;

        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("공개 플레이리스트")
                .user(user)
                .isPublic(true)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));
        given(playlistMusicRepository.findByPlaylistIdOrderByOrderIndex(playlistId))
                .willReturn(List.of());

        // when
        PlaylistDetailDto result = playlistService.getPlaylist(playlistId, userId);

        // then
        assertThat(result.getName()).isEqualTo("공개 플레이리스트");
        assertThat(result.getIsOwner()).isTrue();
    }

    @Test
    @DisplayName("플레이리스트 조회 - 비공개 플레이리스트 (소유자 아님)")
    void getPlaylist_Private_NotOwner() {
        // given
        Long playlistId = 1L;
        Long ownerId = 1L;
        Long otherUserId = 2L;

        User owner = User.builder().id(ownerId).email("owner@example.com").username("소유자").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("비공개 플레이리스트")
                .user(owner)
                .isPublic(false)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));

        // when & then
        assertThatThrownBy(() -> playlistService.getPlaylist(playlistId, otherUserId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("플레이리스트 수정 - 성공")
    void updatePlaylist_Success() {
        // given
        Long playlistId = 1L;
        Long userId = 1L;

        UpdatePlaylistRequest request = UpdatePlaylistRequest.builder()
                .name("수정된 이름")
                .description("수정된 설명")
                .isPublic(false)
                .build();

        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("원래 이름")
                .user(user)
                .isPublic(true)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));

        // when
        PlaylistDto result = playlistService.updatePlaylist(playlistId, request, userId);

        // then
        assertThat(result.getName()).isEqualTo("수정된 이름");
        assertThat(result.getDescription()).isEqualTo("수정된 설명");
        assertThat(result.getIsPublic()).isFalse();
    }

    @Test
    @DisplayName("플레이리스트 수정 - 권한 없음")
    void updatePlaylist_Forbidden() {
        // given
        Long playlistId = 1L;
        Long ownerId = 1L;
        Long otherUserId = 2L;

        UpdatePlaylistRequest request = UpdatePlaylistRequest.builder()
                .name("수정된 이름")
                .build();

        User owner = User.builder().id(ownerId).email("owner@example.com").username("소유자").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("원래 이름")
                .user(owner)
                .isPublic(true)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));

        // when & then
        assertThatThrownBy(() -> playlistService.updatePlaylist(playlistId, request, otherUserId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PLAYLIST_UPDATE_FORBIDDEN);
    }

    @Test
    @DisplayName("플레이리스트 삭제 - 성공")
    void deletePlaylist_Success() {
        // given
        Long playlistId = 1L;
        Long userId = 1L;

        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("플레이리스트")
                .user(user)
                .isPublic(true)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));

        // when
        playlistService.deletePlaylist(playlistId, userId);

        // then
        verify(playlistRepository).delete(playlist);
    }

    @Test
    @DisplayName("플레이리스트에 음악 추가 - 성공")
    void addMusicToPlaylist_Success() {
        // given
        Long playlistId = 1L;
        Long userId = 1L;
        Long musicId = 1L;

        AddMusicToPlaylistRequest request = AddMusicToPlaylistRequest.builder()
                .musicId(musicId)
                .build();

        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("플레이리스트")
                .user(user)
                .isPublic(true)
                .build();

        Music music = Music.builder()
                .title("음악")
                .artist("아티스트")
                .genre(Genre.POP)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));
        given(musicService.getMusicEntity(musicId)).willReturn(music);
        given(playlistMusicRepository.existsByPlaylistIdAndMusicId(playlistId, musicId))
                .willReturn(false);
        given(playlistMusicRepository.findMaxOrderIndexByPlaylistId(playlistId))
                .willReturn(0);

        // when
        playlistService.addMusicToPlaylist(playlistId, request, userId);

        // then
        verify(playlistMusicRepository).save(any(PlaylistMusic.class));
    }

    @Test
    @DisplayName("플레이리스트에 음악 추가 - 이미 존재함")
    void addMusicToPlaylist_AlreadyExists() {
        // given
        Long playlistId = 1L;
        Long userId = 1L;
        Long musicId = 1L;

        AddMusicToPlaylistRequest request = AddMusicToPlaylistRequest.builder()
                .musicId(musicId)
                .build();

        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("플레이리스트")
                .user(user)
                .isPublic(true)
                .build();

        Music music = Music.builder()
                .title("음악")
                .artist("아티스트")
                .genre(Genre.POP)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));
        given(musicService.getMusicEntity(musicId)).willReturn(music);
        given(playlistMusicRepository.existsByPlaylistIdAndMusicId(playlistId, musicId))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> playlistService.addMusicToPlaylist(playlistId, request, userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MUSIC_ALREADY_IN_PLAYLIST);

        verify(playlistMusicRepository, never()).save(any());
    }

    @Test
    @DisplayName("플레이리스트에서 음악 삭제 - 성공")
    void removeMusicFromPlaylist_Success() {
        // given
        Long playlistId = 1L;
        Long userId = 1L;
        Long musicId = 1L;

        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("플레이리스트")
                .user(user)
                .isPublic(true)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));
        given(playlistMusicRepository.existsByPlaylistIdAndMusicId(playlistId, musicId))
                .willReturn(true);

        // when
        playlistService.removeMusicFromPlaylist(playlistId, musicId, userId);

        // then
        verify(playlistMusicRepository).deleteByPlaylistIdAndMusicId(playlistId, musicId);
    }

    @Test
    @DisplayName("플레이리스트에서 음악 삭제 - 음악이 플레이리스트에 없음")
    void removeMusicFromPlaylist_NotInPlaylist() {
        // given
        Long playlistId = 1L;
        Long userId = 1L;
        Long musicId = 1L;

        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("플레이리스트")
                .user(user)
                .isPublic(true)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));
        given(playlistMusicRepository.existsByPlaylistIdAndMusicId(playlistId, musicId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> playlistService.removeMusicFromPlaylist(playlistId, musicId, userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MUSIC_NOT_IN_PLAYLIST);

        verify(playlistMusicRepository, never()).deleteByPlaylistIdAndMusicId(any(), any());
    }

    @Test
    @DisplayName("공개 플레이리스트 목록 조회")
    void getPublicPlaylists() {
        // given
        User user = User.builder().id(1L).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("공개 플레이리스트")
                .user(user)
                .isPublic(true)
                .build();

        Page<Playlist> page = new PageImpl<>(List.of(playlist));
        given(playlistRepository.findPublicPlaylists(PageRequest.of(0, 10)))
                .willReturn(page);

        // when
        Page<PlaylistDto> result = playlistService.getPublicPlaylists(PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("공개 플레이리스트");
    }

    @Test
    @DisplayName("MUTI 타입별 플레이리스트 조회")
    void getPlaylistsByMutiType() {
        // given
        User user = User.builder().id(1L).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Playlist playlist = Playlist.builder()
                .name("ESAP 플레이리스트")
                .user(user)
                .isPublic(true)
                .mutiType(MutiType.ESAP)
                .build();

        given(playlistRepository.findPublicPlaylistsByMutiType(MutiType.ESAP))
                .willReturn(List.of(playlist));

        // when
        List<PlaylistDto> results = playlistService.getPlaylistsByMutiType(MutiType.ESAP);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getMutiType()).isEqualTo("ESAP");
    }
}