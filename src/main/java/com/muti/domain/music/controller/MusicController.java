package com.muti.domain.music.controller;

import com.muti.domain.music.dto.request.CreateMusicRequest;
import com.muti.domain.music.dto.request.UpdateMusicRequest;
import com.muti.domain.music.dto.response.MusicDto;
import com.muti.domain.music.enums.Genre;
import com.muti.domain.music.service.MusicService;
import com.muti.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 음악 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/musics")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    /**
     * 음악 등록 (관리자만)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<MusicDto> createMusic(@Valid @RequestBody CreateMusicRequest request) {
        MusicDto music = musicService.createMusic(request);
        return ApiResponse.success(music);
    }

    /**
     * 음악 조회
     */
    @GetMapping("/{id}")
    public ApiResponse<MusicDto> getMusic(@PathVariable Long id) {
        MusicDto music = musicService.getMusic(id);
        return ApiResponse.success(music);
    }

    /**
     * 음악 수정 (관리자만)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<MusicDto> updateMusic(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMusicRequest request) {
        MusicDto music = musicService.updateMusic(id, request);
        return ApiResponse.success(music);
    }

    /**
     * 음악 삭제 (관리자만)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMusic(@PathVariable Long id) {
        musicService.deleteMusic(id);
        return ApiResponse.success(null);
    }

    /**
     * 음악 검색 (제목 또는 아티스트)
     */
    @GetMapping("/search")
    public ApiResponse<Page<MusicDto>> searchMusic(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<MusicDto> musics = musicService.searchMusic(keyword, pageable);
        return ApiResponse.success(musics);
    }

    /**
     * 장르별 음악 조회
     */
    @GetMapping("/genre/{genre}")
    public ApiResponse<Page<MusicDto>> getMusicsByGenre(
            @PathVariable Genre genre,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<MusicDto> musics = musicService.getMusicsByGenre(genre, pageable);
        return ApiResponse.success(musics);
    }

    /**
     * 최신 음악 조회
     */
    @GetMapping("/recent")
    public ApiResponse<List<MusicDto>> getRecentMusics() {
        List<MusicDto> musics = musicService.getRecentMusics();
        return ApiResponse.success(musics);
    }

    /**
     * Spotify ID로 음악 조회
     */
    @GetMapping("/spotify/{spotifyId}")
    public ApiResponse<MusicDto> getMusicBySpotifyId(@PathVariable String spotifyId) {
        MusicDto music = musicService.getMusicBySpotifyId(spotifyId);
        return ApiResponse.success(music);
    }
}