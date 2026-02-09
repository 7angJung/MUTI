# Phase 4: 배포 준비 진행 상황

## 📅 작업 일시
- 시작: 2026-02-09
- 현재 상태: **Step 5 시작 전**
- Step 4 완료: 2026-02-09 23:51 KST

---

## ✅ 완료된 작업

### Step 1: Supabase PostgreSQL 연결 테스트 ✅

#### 진행 내용
1. **SupabaseDatabaseConnectionTest 활성화**
   - `@Disabled` 애노테이션 제거
   - 위치: `src/test/java/com/muti/infrastructure/database/SupabaseDatabaseConnectionTest.java`

2. **Supabase 연결 설정 최적화**
   - Session Pooler → Transaction Pooler 변경
     - Port: 5432 → 6543
     - 이유: Transaction Pooler가 더 많은 동시 연결 지원
   - Connection Pool 크기 조정
     - `maximum-pool-size`: 20 → 10
     - `minimum-idle`: 10 → 2
   - `prepareThreshold=0` 파라미터 추가
     - 이유: pgBouncer는 prepared statement를 지원하지 않음

3. **테스트 결과**
   - ✅ 4개 테스트 모두 성공
   - ✅ Supabase PostgreSQL 17.6 연결 성공
   - ✅ 쿼리 실행 테스트 성공
   - ✅ 스키마 확인 성공
   - ✅ Flyway 히스토리 확인 성공

#### 트러블슈팅
1. **문제 1: MaxClientsInSessionMode 에러**
   - 원인: Session Pooler의 연결 수 제한
   - 해결: Transaction Pooler (port 6543) 사용

2. **문제 2: Prepared Statement "S_1" already exists**
   - 원인: pgBouncer가 prepared statement 미지원
   - 해결: JDBC URL에 `prepareThreshold=0` 추가

#### 변경된 파일
- `.env` (수정됨, .gitignore에 포함)
- `.env.example` (커밋됨)
- `src/main/resources/application-prod.yml` (커밋됨)
- `src/test/java/com/muti/infrastructure/database/SupabaseDatabaseConnectionTest.java` (커밋됨)

#### 커밋
- Commit: `805fa6e`
- Message: "feat: Connect to Supabase PostgreSQL and run Flyway migrations"

---

### Step 2: Flyway 마이그레이션 실행 및 검증 ✅

#### 진행 내용
1. **Flyway 마이그레이션 자동 실행**
   - Step 1에서 테스트 실행 시 자동으로 마이그레이션 적용됨
   - V1~V6: 이미 적용되어 있음
   - V7 (create music tables): **새로 적용됨**

2. **마이그레이션 검증 결과**
   ```
   ✅ V1: init schema
   ✅ V2: insert initial survey
   ✅ V3: fix direction column type
   ✅ V4: create auth tables (Phase 1)
   ✅ V5: create board tables (Phase 2)
   ✅ V6: insert initial boards
   ✅ V7: create music tables (Phase 3) ← 새로 적용!
   ```

3. **Supabase 데이터베이스 상태**
   - **총 15개 테이블 생성 완료**
     - surveys, questions, question_options, survey_responses, survey_results
     - users, refresh_tokens (Auth)
     - boards, posts, comments, post_likes (Board)
     - musics, playlists, playlist_musics (Music)
     - flyway_schema_history

#### 특이사항
- Step 1 테스트 실행 중 자동으로 완료됨
- 별도 작업 없이 검증 완료

---

### Step 3: 프로덕션 환경에서 API 테스트 ✅

#### 진행 내용
1. **애플리케이션 실행**
   - Profile: `prod`
   - 명령어: `./gradlew bootRun --args='--spring.profiles.active=prod'`
   - 시작 시간: 3.49초
   - 상태: ✅ 성공

2. **API 테스트 결과**

   **Survey API** ✅
   ```bash
   GET /api/v1/surveys
   ```
   - 설문 1개 조회 성공
   - 8개 질문, 16개 선택지 모두 정상 응답
   - Supabase DB에서 데이터 조회 확인

   **Board API** ✅
   ```bash
   GET /api/v1/boards
   ```
   - 17개 게시판 조회 성공
     - 자유게시판 1개
     - MUTI 타입별 게시판 16개 (ESAP ~ IFDU)
   - Supabase DB에서 데이터 조회 확인

   **Music/Playlist API** ⚠️
   ```bash
   GET /api/v1/musics
   GET /api/v1/playlists/public
   ```
   - Response: `401 Unauthorized`
   - 상태: ✅ **정상 (인증 필요한 API이므로 예상된 동작)**
   - Spring Security가 정상 작동 중

3. **확인된 사항**
   - ✅ Supabase PostgreSQL 17.6 연결 정상
   - ✅ 45개 API 엔드포인트 등록
   - ✅ Board 데이터 초기화 스킵 (이미 존재)
   - ✅ Transaction Pooler 연결 안정적

#### 애플리케이션 로그
```
2026-02-09T14:05:18.268 INFO  : Database: jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:6543/postgres
2026-02-09T14:05:18.479 INFO  : Successfully validated 7 migrations
2026-02-09T14:05:18.668 INFO  : Schema "public" is up to date. No migration necessary.
2026-02-09T14:05:20.489 INFO  : Started MutiApplication in 3.49 seconds
```

---

---

### Step 4: Docker 컨테이너화 ✅

#### 진행 내용
1. ✅ **Dockerfile 작성 및 수정**
   - 위치: `/Users/peterj/Desktop/study/MUTI/Dockerfile`
   - Multi-stage build 적용
     - Stage 1 (Builder): Gradle 8.14.4 + JDK 17로 빌드
     - Stage 2 (Runtime): JRE 17으로 최적화
   - 보안: non-root user 사용 (`groupadd`/`useradd`)
   - Health check 설정: `/actuator/health` (30초 간격)
   - 포트: 8080 expose

2. ✅ **docker-compose.yml 작성 및 수정**
   - 위치: `/Users/peterj/Desktop/study/MUTI/docker-compose.yml`
   - 환경 변수 주입 (.env 파일 사용)
   - Health check 설정
   - Network 설정 (muti-network)
   - Restart policy: unless-stopped
   - `version` 필드 제거 (Docker Compose v2 호환)

3. ✅ **Docker 이미지 빌드**
   ```bash
   docker build -t muti-backend:latest .
   ```
   - 빌드 시간: 약 3분 30초
   - 최종 이미지 크기: **571MB**
   - 빌드 캐시 활용으로 재빌드 시 빠른 속도

4. ✅ **Docker 컨테이너 실행 테스트**
   ```bash
   docker-compose up -d
   ```
   - 컨테이너 시작 시간: 약 5초
   - Supabase PostgreSQL 연결 성공
   - Flyway 마이그레이션 검증 완료 (7개)
   - 45개 API 엔드포인트 등록

5. ✅ **컨테이너 상태 확인**
   - **Status**: `healthy`
   - Health check 성공 (3회 연속)
   - 애플리케이션 시작 시간: 4.96초

6. ✅ **API 테스트**
   ```bash
   # Health Check
   curl http://localhost:8080/actuator/health
   # Response: {"status":"UP"}

   # Survey API
   curl http://localhost:8080/api/v1/surveys
   # Response: 설문 1개 조회 성공 (8개 질문)

   # Board API
   curl http://localhost:8080/api/v1/boards
   # Response: 17개 게시판 조회 성공

   # Music API (인증 필요)
   curl http://localhost:8080/api/v1/musics
   # Response: 401 Unauthorized (예상된 동작)
   ```

7. ✅ **컨테이너 정리**
   ```bash
   docker-compose down
   ```
   - 컨테이너 정상 종료
   - 네트워크 정리 완료

#### 트러블슈팅

**문제 1: Alpine Linux 이미지 플랫폼 호환성 문제**
- **증상**: `no match for platform in manifest: not found`
- **원인**:
  - Apple Silicon (ARM64) 시스템에서 Alpine Linux 이미지가 완전히 지원되지 않음
  - `gradle:8.14.4-jdk17-alpine`과 `eclipse-temurin:17-jre-alpine` 이미지 사용 시 발생
- **해결**:
  - Alpine 이미지 → 일반 Debian 기반 이미지로 변경
  - `gradle:8.14.4-jdk17` (Alpine 제거)
  - `eclipse-temurin:17-jre` (Alpine 제거)
  - 이미지 크기는 약간 증가했지만 안정성 확보
- **교훈**:
  - Alpine 이미지는 크기가 작지만 ARM64 호환성 문제가 있을 수 있음
  - 프로덕션 환경에서는 안정성이 크기보다 중요

**문제 2: Alpine 전용 명령어 사용**
- **증상**: `addgroup -S` 및 `adduser -S` 명령어 실패 (exit code 51)
- **원인**:
  - Alpine Linux 전용 명령어를 Debian 기반 이미지에서 사용
  - `-S` 플래그는 Alpine의 `addgroup`/`adduser`에만 존재
- **해결**:
  - `addgroup -S spring && adduser -S spring -G spring`
  - → `groupadd -r spring && useradd -r -g spring spring`
  - Debian/Ubuntu에서 사용하는 표준 명령어로 변경
- **교훈**:
  - 베이스 이미지에 따라 사용 가능한 명령어가 다름
  - Alpine: `addgroup`/`adduser`
  - Debian/Ubuntu: `groupadd`/`useradd`

**문제 3: 포트 8080 충돌**
- **증상**: `bind: address already in use`
- **원인**:
  - 이전에 실행한 Spring Boot 애플리케이션이 포트 8080 사용 중
  - `./gradlew bootRun` 명령어로 실행한 프로세스가 종료되지 않음
- **해결**:
  ```bash
  # 포트 사용 프로세스 확인
  lsof -ti:8080
  # PID: 54974

  # 프로세스 종료
  kill 54974
  ```
- **교훈**:
  - Docker 컨테이너 실행 전 포트 충돌 확인 필요
  - 로컬 개발 서버와 Docker 컨테이너는 동일한 포트를 사용할 수 없음

#### 성능 측정

**빌드 성능:**
- 첫 빌드: 약 3분 30초
  - Stage 1 (Builder): 2분 33초 (Gradle 의존성 + bootJar)
  - Stage 2 (Runtime): 1초 (이미지 레이어 생성)
- 재빌드 (캐시 사용): 약 10초

**실행 성능:**
- 컨테이너 시작: 약 5초
- 애플리케이션 준비: 약 5초 (총 10초)
- Health check 응답 시간: 50~80ms

**이미지 크기:**
- 최종 이미지: **571MB**
  - JRE 17: 약 200MB
  - 애플리케이션 JAR: 약 70MB
  - 시스템 라이브러리: 약 300MB

#### 변경된 파일
1. `Dockerfile` - Alpine → Debian 기반 이미지로 변경
2. `docker-compose.yml` - version 필드 제거

---

## 📝 대기 중인 작업

### Step 5: Phase 4 문서화 (시작 안 함)

#### 해야 할 작업
1. **PHASE_GUIDE.md에 Phase 4 추가**
   - Phase 4 개요
   - Supabase 연결 가이드
   - Docker 사용법
   - 배포 가이드
   - 트러블슈팅 (Step 1에서 발생한 에러)

2. **README.md 업데이트**
   - Docker 실행 방법 추가
   - 프로덕션 환경 설정 가이드

---

## 🚀 다음 단계 (재개 시 진행할 작업)

### 1. Step 4 완료하기

**작업 순서:**
```bash
# 1. Docker 이미지 빌드
docker build -t muti-backend:latest .

# 2. Docker Compose로 실행
docker-compose up -d

# 3. 컨테이너 상태 확인
docker ps
docker logs muti-backend -f

# 4. API 테스트
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/surveys | jq
curl http://localhost:8080/api/v1/boards | jq

# 5. 테스트 완료 후 정리
docker-compose down
```

**예상 소요 시간:** 20-30분

**주의사항:**
- Docker 이미지 빌드는 처음엔 5-10분 소요됨 (의존성 다운로드)
- `.env` 파일이 있어야 컨테이너 실행 가능
- Supabase 연결 정보가 올바른지 확인

### 2. Step 5: 문서화

**작업 내용:**
- PHASE_GUIDE.md에 Phase 4 전체 내용 추가
- README.md에 Docker 사용법 추가
- 트러블슈팅 가이드 작성

**예상 소요 시간:** 30-40분

### 3. Phase 4 완료 커밋

**커밋 메시지 예시:**
```
feat: Complete Phase 4 - Production deployment with Docker

Phase 4 완료: 배포 준비 및 프로덕션 검증

Step 1-3 변경사항:
- Supabase PostgreSQL 연결 및 Flyway 마이그레이션
- 프로덕션 환경 API 테스트

Step 4 변경사항:
- Dockerfile: Multi-stage build, JRE 17-alpine
- docker-compose.yml: 환경 변수 주입, Health check

Step 5 변경사항:
- PHASE_GUIDE.md: Phase 4 문서화
- README.md: Docker 사용법 추가
```

---

## 📂 변경된 파일 목록

### 커밋된 파일
1. `.env.example` - Supabase 연결 정보 업데이트
2. `src/main/resources/application-prod.yml` - Connection pool 설정
3. `src/test/java/com/muti/infrastructure/database/SupabaseDatabaseConnectionTest.java` - @Disabled 제거

### 생성/수정된 파일 (커밋 예정)
1. `Dockerfile` - Docker 이미지 빌드 설정 (Alpine → Debian 기반)
2. `docker-compose.yml` - Docker Compose 설정 (version 제거)
3. `PHASE4_PROGRESS.md` - Step 4 완료 기록

### 수정된 파일 (커밋 제외)
1. `.env` - 실제 Supabase 비밀번호 포함 (.gitignore에 포함됨)

---

## 🔧 환경 설정 정보

### Supabase 연결 정보
```yaml
Host: aws-1-ap-northeast-2.pooler.supabase.com
Port: 6543 (Transaction Pooler)
Database: postgres
Username: postgres.qlnuleskbxqhpyadsoeo
JDBC URL: jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:6543/postgres?prepareThreshold=0
```

### Connection Pool 설정
```yaml
maximum-pool-size: 10
minimum-idle: 2
connection-timeout: 30000
idle-timeout: 600000
max-lifetime: 1800000
```

### Docker 이미지 정보
- Base Image (Build): gradle:8.14.4-jdk17-alpine
- Base Image (Runtime): eclipse-temurin:17-jre-alpine
- Exposed Port: 8080
- Health Check: /actuator/health

---

## 📊 현재 진행률

```
Phase 4 전체 진행률: 80%

Step 1: Supabase 연결 테스트        [████████████] 100%
Step 2: Flyway 마이그레이션         [████████████] 100%
Step 3: 프로덕션 API 테스트         [████████████] 100%
Step 4: Docker 컨테이너화           [████████████] 100%
Step 5: Phase 4 문서화              [            ]   0%
```

---

## 💡 참고 사항

### 배운 내용
1. **Supabase Connection Pooling**
   - Session Pooler: 적은 연결 수, IPv4 호환
   - Transaction Pooler: 많은 연결 수, pgBouncer 기반

2. **pgBouncer 제약사항**
   - Prepared statement 미지원
   - `prepareThreshold=0` 필요

3. **Multi-stage Docker Build**
   - Builder stage: 빌드 환경
   - Runtime stage: 실행 환경
   - 최종 이미지 크기 최소화

### 다음 Phase 예상
- **Phase 5**: 프론트엔드 개발 (React + TypeScript)
- 또는 추가 백엔드 기능 (Spotify API, 음악 추천 등)

---

**작성자**: Claude Sonnet 4.5
**최종 수정**: 2026-02-09 14:40 KST
**다음 작업 시작점**: Step 4 - Docker 이미지 빌드부터 시작