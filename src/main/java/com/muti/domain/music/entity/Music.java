package com.muti.domain.music.entity;

import com.muti.domain.music.enums.Genre;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 음악 엔티티
 */
@Entity
@Table(name = "musics", indexes = {
        @Index(name = "idx_music_artist", columnList = "artist"),
        @Index(name = "idx_music_genre", columnList = "genre"),
        @Index(name = "idx_music_spotify", columnList = "spotify_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String artist;

    @Column(length = 200)
    private String album;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Genre genre;

    @Column(name = "spotify_id", length = 50, unique = true)
    private String spotifyId;

    @Column(name = "youtube_id", length = 50)
    private String youtubeId;

    @Column(name = "duration_ms")
    private Integer durationMs; // 재생 시간 (밀리초)

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "image_url", length = 500)
    private String imageUrl; // 앨범 커버 이미지

    @Column(name = "preview_url", length = 500)
    private String previewUrl; // 미리듣기 URL (Spotify)

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistMusic> playlistMusics = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Music(String title, String artist, String album, Genre genre,
                 String spotifyId, String youtubeId, Integer durationMs,
                 LocalDate releaseDate, String imageUrl, String previewUrl,
                 String description) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.spotifyId = spotifyId;
        this.youtubeId = youtubeId;
        this.durationMs = durationMs;
        this.releaseDate = releaseDate;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
        this.description = description;
    }

    /**
     * 음악 정보 수정
     */
    public void update(String title, String artist, String album, Genre genre,
                       String description) {
        if (title != null) {
            this.title = title;
        }
        if (artist != null) {
            this.artist = artist;
        }
        if (album != null) {
            this.album = album;
        }
        if (genre != null) {
            this.genre = genre;
        }
        if (description != null) {
            this.description = description;
        }
    }

    /**
     * 재생 시간을 분:초 형식으로 반환
     */
    public String getFormattedDuration() {
        if (durationMs == null) {
            return "0:00";
        }
        int seconds = durationMs / 1000;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }
}