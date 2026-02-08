-- =====================================================
-- V4: 인증 관련 테이블 생성
-- Phase 1 Day 13: User, RefreshToken 테이블 추가
-- =====================================================

-- Users 테이블 생성
CREATE TABLE users
(
    user_id    BIGSERIAL PRIMARY KEY,                          -- 사용자 ID (자동 증가)
    email      VARCHAR(100) NOT NULL UNIQUE,                   -- 이메일 (로그인 ID, 중복 불가)
    password   VARCHAR(255) NOT NULL,                          -- 비밀번호 (BCrypt 암호화)
    username   VARCHAR(50)  NOT NULL,                          -- 닉네임
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',           -- 권한 (USER, ADMIN)
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성 시간
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 수정 시간

    -- 제약 조건
    CONSTRAINT chk_role CHECK (role IN ('USER', 'ADMIN'))       -- role은 USER 또는 ADMIN만 가능
);

-- Refresh Tokens 테이블 생성
CREATE TABLE refresh_tokens
(
    token_id    BIGSERIAL PRIMARY KEY,                          -- 토큰 ID (자동 증가)
    token       TEXT         NOT NULL UNIQUE,                   -- Refresh Token 문자열 (중복 불가)
    user_id     BIGINT       NOT NULL,                          -- 사용자 ID (외래 키)
    expiry_date TIMESTAMP    NOT NULL,                          -- 만료 시간
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성 시간

    -- 외래 키 제약 조건
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE CASCADE            -- 사용자 삭제 시 토큰도 함께 삭제
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);  -- user_id로 조회 최적화
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens (token);      -- token으로 조회 최적화

-- 코멘트 추가 (문서화)
COMMENT ON TABLE users IS '사용자 정보 테이블';
COMMENT ON COLUMN users.user_id IS '사용자 ID (PK)';
COMMENT ON COLUMN users.email IS '이메일 (로그인 ID)';
COMMENT ON COLUMN users.password IS 'BCrypt 암호화된 비밀번호';
COMMENT ON COLUMN users.username IS '닉네임';
COMMENT ON COLUMN users.role IS '권한 (USER: 일반 사용자, ADMIN: 관리자)';
COMMENT ON COLUMN users.created_at IS '계정 생성 시간';
COMMENT ON COLUMN users.updated_at IS '계정 수정 시간';

COMMENT ON TABLE refresh_tokens IS 'Refresh Token 관리 테이블';
COMMENT ON COLUMN refresh_tokens.token_id IS '토큰 ID (PK)';
COMMENT ON COLUMN refresh_tokens.token IS 'JWT Refresh Token 문자열';
COMMENT ON COLUMN refresh_tokens.user_id IS '사용자 ID (FK)';
COMMENT ON COLUMN refresh_tokens.expiry_date IS '토큰 만료 시간';
COMMENT ON COLUMN refresh_tokens.created_at IS '토큰 생성 시간';