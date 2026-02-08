-- =====================================================
-- V6: 초기 게시판 데이터 삽입
-- Phase 2 Day 14-18: 자유게시판 + 16개 MUTI 타입 게시판 생성
-- =====================================================

-- 자유게시판
INSERT INTO boards (name, description, board_type, muti_type)
VALUES ('자유게시판', '자유롭게 이야기를 나눠보세요!', 'FREE', NULL);

-- MUTI 타입별 게시판 (16개)
-- E-S 조합
INSERT INTO boards (name, description, board_type, muti_type)
VALUES ('ESAP 게시판', '감성적이고 잔잔한 어쿠스틱 대중 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ESAP'),
       ('ESAU 게시판', '감성적이고 잔잔한 어쿠스틱 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ESAU'),
       ('ESDP 게시판', '감성적이고 잔잔한 디지털 대중 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ESDP'),
       ('ESDU 게시판', '감성적이고 잔잔한 디지털 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ESDU');

-- E-F 조합
INSERT INTO boards (name, description, board_type, muti_type)
VALUES ('EFAP 게시판', '감성적이고 빠른 어쿠스틱 대중 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'EFAP'),
       ('EFAU 게시판', '감성적이고 빠른 어쿠스틱 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'EFAU'),
       ('EFDP 게시판', '감성적이고 빠른 디지털 대중 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'EFDP'),
       ('EFDU 게시판', '감성적이고 빠른 디지털 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'EFDU');

-- I-S 조합
INSERT INTO boards (name, description, board_type, muti_type)
VALUES ('ISAP 게시판', '연주 중심의 잔잔한 어쿠스틱 대중 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ISAP'),
       ('ISAU 게시판', '연주 중심의 잔잔한 어쿠스틱 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ISAU'),
       ('ISDP 게시판', '연주 중심의 잔잔한 디지털 대중 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ISDP'),
       ('ISDU 게시판', '연주 중심의 잔잔한 디지털 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'ISDU');

-- I-F 조합
INSERT INTO boards (name, description, board_type, muti_type)
VALUES ('IFAP 게시판', '연주 중심의 빠른 어쿠스틱 대중 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'IFAP'),
       ('IFAU 게시판', '연주 중심의 빠른 어쿠스틱 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'IFAU'),
       ('IFDP 게시판', '연주 중심의 빠른 디지털 대중 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'IFDP'),
       ('IFDU 게시판', '연주 중심의 빠른 디지털 실험 음악을 좋아하는 분들의 공간', 'MUTI_TYPE', 'IFDU');