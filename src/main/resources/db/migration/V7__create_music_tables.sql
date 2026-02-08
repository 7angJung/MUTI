-- ============================================================================
-- V7: Music 도메인 테이블 생성
-- ============================================================================

-- musics 테이블 (음악 정보)
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

-- musics 테이블 인덱스
CREATE INDEX idx_music_artist ON musics(artist);
CREATE INDEX idx_music_genre ON musics(genre);
CREATE INDEX idx_music_spotify ON musics(spotify_id);

-- musics 테이블 코멘트
COMMENT ON TABLE musics IS '음악 정보';
COMMENT ON COLUMN musics.music_id IS '음악 ID (PK)';
COMMENT ON COLUMN musics.title IS '음악 제목';
COMMENT ON COLUMN musics.artist IS '아티스트';
COMMENT ON COLUMN musics.album IS '앨범명';
COMMENT ON COLUMN musics.genre IS '장르';
COMMENT ON COLUMN musics.spotify_id IS 'Spotify 음악 ID';
COMMENT ON COLUMN musics.youtube_id IS 'YouTube 영상 ID';
COMMENT ON COLUMN musics.duration_ms IS '재생 시간 (밀리초)';
COMMENT ON COLUMN musics.release_date IS '발매일';
COMMENT ON COLUMN musics.image_url IS '앨범 커버 이미지 URL';
COMMENT ON COLUMN musics.preview_url IS '미리듣기 URL';
COMMENT ON COLUMN musics.description IS '음악 설명';


-- playlists 테이블 (플레이리스트)
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

-- playlists 테이블 인덱스
CREATE INDEX idx_playlist_user ON playlists(user_id);
CREATE INDEX idx_playlist_muti_type ON playlists(muti_type);

-- playlists 테이블 코멘트
COMMENT ON TABLE playlists IS '플레이리스트';
COMMENT ON COLUMN playlists.playlist_id IS '플레이리스트 ID (PK)';
COMMENT ON COLUMN playlists.name IS '플레이리스트 이름';
COMMENT ON COLUMN playlists.description IS '플레이리스트 설명';
COMMENT ON COLUMN playlists.user_id IS '작성자 ID (FK)';
COMMENT ON COLUMN playlists.is_public IS '공개 여부';
COMMENT ON COLUMN playlists.muti_type IS 'MUTI 타입 (null이면 일반 플레이리스트)';
COMMENT ON COLUMN playlists.image_url IS '플레이리스트 커버 이미지 URL';


-- playlist_musics 테이블 (플레이리스트-음악 중간 테이블)
CREATE TABLE playlist_musics (
    playlist_music_id BIGSERIAL PRIMARY KEY,
    playlist_id BIGINT NOT NULL REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    music_id BIGINT NOT NULL REFERENCES musics(music_id) ON DELETE CASCADE,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_playlist_music UNIQUE (playlist_id, music_id)
);

-- playlist_musics 테이블 인덱스
CREATE INDEX idx_playlist_music_playlist ON playlist_musics(playlist_id, order_index);
CREATE INDEX idx_playlist_music_music ON playlist_musics(music_id);

-- playlist_musics 테이블 코멘트
COMMENT ON TABLE playlist_musics IS '플레이리스트-음악 중간 테이블';
COMMENT ON COLUMN playlist_musics.playlist_music_id IS '플레이리스트 음악 ID (PK)';
COMMENT ON COLUMN playlist_musics.playlist_id IS '플레이리스트 ID (FK)';
COMMENT ON COLUMN playlist_musics.music_id IS '음악 ID (FK)';
COMMENT ON COLUMN playlist_musics.order_index IS '플레이리스트 내 순서 (0부터 시작)';