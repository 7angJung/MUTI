package com.muti.domain.music.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 플레이리스트-음악 중간 테이블
 * (다대다 관계를 위한 중간 엔티티)
 */
@Entity
@Table(name = "playlist_musics",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_playlist_music",
                columnNames = {"playlist_id", "music_id"}
        ),
        indexes = {
                @Index(name = "idx_playlist_music_playlist", columnList = "playlist_id, order_index"),
                @Index(name = "idx_playlist_music_music", columnList = "music_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaylistMusic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_music_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex; // 플레이리스트 내 순서 (0부터 시작)

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public PlaylistMusic(Playlist playlist, Music music, Integer orderIndex) {
        this.playlist = playlist;
        this.music = music;
        this.orderIndex = (orderIndex != null) ? orderIndex : 0;
    }

    /**
     * 순서 변경
     */
    public void updateOrder(Integer newOrderIndex) {
        this.orderIndex = newOrderIndex;
    }
}