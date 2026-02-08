package com.muti.domain.music.service;

import com.muti.domain.music.dto.request.CreateMusicRequest;
import com.muti.domain.music.dto.request.UpdateMusicRequest;
import com.muti.domain.music.dto.response.MusicDto;
import com.muti.domain.music.entity.Music;
import com.muti.domain.music.enums.Genre;
import com.muti.domain.music.repository.MusicRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 음악 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicService {

    private final MusicRepository musicRepository;

    /**
     * 음악 등록
     */
    @Transactional
    public MusicDto createMusic(CreateMusicRequest request) {
        // Spotify ID 중복 확인
        if (request.getSpotifyId() != null &&
                musicRepository.existsBySpotifyId(request.getSpotifyId())) {
            throw new BusinessException(ErrorCode.MUSIC_ALREADY_EXISTS);
        }

        Music music = Music.builder()
                .title(request.getTitle())
                .artist(request.getArtist())
                .album(request.getAlbum())
                .genre(request.getGenre())
                .spotifyId(request.getSpotifyId())
                .youtubeId(request.getYoutubeId())
                .durationMs(request.getDurationMs())
                .releaseDate(request.getReleaseDate())
                .imageUrl(request.getImageUrl())
                .previewUrl(request.getPreviewUrl())
                .description(request.getDescription())
                .build();

        Music saved = musicRepository.save(music);

        return MusicDto.from(saved);
    }

    /**
     * 음악 조회
     */
    public MusicDto getMusic(Long id) {
        Music music = getMusicEntity(id);
        return MusicDto.from(music);
    }

    /**
     * 음악 수정 (관리자만)
     */
    @Transactional
    public MusicDto updateMusic(Long id, UpdateMusicRequest request) {
        Music music = getMusicEntity(id);

        music.update(
                request.getTitle(),
                request.getArtist(),
                request.getAlbum(),
                request.getGenre(),
                request.getDescription()
        );

        return MusicDto.from(music);
    }

    /**
     * 음악 삭제 (관리자만)
     */
    @Transactional
    public void deleteMusic(Long id) {
        Music music = getMusicEntity(id);
        musicRepository.delete(music);
    }

    /**
     * 음악 검색 (제목 또는 아티스트)
     */
    public Page<MusicDto> searchMusic(String keyword, Pageable pageable) {
        Page<Music> musics = musicRepository.searchByTitleOrArtist(keyword, pageable);
        return musics.map(MusicDto::from);
    }

    /**
     * 장르별 음악 조회
     */
    public Page<MusicDto> getMusicsByGenre(Genre genre, Pageable pageable) {
        Page<Music> musics = musicRepository.findByGenre(genre, pageable);
        return musics.map(MusicDto::from);
    }

    /**
     * 최신 음악 조회
     */
    public List<MusicDto> getRecentMusics() {
        List<Music> musics = musicRepository.findTop10ByOrderByReleaseDateDesc();
        return musics.stream()
                .map(MusicDto::from)
                .collect(Collectors.toList());
    }

    /**
     * Spotify ID로 음악 조회
     */
    public MusicDto getMusicBySpotifyId(String spotifyId) {
        Music music = musicRepository.findBySpotifyId(spotifyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));
        return MusicDto.from(music);
    }

    /**
     * Helper: 음악 Entity 조회
     */
    public Music getMusicEntity(Long id) {
        return musicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MUSIC_NOT_FOUND));
    }
}