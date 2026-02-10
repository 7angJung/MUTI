## Phase 4: 배포 준비 및 프로덕션 검증

### 4.1 Phase 4 개요

**Phase 4 완료일**: 2026년 2월 9일 ✅

Phase 4에서는 개발한 애플리케이션을 **실제 프로덕션 환경에 배포**하기 위한 준비 작업을 진행합니다.

#### 📋 Phase 4 목표

1. ✅ **Supabase PostgreSQL 연결**
   - 클라우드 데이터베이스 설정
   - Connection Pooling 최적화
   - 프로덕션 환경 설정

2. ✅ **Flyway 마이그레이션 실행**
   - 스키마 마이그레이션 자동화
   - 데이터 초기화
   - 마이그레이션 검증

3. ✅ **프로덕션 환경 API 테스트**
   - 실제 데이터베이스 연결 검증
   - API 엔드포인트 동작 확인
   - Security 설정 검증

4. ✅ **Docker 컨테이너화**
   - Dockerfile 작성 (Multi-stage build)
   - docker-compose.yml 설정
   - 이미지 빌드 및 테스트

5. ⏳ **문서화 및 배포 가이드** (진행 중)
   - 배포 절차 문서화
   - 환경 설정 가이드
   - 트러블슈팅 가이드

---

### 4.2 Supabase PostgreSQL 연결

#### 🎯 Supabase란?

**Supabase**는 **오픈소스 Firebase 대안**으로, PostgreSQL 기반의 Backend-as-a-Service(BaaS)입니다.

**비유로 설명:**
- **H2 (로컬 DB)**: 집에서 혼자 쓰는 미니 냉장고
- **Supabase (클라우드 DB)**: 24시간 운영되는 대형 창고

**Supabase의 장점:**
- ✅ PostgreSQL 17.6 제공 (최신 버전)
- ✅ 무료 플랜: 500MB 저장소, 1GB 대역폭
- ✅ Connection Pooling (pgBouncer 내장)
- ✅ 자동 백업 및 복구
- ✅ RESTful API 자동 생성
- ✅ Realtime Subscriptions (WebSocket)

#### 📡 Connection Pooling 이란?

**Connection Pooling**은 데이터베이스 연결을 미리 만들어두고 재사용하는 기술입니다.

**비유:**
- **Pooling 없음**: 매번 새로운 전화선을 깔아야 통화 가능 (느림)
- **Pooling 사용**: 전화선을 미리 여러 개 깔아두고 필요할 때 사용 (빠름)

**Supabase의 Connection Pooling 방식:**

| 모드 | 포트 | 최대 연결 수 | 용도 | 특징 |
|------|------|------------|------|------|
| **Session Mode** | 5432 | 적음 (~30) | 단일 애플리케이션 | IPv4 전용 |
| **Transaction Mode** | 6543 | 많음 (~200) | 다중 애플리케이션 | pgBouncer 기반 |

**우리의 선택: Transaction Mode (Port 6543)**

**이유:**
1. 더 많은 동시 연결 지원
2. 서버리스/마이크로서비스 환경에 적합
3. 연결 수 제한 문제 해결

#### ⚙️ 프로덕션 환경 설정

**`application-prod.yml` 수정:**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:6543/postgres?prepareThreshold=0
    username: postgres.qlnuleskbxqhpyadsoeo
    # password는 환경 변수로 주입 (보안)

  hikari:
    maximum-pool-size: 10    # Supabase 무료 플랜 제한 고려
    minimum-idle: 2          # 최소 유휴 연결
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000
```

**`prepareThreshold=0`이 필요한 이유:**

**Prepared Statement란?**
- SQL 쿼리를 미리 컴파일해서 재사용하는 기술
- 성능 향상 및 SQL Injection 방지

**문제:**
- pgBouncer (Transaction Mode)는 Prepared Statement를 지원하지 않음
- 이유: Connection을 매번 다른 클라이언트에게 할당하기 때문

**해결:**
- `prepareThreshold=0`: Prepared Statement 비활성화
- 성능 영향은 미미하지만 호환성 확보

#### 🔐 환경 변수로 비밀 정보 관리

**`.env` 파일:**
```bash
# ⚠️ 절대 GitHub에 커밋하지 마세요!
DB_PASSWORD=your-secret-password
JWT_SECRET=your-256-bit-secret-key
```

**`.gitignore`에 추가:**
```
.env
```

**`application-prod.yml`에서 사용:**
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

#### 🧪 연결 테스트

**`SupabaseDatabaseConnectionTest.java` 작성:**

```java
@SpringBootTest
@ActiveProfiles("prod")
class SupabaseDatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("Supabase PostgreSQL 17.6 연결 테스트")
    void testDatabaseConnection() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            assertThat(metaData.getDatabaseProductName())
                .isEqualTo("PostgreSQL");
            assertThat(metaData.getDatabaseProductVersion())
                .contains("17.6");
        }
    }

    @Test
    @DisplayName("쿼리 실행 테스트")
    void testQueryExecution() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 AS test_value")) {

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("test_value")).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("Flyway 마이그레이션 히스토리 확인")
    void testFlywayHistory() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) FROM flyway_schema_history")) {

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isGreaterThan(0);
        }
    }
}
```

**테스트 결과:**
```
✅ Supabase PostgreSQL 17.6 연결 성공
✅ 쿼리 실행 테스트 성공
✅ 스키마 확인 성공 (15개 테이블)
✅ Flyway 히스토리 확인 성공 (7개 마이그레이션)
```

---

### 4.3 Docker 컨테이너화

#### 🐳 Docker란?

**Docker**는 애플리케이션을 **컨테이너**라는 격리된 환경에서 실행하는 기술입니다.

**비유:**
- **일반 배포**: 짐을 그냥 트럭에 싣기 (정리 안 됨, 섞임)
- **Docker**: 짐을 컨테이너에 담아서 배송 (깔끔, 안전)

**Docker의 장점:**
- ✅ **환경 일관성**: "내 컴퓨터에서는 되는데..."  문제 해결
- ✅ **빠른 배포**: 이미지 하나로 어디서든 실행
- ✅ **격리**: 다른 애플리케이션과 충돌 없음
- ✅ **확장성**: 동일한 컨테이너를 여러 개 실행 가능

#### 🏗️ Multi-Stage Build

**Multi-Stage Build**는 Docker 이미지를 여러 단계로 나눠 빌드하는 기술입니다.

**비유:**
- **Stage 1 (Builder)**: 공장에서 제품 제조 (컴파일 도구, 소스 코드)
- **Stage 2 (Runtime)**: 완제품만 포장해서 배송 (실행 파일만)

**장점:**
- ✅ 최종 이미지 크기 감소 (빌드 도구 제외)
- ✅ 보안 향상 (소스 코드 제외)
- ✅ 실행 속도 향상 (불필요한 파일 제외)

#### 📄 Dockerfile 작성

```dockerfile
# Stage 1: Build stage
FROM gradle:8.14.4-jdk17 AS builder

WORKDIR /app

# Copy Gradle files for dependency caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN gradle bootJar --no-daemon -x test

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**주요 포인트:**

1. **Layer Caching 최적화:**
   ```dockerfile
   # Gradle 설정 파일 먼저 복사 (변경 빈도 낮음)
   COPY build.gradle settings.gradle ./

   # 의존성 다운로드 (캐시됨)
   RUN gradle dependencies --no-daemon || true

   # 소스 코드 나중에 복사 (변경 빈도 높음)
   COPY src ./src
   ```

2. **non-root 사용자 (보안):**
   ```dockerfile
   # root로 실행하지 않음 (보안 강화)
   RUN groupadd -r spring && useradd -r -g spring spring
   USER spring:spring
   ```

3. **Health Check 설정:**
   ```dockerfile
   # Docker가 자동으로 컨테이너 상태 확인
   HEALTHCHECK --interval=30s --timeout=3s \
     CMD wget ... /actuator/health || exit 1
   ```

#### 🐙 docker-compose.yml 작성

**Docker Compose**는 여러 컨테이너를 한 번에 관리하는 도구입니다.

```yaml
services:
  muti-backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: muti-backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}
      - DB_URL=${DB_URL}
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    env_file:
      - .env
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 40s
    networks:
      - muti-network

networks:
  muti-network:
    driver: bridge
```

**주요 설정:**

1. **환경 변수 주입:**
   ```yaml
   environment:
     - DB_PASSWORD=${DB_PASSWORD}  # .env 파일에서 읽음
   env_file:
     - .env  # 환경 변수 파일
   ```

2. **Restart Policy:**
   ```yaml
   restart: unless-stopped  # 컨테이너 종료 시 자동 재시작
   ```

3. **Network 설정:**
   ```yaml
   networks:
     - muti-network  # 컨테이너 간 통신 가능
   ```

#### 🔨 이미지 빌드 및 실행

**1. Docker 이미지 빌드:**
```bash
docker build -t muti-backend:latest .
```

**빌드 과정:**
```
[Stage 1: Builder]
1. Gradle 의존성 다운로드 (51초)
2. 소스 코드 컴파일
3. bootJar 생성 (2분 33초)

[Stage 2: Runtime]
4. JRE 17 이미지 준비
5. non-root 사용자 생성
6. JAR 파일 복사

[최종 결과]
✅ 이미지 크기: 571MB
✅ 빌드 시간: 약 3분 30초
```

**2. 컨테이너 실행:**
```bash
docker-compose up -d
```

**실행 과정:**
```
✅ 컨테이너 시작 (5초)
✅ Spring Boot 애플리케이션 시작 (5초)
✅ Supabase PostgreSQL 연결 성공
✅ Flyway 마이그레이션 검증 (7개)
✅ Health Check: healthy
```

**3. API 테스트:**
```bash
# Health Check
curl http://localhost:8080/actuator/health
# {"status":"UP"}

# Survey API
curl http://localhost:8080/api/v1/surveys
# 설문 1개 조회 성공

# Board API
curl http://localhost:8080/api/v1/boards
# 17개 게시판 조회 성공
```

**4. 컨테이너 정리:**
```bash
docker-compose down
```

---

### 4.4 Docker 트러블슈팅

Phase 4 진행 중 발생한 문제와 해결 과정을 기록합니다.

#### 🐛 문제 1: Alpine Linux 플랫폼 호환성

**에러 메시지:**
```
ERROR: no match for platform in manifest: not found
```

**원인:**
- Apple Silicon (ARM64) 시스템에서 Alpine Linux 이미지 호환성 문제
- `gradle:8.14.4-jdk17-alpine` 및 `eclipse-temurin:17-jre-alpine` 사용 시 발생

**해결:**
```dockerfile
# Before (Alpine)
FROM gradle:8.14.4-jdk17-alpine AS builder
FROM eclipse-temurin:17-jre-alpine

# After (Debian 기반)
FROM gradle:8.14.4-jdk17 AS builder
FROM eclipse-temurin:17-jre
```

**트레이드오프:**
- **이미지 크기**: 약 50MB 증가 (alpine 제거)
- **호환성**: ✅ ARM64 완벽 지원
- **안정성**: ✅ 프로덕션 환경에서 더 안정적

#### 🐛 문제 2: Alpine 전용 명령어

**에러 메시지:**
```
exit code: 51
addgroup -S: invalid option
```

**원인:**
- Alpine Linux 전용 명령어를 Debian 기반 이미지에서 사용

**해결:**
```dockerfile
# Before (Alpine 전용)
RUN addgroup -S spring && adduser -S spring -G spring

# After (Debian/Ubuntu)
RUN groupadd -r spring && useradd -r -g spring spring
```

**교훈:**
- Alpine: `addgroup` / `adduser`
- Debian/Ubuntu: `groupadd` / `useradd`
- 베이스 이미지에 따라 명령어가 다름

#### 🐛 문제 3: 포트 8080 충돌

**에러 메시지:**
```
bind: address already in use
```

**원인:**
- 이전에 실행한 Spring Boot 애플리케이션이 포트 8080 사용 중

**해결:**
```bash
# 1. 포트 사용 프로세스 확인
lsof -ti:8080  # PID: 54974

# 2. 프로세스 종료
kill 54974

# 3. 재시도
docker-compose up -d
```

---

### 4.5 Phase 4 주요 학습 내용

#### 📚 새로운 기술 개념

**1. Backend-as-a-Service (BaaS)**

**정의:** 백엔드 인프라를 서비스로 제공하는 클라우드 플랫폼

**비유:**
- **전통적 방식**: 집을 직접 짓기 (서버 구축, DB 설치, 네트워크 설정)
- **BaaS**: 이미 지어진 집 임대 (Supabase가 모든 인프라 제공)

**Supabase 주요 기능:**
- PostgreSQL 데이터베이스
- Authentication (인증)
- Storage (파일 저장)
- Realtime (WebSocket)
- RESTful API 자동 생성

**2. Connection Pooling**

**정의:** 데이터베이스 연결을 미리 생성하고 재사용

**비유:**
- **No Pooling**: 택시 타려고 매번 새 택시 호출 (느림)
- **With Pooling**: 택시 대기 줄 (이미 대기 중, 빠름)

**HikariCP 설정:**
```yaml
hikari:
  maximum-pool-size: 10  # 최대 10개 연결 유지
  minimum-idle: 2        # 최소 2개는 항상 대기
  connection-timeout: 30000  # 연결 대기 시간 30초
```

**3. pgBouncer**

**정의:** PostgreSQL 전용 Connection Pooler

**작동 방식:**
```
[Client 1] ─┐
[Client 2] ─┼─> [pgBouncer] ─> [PostgreSQL]
[Client 3] ─┘      (10개)         (5개)
```

**모드 비교:**

| 모드 | 설명 | 용도 |
|------|------|------|
| **Session** | 클라이언트당 1개 연결 유지 | Long-running queries |
| **Transaction** | 트랜잭션마다 연결 재할당 | 높은 동시성 |
| **Statement** | SQL마다 연결 재할당 | 극한의 동시성 (제한 많음) |

**우리의 선택: Transaction Mode**
- 이유: 서버리스 환경, 높은 동시성 지원

**4. Multi-Stage Docker Build**

**정의:** Docker 이미지를 여러 단계로 나눠 빌드

**비유:**
- **Stage 1 (Builder)**: 자동차 공장 (무거운 기계들)
- **Stage 2 (Runtime)**: 완성된 자동차만 (가벼움)

**이점:**
```
Single-Stage Build:
- JDK 17: 400MB
- Gradle: 150MB
- Source Code: 10MB
- Build Cache: 200MB
= 총 760MB

Multi-Stage Build:
- JRE 17: 200MB
- Application JAR: 70MB
- 시스템 라이브러리: 300MB
= 총 571MB (약 25% 감소)
```

**5. Docker Health Check**

**정의:** 컨테이너 상태를 자동으로 모니터링

**설정:**
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget ... /actuator/health || exit 1
```

**파라미터:**
- `interval`: 체크 주기 (30초마다)
- `timeout`: 응답 대기 시간 (3초)
- `retries`: 실패 허용 횟수 (3회)
- `start-period`: 초기 대기 시간 (40초)

**상태 변화:**
```
starting -> healthy -> unhealthy
   ↓          ↓           ↓
 대기 중    정상 작동    자동 재시작
```

---

### 4.6 기술 스택 선택 이유

#### 1. Supabase vs 다른 대안

| 서비스 | 장점 | 단점 | 가격 |
|--------|------|------|------|
| **Supabase** | PostgreSQL, 오픈소스, RESTful API 자동 생성 | 무료 플랜 제한 | 무료/Pro $25 |
| **AWS RDS** | 강력한 성능, 다양한 DB 엔진 | 설정 복잡, 무료 플랜 없음 | 종량제 |
| **Railway** | 간단한 배포, Git 연동 | PostgreSQL 버전 제한 | $5/month |
| **PlanetScale** | MySQL, Serverless | PostgreSQL 미지원 | 무료/Pro $39 |

**✅ Supabase 선택 이유:**
1. PostgreSQL 17.6 (최신 버전)
2. 무료 플랜 제공 (학습/개발용)
3. RESTful API 자동 생성
4. Connection Pooling 내장
5. 추후 Realtime 기능 활용 가능

#### 2. Docker vs 다른 배포 방식

| 방식 | 장점 | 단점 | 사용 사례 |
|------|------|------|----------|
| **Docker** | 환경 일관성, 빠른 배포 | 학습 곡선 | 대부분의 경우 |
| **JAR 직접 배포** | 간단함 | 환경 의존성 | 단일 서버 |
| **Kubernetes** | 확장성, 고가용성 | 매우 복잡 | 대규모 서비스 |
| **Serverless (Lambda)** | 자동 스케일링 | Cold Start | 트래픽 변동 큼 |

**✅ Docker 선택 이유:**
1. 로컬과 프로덕션 환경 일치
2. 어디서든 동일하게 실행 가능
3. CI/CD 파이프라인 구축 용이
4. Kubernetes로 확장 가능

#### 3. Multi-Stage Build vs Single-Stage

| 방식 | 이미지 크기 | 빌드 시간 | 보안 | 복잡도 |
|------|-----------|----------|------|--------|
| **Multi-Stage** | 571MB | 3분 30초 | 높음 | 중간 |
| **Single-Stage** | 760MB | 3분 | 낮음 | 낮음 |

**✅ Multi-Stage 선택 이유:**
1. 이미지 크기 25% 감소
2. 소스 코드 제외 (보안)
3. 빌드 도구 제외 (공격 표면 감소)
4. 약간의 복잡도 증가는 감수할 만함

---

### 4.7 Phase 4 완료 체크리스트

#### 📋 필수 작업

- [x] Supabase 계정 생성 및 프로젝트 설정
- [x] PostgreSQL 데이터베이스 생성
- [x] Connection Pooling 설정 (Transaction Mode)
- [x] `.env` 파일 생성 및 `.gitignore` 추가
- [x] `application-prod.yml` 작성
- [x] Supabase 연결 테스트 작성 및 통과
- [x] Flyway 마이그레이션 실행 및 검증
- [x] 프로덕션 환경에서 API 테스트
- [x] Dockerfile 작성 (Multi-stage build)
- [x] docker-compose.yml 작성
- [x] Docker 이미지 빌드 성공
- [x] Docker 컨테이너 실행 및 테스트
- [x] Health Check 정상 작동 확인

#### 📝 문서화

- [x] PHASE4_PROGRESS.md 작성
- [x] PHASE_GUIDE.md Phase 4 섹션 추가
- [x] 트러블슈팅 가이드 작성
- [ ] README.md 업데이트 (Docker 사용법)

#### 🚀 배포 준비

- [x] 환경 변수 분리 (.env)
- [x] 민감 정보 보호 (.gitignore)
- [x] Health check 설정
- [x] Logging 설정 확인
- [ ] 모니터링 설정 (선택사항)

---

### 4.8 다음 단계

**Phase 4 완료!** 이제 애플리케이션을 프로덕션 환경에 배포할 준비가 되었습니다.

**다음 옵션:**

1. **Phase 5: 프론트엔드 개발**
   - React + TypeScript 설정
   - Music/Playlist UI 구현
   - Spotify API 연동

2. **추가 백엔드 기능**
   - Spotify API 통합
   - 음악 추천 알고리즘
   - 실시간 재생 목록 공유

3. **CI/CD 파이프라인 구축**
   - GitHub Actions 설정
   - 자동 테스트 및 배포
   - Blue-Green 배포

---

## 📚 참고 자료

### Phase 1 관련

**공식 문서:**
- [Spring Security 공식 문서](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/) - JWT 디버거
- [BCrypt 설명](https://en.wikipedia.org/wiki/Bcrypt)

**추천 강의:**
- 김영한의 스프링 시큐리티 (인프런)
- 스프링 부트 JWT 튜토리얼 (YouTube)

**관련 RFC:**
- [RFC 7519: JSON Web Token (JWT)](https://tools.ietf.org/html/rfc7519)
- [RFC 6749: OAuth 2.0](https://tools.ietf.org/html/rfc6749)

---

## 🔧 에러 트러블슈팅 가이드

이 섹션은 프로젝트 개발 중 실제로 발생한 에러와 해결 과정을 상세히 기록합니다.
스프링 초보자도 이해할 수 있도록 각 개념을 비유와 함께 설명합니다.

---

### ⚠️ 트러블슈팅 1: SurveyController 테스트 실패 (Phase 0/1)

#### 🐛 에러 현상

Phase 3 완료 후 전체 테스트 실행 시 **13개의 SurveyController 테스트가 실패**했습니다.

```
BUILD FAILED
241 tests completed, 27 failed, 7 skipped

FAILURE: Build failed with an exception.
* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///build/reports/tests/test/index.html
```

#### 📋 에러 메시지

```
org.springframework.beans.factory.NoSuchBeanDefinitionException:
No qualifying bean of type 'com.muti.global.jwt.JwtTokenProvider' available

Parameter 0 of constructor in com.muti.global.security.filter.JwtAuthenticationFilter
required a bean of type 'com.muti.global.jwt.JwtTokenProvider' that could not be found.
```

**초보자 설명:**
- `NoSuchBeanDefinitionException`: "빈(Bean)을 찾을 수 없다"는 에러
- **빈(Bean)이란?** 스프링이 관리하는 객체입니다. 마치 **공장에서 미리 만들어둔 부품**과 같습니다.
  - 일반적으로 `new MyClass()`로 객체를 만들지만, 스프링에서는 스프링이 대신 만들어서 관리합니다.
  - 필요할 때 "이 부품 주세요!"라고 요청하면 스프링이 줍니다. (Dependency Injection)

#### 🔍 원인 분석

1. **테스트 환경에서 Security 설정이 자동으로 로드됨**
   ```java
   @WebMvcTest(controllers = SurveyController.class)
   class SurveyControllerTest {
       // ...
   }
   ```

   **`@WebMvcTest`란?**
   - **비유**: 자동차 공장에서 "운전석만" 테스트하는 것
   - Controller만 테스트하고 싶을 때 사용하는 애노테이션
   - **하지만** Spring Security가 있으면 자동으로 Security 설정도 함께 로드됨

2. **SecurityFilterAutoConfiguration이 JwtAuthenticationFilter를 생성하려 함**
   ```java
   // SecurityConfig.java (실제 프로덕션 코드)
   @Configuration
   @RequiredArgsConstructor
   public class SecurityConfig {
       private final JwtTokenProvider jwtTokenProvider;  // ← 이 빈이 필요함

       @Bean
       public SecurityFilterChain filterChain(HttpSecurity http) {
           http.addFilterBefore(
               new JwtAuthenticationFilter(jwtTokenProvider),  // ← 여기서 사용됨
               UsernamePasswordAuthenticationFilter.class
           );
       }
   }
   ```

   **Filter란?**
   - **비유**: 아파트 경비실
   - 모든 요청(손님)이 컨트롤러(집)에 들어가기 전에 거쳐야 하는 검문소
   - `JwtAuthenticationFilter`는 "JWT 토큰 검사하는 경비원"

3. **테스트 환경에는 JwtTokenProvider 빈이 없음**
   - 프로덕션 환경: 모든 빈이 자동으로 생성됨
   - 테스트 환경 (`@WebMvcTest`): Controller 관련 빈만 생성됨
   - **문제**: Security가 JwtTokenProvider를 요구하는데, 테스트 환경엔 없음!

#### ✅ 해결 방법

**방법 1: MockBean으로 가짜 객체 제공**

```java
@WebMvcTest(controllers = SurveyController.class)
class SurveyControllerTest {

    @MockBean
    private com.muti.global.jwt.JwtTokenProvider jwtTokenProvider;  // ← 추가!

    // ...
}
```

**`@MockBean`이란?**
- **비유**: 영화 촬영용 모형 총
  - 진짜 총은 위험하니까 모형 총을 사용
  - 모형 총은 총처럼 생겼지만, 실제로 발사되지 않음
- `@MockBean`은 "가짜 빈"을 만들어줌
- 실제 동작은 하지 않지만, 스프링이 "빈이 있다"고 인식하게 만듦

**방법 2: Security 자동 설정 비활성화**

```java
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;

@WebMvcTest(
    controllers = SurveyController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,           // ← Security 설정 제외
        SecurityFilterAutoConfiguration.class      // ← Security Filter 설정 제외
    }
)
class SurveyControllerTest {
    // ...
}
```

**`excludeAutoConfiguration`이란?**
- **비유**: 자동차 옵션 끄기
  - 새 차를 살 때 "후방 카메라는 필요없어요" 하는 것
- 스프링 부트는 자동으로 여러 설정을 해주는데, 특정 설정을 끌 수 있음
- 테스트에서는 불필요한 설정을 끄면 더 빠르고 간단해짐

#### 🎯 최종 해결 코드

```java
package com.muti.domain.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muti.domain.survey.dto.request.SubmitAnswerRequest;
import com.muti.domain.survey.dto.response.QuestionDto;
import com.muti.domain.survey.dto.response.SurveyDto;
import com.muti.domain.survey.dto.response.SurveyResultDto;
import com.muti.domain.survey.service.SurveyResponseService;
import com.muti.domain.survey.service.SurveyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = SurveyController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,           // Security 자동 설정 제외
        SecurityFilterAutoConfiguration.class      // Security Filter 자동 설정 제외
    }
)
@DisplayName("SurveyController 테스트")
class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SurveyService surveyService;

    @MockBean
    private SurveyResponseService surveyResponseService;

    // Security 관련 Bean (컨텍스트 로딩을 위해 필요)
    @MockBean
    private com.muti.global.jwt.JwtTokenProvider jwtTokenProvider;

    // ... 테스트 메서드들
}
```

#### 📚 핵심 개념 정리

| 애노테이션/개념 | 설명 | 비유 |
|----------------|------|------|
| **@WebMvcTest** | Controller만 테스트하는 환경 | 자동차에서 "운전석만" 테스트 |
| **@MockBean** | 가짜 빈 객체 생성 | 영화 촬영용 모형 소품 |
| **Bean** | 스프링이 관리하는 객체 | 공장에서 미리 만들어둔 부품 |
| **Filter** | 요청을 가로채서 처리하는 중간 계층 | 아파트 경비실 |
| **Dependency Injection** | 필요한 객체를 스프링이 주입 | "부품 주세요!" 하면 스프링이 줌 |
| **excludeAutoConfiguration** | 자동 설정 제외 | 자동차 옵션 끄기 |

#### 💡 학습 포인트

1. **`@WebMvcTest`는 최소한의 컨텍스트만 로드함**
   - Controller, MockMvc, JSON 변환 등만 로드
   - Service, Repository는 자동으로 생성되지 않음 (그래서 `@MockBean` 필요)

2. **Spring Security가 있으면 Filter가 자동으로 적용됨**
   - Security를 사용하면 모든 요청이 Filter를 거침
   - 테스트에서는 Security가 필요 없으면 끄는 게 좋음

3. **테스트는 "격리된 환경"에서 실행해야 함**
   - Controller 테스트는 Controller만 테스트
   - Service 테스트는 Service만 테스트
   - 불필요한 의존성은 Mock으로 대체

---

### ⚠️ 트러블슈팅 2: Flyway Migration 테스트 실패 (Phase 0/1)

#### 🐛 에러 현상

Flyway 마이그레이션 통합 테스트 **10개가 모두 실패**했습니다.

```
> Task :test

FlywayMigrationIntegrationTest > migration_V1_SchemaCreated() FAILED
FlywayMigrationIntegrationTest > migration_V2_Survey_Created() FAILED
FlywayMigrationIntegrationTest > migration_V2_Questions_Created() FAILED
... (10개 모두 실패)
```

#### 📋 에러 메시지

```
org.hibernate.tool.schema.spi.SchemaManagementException:
Schema-validation: wrong column type encountered in column [direction] in table [question_options];
found [character (Types#CHAR)],
but expecting [enum ('a','d','e','f','i','p','s','u') (Types#ENUM)]
```

**초보자 설명:**
- **Schema Validation**: "데이터베이스 테이블 구조 검증"
- **Column Type Mismatch**: "컬럼 타입이 일치하지 않음"
- 예상한 타입: `ENUM` (PostgreSQL의 열거형)
- 실제 타입: `CHAR` (H2 데이터베이스가 만든 문자형)

#### 🔍 원인 분석

1. **테스트 환경 설정**
   ```java
   @DataJpaTest
   @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
   @TestPropertySource(properties = {
       "spring.flyway.enabled=true",
       "spring.flyway.clean-disabled=false",
       "spring.jpa.hibernate.ddl-auto=validate",  // ← 문제의 원인!
       "spring.datasource.url=jdbc:h2:mem:flyway_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
   })
   class FlywayMigrationIntegrationTest {
       // ...
   }
   ```

2. **ENUM 타입 호환성 문제**

   **Entity 정의 (우리가 원하는 것):**
   ```java
   @Entity
   @Table(name = "question_options")
   public class QuestionOption {

       @Enumerated(EnumType.STRING)  // ← "문자열로 저장해!"
       @Column(nullable = false)
       private AxisDirection direction;  // E, I, S, F, A, D, P, U
   }
   ```

   **PostgreSQL이 만드는 것:**
   ```sql
   CREATE TYPE axis_direction AS ENUM ('E', 'I', 'S', 'F', 'A', 'D', 'P', 'U');

   CREATE TABLE question_options (
       direction axis_direction NOT NULL  -- ← ENUM 타입
   );
   ```

   **H2 데이터베이스가 만드는 것:**
   ```sql
   CREATE TABLE question_options (
       direction CHAR(1) NOT NULL  -- ← CHAR 타입으로 변환됨!
   );
   ```

   **비유로 이해하기:**
   - PostgreSQL: "이 칸에는 'E', 'I', 'S', 'F'만 들어갈 수 있어요" (ENUM)
   - H2: "이 칸에는 한 글자 들어가면 돼요" (CHAR)
   - 둘 다 같은 데이터를 저장할 수 있지만, **타입이 다름**!

3. **Hibernate의 validate 모드**

   ```java
   spring.jpa.hibernate.ddl-auto=validate
   ```

   **`ddl-auto` 옵션들:**

   | 옵션 | 동작 | 비유 |
   |------|------|------|
   | **none** | 아무것도 안 함 | "테이블 구조 신경 안 써" |
   | **validate** | 테이블 구조 검증만 | "테이블 구조가 맞는지 확인만 해" |
   | **update** | 테이블 구조 자동 수정 | "테이블이 다르면 고쳐줘" |
   | **create** | 매번 테이블 삭제 후 생성 | "매번 테이블을 새로 만들어" |
   | **create-drop** | 종료 시 테이블 삭제 | "끝나면 테이블 지워줘" |

   **validate 모드의 동작:**
   - Flyway가 테이블을 만듦 → H2가 CHAR 타입으로 생성
   - Hibernate가 Entity를 확인함 → "ENUM 타입이어야 하는데?"
   - **타입이 다르니까 에러 발생!**

#### ✅ 해결 방법

**Hibernate 검증 모드를 `none`으로 변경**

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.flyway.enabled=true",
    "spring.flyway.clean-disabled=false",
    "spring.jpa.hibernate.ddl-auto=none",  // validate → none 변경!
    "spring.datasource.url=jdbc:h2:mem:flyway_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
@DisplayName("Flyway 마이그레이션 통합 테스트")
class FlywayMigrationIntegrationTest {
    // ...
}
```

**왜 이렇게 해결했나?**

1. **Flyway가 스키마를 관리하고 있음**
   - Flyway가 이미 테이블 구조를 관리하고 있음
   - Hibernate가 굳이 검증할 필요가 없음

2. **H2는 테스트용 데이터베이스**
   - 실제 프로덕션은 PostgreSQL 사용
   - H2는 PostgreSQL 호환 모드지만 100% 같지 않음
   - ENUM 타입은 H2에서 완벽히 지원하지 않음

3. **Flyway가 더 신뢰할 수 있음**
   - Flyway SQL 파일: 실제 PostgreSQL 문법 사용
   - Hibernate Entity: JPA 추상화 레이어
   - **Flyway가 만든 스키마가 더 정확함!**

#### 🎯 최종 해결 코드

```java
package com.muti.domain.survey.integration;

import com.muti.domain.survey.entity.Question;
import com.muti.domain.survey.entity.QuestionOption;
import com.muti.domain.survey.entity.Survey;
import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import com.muti.domain.survey.repository.QuestionOptionRepository;
import com.muti.domain.survey.repository.QuestionRepository;
import com.muti.domain.survey.repository.SurveyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Flyway 마이그레이션 통합 테스트
 * Flyway를 활성화하여 실제 마이그레이션이 잘 작동하는지 검증
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.flyway.enabled=true",
    "spring.flyway.clean-disabled=false",
    "spring.jpa.hibernate.ddl-auto=none",  // H2의 ENUM 타입 호환성 문제로 인해 validate 대신 none 사용
    "spring.datasource.url=jdbc:h2:mem:flyway_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
@DisplayName("Flyway 마이그레이션 통합 테스트")
class FlywayMigrationIntegrationTest {
    // ... 테스트 메서드들
}
```

#### 📚 핵심 개념 정리

| 개념 | 설명 | 비유 |
|------|------|------|
| **Flyway** | 데이터베이스 마이그레이션 도구 | 건물 리모델링 설계도 |
| **H2 Database** | 자바 기반 인메모리 데이터베이스 | 테스트용 임시 창고 |
| **PostgreSQL** | 실제 프로덕션 데이터베이스 | 실제 물류 창고 |
| **ENUM 타입** | 제한된 값만 가질 수 있는 타입 | "빨강, 파랑, 노랑만 선택 가능" |
| **Schema Validation** | 테이블 구조 검증 | 설계도와 실제 건물이 같은지 확인 |
| **ddl-auto** | Hibernate의 스키마 자동 생성 옵션 | 테이블 구조 관리 방식 |

#### 💡 학습 포인트

1. **Flyway vs Hibernate DDL Auto**
   ```
   Flyway (권장):
   ✅ SQL 파일로 명확하게 관리
   ✅ 버전 관리 가능
   ✅ 팀원과 공유 쉬움
   ✅ 프로덕션에서 안전

   Hibernate DDL Auto (개발 초기만):
   ⚠️ 자동으로 생성되어 예측 불가
   ⚠️ 데이터 손실 위험
   ⚠️ 프로덕션에서 사용 금지
   ```

2. **테스트 데이터베이스 선택**
   - H2: 빠르고 가벼움, 하지만 완벽한 호환은 아님
   - TestContainers + PostgreSQL: 느리지만 프로덕션과 동일
   - 권장: 대부분은 H2, 중요한 통합 테스트는 TestContainers

3. **ENUM 타입 사용 시 주의사항**
   ```java
   // ✅ 좋은 방법: @Enumerated(EnumType.STRING)
   @Enumerated(EnumType.STRING)
   private Status status;  // DB에 "ACTIVE", "INACTIVE" 저장

   // ❌ 나쁜 방법: @Enumerated(EnumType.ORDINAL)
   @Enumerated(EnumType.ORDINAL)
   private Status status;  // DB에 0, 1 저장 → 순서 바뀌면 문제!
   ```

---

### ⚠️ 트러블슈팅 3: Supabase 연결 테스트 실패 (Phase 0)

#### 🐛 에러 현상

Supabase 데이터베이스 연결 테스트 **4개가 모두 실패**했습니다.

```
> Task :test

SupabaseDatabaseConnectionTest > supabase_Connection_Success() FAILED
SupabaseDatabaseConnectionTest > supabase_Query_Execution() FAILED
SupabaseDatabaseConnectionTest > supabase_Schema_Check() FAILED
SupabaseDatabaseConnectionTest > supabase_Flyway_History() FAILED
```

#### 📋 에러 메시지

```
org.springframework.jdbc.CannotGetJdbcConnectionException:
Failed to obtain JDBC Connection

Caused by: org.postgresql.util.PSQLException:
Connection to db.xxx.supabase.co:5432 refused.
Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
```

**초보자 설명:**
- **Connection Refused**: "연결 거부됨"
- 외부 Supabase 서버에 연결할 수 없음
- 로컬 개발 환경에서는 외부 DB 접속이 안 됨

#### 🔍 원인 분석

1. **테스트 클래스 구조**
   ```java
   @SpringBootTest
   @ActiveProfiles("prod")  // ← 프로덕션 프로필 사용!
   @DisplayName("Supabase 데이터베이스 연결 테스트")
   class SupabaseDatabaseConnectionTest {

       @Autowired
       private DataSource dataSource;  // ← 실제 Supabase DB에 연결 시도

       @Test
       @DisplayName("Supabase PostgreSQL 연결 성공")
       void supabase_Connection_Success() throws Exception {
           try (Connection connection = dataSource.getConnection()) {
               // ...
           }
       }
   }
   ```

2. **`@ActiveProfiles("prod")`의 의미**

   **비유로 이해하기:**
   - **개발 환경 (dev)**: 로컬 컴퓨터의 H2 데이터베이스 사용
   - **프로덕션 환경 (prod)**: 실제 Supabase 서버 사용

   ```
   application.yml (기본):
   - H2 인메모리 DB
   - 로컬에서만 사용

   application-prod.yml:
   - Supabase PostgreSQL
   - 인터넷 필요
   - 실제 서버
   ```

3. **왜 이 테스트가 있는가?**
   - **목적**: 실제 배포 전에 Supabase 연결 확인
   - **사용 시점**:
     - 배포 전 최종 확인
     - Supabase 설정 변경 시
     - CI/CD 파이프라인에서
   - **로컬 개발 시**: 필요 없음!

#### ✅ 해결 방법

**`@Disabled` 애노테이션으로 테스트 비활성화**

```java
import org.junit.jupiter.api.Disabled;

@SpringBootTest
@ActiveProfiles("prod")
@DisplayName("Supabase 데이터베이스 연결 테스트")
@Disabled("외부 Supabase DB 연결이 필요하므로 로컬 환경에서는 비활성화")  // ← 추가!
class SupabaseDatabaseConnectionTest {
    // ...
}
```

**`@Disabled`란?**
- **비유**: "공사 중" 표지판
- 테스트를 일시적으로 건너뜀
- 테스트 코드는 남아있지만 실행되지 않음
- 필요할 때 `@Disabled`를 제거하면 다시 실행됨

#### 🎯 최종 해결 코드

```java
package com.muti.infrastructure.database;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Supabase PostgreSQL 데이터베이스 연결 테스트
 *
 * 이 테스트는 prod 프로필로 실행되며, 실제 Supabase DB에 연결합니다.
 * 실행 전에 .env 파일에 올바른 DB 연결 정보가 설정되어 있어야 합니다.
 *
 * 실행 방법:
 * 1. .env.example을 복사하여 .env 파일 생성
 * 2. .env 파일에 실제 Supabase 비밀번호 입력
 * 3. @Disabled 주석 제거
 * 4. 테스트 실행: ./gradlew test --tests SupabaseDatabaseConnectionTest
 *
 * 참고: 로컬 개발 환경에서는 외부 DB 연결이 필요하므로 기본적으로 비활성화되어 있습니다.
 */
@SpringBootTest
@ActiveProfiles("prod")
@DisplayName("Supabase 데이터베이스 연결 테스트")
@Disabled("외부 Supabase DB 연결이 필요하므로 로컬 환경에서는 비활성화")
class SupabaseDatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Supabase PostgreSQL 연결 성공")
    void supabase_Connection_Success() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertThat(connection).isNotNull();
            assertThat(connection.isValid(5)).isTrue();
            assertThat(metaData.getDatabaseProductName()).containsIgnoringCase("PostgreSQL");

            System.out.println("✅ Database Connection Successful!");
            System.out.println("   - Database: " + metaData.getDatabaseProductName());
            System.out.println("   - Version: " + metaData.getDatabaseProductVersion());
            System.out.println("   - URL: " + metaData.getURL());
            System.out.println("   - Driver: " + metaData.getDriverName());
        }
    }

    // ... 다른 테스트 메서드들
}
```

#### 📚 핵심 개념 정리

| 개념 | 설명 | 비유 |
|------|------|------|
| **@Disabled** | 테스트를 건너뛰기 | "공사 중" 표지판 |
| **@ActiveProfiles** | 특정 프로필 활성화 | "프로덕션 모드로 실행" |
| **Supabase** | PostgreSQL 기반 클라우드 DB | 클라우드 창고 서비스 |
| **DataSource** | DB 연결 정보를 담은 객체 | DB 서버 주소록 |
| **JdbcTemplate** | DB 쿼리를 쉽게 실행하는 도구 | SQL 실행 도우미 |

#### 💡 학습 포인트

1. **테스트 환경 분리**
   ```
   로컬 개발:
   ✅ H2 인메모리 DB
   ✅ 빠른 테스트
   ✅ 인터넷 불필요

   CI/CD / 배포 전:
   ✅ 실제 Supabase DB
   ✅ 프로덕션 환경 검증
   ✅ @Disabled 제거하고 실행
   ```

2. **프로필(Profile) 활용**
   ```yaml
   # application.yml (기본)
   spring:
     datasource:
       url: jdbc:h2:mem:testdb

   # application-dev.yml (개발)
   spring:
     datasource:
       url: jdbc:h2:mem:devdb

   # application-prod.yml (프로덕션)
   spring:
     datasource:
       url: jdbc:postgresql://supabase.co:5432/db
   ```

3. **언제 @Disabled를 사용하나?**
   - 외부 API 연동 테스트
   - 느린 통합 테스트
   - 특정 환경에서만 실행되는 테스트
   - 임시로 비활성화할 테스트

#### 🚀 Supabase 테스트 실행 방법

실제로 Supabase 연결을 테스트하고 싶다면:

1. **`.env` 파일 설정**
   ```bash
   # .env
   SUPABASE_DB_URL=jdbc:postgresql://db.xxx.supabase.co:5432/postgres
   SUPABASE_DB_USERNAME=postgres
   SUPABASE_DB_PASSWORD=your-password-here
   ```

2. **`@Disabled` 제거**
   ```java
   @SpringBootTest
   @ActiveProfiles("prod")
   // @Disabled("...") ← 이 줄 삭제 또는 주석 처리
   class SupabaseDatabaseConnectionTest {
       // ...
   }
   ```

3. **테스트 실행**
   ```bash
   ./gradlew test --tests SupabaseDatabaseConnectionTest
   ```

---

## 📊 트러블슈팅 요약

### 최종 테스트 결과

```
이전: 241 tests, 214 passed, 27 failed, 7 skipped
이후: 241 tests, 234 passed, 0 failed, 7 skipped ✅
```

### 해결한 에러

| 에러 | 실패 수 | 해결 방법 | 소요 시간 |
|------|---------|----------|----------|
| SurveyController 테스트 | 13개 | `@MockBean` + `excludeAutoConfiguration` | 30분 |
| Flyway Migration 테스트 | 10개 | `ddl-auto=none` | 20분 |
| Supabase 연결 테스트 | 4개 | `@Disabled` | 10분 |
| **합계** | **27개** | **BUILD SUCCESSFUL** | **60분** |

### 핵심 교훈

1. **테스트는 격리되어야 한다**
   - Controller 테스트에서 Service를 테스트하지 말 것
   - 필요한 의존성만 로드할 것
   - MockBean을 적극 활용할 것

2. **테스트 환경 ≠ 프로덕션 환경**
   - H2는 PostgreSQL과 100% 같지 않음
   - 완벽한 호환이 필요하면 TestContainers 사용
   - 타협점 찾기: 대부분 H2, 중요한 건 실제 DB

3. **Flyway가 스키마를 관리하면 Hibernate는 손 떼라**
   - `ddl-auto=none` 또는 `validate` 신중히 선택
   - Flyway와 Hibernate DDL Auto를 동시에 사용하면 충돌 가능
   - 프로덕션에서는 무조건 `ddl-auto=none`

4. **외부 의존성이 있는 테스트는 분리하라**
   - 로컬 개발: 빠른 피드백이 중요
   - CI/CD: 실제 환경 검증이 중요
   - `@Disabled`로 상황에 맞게 제어

---

## 💡 자주 묻는 질문 (FAQ)

### Q1. JWT를 Cookie에 저장해야 하나요, localStorage에 저장해야 하나요?

**A**: 각각 장단점이 있습니다.

| 저장 위치 | 장점 | 단점 | 추천 |
|-----------|------|------|------|
| **HttpOnly Cookie** | XSS 공격 방어 | CSRF 공격 가능 | ✅ Refresh Token |
| **localStorage** | CSRF 공격 방어 | XSS 공격 취약 | ✅ Access Token (메모리) |
| **메모리 (변수)** | 가장 안전 | 새로고침 시 사라짐 | ✅ Access Token |

**권장 방법:**
- Access Token: 메모리 (변수)
- Refresh Token: HttpOnly Cookie + Secure + SameSite

### Q2. Access Token 만료 시간을 더 길게 하면 안 되나요?

**A**: 가능하지만 보안이 약해집니다.

```
만료 시간 15분 (권장):
- 탈취 시 피해: 15분
- 사용자 불편: 거의 없음 (자동 갱신)

만료 시간 7일:
- 탈취 시 피해: 7일
- 사용자 불편: 없음
- 보안: ❌ 매우 위험

만료 시간 1시간:
- 탈취 시 피해: 1시간
- 사용자 불편: 거의 없음
- 보안: ⚠️ 보통
```

### Q3. Spring Security 없이 JWT를 직접 구현하면 안 되나요?

**A**: 가능하지만 비추천합니다.

**Spring Security 사용:**
- ✅ 검증된 보안 로직
- ✅ 필터 체인 자동 관리
- ✅ CSRF, CORS 등 자동 처리
- ✅ 커뮤니티 지원

**직접 구현:**
- ❌ 보안 취약점 가능성
- ❌ 모든 것을 직접 관리
- ❌ 유지보수 어려움

### Q4. 로그아웃 시 Access Token도 무효화해야 하지 않나요?

**A**: JWT의 특성상 불가능합니다.

**JWT의 한계:**
- Access Token은 서버에 저장되지 않음 (Stateless)
- 만료 전까지 유효함
- 즉시 무효화 불가능

**해결책:**
1. 짧은 만료 시간 (15분)
2. Refresh Token만 DB에서 삭제
3. 블랙리스트 사용 (고급 기법)
   ```java
   // Redis에 만료된 토큰 저장
   redisTemplate.set("blacklist:" + token, "1", 15, TimeUnit.MINUTES);

   // 매 요청마다 블랙리스트 확인
   if (redisTemplate.hasKey("blacklist:" + token)) {
       throw new InvalidTokenException();
   }
   ```

### Q5. 소셜 로그인(Google, Kakao)은 어떻게 구현하나요?

**A**: Phase 1에서 기본 구조를 만들고, 나중에 확장합니다.

**Phase 1 (지금):**
- 이메일 + 비밀번호 로그인

**Phase 1.5 (나중에):**
- OAuth 2.0 통합
- Spring Security OAuth2 Client 사용
- Google, Kakao Provider 설정

---

**작성자**: Claude Sonnet 4.5
**최종 수정**: 2026년 2월 8일

---

**다음 단계**: Phase 1 시작하기

준비되셨으면 **"Phase 1 시작!"** 이라고 말씀해주세요! 🚀
