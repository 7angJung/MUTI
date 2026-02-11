-- ================================================
-- MUTI Survey Conversion: Binary → 5-Point Likert
-- Version: 8.0
-- Description: Convert survey from binary choice to 5-point Likert scale
-- Scoring: [+5, +3, +1, -3, -5] (symmetric with weak positive neutral)
-- ================================================

-- ================================================
-- 1. Clean existing data
-- ================================================
DELETE FROM survey_responses;
DELETE FROM survey_results;
DELETE FROM question_options;
DELETE FROM questions;
DELETE FROM surveys;

-- ================================================
-- 2. Create survey
-- ================================================
INSERT INTO surveys (id, title, description, active, created_at, updated_at) VALUES
(1, 'MUTI 음악 성향 테스트', '당신의 음악 취향을 16가지 타입으로 분석합니다. 총 8개의 질문에 답해주세요.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- E_I 축 질문 (Emotion vs Instrument - 감정선 vs 연주·프로덕션)
-- ================================================

-- Q1: E_I 축 - 음악 템포 선호도
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(1, 1, '나는 빠르고 역동적인 템포의 음악을 선호한다', 'E_I', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(1, '매우 그렇다', 'E', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '그렇다', 'E', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '보통이다', 'E', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '그렇지 않다', 'I', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '매우 그렇지 않다', 'I', 5, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Q2: E_I 축 - 운동/활동 시 음악
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(2, 1, '나는 운동할 때 에너지 넘치는 신나는 음악을 듣는다', 'E_I', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(2, '매우 그렇다', 'E', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '그렇다', 'E', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '보통이다', 'E', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '그렇지 않다', 'I', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '매우 그렇지 않다', 'I', 5, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- S_F 축 질문 (Slow vs Fast - 잔잔한 음악 vs 빠른 음악)
-- ================================================

-- Q3: S_F 축 - 음악 감상 포인트
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(3, 1, '나는 음악에서 강렬한 비트와 리듬감을 중요하게 생각한다', 'S_F', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(3, '매우 그렇다', 'S', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '그렇다', 'S', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '보통이다', 'S', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '그렇지 않다', 'F', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '매우 그렇지 않다', 'F', 5, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Q4: S_F 축 - 음악 선택 기준
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(4, 1, '나는 몸이 절로 움직이게 만드는 그루브감 있는 음악을 선호한다', 'S_F', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(4, '매우 그렇다', 'S', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '그렇다', 'S', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '보통이다', 'S', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '그렇지 않다', 'F', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '매우 그렇지 않다', 'F', 5, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- A_D 축 질문 (Acoustic vs Digital - 어쿠스틱 vs 전자음)
-- ================================================

-- Q5: A_D 축 - 악기 선호도
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(5, 1, '나는 어쿠스틱 악기의 생생한 연주 사운드를 선호한다', 'A_D', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(5, '매우 그렇다', 'A', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '그렇다', 'A', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '보통이다', 'A', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '그렇지 않다', 'D', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '매우 그렇지 않다', 'D', 5, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Q6: A_D 축 - 음악 감상 방식
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(6, 1, '나는 라이브 공연의 날것 그대로의 에너지를 더 좋아한다', 'A_D', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(6, '매우 그렇다', 'A', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '그렇다', 'A', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '보통이다', 'A', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '그렇지 않다', 'D', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '매우 그렇지 않다', 'D', 5, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- P_U 축 질문 (Popular vs Underground - 대중적 vs 언더그라운드)
-- ================================================

-- Q7: P_U 축 - 음악 발견 방식
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(7, 1, '나는 주로 음악 차트나 인기곡 플레이리스트에서 음악을 발견한다', 'P_U', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(7, '매우 그렇다', 'P', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, '그렇다', 'P', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, '보통이다', 'P', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, '그렇지 않다', 'U', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, '매우 그렇지 않다', 'U', 5, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Q8: P_U 축 - 음악 취향
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(8, 1, '나는 많은 사람들이 공감할 수 있는 대중적인 음악을 선호한다', 'P_U', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(8, '매우 그렇다', 'P', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '그렇다', 'P', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '보통이다', 'P', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '그렇지 않다', 'U', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '매우 그렇지 않다', 'U', 5, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- Scoring Summary
-- ================================================
-- Each option's calculated score:
--   매우 그렇다 (score 5, first direction)  → +5 points
--   그렇다 (score 3, first direction)       → +3 points
--   보통이다 (score 1, first direction)     → +1 point (weak positive)
--   그렇지 않다 (score 3, second direction) → -3 points
--   매우 그렇지 않다 (score 5, second direction) → -5 points
--
-- Symmetric scale: [-5, -3, +1, +3, +5]
-- Per axis range (2 questions): -10 to +10 points
-- ================================================