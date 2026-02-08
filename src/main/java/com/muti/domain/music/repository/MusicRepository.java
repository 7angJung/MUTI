package com.muti.domain.music.repository;

import com.muti.domain.music.entity.Music;
import com.muti.domain.music.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 음악 Repository
 */
@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

    /**
     * Spotify ID로 음악 찾기
     */
    Optional<Music> findBySpotifyId(String spotifyId);

    /**
     * 아티스트로 음악 검색
     */
    List<Music> findByArtistContaining(String artist);

    /**
     * 제목으로 음악 검색
     */
    List<Music> findByTitleContaining(String title);

    /**
     * 장르별 음악 조회 (페이징)
     */
    Page<Music> findByGenre(Genre genre, Pageable pageable);

    /**
     * 제목 또는 아티스트로 음악 검색 (페이징)
     */
    @Query("SELECT m FROM Music m " +
           "WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.artist) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Music> searchByTitleOrArtist(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 최신 음악 조회 (발매일 기준)
     */
    List<Music> findTop10ByOrderByReleaseDateDesc();

    /**
     * Spotify ID 존재 여부 확인
     */
    boolean existsBySpotifyId(String spotifyId);

    /**
     * 장르별 음악 개수
     */
    long countByGenre(Genre genre);
}