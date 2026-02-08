package com.muti.domain.music.repository;

import com.muti.domain.music.entity.PlaylistMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 플레이리스트-음악 Repository
 */
@Repository
public interface PlaylistMusicRepository extends JpaRepository<PlaylistMusic, Long> {

    /**
     * 플레이리스트의 음악 목록 조회 (순서대로)
     */
    @Query("SELECT pm FROM PlaylistMusic pm " +
           "JOIN FETCH pm.music m " +
           "WHERE pm.playlist.id = :playlistId " +
           "ORDER BY pm.orderIndex ASC")
    List<PlaylistMusic> findByPlaylistIdOrderByOrderIndex(@Param("playlistId") Long playlistId);

    /**
     * 플레이리스트에 음악이 존재하는지 확인
     */
    boolean existsByPlaylistIdAndMusicId(Long playlistId, Long musicId);

    /**
     * 플레이리스트에서 음악 찾기
     */
    Optional<PlaylistMusic> findByPlaylistIdAndMusicId(Long playlistId, Long musicId);

    /**
     * 플레이리스트의 음악 개수
     */
    long countByPlaylistId(Long playlistId);

    /**
     * 플레이리스트의 최대 orderIndex 찾기
     */
    @Query("SELECT COALESCE(MAX(pm.orderIndex), -1) FROM PlaylistMusic pm " +
           "WHERE pm.playlist.id = :playlistId")
    Integer findMaxOrderIndexByPlaylistId(@Param("playlistId") Long playlistId);

    /**
     * 플레이리스트에서 음악 삭제
     */
    @Modifying
    @Transactional
    void deleteByPlaylistIdAndMusicId(Long playlistId, Long musicId);

    /**
     * 특정 음악이 포함된 플레이리스트 개수
     */
    long countByMusicId(Long musicId);

    /**
     * 사용자의 플레이리스트에 음악이 있는지 확인
     */
    @Query("SELECT CASE WHEN COUNT(pm) > 0 THEN true ELSE false END " +
           "FROM PlaylistMusic pm " +
           "WHERE pm.music.id = :musicId " +
           "AND pm.playlist.user.id = :userId")
    boolean existsByMusicIdAndUserId(@Param("musicId") Long musicId,
                                       @Param("userId") Long userId);
}