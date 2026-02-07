-- ================================================
-- MUTI Initial Survey Data
-- Version: 2.0
-- Description: Insert initial MUTI survey questions
-- ================================================

-- Survey 생성
INSERT INTO surveys (id, title, description, active, created_at, updated_at) VALUES
(1, 'MUTI 음악 성향 테스트', '당신의 음악 취향을 16가지 타입으로 분석합니다. 총 8개의 질문에 답해주세요.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- E_I 축 질문 (Energetic vs Introspective)
-- ================================================

-- Q1: E_I 축 - 음악 템포 선호도
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(1, 1, '평소 즐겨 듣는 음악의 템포는?', 'E_I', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(1, '빠르고 역동적인 음악 (댄스, EDM, 업템포 록)', 'E', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '느리고 차분한 음악 (발라드, 앰비언트, 재즈)', 'I', 5, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Q2: E_I 축 - 운동/활동 시 음악
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(2, 1, '운동이나 활동할 때 선호하는 음악은?', 'E_I', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(2, '에너지 넘치는 신나는 음악', 'E', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '집중을 돕는 잔잔한 배경음악', 'I', 4, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- S_F 축 질문 (Sensory vs Feeling)
-- ================================================

-- Q3: S_F 축 - 음악 감상 포인트
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(3, 1, '음악을 들을 때 가장 중요하게 생각하는 요소는?', 'S_F', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(3, '강렬한 비트와 리듬감 (EDM, 힙합, 펑크)', 'S', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '서정적인 멜로디와 감동적인 가사 (발라드, 포크)', 'F', 5, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Q4: S_F 축 - 음악 선택 기준
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(4, 1, '새로운 음악을 고를 때 더 끌리는 것은?', 'S_F', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(4, '몸이 절로 움직이게 만드는 그루브', 'S', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '마음을 울리는 감성적인 분위기', 'F', 4, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- A_D 축 질문 (Analog vs Digital)
-- ================================================

-- Q5: A_D 축 - 악기 선호도
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(5, 1, '더 매력적으로 느껴지는 사운드는?', 'A_D', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(5, '어쿠스틱 악기의 생생한 연주 (기타, 피아노, 드럼)', 'A', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '전자 악기의 독특한 신스 사운드 (신시사이저, 일렉트로닉)', 'D', 5, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Q6: A_D 축 - 음악 감상 방식
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(6, 1, '라이브 공연과 스튜디오 음원 중 선호하는 것은?', 'A_D', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(6, '라이브 공연의 날것 그대로의 에너지', 'A', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '스튜디오 음원의 완벽하게 다듬어진 사운드', 'D', 4, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- P_U 축 질문 (Popular vs Underground)
-- ================================================

-- Q7: P_U 축 - 음악 발견 방식
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(7, 1, '주로 어떤 방식으로 음악을 발견하나요?', 'P_U', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(7, '음악 차트나 인기곡 플레이리스트', 'P', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, '숨겨진 인디 아티스트나 언더그라운드 씬', 'U', 5, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Q8: P_U 축 - 음악 취향
INSERT INTO questions (id, survey_id, content, axis, order_index, created_at, updated_at) VALUES
(8, 1, '더 선호하는 음악 스타일은?', 'P_U', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO question_options (question_id, content, direction, score, order_index, created_at, updated_at) VALUES
(8, '많은 사람들이 공감할 수 있는 대중적인 음악', 'P', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '독특하고 실험적인 비주류 음악', 'U', 4, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);