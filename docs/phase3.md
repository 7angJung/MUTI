## Phase 3: Music/Playlist 도메인

### 3.1 Phase 3 개요

#### 🎯 목표

**"사용자들이 음악을 탐색하고, 플레이리스트를 만들고, MUTI 타입별 추천 음악을 받을 수 있게 만들기"**

#### 📦 만들 것들

| 항목 | 설명 | 파일 |
|------|------|------|
| **Genre Enum** | 20개 음악 장르 정의 | `Genre.java` |
| **Music Entity** | 음악 메타데이터 (Spotify/YouTube ID 포함) | `Music.java` |
| **Playlist Entity** | 사용자 플레이리스트 (공개/비공개, MUTI 타입) | `Playlist.java` |
| **PlaylistMusic Entity** | 플레이리스트-음악 중간 테이블 (순서 관리) | `PlaylistMusic.java` |
| **Repository** | 데이터베이스 접근 (3개) | `*Repository.java` |
| **Service** | 비즈니스 로직 (2개) | `MusicService.java`, `PlaylistService.java` |
| **Controller** | API 엔드포인트 (2개) | `MusicController.java`, `PlaylistController.java` |
| **DTO** | 요청/응답 객체 (10개) | `dto/*` |

#### ⏱️ 예상 소요 시간

```
Week 6 (Day 36-42): 총 7일
├─ Day 36-37: Entity & Repository (2일)
├─ Day 38-40: Service & 비즈니스 로직 (3일)
└─ Day 41-42: Controller & API 테스트 (2일)
```

**Phase 3 완료일**: 2026년 2월 9일 ✅

---

### 3.2 Music/Playlist 아키텍처 이해

#### 📊 데이터베이스 설계

**비유: 음악 스트리밍 서비스**

```
Music (음악) = 곡 라이브러리
├─ 기본 정보: 제목, 아티스트, 앨범
├─ 메타데이터: 장르, 재생시간, 발매일
└─ 외부 ID: Spotify ID, YouTube ID (향후 API 통합용)

Playlist (플레이리스트) = 사용자가 만든 앨범
├─ 공개 플레이리스트 (모두가 볼 수 있음)
├─ 비공개 플레이리스트 (본인만 볼 수 있음)
└─ MUTI 타입 플레이리스트 (ESAP, IFDU 등 타입별 추천)

PlaylistMusic (중간 테이블) = 플레이리스트 안의 곡 순서
├─ 플레이리스트 ID
├─ 음악 ID
└─ 순서 (orderIndex: 0, 1, 2, ...)
```

#### 🗄️ ERD (Entity Relationship Diagram)

```
┌──────────┐         ┌──────────────┐         ┌──────────┐
│  users   │         │   playlists  │         │  musics  │
│          │         │              │         │          │
│  id (PK) │         │  id (PK)     │         │  id (PK) │
│  email   │         │  name        │         │  title   │
│  username│         │  is_public   │         │  artist  │
└────┬─────┘         │  muti_type   │         │  album   │
     │               └────┬─────────┘         │  genre   │
     │ 1                  │                   │ spotify_id│
     │                    │ 1                 └────┬─────┘
     │ N                  │                        │
┌────┴──────────┐         │ N                      │ 1
│   playlists   │         │                        │
└───────────────┘         │                        │ N
                          │                   ┌────┴────────┐
                          │                   │playlist_    │
                          │                   │musics       │
                          │ 1                 │             │
                          │                   │  id (PK)    │
                          └───────────────────│playlist_id  │
                                      N       │  music_id   │
                                              │order_index  │
                                              └─────────────┘

관계:
- User 1 : N Playlist (한 사용자가 여러 플레이리스트 소유)
- Playlist N : M Music (다대다, PlaylistMusic 중간 테이블)
- PlaylistMusic: 플레이리스트 안의 곡 순서 관리
```

---

### 3.3 Entity 설계

#### 📝 Music Entity (음악)

**역할**: 음악의 메타데이터를 저장하는 도메인 모델

**핵심 필드:**
```java
@Entity
@Table(name = "musics")
public class Music {
    @Id
    @GeneratedValue
    private Long id;

    private String title;          // 곡 제목
    private String artist;         // 아티스트
    private String album;          // 앨범명

    @Enumerated(EnumType.STRING)
    private Genre genre;           // 장르 (POP, ROCK, ...)

    private String spotifyId;      // Spotify API 연동용
    private String youtubeId;      // YouTube API 연동용
    private Integer durationMs;    // 재생 시간 (밀리초)
    private LocalDate releaseDate; // 발매일

    private String imageUrl;       // 앨범 커버
    private String previewUrl;     // 미리듣기 URL

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**비즈니스 메서드:**
```java
// 재생 시간을 "3:45" 형식으로 변환
public String getFormattedDuration() {
    int seconds = durationMs / 1000;
    int minutes = seconds / 60;
    int remainingSeconds = seconds % 60;
    return String.format("%d:%02d", minutes, remainingSeconds);
}
```

#### 📝 Playlist Entity (플레이리스트)

**역할**: 사용자가 만든 플레이리스트 정보 저장

**핵심 필드:**
```java
@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue
    private Long id;

    private String name;           // 플레이리스트 이름
    private String description;    // 설명

    @ManyToOne
    private User user;             // 소유자

    private Boolean isPublic;      // 공개/비공개

    @Enumerated(EnumType.STRING)
    private MutiType mutiType;     // MUTI 타입별 추천용

    @OneToMany(mappedBy = "playlist")
    private List<PlaylistMusic> playlistMusics;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**비즈니스 메서드:**
```java
// 소유자 확인
public boolean isOwner(Long userId) {
    return this.user.getId().equals(userId);
}

// 음악 추가
public void addMusic(Music music, Integer orderIndex) {
    PlaylistMusic pm = PlaylistMusic.builder()
        .playlist(this)
        .music(music)
        .orderIndex(orderIndex)
        .build();
    this.playlistMusics.add(pm);
}
```

#### 📝 PlaylistMusic Entity (중간 테이블)

**역할**: 플레이리스트-음악 다대다 관계 + 순서 관리

**핵심 필드:**
```java
@Entity
@Table(name = "playlist_musics",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"playlist_id", "music_id"}
    ))
public class PlaylistMusic {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Playlist playlist;

    @ManyToOne
    private Music music;

    private Integer orderIndex;    // 플레이리스트 내 순서

    @CreatedDate
    private LocalDateTime createdAt;
}
```

**왜 중간 테이블이 필요한가?**

```
일반 다대다 관계 (X):
Playlist ------ Music
- 순서 정보 없음
- 같은 곡을 여러 번 추가 불가

중간 테이블 사용 (O):
Playlist --(1:N)-- PlaylistMusic --(N:1)-- Music
- orderIndex로 순서 관리
- 추가 정보 (추가 날짜 등) 저장 가능
```

#### 📝 Genre Enum (장르)

**역할**: 음악 장르 정의

```java
public enum Genre {
    POP("팝"),
    ROCK("록"),
    HIPHOP("힙합"),
    RNB("알앤비"),
    JAZZ("재즈"),
    CLASSICAL("클래식"),
    ELECTRONIC("일렉트로닉"),
    FOLK("포크"),
    INDIE("인디"),
    BALLAD("발라드"),
    DANCE("댄스"),
    METAL("메탈"),
    ALTERNATIVE("얼터너티브"),
    SOUL("소울"),
    COUNTRY("컨트리"),
    REGGAE("레게"),
    BLUES("블루스"),
    AMBIENT("앰비언트"),
    EXPERIMENTAL("실험음악"),
    OTHER("기타");

    private final String korean;
}
```

---

### 3.4 Repository 구현

#### 📂 MusicRepository

**역할**: 음악 데이터 접근

**주요 메서드:**
```java
public interface MusicRepository extends JpaRepository<Music, Long> {
    // Spotify ID로 음악 찾기
    Optional<Music> findBySpotifyId(String spotifyId);
    boolean existsBySpotifyId(String spotifyId);

    // 검색
    @Query("SELECT m FROM Music m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.artist) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Music> searchByTitleOrArtist(@Param("keyword") String keyword,
                                       Pageable pageable);

    // 장르별 조회
    Page<Music> findByGenre(Genre genre, Pageable pageable);

    // 최신 음악
    List<Music> findTop10ByOrderByReleaseDateDesc();
}
```

**활용 예시:**
```java
// 검색: "아이유" 검색
Page<Music> results = musicRepository
    .searchByTitleOrArtist("아이유", PageRequest.of(0, 20));

// 장르별: POP 장르 음악
Page<Music> popMusics = musicRepository
    .findByGenre(Genre.POP, PageRequest.of(0, 20));
```

#### 📂 PlaylistRepository

**역할**: 플레이리스트 데이터 접근

**주요 메서드:**
```java
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    // 사용자별 조회
    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId")
    List<Playlist> findByUserId(@Param("userId") Long userId);

    // 공개 플레이리스트 (페이징)
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true")
    Page<Playlist> findPublicPlaylists(Pageable pageable);

    // MUTI 타입별 추천 플레이리스트
    @Query("SELECT p FROM Playlist p WHERE " +
           "p.mutiType = :mutiType AND p.isPublic = true")
    List<Playlist> findPublicPlaylistsByMutiType(
        @Param("mutiType") MutiType mutiType);

    // 검색
    @Query("SELECT p FROM Playlist p WHERE " +
           "p.isPublic = true AND " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Playlist> searchByName(@Param("keyword") String keyword,
                                 Pageable pageable);
}
```

#### 📂 PlaylistMusicRepository

**역할**: 플레이리스트-음악 관계 관리

**주요 메서드:**
```java
public interface PlaylistMusicRepository
    extends JpaRepository<PlaylistMusic, Long> {

    // 플레이리스트의 음악 목록 (순서대로)
    @Query("SELECT pm FROM PlaylistMusic pm " +
           "JOIN FETCH pm.music " +
           "WHERE pm.playlist.id = :playlistId " +
           "ORDER BY pm.orderIndex")
    List<PlaylistMusic> findByPlaylistIdOrderByOrderIndex(
        @Param("playlistId") Long playlistId);

    // 중복 확인
    boolean existsByPlaylistIdAndMusicId(Long playlistId, Long musicId);

    // 최대 orderIndex 찾기 (다음 순서 계산용)
    @Query("SELECT COALESCE(MAX(pm.orderIndex), -1) " +
           "FROM PlaylistMusic pm " +
           "WHERE pm.playlist.id = :playlistId")
    Integer findMaxOrderIndexByPlaylistId(@Param("playlistId") Long playlistId);

    // 음악 삭제
    @Modifying
    void deleteByPlaylistIdAndMusicId(Long playlistId, Long musicId);
}
```

---

### 3.5 Service 비즈니스 로직

#### 🔧 MusicService

**역할**: 음악 관련 비즈니스 로직 처리

**주요 메서드:**
```java
@Service
@Transactional(readOnly = true)
public class MusicService {

    // 음악 등록 (관리자만)
    @Transactional
    public MusicDto createMusic(CreateMusicRequest request) {
        // 1. Spotify ID 중복 체크
        if (request.getSpotifyId() != null &&
            musicRepository.existsBySpotifyId(request.getSpotifyId())) {
            throw new BusinessException(ErrorCode.MUSIC_ALREADY_EXISTS);
        }

        // 2. Music 엔티티 생성 및 저장
        Music music = Music.builder()
            .title(request.getTitle())
            .artist(request.getArtist())
            .genre(request.getGenre())
            .spotifyId(request.getSpotifyId())
            .build();

        Music saved = musicRepository.save(music);
        return MusicDto.from(saved);
    }

    // 음악 검색
    public Page<MusicDto> searchMusic(String keyword, Pageable pageable) {
        return musicRepository
            .searchByTitleOrArtist(keyword, pageable)
            .map(MusicDto::from);
    }

    // 장르별 조회
    public Page<MusicDto> getMusicsByGenre(Genre genre, Pageable pageable) {
        return musicRepository
            .findByGenre(genre, pageable)
            .map(MusicDto::from);
    }
}
```

#### 🔧 PlaylistService

**역할**: 플레이리스트 관련 비즈니스 로직 처리

**주요 메서드:**
```java
@Service
@Transactional(readOnly = true)
public class PlaylistService {

    // 플레이리스트 생성
    @Transactional
    public PlaylistDto createPlaylist(CreatePlaylistRequest request,
                                       Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Playlist playlist = Playlist.builder()
            .name(request.getName())
            .description(request.getDescription())
            .user(user)
            .isPublic(request.getIsPublic())
            .build();

        Playlist saved = playlistRepository.save(playlist);
        return PlaylistDto.from(saved, userId);
    }

    // 음악 추가
    @Transactional
    public void addMusicToPlaylist(Long playlistId,
                                    AddMusicToPlaylistRequest request,
                                    Long userId) {
        // 1. 플레이리스트 조회 및 권한 확인
        Playlist playlist = getPlaylistOrThrow(playlistId);
        validateOwner(playlist, userId);

        // 2. 음악 조회
        Music music = musicService.getMusicEntity(request.getMusicId());

        // 3. 중복 체크
        if (playlistMusicRepository
                .existsByPlaylistIdAndMusicId(playlistId, music.getId())) {
            throw new BusinessException(ErrorCode.MUSIC_ALREADY_IN_PLAYLIST);
        }

        // 4. 다음 orderIndex 계산
        Integer maxOrder = playlistMusicRepository
            .findMaxOrderIndexByPlaylistId(playlistId);
        Integer nextOrder = maxOrder + 1;

        // 5. PlaylistMusic 생성
        PlaylistMusic pm = PlaylistMusic.builder()
            .playlist(playlist)
            .music(music)
            .orderIndex(nextOrder)
            .build();

        playlistMusicRepository.save(pm);
    }

    // 권한 검증
    private void validateOwner(Playlist playlist, Long userId) {
        if (!playlist.isOwner(userId)) {
            throw new BusinessException(ErrorCode.PLAYLIST_UPDATE_FORBIDDEN);
        }
    }
}
```

---

### 3.6 Controller API 설계

#### 🌐 MusicController

**역할**: 음악 관련 REST API 제공

**엔드포인트:**
```java
@RestController
@RequestMapping("/api/v1/musics")
public class MusicController {

    // 음악 등록 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<MusicDto> createMusic(
            @Valid @RequestBody CreateMusicRequest request) {
        MusicDto music = musicService.createMusic(request);
        return ApiResponse.success(music);
    }

    // 음악 검색
    @GetMapping("/search")
    public ApiResponse<Page<MusicDto>> searchMusic(
            @RequestParam String keyword,
            Pageable pageable) {
        Page<MusicDto> musics = musicService.searchMusic(keyword, pageable);
        return ApiResponse.success(musics);
    }

    // 장르별 조회
    @GetMapping("/genre/{genre}")
    public ApiResponse<Page<MusicDto>> getMusicsByGenre(
            @PathVariable Genre genre,
            Pageable pageable) {
        Page<MusicDto> musics = musicService.getMusicsByGenre(genre, pageable);
        return ApiResponse.success(musics);
    }

    // 최신 음악
    @GetMapping("/recent")
    public ApiResponse<List<MusicDto>> getRecentMusics() {
        List<MusicDto> musics = musicService.getRecentMusics();
        return ApiResponse.success(musics);
    }
}
```

#### 🌐 PlaylistController

**역할**: 플레이리스트 관련 REST API 제공

**엔드포인트:**
```java
@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistController {

    // 플레이리스트 생성
    @PostMapping
    public ApiResponse<PlaylistDto> createPlaylist(
            @Valid @RequestBody CreatePlaylistRequest request) {
        Long userId = getCurrentUserId();
        PlaylistDto playlist = playlistService.createPlaylist(request, userId);
        return ApiResponse.success(playlist);
    }

    // 음악 추가
    @PostMapping("/{id}/musics")
    public ApiResponse<Void> addMusicToPlaylist(
            @PathVariable Long id,
            @Valid @RequestBody AddMusicToPlaylistRequest request) {
        Long userId = getCurrentUserId();
        playlistService.addMusicToPlaylist(id, request, userId);
        return ApiResponse.success(null, "음악이 추가되었습니다.");
    }

    // 공개 플레이리스트 목록
    @GetMapping("/public")
    public ApiResponse<Page<PlaylistDto>> getPublicPlaylists(
            Pageable pageable) {
        Page<PlaylistDto> playlists =
            playlistService.getPublicPlaylists(pageable);
        return ApiResponse.success(playlists);
    }

    // MUTI 타입별 추천
    @GetMapping("/muti-type/{mutiType}")
    public ApiResponse<List<PlaylistDto>> getPlaylistsByMutiType(
            @PathVariable MutiType mutiType) {
        List<PlaylistDto> playlists =
            playlistService.getPlaylistsByMutiType(mutiType);
        return ApiResponse.success(playlists);
    }
}
```

---

### 3.7 Flyway 마이그레이션

#### 📄 V7__create_music_tables.sql

**역할**: Music domain 테이블 생성

```sql
-- musics 테이블
CREATE TABLE musics (
    music_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    artist VARCHAR(200) NOT NULL,
    album VARCHAR(200),
    genre VARCHAR(30),
    spotify_id VARCHAR(50) UNIQUE,
    youtube_id VARCHAR(50),
    duration_ms INTEGER,
    release_date DATE,
    image_url VARCHAR(500),
    preview_url VARCHAR(500),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- playlists 테이블
CREATE TABLE playlists (
    playlist_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    muti_type VARCHAR(4),
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- playlist_musics 중간 테이블
CREATE TABLE playlist_musics (
    playlist_music_id BIGSERIAL PRIMARY KEY,
    playlist_id BIGINT NOT NULL REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    music_id BIGINT NOT NULL REFERENCES musics(music_id) ON DELETE CASCADE,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_playlist_music UNIQUE (playlist_id, music_id)
);

-- 인덱스 생성
CREATE INDEX idx_music_artist ON musics(artist);
CREATE INDEX idx_music_genre ON musics(genre);
CREATE INDEX idx_music_spotify ON musics(spotify_id);
CREATE INDEX idx_playlist_user ON playlists(user_id);
CREATE INDEX idx_playlist_muti_type ON playlists(muti_type);
CREATE INDEX idx_playlist_music_playlist
    ON playlist_musics(playlist_id, order_index);
CREATE INDEX idx_playlist_music_music ON playlist_musics(music_id);
```

---

### 3.8 테스트 작성

#### 🧪 테스트 전략

**총 57개 테스트 작성:**
- Repository 테스트: 34개
- Service 테스트: 23개

#### 📝 MusicRepositoryTest (12 tests)

```java
@DataJpaTest
@Import(TestJpaConfig.class)
class MusicRepositoryTest {

    @Test
    @DisplayName("Spotify ID로 음악 찾기")
    void findBySpotifyId() {
        // given
        Music music = Music.builder()
            .title("Test Song")
            .artist("Test Artist")
            .spotifyId("spotify123")
            .build();
        em.persist(music);

        // when
        Optional<Music> result = musicRepository
            .findBySpotifyId("spotify123");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Song");
    }

    @Test
    @DisplayName("제목 또는 아티스트로 검색")
    void searchByTitleOrArtist() {
        // when
        Page<Music> results = musicRepository
            .searchByTitleOrArtist("test", PageRequest.of(0, 10));

        // then
        assertThat(results.getContent()).hasSize(1);
    }
}
```

#### 📝 PlaylistServiceTest (12 tests)

```java
@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Test
    @DisplayName("플레이리스트에 음악 추가 - 성공")
    void addMusicToPlaylist_Success() {
        // given
        Long playlistId = 1L;
        Long userId = 1L;

        given(playlistRepository.findById(playlistId))
            .willReturn(Optional.of(playlist));
        given(musicService.getMusicEntity(musicId))
            .willReturn(music);
        given(playlistMusicRepository
                .existsByPlaylistIdAndMusicId(playlistId, musicId))
            .willReturn(false);
        given(playlistMusicRepository
                .findMaxOrderIndexByPlaylistId(playlistId))
            .willReturn(0);

        // when
        playlistService.addMusicToPlaylist(playlistId, request, userId);

        // then
        verify(playlistMusicRepository).save(any(PlaylistMusic.class));
    }

    @Test
    @DisplayName("플레이리스트에 음악 추가 - 이미 존재함")
    void addMusicToPlaylist_AlreadyExists() {
        // given
        given(playlistMusicRepository
                .existsByPlaylistIdAndMusicId(any(), any()))
            .willReturn(true);

        // when & then
        assertThatThrownBy(() ->
            playlistService.addMusicToPlaylist(playlistId, request, userId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode",
                ErrorCode.MUSIC_ALREADY_IN_PLAYLIST);
    }
}
```

---

### 3.9 Phase 3 완료 체크리스트

#### ✅ 구현 완료 항목

**Entities (4개)**
- [x] Genre enum - 20개 장르 정의
- [x] Music - 음악 메타데이터, Spotify/YouTube ID
- [x] Playlist - 사용자 플레이리스트, 공개/비공개, MUTI 타입
- [x] PlaylistMusic - 중간 테이블, 순서 관리

**Repositories (3개)**
- [x] MusicRepository - 검색, 장르별, Spotify ID 조회
- [x] PlaylistRepository - 공개, MUTI 타입, 사용자별 조회
- [x] PlaylistMusicRepository - 순서 관리, 중복 체크

**Services (2개)**
- [x] MusicService - CRUD, 검색, 장르별 조회
- [x] PlaylistService - CRUD, 음악 추가/삭제, 권한 검증

**Controllers (2개)**
- [x] MusicController - 음악 관리 API (관리자), 검색 (공개)
- [x] PlaylistController - 플레이리스트 관리, 음악 추가/삭제

**DTOs (10개)**
- [x] CreateMusicRequest, UpdateMusicRequest
- [x] CreatePlaylistRequest, UpdatePlaylistRequest
- [x] AddMusicToPlaylistRequest
- [x] MusicDto, PlaylistDto, PlaylistDetailDto
- [x] ErrorCode 추가 (8개)

**Database**
- [x] V7 migration - musics, playlists, playlist_musics 테이블
- [x] 인덱스 - artist, genre, spotify_id, user_id, muti_type
- [x] 제약조건 - UNIQUE(playlist_id, music_id)

**Tests (57개)**
- [x] MusicRepositoryTest - 12 tests
- [x] PlaylistRepositoryTest - 11 tests
- [x] PlaylistMusicRepositoryTest - 11 tests
- [x] MusicServiceTest - 11 tests
- [x] PlaylistServiceTest - 12 tests

---

### 3.10 핵심 학습 내용

#### 💡 중간 테이블 (PlaylistMusic)의 중요성

**문제: 단순 다대다 관계**
```java
@ManyToMany
private List<Music> musics;  // X - 순서 정보 없음
```

**해결: 중간 엔티티 사용**
```java
@OneToMany(mappedBy = "playlist")
private List<PlaylistMusic> playlistMusics;  // O - 순서, 추가 정보 관리
```

**장점:**
1. **순서 관리**: orderIndex로 플레이리스트 내 곡 순서 제어
2. **추가 정보**: 추가 날짜, 추가한 사용자 등 저장 가능
3. **유연성**: 같은 곡을 여러 플레이리스트에 다른 순서로 추가

#### 💡 Enum 활용 (Genre)

**장점:**
- 타입 안정성 (오타 방지)
- IDE 자동완성
- 유효한 값만 허용

```java
public enum Genre {
    POP("팝"), ROCK("록"), JAZZ("재즈");

    @JsonValue  // JSON 응답 시 name() 대신 korean 사용
    public String getKorean() {
        return this.korean;
    }
}
```

#### 💡 BaseTimeEntity 미사용 패턴

이 프로젝트에서는 각 엔티티에 직접 타임스탬프 필드를 선언:

```java
@EntityListeners(AuditingEntityListener.class)
public class Music {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

**이유:**
- 명시적인 필드 관리
- 엔티티별 커스터마이징 가능

#### 💡 SecurityContextHolder 직접 사용

UserPrincipal 대신 SecurityContextHolder를 직접 사용:

```java
private Long getCurrentUserId() {
    Object principal = SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

    if (principal instanceof Long) {
        return (Long) principal;
    } else if (principal instanceof String) {
        return Long.parseLong((String) principal);
    }
    return null;
}
```

---

### 3.11 다음 단계

**Phase 4: 배포 준비 ✅ (완료)**
- Supabase PostgreSQL 연결
- Flyway 마이그레이션
- Docker 컨테이너화
- 프로덕션 환경 검증

**Phase 5: 프론트엔드 개발 (예정)**
- React + TypeScript 설정
- Music/Playlist UI 구현
- 음악 플레이어 구현
- Spotify API 연동

---

