package com.muti.domain.music.service;

import com.muti.domain.music.dto.request.CreateMusicRequest;
import com.muti.domain.music.dto.request.UpdateMusicRequest;
import com.muti.domain.music.dto.response.MusicDto;
import com.muti.domain.music.entity.Music;
import com.muti.domain.music.enums.Genre;
import com.muti.domain.music.repository.MusicRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * MusicService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MusicService 테스트")
class MusicServiceTest {

    @Mock
    private MusicRepository musicRepository;

    @InjectMocks
    private MusicService musicService;

    @Test
    @DisplayName("음악 등록 - 성공")
    void createMusic_Success() {
        // given
        CreateMusicRequest request = CreateMusicRequest.builder()
                .title("테스트 음악")
                .artist("테스트 아티스트")
                .genre(Genre.POP)
                .spotifyId("spotify123")
                .build();

        Music savedMusic = Music.builder()
                .title("테스트 음악")
                .artist("테스트 아티스트")
                .genre(Genre.POP)
                .spotifyId("spotify123")
                .build();

        given(musicRepository.existsBySpotifyId("spotify123")).willReturn(false);
        given(musicRepository.save(any(Music.class))).willReturn(savedMusic);

        // when
        MusicDto result = musicService.createMusic(request);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 음악");
        assertThat(result.getArtist()).isEqualTo("테스트 아티스트");
        verify(musicRepository).save(any(Music.class));
    }

    @Test
    @DisplayName("음악 등록 - Spotify ID 중복")
    void createMusic_DuplicateSpotifyId() {
        // given
        CreateMusicRequest request = CreateMusicRequest.builder()
                .title("테스트 음악")
                .artist("테스트 아티스트")
                .spotifyId("spotify123")
                .build();

        given(musicRepository.existsBySpotifyId("spotify123")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> musicService.createMusic(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MUSIC_ALREADY_EXISTS);

        verify(musicRepository, never()).save(any());
    }

    @Test
    @DisplayName("음악 조회 - 성공")
    void getMusic_Success() {
        // given
        Music music = Music.builder()
                .title("테스트 음악")
                .artist("테스트 아티스트")
                .build();

        given(musicRepository.findById(1L)).willReturn(Optional.of(music));

        // when
        MusicDto result = musicService.getMusic(1L);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 음악");
        verify(musicRepository).findById(1L);
    }

    @Test
    @DisplayName("음악 조회 - 존재하지 않음")
    void getMusic_NotFound() {
        // given
        given(musicRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> musicService.getMusic(999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MUSIC_NOT_FOUND);
    }

    @Test
    @DisplayName("음악 수정 - 성공")
    void updateMusic_Success() {
        // given
        UpdateMusicRequest request = UpdateMusicRequest.builder()
                .title("수정된 제목")
                .artist("수정된 아티스트")
                .genre(Genre.JAZZ)
                .build();

        Music music = Music.builder()
                .title("원래 제목")
                .artist("원래 아티스트")
                .genre(Genre.POP)
                .build();

        given(musicRepository.findById(1L)).willReturn(Optional.of(music));

        // when
        MusicDto result = musicService.updateMusic(1L, request);

        // then
        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getArtist()).isEqualTo("수정된 아티스트");
        assertThat(result.getGenre()).isEqualTo("JAZZ");
        verify(musicRepository).findById(1L);
    }

    @Test
    @DisplayName("음악 삭제 - 성공")
    void deleteMusic_Success() {
        // given
        Music music = Music.builder()
                .title("테스트 음악")
                .artist("테스트 아티스트")
                .build();

        given(musicRepository.findById(1L)).willReturn(Optional.of(music));

        // when
        musicService.deleteMusic(1L);

        // then
        verify(musicRepository).delete(music);
    }

    @Test
    @DisplayName("음악 검색 - 성공")
    void searchMusic_Success() {
        // given
        Music music = Music.builder()
                .title("검색된 음악")
                .artist("검색된 아티스트")
                .build();

        Page<Music> page = new PageImpl<>(List.of(music));
        given(musicRepository.searchByTitleOrArtist("검색", PageRequest.of(0, 10)))
                .willReturn(page);

        // when
        Page<MusicDto> result = musicService.searchMusic("검색", PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("검색된 음악");
    }

    @Test
    @DisplayName("장르별 음악 조회 - 성공")
    void getMusicsByGenre_Success() {
        // given
        Music music = Music.builder()
                .title("팝 음악")
                .artist("아티스트")
                .genre(Genre.POP)
                .build();

        Page<Music> page = new PageImpl<>(List.of(music));
        given(musicRepository.findByGenre(Genre.POP, PageRequest.of(0, 10)))
                .willReturn(page);

        // when
        Page<MusicDto> result = musicService.getMusicsByGenre(Genre.POP, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getGenre()).isEqualTo("POP");
    }

    @Test
    @DisplayName("최신 음악 조회 - 성공")
    void getRecentMusics_Success() {
        // given
        Music music1 = Music.builder()
                .title("최신 음악 1")
                .artist("아티스트 1")
                .releaseDate(LocalDate.of(2024, 2, 1))
                .build();
        Music music2 = Music.builder()
                .title("최신 음악 2")
                .artist("아티스트 2")
                .releaseDate(LocalDate.of(2024, 1, 1))
                .build();

        given(musicRepository.findTop10ByOrderByReleaseDateDesc())
                .willReturn(List.of(music1, music2));

        // when
        List<MusicDto> results = musicService.getRecentMusics();

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTitle()).isEqualTo("최신 음악 1");
    }

    @Test
    @DisplayName("Spotify ID로 음악 조회 - 성공")
    void getMusicBySpotifyId_Success() {
        // given
        Music music = Music.builder()
                .title("Spotify 음악")
                .artist("아티스트")
                .spotifyId("spotify123")
                .build();

        given(musicRepository.findBySpotifyId("spotify123")).willReturn(Optional.of(music));

        // when
        MusicDto result = musicService.getMusicBySpotifyId("spotify123");

        // then
        assertThat(result.getSpotifyId()).isEqualTo("spotify123");
        verify(musicRepository).findBySpotifyId("spotify123");
    }

    @Test
    @DisplayName("Spotify ID로 음악 조회 - 존재하지 않음")
    void getMusicBySpotifyId_NotFound() {
        // given
        given(musicRepository.findBySpotifyId("nonexistent")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> musicService.getMusicBySpotifyId("nonexistent"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MUSIC_NOT_FOUND);
    }
}