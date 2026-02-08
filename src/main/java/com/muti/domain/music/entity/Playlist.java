package com.muti.domain.music.entity;

import com.muti.domain.auth.entity.User;
import com.muti.domain.survey.enums.MutiType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 플레이리스트 엔티티
 */
@Entity
@Table(name = "playlists", indexes = {
        @Index(name = "idx_playlist_user", columnList = "user_id"),
        @Index(name = "idx_playlist_muti_type", columnList = "muti_type")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true; // 공개 여부

    @Enumerated(EnumType.STRING)
    @Column(name = "muti_type", length = 4)
    private MutiType mutiType; // MUTI 타입별 플레이리스트 (null이면 일반 플레이리스트)

    @Column(name = "image_url", length = 500)
    private String imageUrl; // 플레이리스트 커버 이미지

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistMusic> playlistMusics = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Playlist(String name, String description, User user,
                    Boolean isPublic, MutiType mutiType, String imageUrl) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.isPublic = (isPublic != null) ? isPublic : true;
        this.mutiType = mutiType;
        this.imageUrl = imageUrl;
    }

    /**
     * 플레이리스트 정보 수정
     */
    public void update(String name, String description, Boolean isPublic) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (isPublic != null) {
            this.isPublic = isPublic;
        }
    }

    /**
     * 작성자 확인
     */
    public boolean isOwner(Long userId) {
        return this.user.getId().equals(userId);
    }

    /**
     * 음악 추가
     */
    public void addMusic(Music music, Integer orderIndex) {
        PlaylistMusic playlistMusic = PlaylistMusic.builder()
                .playlist(this)
                .music(music)
                .orderIndex(orderIndex)
                .build();
        this.playlistMusics.add(playlistMusic);
    }

    /**
     * 플레이리스트의 음악 개수
     */
    public int getMusicCount() {
        return this.playlistMusics.size();
    }
}