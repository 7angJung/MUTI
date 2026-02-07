# MUTI (MUsic Type Indicator)

> 음악 성향 기반 추천 서비스 - 당신의 음악 취향을 16가지 타입으로 분석합니다

## 📋 목차
- [프로젝트 개요](#-프로젝트-개요)
- [기술 스택](#-기술-스택)
- [MUTI 타입 시스템](#-muti-타입-시스템)
- [아키텍처](#-아키텍처)
- [API 문서](#-api-문서)
- [시작하기](#-시작하기)
- [테스트](#-테스트)
- [개발 진행 상황](#-개발-진행-상황)

## 🎯 프로젝트 개요

MUTI(MUsic Type Indicator)는 사용자의 음악 선호도를 분석하여 16가지 음악 성향 타입 중 하나로 분류하고, 해당 타입에 맞는 음악을 추천하는 서비스입니다.

### 핵심 기능
- 🎵 **음악 성향 설문**: 4개 축, 16가지 타입을 기반으로 한 설문 조사
- 🎯 **타입 분석**: MBTI 방식의 4축 분석을 통한 정교한 타입 산출
- 📊 **결과 제공**: 사용자의 음악 취향 프로필 및 상세 설명
- 🎼 **음악 추천**: 타입별 맞춤 음악 추천 (향후 구현 예정)

### MUTI란?
MBTI의 음악 버전으로, 사용자의 음악 취향을 4개의 축으로 분석합니다:
- **E(Energetic) ↔ I(Introspective)**: 에너제틱 vs 성찰적
- **S(Sensory) ↔ F(Feeling)**: 감각적 vs 감성적
- **A(Analog) ↔ D(Digital)**: 아날로그 vs 디지털
- **P(Popular) ↔ U(Underground)**: 대중적 vs 언더그라운드

## 🛠 기술 스택

### Backend
- **Java 17**: LTS 버전의 최신 Java
- **Spring Boot 3.5.10**: 최신 스프링 부트 프레임워크
- **Spring Data JPA**: ORM 및 데이터 접근 추상화
- **Hibernate**: JPA 구현체
- **Spring Security**: 보안 및 인증 (향후 확장)
- **Validation**: Bean Validation (Jakarta Validation)

### Database
- **H2 Database**: 로컬 개발 및 테스트용 인메모리 DB
- **PostgreSQL**: 프로덕션 환경 (Supabase)
- **Flyway**: 데이터베이스 마이그레이션 관리

### Testing
- **JUnit 5**: 테스트 프레임워크
- **Mockito**: 모킹 프레임워크
- **AssertJ**: 유창한 assertion 라이브러리
- **Spring Boot Test**: 통합 테스트 지원

### Documentation
- **Swagger/OpenAPI 3**: API 문서 자동 생성
- **SpringDoc OpenAPI**: Swagger UI 통합

### Build & DevOps
- **Gradle 8.14.4**: 빌드 도구
- **Git**: 버전 관리

## 🎭 MUTI 타입 시스템

### 4개의 축 (Axes)

#### 1. E_I 축: Energetic ↔ Introspective
- **E (Energetic)**: 빠르고 역동적인 음악 선호
  - 댄스, 일렉트로닉, 업템포 록
- **I (Introspective)**: 차분하고 사색적인 음악 선호
  - 재즈, 클래식, 앰비언트

#### 2. S_F 축: Sensory ↔ Feeling
- **S (Sensory)**: 리듬과 비트가 강한 음악 선호
  - EDM, 힙합, 펑크
- **F (Feeling)**: 멜로디와 가사가 중요한 음악 선호
  - 발라드, 포크, 인디

#### 3. A_D 축: Analog ↔ Digital
- **A (Analog)**: 아날로그적이고 생생한 음악 선호
  - 라이브 음악, 어쿠스틱, 클래식
- **D (Digital)**: 전자음악과 신스 사운드 선호
  - 일렉트로닉, 신스팝, 테크노

#### 4. P_U 축: Popular ↔ Underground
- **P (Popular)**: 대중적이고 친숙한 음악 선호
  - 팝, 차트 음악, 메인스트림
- **U (Underground)**: 실험적이고 독특한 음악 선호
  - 인디, 실험음악, 언더그라운드

### 16가지 MUTI 타입

| 타입 | 설명 | 대표 장르 |
|------|------|-----------|
| **ESAP** | 활발한 감각파 | 팝, 댄스, K-POP |
| **ESAU** | 역동적 탐험가 | 힙합, R&B, 얼터너티브 |
| **ESDP** | 디지털 감각파 | EDM, 일렉트로닉 댄스 |
| **ESDU** | 미래지향 탐험가 | 베이스뮤직, 글리치 |
| **EFAP** | 감성 대중가 | 발라드, 어쿠스틱 팝 |
| **EFAU** | 감성 유목민 | 인디 팝, 싱어송라이터 |
| **EFDP** | 미래형 감성파 | 신스팝, 퓨처 베이스 |
| **EFDU** | 실험적 감성파 | 아트 팝, 일렉트로닉 인디 |
| **ISAP** | 차분한 클래식파 | 클래식, 뉴에이지 |
| **ISAU** | 사색적 탐험가 | 포스트 록, 실험 재즈 |
| **ISDP** | 전자 명상가 | 앰비언트, 칠아웃 |
| **ISDU** | 실험적 몽상가 | IDM, 글리치 앰비언트 |
| **IFAP** | 서정적 로맨티스트 | OST, 뉴에이지 발라드 |
| **IFAU** | 감성 예술가 | 포크, 챔버 팝 |
| **IFDP** | 몽환적 전자파 | 드림 팝, 신스웨이브 |
| **IFDU** | 추상적 예술가 | 실험 전자음악, 사운드스케이프 |

## 🏗 아키텍처

### 프로젝트 구조
```
src/main/java/com/muti/
├── MutiApplication.java              # 메인 애플리케이션
├── domain/
│   └── survey/                       # 설문 도메인
│       ├── controller/
│       │   └── SurveyController.java       # REST API 컨트롤러
│       ├── service/
│       │   ├── SurveyService.java          # 설문 조회 서비스
│       │   └── SurveyResponseService.java  # 설문 응답 처리 서비스
│       ├── repository/
│       │   ├── SurveyRepository.java
│       │   ├── QuestionRepository.java
│       │   ├── QuestionOptionRepository.java
│       │   └── SurveyResultRepository.java
│       ├── entity/
│       │   ├── Survey.java                 # 설문 엔티티
│       │   ├── Question.java               # 질문 엔티티
│       │   ├── QuestionOption.java         # 선택지 엔티티
│       │   ├── SurveyResponse.java         # 응답 엔티티
│       │   └── SurveyResult.java           # 결과 엔티티
│       ├── dto/
│       │   ├── request/
│       │   │   ├── SubmitSurveyRequest.java
│       │   │   └── SurveyAnswerDto.java
│       │   └── response/
│       │       ├── SurveyDto.java
│       │       ├── QuestionDto.java
│       │       ├── QuestionOptionDto.java
│       │       └── SurveyResultDto.java
│       ├── mapper/
│       │   └── SurveyMapper.java           # 엔티티-DTO 변환
│       └── enums/
│           ├── MutiType.java               # 16가지 타입
│           ├── MutiAxis.java               # 4개 축
│           └── AxisDirection.java          # 8개 방향
└── global/
    ├── common/
    │   └── ApiResponse.java          # 통일된 API 응답 포맷
    ├── config/
    │   ├── JpaConfig.java            # JPA 설정
    │   └── SecurityConfig.java       # Security 설정
    ├── error/
    │   ├── ErrorCode.java            # 에러 코드 정의
    │   └── BusinessException.java    # 비즈니스 예외
    └── advice/
        └── GlobalExceptionHandler.java # 전역 예외 처리
```

### Domain-Driven Design (DDD)
- **도메인 중심 설계**: Survey 도메인을 중심으로 구조화
- **계층 분리**: Controller → Service → Repository → Entity
- **엔티티 주도**: 풍부한 도메인 모델과 비즈니스 로직

### 데이터베이스 스키마

#### ERD 개요
```
Survey (1) ──< (N) Question (1) ──< (N) QuestionOption
   │
   └──< (N) SurveyResult (1) ──< (N) SurveyResponse
```

#### 주요 테이블

**surveys** - 설문
- `id`: 설문 ID (PK)
- `title`: 설문 제목
- `description`: 설문 설명
- `active`: 활성화 여부
- `created_at`, `updated_at`: 타임스탬프

**questions** - 질문
- `id`: 질문 ID (PK)
- `survey_id`: 설문 ID (FK)
- `content`: 질문 내용
- `axis`: 축 (E_I, S_F, A_D, P_U)
- `order_index`: 질문 순서

**question_options** - 선택지
- `id`: 선택지 ID (PK)
- `question_id`: 질문 ID (FK)
- `content`: 선택지 내용
- `direction`: 방향 (E, I, S, F, A, D, P, U)
- `score`: 점수 (1~5)
- `order_index`: 선택지 순서

**survey_results** - 설문 결과
- `id`: 결과 ID (PK)
- `survey_id`: 설문 ID (FK)
- `muti_type`: MUTI 타입 (ESAP, IFDU 등)
- `ei_score`: E_I 축 점수
- `sf_score`: S_F 축 점수
- `ad_score`: A_D 축 점수
- `pu_score`: P_U 축 점수
- `session_id`: 세션 ID (비로그인 사용자)
- `user_id`: 사용자 ID (향후)

**survey_responses** - 개별 응답
- `id`: 응답 ID (PK)
- `result_id`: 결과 ID (FK)
- `question_id`: 질문 ID (FK)
- `selected_option_id`: 선택한 선택지 ID (FK)

## 📡 API 문서

### Base URL
```
http://localhost:8080/api/v1
```

### Endpoints

#### 1. 헬스체크
```http
GET /surveys/ping
```
**응답**
```json
{
  "success": true,
  "data": "pong"
}
```

#### 2. 활성 설문 목록 조회
```http
GET /surveys
```
**응답**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "MUTI 음악 성향 테스트",
      "description": "당신의 음악 취향을 분석합니다",
      "active": true,
      "questions": null,
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ]
}
```

#### 3. 설문 상세 조회
```http
GET /surveys/{surveyId}
```
**응답**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "MUTI 음악 성향 테스트",
    "description": "당신의 음악 취향을 분석합니다",
    "active": true,
    "questions": [
      {
        "id": 1,
        "content": "어떤 음악을 더 선호하시나요?",
        "axis": "E_I",
        "orderIndex": 1,
        "options": [
          {
            "id": 1,
            "content": "빠르고 신나는 음악",
            "direction": "E",
            "score": 5,
            "orderIndex": 1
          },
          {
            "id": 2,
            "content": "차분하고 잔잔한 음악",
            "direction": "I",
            "score": 5,
            "orderIndex": 2
          }
        ]
      }
    ]
  }
}
```

#### 4. 설문 응답 제출
```http
POST /surveys/{surveyId}/submit
Content-Type: application/json
```
**요청 본문**
```json
{
  "surveyId": 1,
  "answers": [
    {
      "questionId": 1,
      "optionId": 1
    },
    {
      "questionId": 2,
      "optionId": 3
    }
  ],
  "sessionId": "session-123"
}
```

**응답**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "surveyId": 1,
    "mutiType": "ESAP",
    "mutiTypeName": "활발한 감각파",
    "mutiTypeDescription": "빠르고 감각적인 대중 음악을 선호하는 활발한 성향",
    "axisScores": {
      "E_I": 10,
      "S_F": 8,
      "A_D": 12,
      "P_U": 6
    },
    "axisDirections": {
      "E_I": "E",
      "S_F": "S",
      "A_D": "A",
      "P_U": "P"
    },
    "createdAt": "2024-01-01T12:00:00"
  },
  "message": "MUTI 타입이 성공적으로 산출되었습니다."
}
```

### 에러 응답
```json
{
  "success": false,
  "errorCode": "S001",
  "message": "설문을 찾을 수 없습니다."
}
```

### 에러 코드
| 코드 | HTTP 상태 | 설명 |
|------|-----------|------|
| S001 | 404 | 설문을 찾을 수 없음 |
| S002 | 400 | 이미 완료된 설문 |
| S003 | 400 | 설문 응답이 올바르지 않음 |
| C001 | 400 | 입력값이 올바르지 않음 |
| C003 | 500 | 서버 내부 오류 |

## 🚀 시작하기

### 필수 요구사항
- Java 17 이상
- Gradle 8.14.4 이상

### 설치 및 실행

1. **저장소 클론**
```bash
git clone [repository-url]
cd MUTI
```

2. **의존성 설치 및 빌드**
```bash
./gradlew build
```

3. **애플리케이션 실행**
```bash
./gradlew bootRun
```

4. **접속**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:muti`
  - Username: `sa`
  - Password: (empty)

### 프로파일
- **local**: 로컬 개발 환경 (H2 인메모리 DB)
- **prod**: 프로덕션 환경 (PostgreSQL/Supabase)

```bash
# 프로덕션 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## 🧪 테스트

### 전체 테스트 실행
```bash
./gradlew test
```

### 특정 테스트 실행
```bash
# 컨트롤러 테스트
./gradlew test --tests "com.muti.domain.survey.controller.*"

# 서비스 테스트
./gradlew test --tests "com.muti.domain.survey.service.*"

# 리포지토리 테스트
./gradlew test --tests "com.muti.domain.survey.repository.*"
```

### 테스트 커버리지
```bash
./gradlew jacocoTestReport
# 결과: build/reports/jacoco/test/html/index.html
```

### 테스트 현황
```
📊 총 121개 테스트
✅ 성공: 120개
⏭️  스킵: 1개 (MultipleBagFetch 제약)
❌ 실패: 0개
```

#### 테스트 구성
- **단위 테스트 (Unit Tests)**: 64개
  - Enum 테스트: 24개
  - Entity 테스트: 19개
  - Service 테스트: 14개
  - Mapper 테스트: 6개
  - Controller 테스트: 1개 (컨텍스트 로드)

- **통합 테스트 (Integration Tests)**: 56개
  - Repository 테스트: 15개
  - Controller 테스트: 13개
  - 데이터 초기화 테스트: 10개
  - Flyway 마이그레이션 테스트: 10개
  - E2E API 테스트: 8개

## 📈 개발 진행 상황

### ✅ 완료된 기능 (Steps 0-18)

#### Phase 1: 프로젝트 초기 설정 (Steps 0-7)
- [x] Git 저장소 초기화
- [x] 프로젝트 구조 설정 (Domain-Driven Design)
- [x] 설정 파일 작성 (application.yml, 프로파일 분리)
- [x] 헬스체크 API
- [x] Spring Security 기본 설정
- [x] 전역 예외 처리 (GlobalExceptionHandler)
- [x] API 응답 포맷 통일 (ApiResponse)
- [x] Swagger/OpenAPI 문서화 설정

#### Phase 2: 도메인 모델 구현 (Steps 8-10)
- [x] Entity 설계 및 구현
  - Survey (설문)
  - Question (질문)
  - QuestionOption (선택지)
  - SurveyResponse (응답)
  - SurveyResult (결과)
- [x] Enum 설계 및 구현
  - MutiType (16가지 타입)
  - MutiAxis (4개 축)
  - AxisDirection (8개 방향)
- [x] Repository 구현 (JPA + 페치 조인 최적화)
- [x] DTO 및 Mapper 구현

#### Phase 3: 테스트 인프라 구축 (Steps 11-15)
- [x] 테스트 설정 (TestJpaConfig)
- [x] Enum 테스트 (24개)
- [x] Entity 테스트 (19개)
- [x] Repository 통합 테스트 (15개)
- [x] Mapper 테스트 (6개)

#### Phase 4: 비즈니스 로직 구현 (Steps 16-17)
- [x] SurveyService 구현 및 테스트 (8개)
  - 활성 설문 조회
  - 설문 상세 조회
  - 설문 존재 여부 확인
- [x] SurveyResponseService 구현 및 테스트 (6개)
  - **핵심 알고리즘**: MUTI 타입 산출 로직
  - 방향 기반 점수 계산 (E/S/A/P: 양수, I/F/D/U: 음수)
  - 설문 응답 검증 및 저장

#### Phase 5: API 엔드포인트 구현 (Step 18)
- [x] SurveyController 확장 및 테스트 (13개)
  - GET /api/v1/surveys (활성 설문 목록)
  - GET /api/v1/surveys/{surveyId} (설문 상세)
  - POST /api/v1/surveys/{surveyId}/submit (설문 응답 제출)

#### Phase 6: 데이터 초기화 및 Flyway (Steps 19-20)
- [x] 초기 설문 데이터 생성 (Step 19)
  - 프로그래밍 방식으로 데이터 초기화 (SurveyDataInitializer)
  - 4개 축별로 각 2개씩 총 8개의 질문 생성
  - 각 질문당 2개 선택지 (총 16개)
  - 로컬 환경에서 자동 실행 (@Profile("local"))
- [x] 데이터 초기화 통합 테스트 (10개)
  - Survey, Question, QuestionOption 생성 검증
  - 각 축별 질문 검증 (E_I, S_F, A_D, P_U)
  - 점수 범위 및 순서 검증
  - 중복 실행 방지 검증
- [x] Flyway 마이그레이션 구현 (Step 20)
  - V1__init_schema.sql: PostgreSQL 표준 문법으로 스키마 생성
  - V2__insert_initial_survey.sql: 초기 설문 데이터 삽입
  - H2 PostgreSQL 호환 모드 완벽 지원
  - Flyway 마이그레이션 통합 테스트 (10개)

#### Phase 7: E2E 테스트 (Step 21)
- [x] E2E API 통합 테스트 (8개)
  - 헬스체크 API 검증
  - 설문 목록 → 상세 조회 플로우
  - 전체 플로우: 조회 → 응답 제출 → ESAP/IFDU 타입 산출
  - 실패 케이스: 존재하지 않는 설문, 불완전한 응답, 빈 응답
  - 다중 사용자 시나리오 검증

### 🔄 진행 중

#### Phase 8: 배포 준비
- [ ] Supabase PostgreSQL 연결
- [ ] 프로덕션 환경 설정
- [ ] Docker 컨테이너화

### 📝 향후 계획

#### Phase 7: 추가 기능
- [ ] 사용자 인증 (Spring Security + JWT)
- [ ] MUTI 타입별 음악 추천 API
- [ ] Spotify/YouTube Music API 연동
- [ ] 결과 공유 기능
- [ ] 통계 및 분석 대시보드

#### Phase 8: DevOps
- [ ] Docker 컨테이너화
- [ ] CI/CD 파이프라인 (GitHub Actions)
- [ ] 무중단 배포 설정
- [ ] 모니터링 및 로깅 (Prometheus, Grafana)

## 🎯 MUTI 타입 산출 알고리즘

### 핵심 로직
```java
// 1. 각 축별 점수 계산
for (각 응답) {
    선택지의 방향에 따라 점수 부여:
    - E, S, A, P 방향 → 양수 점수
    - I, F, D, U 방향 → 음수 점수

    해당 축의 총점에 누적
}

// 2. MUTI 타입 결정
E_I 축 점수 >= 0 ? "E" : "I"
S_F 축 점수 >= 0 ? "S" : "F"
A_D 축 점수 >= 0 ? "A" : "D"
P_U 축 점수 >= 0 ? "P" : "U"

// 3. 최종 타입 조합
MUTI Type = E/I + S/F + A/D + P/U
```

### 예시
```
응답:
- Q1 (E_I): E 방향 선택지 (+5점) → E_I = +5
- Q2 (S_F): S 방향 선택지 (+3점) → S_F = +3
- Q3 (A_D): A 방향 선택지 (+4점) → A_D = +4
- Q4 (P_U): P 방향 선택지 (+2점) → P_U = +2

결과: E(+5) + S(+3) + A(+4) + P(+2) = ESAP
```

## 📚 참고 자료

### 기술 문서
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Hibernate ORM](https://hibernate.org/orm/)
- [SpringDoc OpenAPI](https://springdoc.org/)

### 개발 표준
- Java Code Convention
- RESTful API 설계 가이드
- Domain-Driven Design (DDD) 패턴

## 📞 문의

프로젝트 관련 문의사항이나 버그 리포트는 이슈로 등록해주세요.

---

**MUTI** - 음악으로 나를 발견하는 시간 🎵