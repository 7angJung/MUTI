package com.muti.domain.music.service;

import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.repository.UserRepository;
import com.muti.domain.music.dto.request.AddMusicToPlaylistRequest;
import com.muti.domain.music.dto.request.CreatePlaylistRequest;
import com.muti.domain.music.dto.request.UpdatePlaylistRequest;
import com.muti.domain.music.dto.response.PlaylistDetailDto;
import com.muti.domain.music.dto.response.PlaylistDto;
import com.muti.domain.music.entity.Music;
import com.muti.domain.music.entity.Playlist;
import com.muti.domain.music.entity.PlaylistMusic;
import com.muti.domain.music.repository.PlaylistMusicRepository;
import com.muti.domain.music.repository.PlaylistRepository;
import com.muti.domain.survey.enums.MutiType;
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
 * 플레이리스트 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMusicRepository playlistMusicRepository;
    private final MusicService musicService;
    private final UserRepository userRepository;

    /**
     * 플레이리스트 생성
     */
    @Transactional
    public PlaylistDto createPlaylist(CreatePlaylistRequest request, Long userId) {
        User user = getUserEntity(userId);

        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(user)
                .isPublic(request.getIsPublic())
                .mutiType(request.getMutiType())
                .imageUrl(request.getImageUrl())
                .build();

        Playlist saved = playlistRepository.save(playlist);

        return PlaylistDto.from(saved, true);
    }

    /**
     * 플레이리스트 조회 (상세)
     */
    public PlaylistDetailDto getPlaylist(Long id, Long currentUserId) {
        Playlist playlist = getPlaylistEntity(id);

        // 비공개 플레이리스트는 소유자만 조회 가능
        if (!playlist.getIsPublic() && !playlist.isOwner(currentUserId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 플레이리스트의 음악 목록 조회
        List<PlaylistMusic> playlistMusics =
                playlistMusicRepository.findByPlaylistIdOrderByOrderIndex(id);

        boolean isOwner = playlist.isOwner(currentUserId);

        return PlaylistDetailDto.from(playlist, playlistMusics, isOwner);
    }

    /**
     * 플레이리스트 수정 (소유자만)
     */
    @Transactional
    public PlaylistDto updatePlaylist(Long id, UpdatePlaylistRequest request, Long userId) {
        Playlist playlist = getPlaylistEntity(id);

        // 권한 검증
        if (!playlist.isOwner(userId)) {
            throw new BusinessException(ErrorCode.PLAYLIST_UPDATE_FORBIDDEN);
        }

        playlist.update(request.getName(), request.getDescription(), request.getIsPublic());

        return PlaylistDto.from(playlist, true);
    }

    /**
     * 플레이리스트 삭제 (소유자만)
     */
    @Transactional
    public void deletePlaylist(Long id, Long userId) {
        Playlist playlist = getPlaylistEntity(id);

        // 권한 검증
        if (!playlist.isOwner(userId)) {
            throw new BusinessException(ErrorCode.PLAYLIST_DELETE_FORBIDDEN);
        }

        playlistRepository.delete(playlist);
    }

    /**
     * 플레이리스트에 음악 추가
     */
    @Transactional
    public void addMusicToPlaylist(Long playlistId, AddMusicToPlaylistRequest request, Long userId) {
        Playlist playlist = getPlaylistEntity(playlistId);

        // 권한 검증
        if (!playlist.isOwner(userId)) {
            throw new BusinessException(ErrorCode.PLAYLIST_UPDATE_FORBIDDEN);
        }

        // 음악 조회
        Music music = musicService.getMusicEntity(request.getMusicId());

        // 중복 확인
        if (playlistMusicRepository.existsByPlaylistIdAndMusicId(playlistId, request.getMusicId())) {
            throw new BusinessException(ErrorCode.MUSIC_ALREADY_IN_PLAYLIST);
        }

        // orderIndex 결정
        Integer orderIndex = request.getOrderIndex();
        if (orderIndex == null) {
            // null이면 맨 뒤에 추가
            Integer maxOrder = playlistMusicRepository.findMaxOrderIndexByPlaylistId(playlistId);
            orderIndex = maxOrder + 1;
        }

        // 플레이리스트에 음악 추가
        PlaylistMusic playlistMusic = PlaylistMusic.builder()
                .playlist(playlist)
                .music(music)
                .orderIndex(orderIndex)
                .build();

        playlistMusicRepository.save(playlistMusic);
    }

    /**
     * 플레이리스트에서 음악 삭제
     */
    @Transactional
    public void removeMusicFromPlaylist(Long playlistId, Long musicId, Long userId) {
        Playlist playlist = getPlaylistEntity(playlistId);

        // 권한 검증
        if (!playlist.isOwner(userId)) {
            throw new BusinessException(ErrorCode.PLAYLIST_UPDATE_FORBIDDEN);
        }

        // 음악이 플레이리스트에 있는지 확인
        if (!playlistMusicRepository.existsByPlaylistIdAndMusicId(playlistId, musicId)) {
            throw new BusinessException(ErrorCode.MUSIC_NOT_IN_PLAYLIST);
        }

        playlistMusicRepository.deleteByPlaylistIdAndMusicId(playlistId, musicId);
    }

    /**
     * 사용자의 플레이리스트 목록 조회
     */
    public List<PlaylistDto> getUserPlaylists(Long userId, Long currentUserId) {
        List<Playlist> playlists = playlistRepository.findByUserId(userId);

        boolean isOwner = userId.equals(currentUserId);

        return playlists.stream()
                .filter(p -> p.getIsPublic() || isOwner) // 공개 플레이리스트 또는 본인 것만
                .map(p -> PlaylistDto.from(p, isOwner))
                .collect(Collectors.toList());
    }

    /**
     * 공개 플레이리스트 목록 조회 (페이징)
     */
    public Page<PlaylistDto> getPublicPlaylists(Pageable pageable) {
        Page<Playlist> playlists = playlistRepository.findPublicPlaylists(pageable);
        return playlists.map(PlaylistDto::from);
    }

    /**
     * MUTI 타입별 플레이리스트 조회
     */
    public List<PlaylistDto> getPlaylistsByMutiType(MutiType mutiType) {
        List<Playlist> playlists = playlistRepository.findPublicPlaylistsByMutiType(mutiType);
        return playlists.stream()
                .map(PlaylistDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 플레이리스트 검색
     */
    public Page<PlaylistDto> searchPlaylists(String keyword, Pageable pageable) {
        Page<Playlist> playlists = playlistRepository.searchByName(keyword, pageable);
        return playlists.map(PlaylistDto::from);
    }

    /**
     * Helper: 플레이리스트 Entity 조회
     */
    private Playlist getPlaylistEntity(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));
    }

    /**
     * Helper: 사용자 Entity 조회
     */
    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}