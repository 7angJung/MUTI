package com.muti.domain.music.controller;

import com.muti.domain.music.dto.request.AddMusicToPlaylistRequest;
import com.muti.domain.music.dto.request.CreatePlaylistRequest;
import com.muti.domain.music.dto.request.UpdatePlaylistRequest;
import com.muti.domain.music.dto.response.PlaylistDetailDto;
import com.muti.domain.music.dto.response.PlaylistDto;
import com.muti.domain.music.service.PlaylistService;
import com.muti.domain.survey.enums.MutiType;
import com.muti.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 플레이리스트 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    /**
     * SecurityContext에서 현재 사용자 ID 추출
     */
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        } else if (principal instanceof String) {
            String principalStr = (String) principal;
            if ("anonymousUser".equals(principalStr)) {
                return null;
            }
            try {
                return Long.parseLong(principalStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 플레이리스트 생성
     */
    @PostMapping
    public ApiResponse<PlaylistDto> createPlaylist(
            @Valid @RequestBody CreatePlaylistRequest request) {
        Long userId = getCurrentUserId();
        PlaylistDto playlist = playlistService.createPlaylist(request, userId);
        return ApiResponse.success(playlist);
    }

    /**
     * 플레이리스트 조회 (상세)
     */
    @GetMapping("/{id}")
    public ApiResponse<PlaylistDetailDto> getPlaylist(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        PlaylistDetailDto playlist = playlistService.getPlaylist(id, userId);
        return ApiResponse.success(playlist);
    }

    /**
     * 플레이리스트 수정 (소유자만)
     */
    @PutMapping("/{id}")
    public ApiResponse<PlaylistDto> updatePlaylist(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlaylistRequest request) {
        Long userId = getCurrentUserId();
        PlaylistDto playlist = playlistService.updatePlaylist(id, request, userId);
        return ApiResponse.success(playlist);
    }

    /**
     * 플레이리스트 삭제 (소유자만)
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePlaylist(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        playlistService.deletePlaylist(id, userId);
        return ApiResponse.success(null);
    }

    /**
     * 플레이리스트에 음악 추가
     */
    @PostMapping("/{id}/musics")
    public ApiResponse<Void> addMusicToPlaylist(
            @PathVariable Long id,
            @Valid @RequestBody AddMusicToPlaylistRequest request) {
        Long userId = getCurrentUserId();
        playlistService.addMusicToPlaylist(id, request, userId);
        return ApiResponse.success(null, "음악이 플레이리스트에 추가되었습니다.");
    }

    /**
     * 플레이리스트에서 음악 삭제
     */
    @DeleteMapping("/{playlistId}/musics/{musicId}")
    public ApiResponse<Void> removeMusicFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long musicId) {
        Long userId = getCurrentUserId();
        playlistService.removeMusicFromPlaylist(playlistId, musicId, userId);
        return ApiResponse.success(null, "음악이 플레이리스트에서 삭제되었습니다.");
    }

    /**
     * 사용자의 플레이리스트 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<PlaylistDto>> getUserPlaylists(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserId();
        List<PlaylistDto> playlists = playlistService.getUserPlaylists(userId, currentUserId);
        return ApiResponse.success(playlists);
    }

    /**
     * 공개 플레이리스트 목록 조회 (페이징)
     */
    @GetMapping("/public")
    public ApiResponse<Page<PlaylistDto>> getPublicPlaylists(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<PlaylistDto> playlists = playlistService.getPublicPlaylists(pageable);
        return ApiResponse.success(playlists);
    }

    /**
     * MUTI 타입별 플레이리스트 조회
     */
    @GetMapping("/muti-type/{mutiType}")
    public ApiResponse<List<PlaylistDto>> getPlaylistsByMutiType(
            @PathVariable MutiType mutiType) {
        List<PlaylistDto> playlists = playlistService.getPlaylistsByMutiType(mutiType);
        return ApiResponse.success(playlists);
    }

    /**
     * 플레이리스트 검색
     */
    @GetMapping("/search")
    public ApiResponse<Page<PlaylistDto>> searchPlaylists(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<PlaylistDto> playlists = playlistService.searchPlaylists(keyword, pageable);
        return ApiResponse.success(playlists);
    }
}