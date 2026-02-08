-- =====================================================
-- V5: 게시판 도메인 테이블 생성
-- Phase 2 Day 14-18: Board, Post, Comment, PostLike 테이블 추가
-- =====================================================

-- 1. boards 테이블 생성
CREATE TABLE boards
(
    board_id    BIGSERIAL PRIMARY KEY,                           -- 게시판 ID (자동 증가)
    name        VARCHAR(100) NOT NULL UNIQUE,                    -- 게시판 이름 (중복 불가)
    description VARCHAR(500),                                    -- 게시판 설명
    board_type  VARCHAR(20)  NOT NULL,                           -- 게시판 타입 (FREE, MUTI_TYPE)
    muti_type   VARCHAR(4),                                      -- MUTI 타입 (ESAP, IFDU, etc.)
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성 시간

    -- 제약 조건
    CONSTRAINT chk_board_type CHECK (board_type IN ('FREE', 'MUTI_TYPE')),
    CONSTRAINT chk_board_muti_type CHECK (
        muti_type IS NULL OR
                muti_type IN ('ESAP', 'ESAU', 'ESDP', 'ESDU', 'EFAP', 'EFAU', 'EFDP', 'EFDU',
                              'ISAP', 'ISAU', 'ISDP', 'ISDU', 'IFAP', 'IFAU', 'IFDP', 'IFDU')
        ),
    -- MUTI_TYPE 게시판은 반드시 muti_type이 있어야 함
    CONSTRAINT chk_board_muti_type_required CHECK (
        (board_type = 'MUTI_TYPE' AND muti_type IS NOT NULL) OR
        (board_type = 'FREE' AND muti_type IS NULL)
        )
);

-- 2. posts 테이블 생성
CREATE TABLE posts
(
    post_id       BIGSERIAL PRIMARY KEY,                           -- 게시글 ID (자동 증가)
    board_id      BIGINT       NOT NULL,                           -- 게시판 ID (외래 키)
    user_id       BIGINT       NOT NULL,                           -- 작성자 ID (외래 키)
    title         VARCHAR(200) NOT NULL,                           -- 제목
    content       TEXT         NOT NULL,                           -- 내용
    view_count    INTEGER      NOT NULL DEFAULT 0,                 -- 조회수
    like_count    INTEGER      NOT NULL DEFAULT 0,                 -- 좋아요 수
    comment_count INTEGER      NOT NULL DEFAULT 0,                 -- 댓글 수
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성 시간
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 수정 시간

    -- 외래 키 제약 조건
    CONSTRAINT fk_posts_board FOREIGN KEY (board_id)
        REFERENCES boards (board_id) ON DELETE CASCADE,
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE CASCADE
);

-- 3. comments 테이블 생성
CREATE TABLE comments
(
    comment_id        BIGSERIAL PRIMARY KEY,                           -- 댓글 ID (자동 증가)
    post_id           BIGINT    NOT NULL,                           -- 게시글 ID (외래 키)
    user_id           BIGINT    NOT NULL,                           -- 작성자 ID (외래 키)
    parent_comment_id BIGINT,                                       -- 부모 댓글 ID (대댓글용, nullable)
    content           TEXT      NOT NULL,                           -- 내용
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성 시간

    -- 외래 키 제약 조건
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id)
        REFERENCES posts (post_id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_comment_id)
        REFERENCES comments (comment_id) ON DELETE CASCADE
);

-- 4. post_likes 테이블 생성
CREATE TABLE post_likes
(
    post_like_id BIGSERIAL PRIMARY KEY,                           -- 좋아요 ID (자동 증가)
    post_id      BIGINT    NOT NULL,                           -- 게시글 ID (외래 키)
    user_id      BIGINT    NOT NULL,                           -- 사용자 ID (외래 키)
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성 시간

    -- 외래 키 제약 조건
    CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id)
        REFERENCES posts (post_id) ON DELETE CASCADE,
    CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE CASCADE,

    -- 중복 좋아요 방지
    CONSTRAINT uk_post_likes_post_user UNIQUE (post_id, user_id)
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX idx_boards_board_type ON boards (board_type);
CREATE INDEX idx_boards_muti_type ON boards (muti_type);

CREATE INDEX idx_posts_board_id ON posts (board_id);
CREATE INDEX idx_posts_user_id ON posts (user_id);
CREATE INDEX idx_posts_created_at ON posts (created_at DESC); -- 최신순 정렬용

CREATE INDEX idx_comments_post_id ON comments (post_id);
CREATE INDEX idx_comments_user_id ON comments (user_id);
CREATE INDEX idx_comments_parent_comment_id ON comments (parent_comment_id);

CREATE INDEX idx_post_likes_post_id ON post_likes (post_id);
CREATE INDEX idx_post_likes_user_id ON post_likes (user_id);

-- 코멘트 추가 (문서화)
COMMENT ON TABLE boards IS '게시판 테이블';
COMMENT ON COLUMN boards.board_id IS '게시판 ID (PK)';
COMMENT ON COLUMN boards.name IS '게시판 이름 (UNIQUE)';
COMMENT ON COLUMN boards.description IS '게시판 설명';
COMMENT ON COLUMN boards.board_type IS '게시판 타입 (FREE: 자유게시판, MUTI_TYPE: MUTI 타입별 게시판)';
COMMENT ON COLUMN boards.muti_type IS 'MUTI 타입 (MUTI_TYPE 게시판인 경우에만 값 존재)';
COMMENT ON COLUMN boards.created_at IS '생성 시간';

COMMENT ON TABLE posts IS '게시글 테이블';
COMMENT ON COLUMN posts.post_id IS '게시글 ID (PK)';
COMMENT ON COLUMN posts.board_id IS '게시판 ID (FK)';
COMMENT ON COLUMN posts.user_id IS '작성자 ID (FK)';
COMMENT ON COLUMN posts.title IS '제목';
COMMENT ON COLUMN posts.content IS '내용';
COMMENT ON COLUMN posts.view_count IS '조회수';
COMMENT ON COLUMN posts.like_count IS '좋아요 수';
COMMENT ON COLUMN posts.comment_count IS '댓글 수';
COMMENT ON COLUMN posts.created_at IS '생성 시간';
COMMENT ON COLUMN posts.updated_at IS '수정 시간';

COMMENT ON TABLE comments IS '댓글 테이블';
COMMENT ON COLUMN comments.comment_id IS '댓글 ID (PK)';
COMMENT ON COLUMN comments.post_id IS '게시글 ID (FK)';
COMMENT ON COLUMN comments.user_id IS '작성자 ID (FK)';
COMMENT ON COLUMN comments.parent_comment_id IS '부모 댓글 ID (대댓글용, nullable)';
COMMENT ON COLUMN comments.content IS '내용';
COMMENT ON COLUMN comments.created_at IS '생성 시간';

COMMENT ON TABLE post_likes IS '게시글 좋아요 테이블';
COMMENT ON COLUMN post_likes.post_like_id IS '좋아요 ID (PK)';
COMMENT ON COLUMN post_likes.post_id IS '게시글 ID (FK)';
COMMENT ON COLUMN post_likes.user_id IS '사용자 ID (FK)';
COMMENT ON COLUMN post_likes.created_at IS '생성 시간';