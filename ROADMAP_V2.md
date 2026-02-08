# MUTI v2.0 업그레이드 로드맵

> 🚀 MUTI를 기본 설문 서비스에서 엔터프라이즈급 음악 소셜 플랫폼으로 전환

**작성일**: 2026년 2월 8일
**목표 완료**: 2026년 6월 (15주)
**현재 버전**: v1.0.0
**목표 버전**: v2.0.0

---

## 📋 목차

1. [업그레이드 개요](#-업그레이드-개요)
2. [AS-IS vs TO-BE](#-as-is-vs-to-be)
3. [기술 스택 변경](#-기술-스택-변경)
4. [AWS 인프라 가이드](#-aws-인프라-가이드)
5. [새로운 기능](#-새로운-기능)
6. [전체 아키텍처](#-전체-아키텍처)
7. [단계별 실행 계획](#-단계별-실행-계획)
8. [예상 비용](#-예상-비용)

---

## 🎯 업그레이드 개요

### 비전

**"개인 프로젝트에서 포트폴리오용 엔터프라이즈 프로젝트로"**

MUTI v1.0은 기본 설문 기능을 갖춘 프로토타입입니다. v2.0에서는:
- ✅ **확장 가능한 인프라**: AWS + Docker + Kubernetes
- ✅ **완전한 기능**: 인증, 게시판, 음악 추천
- ✅ **모던 프론트엔드**: React + TypeScript
- ✅ **프로덕션 준비**: CI/CD, 모니터링, 로깅

### 주요 목표

1. **기술적 성장**: AWS, Docker, K8s, React 경험
2. **포트폴리오**: 취업용 프로젝트로 활용
3. **실전 경험**: 실제 사용자가 이용할 수 있는 서비스
4. **비용 효율**: 프리티어 최대 활용 ($0-5/월)

---

## 🔄 AS-IS vs TO-BE

### 인프라 비교

| 항목 | AS-IS (v1.0) | TO-BE (v2.0) | 변경 이유 |
|------|--------------|--------------|-----------|
| **백엔드 호스팅** | Railway | AWS EC2 | 완전한 제어, 학습 가치 |
| **프론트 호스팅** | Vercel | AWS S3 + CloudFront | 통합 관리, CDN |
| **데이터베이스** | Supabase | AWS RDS PostgreSQL | 데이터 주권, 통합 |
| **컨테이너** | 없음 | Docker + K8s | 환경 일관성, 확장성 |
| **도메인** | Railway 서브도메인 | 공식 도메인 (muti.com) | 브랜딩, 신뢰성 |
| **SSL** | 자동 제공 | Let's Encrypt | 무료 |
| **캐시** | 없음 | Redis | 성능 향상 |
| **로드밸런싱** | 없음 | AWS ALB | 고가용성 |
| **모니터링** | Railway 기본 | CloudWatch | 상세 메트릭 |

### 프론트엔드 비교

| 항목 | AS-IS (v1.0) | TO-BE (v2.0) | 변경 이유 |
|------|--------------|--------------|-----------|
| **프레임워크** | Vanilla JS | React 18 + TypeScript | 컴포넌트 재사용, 타입 안전성 |
| **상태 관리** | SessionStorage | Redux Toolkit | 중앙 집중식 |
| **라우팅** | 페이지 이동 | React Router | SPA |
| **UI 라이브러리** | 직접 CSS | Material-UI | 일관된 디자인 |
| **차트** | 없음 | Recharts | 데이터 시각화 |
| **API 통신** | Fetch | Axios + React Query | 캐싱, 에러 처리 |
| **빌드 도구** | 없음 | Vite | 빠른 개발, HMR |

### 백엔드 비교

| 항목 | AS-IS (v1.0) | TO-BE (v2.0) | 변경 이유 |
|------|--------------|--------------|-----------|
| **인증/인가** | 없음 | Spring Security + JWT | 사용자 관리 |
| **사용자 관리** | 없음 | User Entity + OAuth2 | 회원가입, 소셜 로그인 |
| **게시판** | 없음 | Board/Post/Comment | 커뮤니티 |
| **파일 업로드** | 없음 | AWS S3 | 프로필 이미지 |
| **음악 추천** | 없음 | Spotify API | 타입별 추천 |
| **캐싱** | 없음 | Redis | 세션, API 캐시 |
| **설문 방식** | 2지선다 | 리커트 5점 척도 | 정교한 분석 |

### 새로운 기능

| 기능 | 설명 | 우선순위 |
|------|------|----------|
| **로그인/회원가입** | 이메일 + 소셜 로그인 (Google, Kakao) | ⭐⭐⭐⭐⭐ |
| **게시판** | 자유게시판, 타입별 게시판 (16개) | ⭐⭐⭐⭐ |
| **리커트 척도** | 1-5점 척도 설문 (더 정교한 분석) | ⭐⭐⭐⭐⭐ |
| **결과 시각화** | 레이더 차트, 타입 분포 그래프 | ⭐⭐⭐⭐⭐ |
| **음악 추천** | Spotify API 기반 타입별 추천 | ⭐⭐⭐⭐ |
| **아티스트 추천** | 선호 아티스트 자동 발굴 | ⭐⭐⭐ |
| **결과 공유** | SNS 공유, 링크 생성 | ⭐⭐⭐ |
| **통계 페이지** | 전체 사용자 통계 | ⭐⭐ |
| **모바일 앱** | React Native (v2.1) | ⭐⭐ |

---

## 🛠 기술 스택 변경

### Backend

| 카테고리 | v1.0 | v2.0 | 변경 이유 |
|---------|------|------|-----------|
| **Language** | Java 17 | Java 17 | 유지 |
| **Framework** | Spring Boot 3.5.10 | Spring Boot 3.5.10 | 유지 |
| **Security** | 기본 설정 | **Spring Security + JWT** ✨ | 인증/인가 |
| **ORM** | JPA + Hibernate | JPA + Hibernate | 유지 |
| **Database** | H2 (local), PostgreSQL (prod) | PostgreSQL | 유지 |
| **Cache** | 없음 | **Redis** ✨ | 성능 |
| **Migration** | Flyway | Flyway | 유지 |
| **External API** | 없음 | **Spotify API** ✨ | 음악 추천 |
| **Testing** | JUnit 5, Mockito | JUnit 5, Mockito | 유지 |
| **Build** | Gradle 8.14.4 | Gradle 8.14.4 | 유지 |

### Frontend

| 카테고리 | v1.0 | v2.0 | 변경 이유 |
|---------|------|------|-----------|
| **Framework** | Vanilla JS | **React 18** ✨ | 컴포넌트 재사용 |
| **Language** | JavaScript | **TypeScript** ✨ | 타입 안전성 |
| **State** | SessionStorage | **Redux Toolkit** ✨ | 상태 관리 |
| **Router** | 없음 | **React Router 6** ✨ | SPA |
| **UI Library** | 직접 CSS | **Material-UI** ✨ | 디자인 시스템 |
| **Charts** | 없음 | **Recharts** ✨ | 시각화 |
| **HTTP Client** | Fetch | **Axios** ✨ | 인터셉터, 에러 처리 |
| **Server State** | 없음 | **React Query** ✨ | 캐싱 |
| **Form** | 직접 구현 | **React Hook Form** ✨ | 폼 관리 |
| **Build** | 없음 | **Vite** ✨ | 빠른 빌드 |

### Infrastructure

| 카테고리 | v1.0 | v2.0 | 변경 이유 |
|---------|------|------|-----------|
| **Container** | 없음 | **Docker** ✨ | 환경 일관성 |
| **Orchestration** | 없음 | **Kubernetes (k3s)** ✨ | 오케스트레이션 |
| **Cloud** | Railway, Vercel | **AWS (EC2, RDS, S3)** ✨ | 통합 관리 |
| **CI/CD** | Railway/Vercel 자동 | **GitHub Actions** ✨ | 커스터마이징 |
| **Monitoring** | Railway 기본 | **CloudWatch** ✨ | 상세 모니터링 |
| **DNS** | 없음 | **Route 53** ✨ | 도메인 관리 |
| **CDN** | Vercel | **CloudFront** ✨ | 글로벌 배포 |
| **Registry** | 없음 | **AWS ECR** ✨ | 이미지 저장 |

---

## ☁️ AWS 인프라 가이드

### 프리티어 활용 전략

#### 12개월 무료 서비스

| 서비스 | 무료 한도 | 초과 비용 | 예상 사용량 | 상태 |
|--------|-----------|-----------|-------------|------|
| **EC2** | t2.micro 750시간/월 | $0.0116/시간 | 720시간/월 (24/7) | ✅ 충분 |
| **RDS** | t2.micro 750시간/월 | $0.018/시간 | 720시간/월 (24/7) | ✅ 충분 |
| **S3** | 5GB 저장 | $0.023/GB | 2GB (이미지) | ✅ 충분 |
| **EBS** | 30GB SSD | $0.10/GB | 20GB | ✅ 충분 |
| **데이터 전송** | 15GB 아웃바운드 | $0.09/GB | 10GB/월 | ✅ 충분 |

#### 항상 무료 서비스

| 서비스 | 무료 한도 | 비고 |
|--------|-----------|------|
| **Lambda** | 100만 요청/월 | v3.0 서버리스 전환 시 |
| **CloudWatch** | 기본 메트릭 | 로그 1GB 무료 |
| **Certificate Manager** | 무제한 SSL 인증서 | HTTPS 무료 |

### 예상 월별 비용

#### 시나리오 1: 프리티어 기간 (첫 12개월)

```
EC2 t2.micro (750시간):          $0
RDS t2.micro (750시간):          $0
S3 (5GB):                        $0
EBS (30GB):                      $0
데이터 전송 (15GB):              $0
─────────────────────────────────
Route 53 (1개 호스팅 영역):      $0.50
추가 데이터 전송 (5GB):          $0.45
CloudFront:                      $0.85
─────────────────────────────────
월 합계:                         $1.80
연간 합계:                       $21.60
```

#### 시나리오 2: 프리티어 종료 후

```
EC2 t2.micro:                    $8.35
RDS t2.micro:                    $12.96
S3 (10GB):                       $0.23
EBS (30GB):                      $3.00
데이터 전송 (50GB):              $3.15
Route 53:                        $0.50
CloudFront:                      $0.85
─────────────────────────────────
월 합계:                         $29.04
연간 합계:                       $348.48
```

#### 비교: 다른 플랫폼

| 플랫폼 | 월 비용 | 연 비용 | 비고 |
|--------|---------|---------|------|
| **AWS (프리티어)** | $2 | $24 | ✅ 첫 12개월 |
| **AWS (유료)** | $29 | $348 | 프리티어 종료 후 |
| Railway + Vercel | $5-20 | $60-240 | 현재 사용 중 |
| Heroku | $25-50 | $300-600 | 비쌈 |
| DigitalOcean | $12-24 | $144-288 | 간단함 |

**결론**: AWS가 장기적으로 가장 경제적이고 학습 가치 높음!

### 비용 최적화 팁

1. **Reserved Instance**: 1년 약정 시 40% 할인
2. **Spot Instance**: 개발/테스트 서버로 사용 (70% 할인)
3. **S3 Glacier**: 오래된 백업은 저렴한 스토리지로 이동
4. **CloudFront 캐싱**: 데이터 전송비용 절감
5. **오토스케일링**: 야간 시간 인스턴스 축소

### AWS 아키텍처

```
┌────────────────────────────────────────────────────────────────┐
│                    인터넷 (Internet)                             │
└──────────────────────┬─────────────────────────────────────────┘
                       │
                       ▼
              ┌────────────────┐
              │  Route 53      │ ← DNS (도메인 → IP)
              │  (DNS 관리)     │
              └────────┬───────┘
                       │
                       ▼
              ┌────────────────┐
              │  CloudFront    │ ← CDN (정적 파일 캐싱)
              │  (CDN)         │
              └────────┬───────┘
                       │
         ┌─────────────┴─────────────┐
         │                           │
         ▼                           ▼
┌────────────────┐          ┌────────────────┐
│  S3 (Frontend) │          │  ALB           │ ← 로드밸런서
│  React Build   │          │  (로드밸런싱)   │
└────────────────┘          └────────┬───────┘
                                     │
                    ┌────────────────┼────────────────┐
                    │                │                │
                    ▼                ▼                ▼
           ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
           │   EC2 #1    │  │   EC2 #2    │  │   EC2 #3    │
           │  (K8s Node) │  │  (K8s Node) │  │  (K8s Node) │
           │             │  │             │  │             │
           │ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │
           │ │K8s Pod 1│ │  │ │K8s Pod 2│ │  │ │K8s Pod 3│ │
           │ │ Backend │ │  │ │ Backend │ │  │ │ Backend │ │
           │ └─────────┘ │  │ └─────────┘ │  │ └─────────┘ │
           └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
                  │                │                │
                  └────────────────┼────────────────┘
                                   │
                    ┌──────────────┴──────────────┐
                    │                             │
                    ▼                             ▼
          ┌──────────────────┐         ┌──────────────────┐
          │  RDS (PostgreSQL)│         │  ElastiCache     │
          │                  │         │  (Redis)         │
          │  Multi-AZ        │         │                  │
          │  자동 백업       │         │  세션 캐시       │
          └──────────────────┘         └──────────────────┘
```

### AWS 서비스별 역할

| 서비스 | 역할 | 사용 이유 |
|--------|------|-----------|
| **Route 53** | DNS 관리 | muti.com → IP 주소 변환 |
| **CloudFront** | CDN | 전 세계 빠른 접속, 정적 파일 캐싱 |
| **S3** | 스토리지 | React 빌드 파일, 사용자 업로드 이미지 |
| **ALB** | 로드밸런서 | 트래픽 분산, SSL 종료, 헬스 체크 |
| **EC2** | 가상 서버 | 백엔드 애플리케이션 실행 |
| **RDS** | 관리형 DB | PostgreSQL 자동 백업, 패치 |
| **ElastiCache** | 관리형 Redis | 세션 저장, API 캐싱 |
| **CloudWatch** | 모니터링 | 로그, 메트릭, 알람 |
| **ECR** | 컨테이너 레지스트리 | Docker 이미지 저장 |

---

## 🎵 새로운 기능

### 1. 로그인/회원가입 시스템

#### 데이터베이스 스키마

```sql
-- users 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- BCrypt 암호화
    username VARCHAR(50) UNIQUE NOT NULL,
    profile_image_url VARCHAR(500),
    role VARCHAR(20) DEFAULT 'USER',
    provider VARCHAR(20),  -- LOCAL, GOOGLE, KAKAO
    provider_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- refresh_tokens 테이블
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### API 엔드포인트

```http
POST   /api/v1/auth/signup          # 회원가입
POST   /api/v1/auth/login           # 로그인
POST   /api/v1/auth/logout          # 로그아웃
POST   /api/v1/auth/refresh         # 토큰 갱신
GET    /api/v1/auth/me              # 내 정보 조회
PUT    /api/v1/auth/me              # 내 정보 수정

# 소셜 로그인
GET    /api/v1/auth/oauth2/google   # Google 로그인
GET    /api/v1/auth/oauth2/kakao    # Kakao 로그인
```

#### JWT 인증 플로우

```
1. 사용자 로그인 요청
   POST /api/v1/auth/login
   Body: { email, password }

2. 서버 검증
   - DB에서 사용자 조회
   - BCrypt로 비밀번호 확인

3. JWT 발급
   - Access Token (15분)
   - Refresh Token (7일)

4. 이후 API 요청
   Header: Authorization: Bearer <access-token>

5. 토큰 만료 시
   POST /api/v1/auth/refresh
   Body: { refreshToken }
```

### 2. CRUD 게시판

#### 데이터베이스 스키마

```sql
-- boards 테이블 (게시판 종류)
CREATE TABLE boards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    board_type VARCHAR(20),  -- FREE, MUTI_TYPE
    muti_type VARCHAR(4),    -- ESAP, IFDU 등
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- posts 테이블
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    board_id BIGINT REFERENCES boards(id),
    user_id BIGINT REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- comments 테이블
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES posts(id),
    user_id BIGINT REFERENCES users(id),
    parent_comment_id BIGINT REFERENCES comments(id),
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### API 엔드포인트

```http
# 게시판
GET    /api/v1/boards                    # 게시판 목록
GET    /api/v1/boards/{id}/posts         # 게시글 목록 (페이징)

# 게시글
POST   /api/v1/posts                     # 게시글 작성
GET    /api/v1/posts/{id}                # 게시글 조회
PUT    /api/v1/posts/{id}                # 게시글 수정
DELETE /api/v1/posts/{id}                # 게시글 삭제
POST   /api/v1/posts/{id}/like           # 좋아요

# 댓글
POST   /api/v1/posts/{id}/comments       # 댓글 작성
GET    /api/v1/posts/{id}/comments       # 댓글 목록
```

### 3. 리커트 5점 척도 설문

#### 기존 vs 새로운

**기존 (v1.0) - 2지선다:**
```
질문: "어떤 음악을 선호하나요?"
□ 빠른 음악 (E, +5점)
□ 느린 음악 (I, -5점)
```

**새로운 (v2.0) - 리커트 5점:**
```
질문: "빠른 음악을 얼마나 좋아하나요?"
○ 매우 그렇다 (E, +5점)
○ 그렇다 (E, +3점)
○ 보통이다 (중립, 0점)
○ 아니다 (I, -3점)
○ 전혀 아니다 (I, -5점)
```

#### 점수 계산 로직

```java
// 리커트 척도: 1(매우 아니다) ~ 5(매우 그렇다)
int[] scores = {-5, -3, 0, 3, 5};
int score = scores[scaleValue - 1];

// 방향에 따라 부호 반전
if (direction == I || direction == F || direction == D || direction == U) {
    score = -score;
}
```

### 4. 결과 시각화

#### Recharts 레이더 차트

```jsx
import { RadarChart, Radar, PolarGrid, PolarAngleAxis } from 'recharts';

function MutiResultChart({ axisScores }) {
  const data = [
    { axis: 'Energetic', value: Math.abs(axisScores.E_I), fullMark: 20 },
    { axis: 'Sensory', value: Math.abs(axisScores.S_F), fullMark: 20 },
    { axis: 'Analog', value: Math.abs(axisScores.A_D), fullMark: 20 },
    { axis: 'Popular', value: Math.abs(axisScores.P_U), fullMark: 20 },
  ];

  return (
    <RadarChart width={500} height={500} data={data}>
      <PolarGrid />
      <PolarAngleAxis dataKey="axis" />
      <Radar
        name="MUTI Score"
        dataKey="value"
        stroke="#1DB954"
        fill="#1DB954"
        fillOpacity={0.6}
      />
    </RadarChart>
  );
}
```

### 5. Spotify API 음악 추천

#### API 호출 예시

```java
@Service
public class SpotifyRecommendationService {

    public List<TrackDto> getRecommendations(MutiType mutiType) {
        // 1. 타입별 파라미터
        RecommendationParams params = getParamsForType(mutiType);

        // 2. Spotify API 호출
        GET https://api.spotify.com/v1/recommendations
        ?seed_genres=k-pop,dance,edm
        &target_energy=0.8
        &target_danceability=0.9
        &limit=20

        // 3. 결과 반환
        return trackList;
    }
}
```

#### 타입별 파라미터 예시

```
ESAP (활발한 감각파)
→ 장르: dance-pop, k-pop, edm
→ 특성: high energy (0.8), high danceability (0.9)

IFDU (추상적 예술가)
→ 장르: ambient, experimental, idm
→ 특성: low energy (0.2), high acousticness (0.8)
```

---

## 🏗 전체 아키텍처

### 도메인 주도 설계 (DDD)

```
src/main/java/com/muti/
├── domain/
│   ├── auth/                      # 인증/인가 도메인 ✨ NEW
│   │   ├── controller/
│   │   │   └── AuthController.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── RefreshTokenRepository.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── RefreshToken.java
│   │   │   └── Role.java (enum)
│   │   ├── dto/
│   │   │   ├── SignUpRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   └── LoginResponse.java
│   │   └── security/
│   │       ├── JwtTokenProvider.java
│   │       ├── JwtAuthenticationFilter.java
│   │       └── UserPrincipal.java
│   │
│   ├── survey/                    # 설문 도메인 (기존)
│   │   └── ... (기존 구조 유지)
│   │
│   ├── board/                     # 게시판 도메인 ✨ NEW
│   │   ├── controller/
│   │   │   ├── BoardController.java
│   │   │   ├── PostController.java
│   │   │   └── CommentController.java
│   │   ├── service/
│   │   │   ├── BoardService.java
│   │   │   ├── PostService.java
│   │   │   └── CommentService.java
│   │   ├── repository/
│   │   │   ├── BoardRepository.java
│   │   │   ├── PostRepository.java
│   │   │   ├── CommentRepository.java
│   │   │   └── PostLikeRepository.java
│   │   └── entity/
│   │       ├── Board.java
│   │       ├── Post.java
│   │       ├── Comment.java
│   │       └── PostLike.java
│   │
│   └── music/                     # 음악 추천 도메인 ✨ NEW
│       ├── controller/
│       │   └── MusicController.java
│       ├── service/
│       │   ├── SpotifyService.java
│       │   └── RecommendationService.java
│       └── dto/
│           ├── TrackDto.java
│           ├── ArtistDto.java
│           └── PlaylistDto.java
│
└── global/                        # 공통 계층
    ├── config/
    │   ├── SecurityConfig.java    ✨ NEW
    │   ├── RedisConfig.java       ✨ NEW
    │   └── ... (기존 유지)
    └── ... (기존 유지)
```

### 데이터베이스 ERD

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│  users   │ 1────N  │  posts   │ N────1  │  boards  │
│          │         │          │         │          │
│  id (PK) │         │  id (PK) │         │  id (PK) │
│  email   │         │  user_id │         │  name    │
│  password│         │  board_id│         │  type    │
│  username│         │  title   │         │  muti_   │
│  role    │         │  content │         │  type    │
└────┬─────┘         └────┬─────┘         └──────────┘
     │ 1                  │ 1
     │                    │
     │ N                  │ N
     │              ┌─────┴──────┐
┌────┴──────┐      │  comments  │
│  survey_  │      │            │
│  results  │      │  id (PK)   │
│           │      │  post_id   │
│  id (PK)  │      │  user_id   │
│  user_id  │      │  parent_id │
│  muti_type│      │  content   │
│  ei_score │      └────────────┘
└───────────┘

┌──────────┐         ┌──────────┐
│ surveys  │ 1────N  │questions │
│          │         │          │
│  id (PK) │         │  id (PK) │
│  title   │         │survey_id │
└──────────┘         └────┬─────┘
                          │ 1
                          │ N
                    ┌─────┴──────┐
                    │ question_  │
                    │  options   │
                    │            │
                    │  id (PK)   │
                    │question_id │
                    │ direction  │
                    │scale_value │ ✨ NEW
                    └────────────┘
```

---

## 📅 단계별 실행 계획

### 전체 타임라인 (15주)

```
Week 1  : Phase 0 - 프로젝트 리팩토링 준비
Week 2-3: Phase 1 - 백엔드 인증/인가
Week 4-5: Phase 2 - CRUD 게시판
Week 6-8: Phase 3 - React 프론트엔드 전환
Week 9-10: Phase 4 - 설문 고도화
Week 11-12: Phase 5 - 음악 추천
Week 13: Phase 6 - Docker 컨테이너화
Week 14-15: Phase 7 - AWS 인프라 & 배포
```

### Phase 0: 프로젝트 리팩토링 준비 (1주) ⭐ 지금 시작!

#### Week 1: 환경 정리 및 문서화

**Day 1-2: Git 브랜치 전략**
```bash
# 사용자가 할 일

# 1. dev 브랜치 생성
git checkout -b dev

# 2. .gitignore 업데이트 (이미 있음)

# 3. 브랜치 보호 규칙 설정 (GitHub)
Settings → Branches → Add rule
- Branch name pattern: main
- ☑ Require pull request reviews
- ☑ Require status checks to pass
```

**Day 3-4: 개발 환경 정리**
```bash
# 사용자가 할 일

# 1. IDE 설정 확인
- IntelliJ IDEA: Java 17, Gradle 8.14.4
- VS Code 확장:
  - Java Extension Pack
  - Spring Boot Extension Pack
  - Docker
  - Kubernetes

# 2. 로컬 PostgreSQL 설치 (선택)
# macOS
brew install postgresql@15
brew services start postgresql@15

# 3. Redis 설치 (선택)
brew install redis
brew services start redis

# 4. Docker Desktop 설치
https://www.docker.com/products/docker-desktop/
```

**Day 5-7: 문서 작성**
- [x] ROADMAP_V2.md (이 파일)
- [ ] CONTRIBUTING.md (기여 가이드)
- [ ] CHANGELOG.md (변경 로그)

**체크리스트:**
```
□ Git 브랜치 전략 설정 완료
□ 개발 환경 (Java, Node, Docker) 설치 완료
□ PostgreSQL 로컬 설치 완료 (선택)
□ Redis 로컬 설치 완료 (선택)
□ ROADMAP_V2.md 읽고 이해 완료
□ 질문 사항 정리
```

---

### Phase 1: 백엔드 인증/인가 (2주)

#### Week 2: Spring Security + JWT

**Day 8-10: Entity 및 Repository**

**사용자가 할 일:**
1. 새 브랜치 생성
```bash
git checkout dev
git pull origin dev
git checkout -b feature/auth
```

2. 기다리기 - 제가 다음 파일들을 생성:
   - `User.java`
   - `RefreshToken.java`
   - `Role.java`
   - `UserRepository.java`
   - `RefreshTokenRepository.java`

3. Flyway 마이그레이션 실행 확인
```bash
./gradlew bootRun
# 콘솔에서 "Flyway: Migrated V4__create_users_table" 확인
```

**Day 11-14: JWT & Security Config**

**사용자가 할 일:**
1. `application.yml`에 JWT 설정 추가:
```yaml
jwt:
  secret: your-256-bit-secret-key-change-this-in-production
  access-token-validity: 900000      # 15분
  refresh-token-validity: 604800000  # 7일
```

2. 기다리기 - 제가 다음 파일들을 생성:
   - `JwtTokenProvider.java`
   - `JwtAuthenticationFilter.java`
   - `SecurityConfig.java`
   - `AuthService.java`
   - `AuthController.java`

3. 테스트 실행
```bash
./gradlew test --tests "com.muti.domain.auth.*"
```

4. Postman으로 API 테스트:
```http
POST http://localhost:8080/api/v1/auth/signup
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123",
  "username": "testuser"
}
```

**체크리스트:**
```
□ feature/auth 브랜치 생성
□ Entity 파일 생성 확인
□ JWT 설정 추가
□ 회원가입 API 테스트 성공
□ 로그인 API 테스트 성공
□ 토큰으로 인증 API 호출 성공
□ PR 생성 (feature/auth → dev)
```

---

### Phase 2: CRUD 게시판 (2주)

#### Week 4-5: Board, Post, Comment

**사용자가 할 일:**
1. 새 브랜치 생성
```bash
git checkout dev
git pull origin dev
git checkout -b feature/board
```

2. 기다리기 - 제가 Entity, Service, Controller 생성

3. API 테스트:
```http
# 게시판 생성 (관리자만)
POST http://localhost:8080/api/v1/boards
Authorization: Bearer <admin-token>

{
  "name": "자유게시판",
  "description": "자유롭게 이야기를 나눠요",
  "boardType": "FREE"
}

# 게시글 작성
POST http://localhost:8080/api/v1/posts
Authorization: Bearer <your-token>

{
  "boardId": 1,
  "title": "첫 게시글입니다",
  "content": "안녕하세요!"
}
```

**체크리스트:**
```
□ feature/board 브랜치 생성
□ 게시판 생성 API 테스트
□ 게시글 CRUD API 테스트
□ 댓글 작성 API 테스트
□ 좋아요 기능 테스트
□ PR 생성 (feature/board → dev)
```

---

### Phase 3: React 프론트엔드 전환 (3주)

#### Week 6: React 프로젝트 생성

**사용자가 할 일:**

1. 새 브랜치 생성
```bash
git checkout dev
git pull origin dev
git checkout -b feature/react-frontend
```

2. React 프로젝트 생성
```bash
cd frontend

# 기존 파일 백업
mkdir ../frontend-v1-backup
mv * ../frontend-v1-backup/

# Vite로 React 프로젝트 생성
npm create vite@latest . -- --template react-ts

# 의존성 설치
npm install
npm install @reduxjs/toolkit react-redux react-router-dom
npm install @mui/material @emotion/react @emotion/styled
npm install axios react-query
npm install recharts
npm install react-hook-form yup @hookform/resolvers

# 개발 서버 실행
npm run dev
```

3. 접속 확인: http://localhost:5173

**체크리스트:**
```
□ feature/react-frontend 브랜치 생성
□ React 프로젝트 생성
□ 의존성 설치
□ 개발 서버 실행 확인
```

#### Week 7-8: 페이지 마이그레이션

**사용자가 할 일:**
- 기다리기 - 제가 React 컴포넌트 생성:
  - 메인 페이지
  - 로그인/회원가입
  - 설문 페이지
  - 결과 페이지
  - 게시판 페이지

- 각 페이지 동작 확인

**체크리스트:**
```
□ 메인 페이지 완성
□ 로그인/회원가입 완성
□ 설문 페이지 완성
□ 결과 페이지 완성
□ 게시판 페이지 완성
□ PR 생성 (feature/react-frontend → dev)
```

---

### Phase 4: 설문 고도화 (2주)

**사용자가 할 일:**
1. 새 브랜치 생성
```bash
git checkout dev
git pull origin dev
git checkout -b feature/likert-scale
```

2. 기다리기 - 제가 리커트 척도 적용

3. 테스트:
```bash
./gradlew test
npm run dev
```

**체크리스트:**
```
□ 리커트 5점 척도 적용
□ 레이더 차트 시각화
□ PDF 다운로드 기능
□ PR 생성
```

---

### Phase 5: 음악 추천 (2주)

**사용자가 할 일:**

1. Spotify API 키 발급
```bash
# Spotify Developer Dashboard
https://developer.spotify.com/dashboard

# 1. 로그인
# 2. Create an App
# 3. Client ID, Client Secret 복사

# 4. .env에 추가
SPOTIFY_CLIENT_ID=your_client_id
SPOTIFY_CLIENT_SECRET=your_client_secret
```

2. 새 브랜치 생성
```bash
git checkout dev
git pull origin dev
git checkout -b feature/music-recommendation
```

3. 기다리기 - 제가 Spotify 연동 구현

**체크리스트:**
```
□ Spotify API 키 발급
□ 타입별 음악 추천 동작
□ 플레이리스트 생성 동작
□ PR 생성
```

---

### Phase 6: Docker 컨테이너화 (1주)

**사용자가 할 일:**

1. Docker Desktop 실행 확인
```bash
docker --version
docker-compose --version
```

2. 새 브랜치 생성
```bash
git checkout dev
git pull origin dev
git checkout -b feature/dockerize
```

3. 기다리기 - 제가 Dockerfile, docker-compose.yml 생성

4. 로컬에서 컨테이너 실행
```bash
# 빌드
docker-compose build

# 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f backend

# 접속 확인
curl http://localhost:8080/api/v1/surveys/ping
```

**체크리스트:**
```
□ Docker 설치 확인
□ docker-compose up 성공
□ 백엔드 컨테이너 동작
□ 프론트엔드 컨테이너 동작
□ PR 생성
```

---

### Phase 7: AWS 인프라 & 배포 (2주)

**사용자가 할 일:**

#### Week 14: AWS 계정 및 기본 설정

**Day 1-2: AWS 계정 생성**
```bash
# 1. AWS 계정 생성
https://aws.amazon.com/

# 2. 루트 계정으로 로그인

# 3. MFA 설정 (필수!)
My Security Credentials → Multi-factor authentication

# 4. IAM 사용자 생성
IAM → Users → Add user
- Username: muti-admin
- Access type: Programmatic access + AWS Management Console
- Permissions: AdministratorAccess
- Download .csv (중요!)

# 5. AWS CLI 설치
# macOS
brew install awscli

# 6. AWS CLI 설정
aws configure
AWS Access Key ID: <your-key>
AWS Secret Access Key: <your-secret>
Default region name: ap-northeast-2
Default output format: json
```

**Day 3-4: VPC 생성**
```bash
# AWS Console → VPC → Create VPC

VPC 설정:
- Name: muti-vpc
- IPv4 CIDR: 10.0.0.0/16
- ☑ Enable DNS hostnames

Subnets:
- muti-public-subnet-1a: 10.0.1.0/24 (ap-northeast-2a)
- muti-public-subnet-1b: 10.0.2.0/24 (ap-northeast-2b)
- muti-private-subnet-1a: 10.0.11.0/24 (ap-northeast-2a)
- muti-private-subnet-1b: 10.0.12.0/24 (ap-northeast-2b)

Internet Gateway:
- Name: muti-igw
- Attach to muti-vpc

Route Tables:
- Public: 0.0.0.0/0 → Internet Gateway
- Private: (나중에 NAT Gateway 추가)
```

**Day 5-7: EC2 인스턴스 생성**
```bash
# AWS Console → EC2 → Launch Instance

1. Name: muti-node-1
2. AMI: Ubuntu 22.04 LTS
3. Instance type: t2.micro
4. Key pair: Create new (muti-key.pem 다운로드)
5. Network: muti-vpc, muti-public-subnet-1a
6. Security group: Create new
   - Name: muti-backend-sg
   - Inbound rules:
     - SSH (22) from My IP
     - HTTP (80) from 0.0.0.0/0
     - HTTPS (443) from 0.0.0.0/0
     - Custom TCP (8080) from 0.0.0.0/0
7. Storage: 20 GB gp3
8. Launch!

# SSH 접속
chmod 400 muti-key.pem
ssh -i muti-key.pem ubuntu@<EC2-Public-IP>

# 초기 설정
sudo apt update && sudo apt upgrade -y
sudo apt install -y docker.io docker-compose
sudo usermod -aG docker ubuntu
exit

# 재접속 후 Docker 확인
docker --version
```

#### Week 15: RDS, K8s, 배포

**Day 8-10: RDS PostgreSQL 생성**
```bash
# AWS Console → RDS → Create database

1. Engine: PostgreSQL 15
2. Template: Free tier
3. DB instance identifier: muti-db
4. Master username: postgres
5. Master password: <strong-password>
6. Instance type: db.t2.micro
7. Storage: 20 GB gp3
8. VPC: muti-vpc
9. Subnet group: Create new (private subnets)
10. Public access: No
11. Security group: Create new
    - Name: muti-db-sg
    - Inbound: PostgreSQL (5432) from muti-backend-sg
12. Create!

# 엔드포인트 복사
muti-db.xxxx.ap-northeast-2.rds.amazonaws.com
```

**Day 11-13: Kubernetes 설정**
```bash
# EC2에 SSH 접속
ssh -i muti-key.pem ubuntu@<EC2-IP>

# k3s 설치
curl -sfL https://get.k3s.io | sh -

# kubectl 설정
sudo cat /etc/rancher/k3s/k3s.yaml > ~/.kube/config

# 확인
kubectl get nodes
```

**Day 14-15: 애플리케이션 배포**
```bash
# 로컬에서 이미지 빌드
docker build -t muti-backend .

# AWS ECR 생성 및 푸시
aws ecr create-repository --repository-name muti-backend
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.ap-northeast-2.amazonaws.com
docker tag muti-backend:latest <account-id>.dkr.ecr.ap-northeast-2.amazonaws.com/muti-backend:v2.0.0
docker push <account-id>.dkr.ecr.ap-northeast-2.amazonaws.com/muti-backend:v2.0.0

# Kubernetes 배포
kubectl apply -f k8s/deployment.yaml
kubectl rollout status deployment/muti-backend

# 서비스 확인
kubectl get pods
kubectl get svc
```

**체크리스트:**
```
□ AWS 계정 생성
□ MFA 설정
□ IAM 사용자 생성
□ AWS CLI 설정
□ VPC 생성
□ EC2 인스턴스 생성 (3대)
□ RDS PostgreSQL 생성
□ ElastiCache Redis 생성
□ S3 버킷 생성
□ CloudFront 배포
□ Route 53 도메인 설정
□ k3s 설치
□ 애플리케이션 배포
□ 접속 테스트
```

---

## 💰 예상 비용

### 총 비용 요약

#### 개발 기간 (15주)

```
도메인 구매 (1년): $12
AWS 프리티어: $0 (첫 12개월)
───────────────────────
총 비용: $12
```

#### 프리티어 종료 후 (월별)

```
EC2 t2.micro × 3: $25.05
RDS db.t2.micro: $12.96
ElastiCache cache.t2.micro: $11.30
S3 (10GB): $0.23
CloudFront (50GB): $0.85
Route 53: $0.50
───────────────────────
월 합계: $50.89
연 합계: $610.68
```

### 비용 절감 팁

1. **프리티어 최대 활용**: 첫 12개월 무료
2. **개발 서버 중지**: 사용하지 않을 때 EC2 중지
3. **Reserved Instance**: 1년 약정 시 40% 할인
4. **CloudFront 캐싱**: 데이터 전송 비용 절감
5. **S3 Lifecycle**: 오래된 파일 Glacier로 이동

---

## 📚 다음 단계

이 로드맵을 읽었으면:

1. **Phase 0부터 시작**: Git 브랜치 설정, 개발 환경 준비
2. **하나씩 진행**: 각 Phase를 순서대로 완료
3. **문서화**: 각 단계마다 README 업데이트
4. **질문하기**: 막히면 언제든 질문

### 준비되셨나요?

Phase 0부터 시작하겠습니다! 🚀

**다음 단계**: Git 브랜치 전략 설정

```bash
# 이것부터 시작!
git checkout -b dev
git push -u origin dev
```

---

**작성자**: Claude Sonnet 4.5
**최종 수정**: 2026년 2월 8일