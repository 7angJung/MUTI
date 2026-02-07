# Supabase 데이터베이스 설정 가이드

## 📋 개요

MUTI 애플리케이션을 Supabase PostgreSQL 데이터베이스에 연결하는 방법을 설명합니다.

## 🔐 보안 주의사항

⚠️ **중요**: 데이터베이스 비밀번호와 연결 정보를 절대 GitHub에 커밋하지 마세요!

- `.env` 파일은 `.gitignore`에 추가되어 있습니다
- `.env.example` 파일만 커밋되며, 실제 비밀번호는 포함되지 않습니다

## 🚀 설정 단계

### 1. 환경 변수 파일 생성

`.env.example` 파일을 복사하여 `.env` 파일을 생성합니다:

```bash
cp .env.example .env
```

### 2. Supabase 비밀번호 입력

`.env` 파일을 열어 실제 Supabase 비밀번호를 입력합니다:

```env
# MUTI Application Environment Variables

# Database Configuration (Supabase PostgreSQL)
DB_URL=jdbc:postgresql://db.qlnuleskbxqhpyadsoeo.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=여기에_실제_비밀번호_입력

# Application Profile
SPRING_PROFILES_ACTIVE=prod
```

### 3. 데이터베이스 연결 테스트

Supabase 연결이 정상적으로 작동하는지 확인합니다:

```bash
./gradlew test --tests SupabaseDatabaseConnectionTest
```

**예상 출력:**
```
✅ Database Connection Successful!
   - Database: PostgreSQL
   - Version: 15.x.x
   - URL: jdbc:postgresql://db.qlnuleskbxqhpyadsoeo.supabase.co:5432/postgres
   - Driver: PostgreSQL JDBC Driver

✅ Query Execution Successful!

✅ Existing Tables in Supabase:
   - No tables found (Fresh database - Flyway will create them)

✅ Flyway Schema History Table: NOT FOUND
```

### 4. Flyway 마이그레이션 실행

애플리케이션을 prod 프로필로 실행하여 Flyway가 자동으로 스키마를 생성하도록 합니다:

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

**Flyway가 자동으로 수행하는 작업:**
- ✅ V1__init_schema.sql: 테이블 생성 (surveys, questions, question_options, survey_results, survey_responses)
- ✅ V2__insert_initial_survey.sql: 초기 데이터 삽입 (1개 설문, 8개 질문, 16개 선택지)

### 5. 마이그레이션 확인

마이그레이션이 성공적으로 완료되었는지 다시 테스트를 실행합니다:

```bash
./gradlew test --tests SupabaseDatabaseConnectionTest
```

이번에는 다음과 같은 출력이 표시되어야 합니다:

```
✅ Existing Tables in Supabase:
   - flyway_schema_history
   - question_options
   - questions
   - survey_responses
   - survey_results
   - surveys

✅ Flyway Schema History Table: EXISTS
   - V1: init schema (2026-02-07 xx:xx:xx) - ✅
   - V2: insert initial survey (2026-02-07 xx:xx:xx) - ✅
```

## 🔍 Supabase 대시보드에서 확인

### 1. Supabase 프로젝트 접속
https://supabase.com/dashboard

### 2. Table Editor로 이동
- 좌측 메뉴에서 "Table Editor" 클릭
- 생성된 테이블 확인:
  - `surveys` (1개 레코드)
  - `questions` (8개 레코드)
  - `question_options` (16개 레코드)
  - `survey_results` (응답 제출 후)
  - `survey_responses` (응답 제출 후)

### 3. SQL Editor로 데이터 확인

```sql
-- 설문 확인
SELECT * FROM surveys;

-- 질문 확인 (축별 그룹화)
SELECT axis, COUNT(*) as count
FROM questions
GROUP BY axis;

-- 선택지 확인
SELECT q.content as question, qo.content as option, qo.direction, qo.score
FROM questions q
JOIN question_options qo ON q.id = qo.question_id
ORDER BY q.order_index, qo.order_index;
```

## 📊 데이터베이스 스키마

### 주요 테이블

| 테이블명 | 설명 | 레코드 수 (초기) |
|---------|------|-----------------|
| surveys | 설문 정보 | 1 |
| questions | 질문 (E_I, S_F, A_D, P_U 축) | 8 |
| question_options | 질문 선택지 | 16 |
| survey_results | 설문 응답 결과 (MUTI 타입) | 0 (응답 후 추가) |
| survey_responses | 개별 질문 응답 | 0 (응답 후 추가) |

### ERD (간략)

```
surveys (1)
  ↓ 1:N
questions (8)
  ↓ 1:N
question_options (16)

survey_results
  ↓ 1:N
survey_responses → questions
survey_responses → question_options
```

## 🌐 API 테스트

### 1. 애플리케이션 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### 2. Health Check

```bash
curl http://localhost:8080/api/v1/surveys/ping
```

**예상 응답:**
```json
{
  "success": true,
  "data": "pong",
  "message": "MUTI API is running successfully"
}
```

### 3. 설문 목록 조회

```bash
curl http://localhost:8080/api/v1/surveys
```

### 4. 설문 상세 조회

```bash
curl http://localhost:8080/api/v1/surveys/1
```

### 5. 설문 응답 제출

```bash
curl -X POST http://localhost:8080/api/v1/surveys/1/submit \
  -H "Content-Type: application/json" \
  -d '{
    "surveyId": 1,
    "answers": [
      {"questionId": 1, "optionId": 1},
      {"questionId": 2, "optionId": 3},
      {"questionId": 3, "optionId": 5},
      {"questionId": 4, "optionId": 7},
      {"questionId": 5, "optionId": 9},
      {"questionId": 6, "optionId": 11},
      {"questionId": 7, "optionId": 13},
      {"questionId": 8, "optionId": 15}
    ],
    "sessionId": "test-session-001"
  }'
```

**예상 응답:**
```json
{
  "success": true,
  "data": {
    "mutiType": "ESAP",
    "mutiTypeName": "에너지 넘치는 스토리텔러",
    "axisScores": {
      "E_I": 4,
      "S_F": 4,
      "A_D": 4,
      "P_U": 4
    }
  },
  "message": "MUTI 타입이 성공적으로 산출되었습니다."
}
```

## 🔧 트러블슈팅

### 문제 1: 연결 시간 초과 (Connection Timeout)

**원인**: 네트워크 또는 방화벽 문제

**해결**:
1. Supabase 프로젝트가 일시 중지되지 않았는지 확인
2. 인터넷 연결 확인
3. 방화벽에서 5432 포트 허용 확인

### 문제 2: 인증 실패 (Authentication Failed)

**원인**: 잘못된 비밀번호

**해결**:
1. `.env` 파일의 `DB_PASSWORD` 확인
2. Supabase 대시보드에서 비밀번호 재설정
3. 비밀번호에 특수문자가 있다면 URL 인코딩 필요

### 문제 3: Flyway 마이그레이션 실패

**원인**: 기존 테이블 존재 또는 권한 문제

**해결**:
1. Supabase SQL Editor에서 기존 테이블 삭제:
```sql
DROP TABLE IF EXISTS survey_responses CASCADE;
DROP TABLE IF EXISTS survey_results CASCADE;
DROP TABLE IF EXISTS question_options CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS surveys CASCADE;
DROP TABLE IF EXISTS flyway_schema_history CASCADE;
```

2. 애플리케이션 재시작

### 문제 4: H2 Database와 충돌

**원인**: 로컬 프로필로 실행 중

**해결**:
- prod 프로필로 실행: `--spring.profiles.active=prod`
- 또는 환경 변수 설정: `export SPRING_PROFILES_ACTIVE=prod`

## 📚 참고 자료

- [Supabase 공식 문서](https://supabase.com/docs)
- [Spring Boot Flyway 가이드](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
- [PostgreSQL JDBC 드라이버](https://jdbc.postgresql.org/documentation/)

## ✅ 체크리스트

설정 완료 확인:

- [ ] `.env` 파일 생성 및 비밀번호 입력
- [ ] `.env` 파일이 `.gitignore`에 포함되어 커밋되지 않음 확인
- [ ] `SupabaseDatabaseConnectionTest` 모든 테스트 통과
- [ ] Flyway 마이그레이션 성공 (V1, V2)
- [ ] Supabase 대시보드에서 테이블 확인
- [ ] API 엔드포인트 정상 작동 확인
- [ ] 설문 응답 제출 및 MUTI 타입 산출 확인

---

**다음 단계**: 프론트엔드 연동 또는 배포 준비