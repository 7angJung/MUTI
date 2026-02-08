# MUTI v2.0 Phase별 개발 가이드

> 💡 각 Phase의 핵심 개념, 기술 스택, 구현 방법을 **초보자도 이해할 수 있도록** 상세하게 설명합니다.

**작성일**: 2026년 2월 8일
**대상**: 백엔드 개발을 학습하는 모든 분들

---

## 📋 목차

- [Phase 1: 백엔드 인증/인가](#phase-1-백엔드-인증인가)
  - [1.1 Phase 1 개요](#11-phase-1-개요)
  - [1.2 인증 vs 인가](#12-인증-vs-인가)
  - [1.3 JWT란 무엇인가?](#13-jwt란-무엇인가)
  - [1.4 Spring Security란 무엇인가?](#14-spring-security란-무엇인가)
  - [1.5 BCrypt 암호화](#15-bcrypt-암호화)
  - [1.6 Access Token vs Refresh Token](#16-access-token-vs-refresh-token)
  - [1.7 전체 인증 플로우](#17-전체-인증-플로우)
  - [1.8 Phase 1 진행 방식](#18-phase-1-진행-방식)
- [Phase 2: CRUD 게시판](#phase-2-crud-게시판) (준비 중)
- [Phase 3: React 프론트엔드](#phase-3-react-프론트엔드) (준비 중)

---

## Phase 1: 백엔드 인증/인가

### 1.1 Phase 1 개요

#### 🎯 목표

**"사용자가 회원가입하고, 로그인하고, 안전하게 API를 사용할 수 있게 만들기"**

#### 📦 만들 것들

| 항목 | 설명 | 파일 |
|------|------|------|
| **User Entity** | 사용자 정보를 저장하는 데이터 모델 | `User.java` |
| **RefreshToken Entity** | 토큰 갱신용 데이터 | `RefreshToken.java` |
| **Repository** | 데이터베이스 접근 | `UserRepository.java` |
| **JWT Provider** | 토큰 생성 및 검증 | `JwtTokenProvider.java` |
| **Security Config** | 보안 설정 | `SecurityConfig.java` |
| **Auth Service** | 회원가입/로그인 로직 | `AuthService.java` |
| **Auth Controller** | API 엔드포인트 | `AuthController.java` |

#### ⏱️ 예상 소요 시간

```
Week 2 (Day 8-14): 총 7일
├─ Day 8-10: Entity & Repository (3일)
├─ Day 11-12: JWT & Security Config (2일)
└─ Day 13-14: Service & Controller (2일)
```

---

### 1.2 인증 vs 인가

많은 사람들이 헷갈려하는 개념입니다. 명확히 구분해봅시다.

#### 🔐 인증 (Authentication)

**"당신은 누구인가?"** - 신원 확인

**비유: 공항 보안 검색대**
```
승객: "저 탑승객 김철수입니다"
직원: "신분증 보여주세요"
승객: [여권 제시]
직원: "확인되었습니다. 통과하세요"
```

**웹 서비스 예시:**
```
사용자: "저 로그인하겠습니다"
서버: "이메일과 비밀번호를 입력하세요"
사용자: email: user@example.com, password: 1234
서버: [DB에서 확인] "확인되었습니다. 토큰을 드립니다"
```

**핵심:**
- 사용자가 **본인이 맞는지** 확인
- 로그인이 인증의 대표적인 예

#### 🚪 인가 (Authorization)

**"당신은 무엇을 할 수 있는가?"** - 권한 확인

**비유: 호텔 룸키**
```
투숙객: [룸키로 1001호 문 열려고 시도]
시스템: "당신은 1001호 권한이 있습니다. 문을 엽니다"

투숙객: [룸키로 1002호 문 열려고 시도]
시스템: "권한이 없습니다. 거부됩니다"
```

**웹 서비스 예시:**
```
사용자: [일반 사용자로 로그인됨]
사용자: "게시글을 작성하겠습니다"
서버: "권한이 있습니다. 허용합니다"

사용자: "다른 사용자를 관리자로 승격시키겠습니다"
서버: "관리자 권한이 없습니다. 거부합니다"
```

**핵심:**
- 사용자가 **어떤 작업을 할 권한**이 있는지 확인
- 일반 사용자 vs 관리자

#### 📊 비교표

| 구분 | 인증 (Authentication) | 인가 (Authorization) |
|------|----------------------|---------------------|
| **질문** | 당신은 누구? | 무엇을 할 수 있나? |
| **시점** | 로그인 시 | API 호출 시마다 |
| **방법** | 이메일 + 비밀번호 | JWT 토큰 + 역할(Role) |
| **실패 시** | 401 Unauthorized | 403 Forbidden |
| **예시** | 로그인, 회원가입 | 게시글 작성, 관리자 페이지 |

#### 🔄 인증과 인가의 흐름

```
1. [인증] 로그인
   사용자: email + password 입력
   서버: DB 확인 → JWT 토큰 발급

2. [인가] API 호출
   사용자: 게시글 작성 API 호출 + JWT 토큰
   서버: 토큰 검증 → 권한 확인 → 허용/거부
```

---

### 1.3 JWT란 무엇인가?

#### 📝 JWT (JSON Web Token)

**정의**: 사용자 정보를 JSON 형태로 안전하게 전달하는 토큰

**비유: 놀이공원 입장 팔찌**

```
입구 (로그인):
직원: "티켓 확인하겠습니다"
나: [티켓 제시]
직원: [팔찌 착용] "이 팔찌로 모든 놀이기구를 이용하세요"

놀이기구 (API):
나: [팔찌 보여줌]
직원: "팔찌가 있으시네요! 탑승하세요"
```

#### 🧩 JWT 구조

JWT는 **3개 부분**으로 구성됩니다:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

Header . Payload . Signature
  ①       ②         ③
```

##### ① Header (헤더)

**역할**: 토큰 타입과 암호화 알고리즘 명시

```json
{
  "alg": "HS256",  // 알고리즘: HMAC SHA256
  "typ": "JWT"     // 타입: JWT
}
```

**비유**: 팔찌 재질 (방수, 종이, 플라스틱...)

##### ② Payload (페이로드)

**역할**: 실제 사용자 정보 담기

```json
{
  "userId": 123,
  "email": "user@example.com",
  "role": "USER",
  "exp": 1672531200  // 만료 시간
}
```

**비유**: 팔찌에 적힌 정보 (이름, 유효기간, 등급)

**주의**:
- 누구나 디코딩할 수 있음 (암호화 아님!)
- 비밀번호 같은 민감한 정보는 **절대 담으면 안 됨**

##### ③ Signature (서명)

**역할**: 토큰이 변조되지 않았는지 검증

```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret_key
)
```

**비유**: 팔찌의 홀로그램 스티커 (위조 방지)

**작동 원리**:
1. 서버만 아는 비밀 키(secret_key)로 서명 생성
2. 해커가 Payload를 변조하면 Signature가 맞지 않음
3. 서버가 검증 시 서명 불일치 → 거부

#### 🔒 JWT의 보안

**좋은 점:**
- ✅ 서버가 세션을 저장하지 않아도 됨 (Stateless)
- ✅ 확장성 좋음 (여러 서버에서 검증 가능)

**위험한 점:**
- ❌ 토큰 탈취 시 만료 전까지 무효화 불가능
- ❌ Payload는 누구나 볼 수 있음 (암호화 아님)

**해결책:**
- 짧은 만료 시간 (15분)
- Refresh Token 분리
- HTTPS 사용 (전송 암호화)
- HttpOnly Cookie에 저장

#### 📊 세션 vs JWT 비교

| 항목 | 세션 (Session) | JWT |
|------|----------------|-----|
| **저장 위치** | 서버 메모리/DB | 클라이언트 (쿠키/로컬스토리지) |
| **확장성** | 나쁨 (서버마다 세션 공유 필요) | 좋음 (어느 서버든 검증 가능) |
| **보안** | 좋음 (서버에서 즉시 무효화) | 나쁨 (만료 전까지 무효화 불가) |
| **속도** | 느림 (DB 조회 필요) | 빠름 (검증만) |
| **사용 사례** | 전통적 웹 (서버 1-2대) | REST API, MSA, 모바일 앱 |

---

### 1.4 Spring Security란 무엇인가?

#### 🛡️ Spring Security

**정의**: 스프링 기반 애플리케이션의 보안을 담당하는 프레임워크

**비유: 건물 보안 시스템**

```
건물 = Spring Boot 애플리케이션
보안 시스템 = Spring Security

1. 출입구 (Filter): 모든 사람을 검문
2. 신분증 확인 (Authentication): 직원인가? 방문객인가?
3. 출입 허가 (Authorization): 어느 층까지 갈 수 있는가?
4. 보안 기록 (Security Context): 현재 누가 들어와 있는가?
```

#### 🔍 Spring Security의 핵심 개념

##### 1. Filter Chain (필터 체인)

**모든 HTTP 요청이 거치는 관문**

```
HTTP 요청
    │
    ▼
┌─────────────────────────────────────┐
│   Spring Security Filter Chain      │
├─────────────────────────────────────┤
│ 1. SecurityContextPersistenceFilter │ ← 보안 컨텍스트 로드
│ 2. LogoutFilter                     │ ← 로그아웃 처리
│ 3. UsernamePasswordAuthFilter       │ ← 로그인 처리
│ 4. JwtAuthenticationFilter          │ ← JWT 검증 ✨ 우리가 추가
│ 5. AuthorizationFilter              │ ← 권한 확인
│ ...                                  │
└─────────────────────────────────────┘
    │
    ▼
Controller (API 엔드포인트)
```

**JwtAuthenticationFilter 예시:**
```java
// 모든 요청마다 실행
public void doFilterInternal(HttpServletRequest request) {
    // 1. Authorization 헤더에서 토큰 추출
    String token = extractToken(request);

    // 2. 토큰 유효성 검증
    if (tokenProvider.validateToken(token)) {
        // 3. 토큰에서 사용자 정보 추출
        Long userId = tokenProvider.getUserIdFromToken(token);

        // 4. SecurityContext에 저장 (인증 완료)
        Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // 5. 다음 필터로 전달
    chain.doFilter(request, response);
}
```

##### 2. Authentication (인증 객체)

**현재 로그인한 사용자 정보**

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();

auth.getPrincipal();    // 사용자 정보 (UserDetails)
auth.getCredentials();  // 비밀번호 (보통 null)
auth.getAuthorities();  // 권한 목록 [ROLE_USER, ROLE_ADMIN]
auth.isAuthenticated(); // 인증 여부 true/false
```

**비유**: 출입증
- Principal (주체): 이름, 사진
- Credentials (자격증명): 비밀번호 (검증 후 삭제)
- Authorities (권한): 출입 가능한 층

##### 3. SecurityContext

**현재 요청의 보안 정보 저장소**

```java
// 인증 정보 저장
SecurityContext context = SecurityContextHolder.getContext();
context.setAuthentication(auth);

// 인증 정보 조회
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
```

**비유**: 현재 건물 안에 있는 사람들의 명단

##### 4. UserDetails

**스프링 시큐리티가 관리하는 사용자 정보**

```java
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getUsername() { return email; }

    @Override
    public String getPassword() { return password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
```

#### ⚙️ SecurityConfig 예시

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (JWT 사용 시 불필요)
            .csrf().disable()

            // CORS 설정
            .cors()

            .and()

            // 세션 사용 안 함
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()

            // URL별 권한 설정
            .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()      // 누구나 접근
                .requestMatchers("/api/surveys/**").permitAll()   // 누구나 접근
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자만
                .anyRequest().authenticated()                      // 나머지는 로그인 필요

            .and()

            // JWT 필터 추가
            .addFilterBefore(jwtAuthenticationFilter(),
                             UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

**설명:**
1. **CSRF 비활성화**: JWT 사용 시 불필요 (Cookie 기반이 아님)
2. **Stateless**: 세션 저장 안 함 (JWT가 상태 정보 담음)
3. **URL별 권한**: 어떤 URL은 누구나, 어떤 URL은 로그인 필요
4. **JWT 필터**: 모든 요청 전에 JWT 검증

---

### 1.5 BCrypt 암호화

#### 🔐 왜 암호화가 필요한가?

**문제 상황:**
```sql
-- ❌ 평문 저장 (위험!)
INSERT INTO users (email, password) VALUES ('user@example.com', 'password123');

-- DB가 해킹당하면?
SELECT * FROM users;
-- 결과: user@example.com | password123
-- 해커가 모든 사용자의 비밀번호를 그대로 볼 수 있음!
```

**해결책:**
```sql
-- ✅ 암호화 저장 (안전!)
INSERT INTO users (email, password) VALUES ('user@example.com', '$2a$10$N9qo8uLO...');

-- DB가 해킹당해도?
SELECT * FROM users;
-- 결과: user@example.com | $2a$10$N9qo8uLO...
-- 해커가 원래 비밀번호를 알 수 없음!
```

#### 🧂 BCrypt란?

**정의**: 비밀번호 암호화 전용 알고리즘

**특징:**
1. **단방향 암호화**: 암호화는 가능, 복호화는 불가능
2. **Salt 자동 추가**: 같은 비밀번호도 다르게 암호화
3. **속도 조절 가능**: 의도적으로 느리게 (무차별 대입 공격 방지)

#### 🔍 BCrypt 작동 원리

**1. 회원가입 시 (암호화)**
```java
String plainPassword = "password123";
String hashedPassword = passwordEncoder.encode(plainPassword);

System.out.println(hashedPassword);
// 출력: $2a$10$N9qo8uLOickgx2ZMRZoMye5IcZZRLikbE4B0LDEkp65Y0dVYqAHaC
//       ┬   ┬  ┬──────────────┬  ┬──────────────────────────┬
//       │   │  │              │  │                          │
//       │   │  │              │  └─ Hash (해시)            │
//       │   │  │              └─ Salt (소금)               │
//       │   │  └─ Salt의 일부                              │
//       │   └─ Cost Factor (10 = 2^10번 반복)             │
//       └─ 알고리즘 버전 (2a)                               │
```

**2. 로그인 시 (검증)**
```java
String inputPassword = "password123";  // 사용자 입력
String storedHash = "$2a$10$N9qo8uLO...";  // DB에 저장된 해시

boolean matches = passwordEncoder.matches(inputPassword, storedHash);
// true: 비밀번호 일치!

// 내부 동작:
// 1. storedHash에서 Salt 추출
// 2. inputPassword + Salt로 다시 해싱
// 3. 결과가 storedHash와 같은지 비교
```

#### 🧂 Salt의 역할

**Salt 없이 (위험):**
```
사용자 A: "password123" → hash1
사용자 B: "password123" → hash1 (같음!)

해커: "아, hash1이 'password123'이구나!"
```

**Salt 사용 (안전):**
```
사용자 A: "password123" + saltA → hashA
사용자 B: "password123" + saltB → hashB (다름!)

해커: "hash가 달라서 뭐가 뭔지 모르겠네..."
```

#### 💻 Spring Security에서 사용

```java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void signUp(String email, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, hashedPassword);
        userRepository.save(user);
    }

    // 로그인
    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email);
        return passwordEncoder.matches(password, user.getPassword());
    }
}
```

#### 📊 다른 암호화와 비교

| 암호화 | 종류 | 복호화 | 속도 | 용도 |
|--------|------|--------|------|------|
| **BCrypt** | 단방향 해시 | 불가능 | 느림 (의도적) | 비밀번호 |
| SHA-256 | 단방향 해시 | 불가능 | 빠름 | 파일 무결성 |
| MD5 | 단방향 해시 | 불가능 | 매우 빠름 | 체크섬 (취약함) |
| AES | 양방향 암호화 | 가능 | 빠름 | 파일 암호화 |
| RSA | 양방향 암호화 | 가능 | 느림 | 공개키 암호화 |

**왜 비밀번호는 BCrypt?**
- ✅ 복호화 불가능 (안전)
- ✅ Salt 자동 (레인보우 테이블 공격 방어)
- ✅ 느린 속도 (무차별 대입 공격 방어)

---

### 1.6 Access Token vs Refresh Token

#### 🎫 왜 토큰을 2개로 나눴나?

**문제 상황:**
```
토큰 1개만 사용하면?

옵션 A: 만료 시간 짧게 (15분)
❌ 15분마다 다시 로그인해야 함 (불편!)

옵션 B: 만료 시간 길게 (7일)
❌ 토큰 탈취 시 7일 동안 해커가 사용 가능 (위험!)
```

**해결책: 토큰 2개 사용**
```
Access Token: 짧은 수명 (15분) + 자주 사용
Refresh Token: 긴 수명 (7일) + 가끔 사용

→ 보안 + 편의성 둘 다 확보!
```

#### 🔑 Access Token (접근 토큰)

**역할**: API 호출 시 인증

**특징:**
- ⏱️ **수명**: 15분 (짧음)
- 🔄 **사용 빈도**: 매우 높음 (모든 API 호출)
- 💾 **저장 위치**: 메모리 (또는 짧은 수명 쿠키)
- 🔒 **탈취 위험**: 높음 (자주 전송)

**내용:**
```json
{
  "userId": 123,
  "email": "user@example.com",
  "role": "USER",
  "exp": 1672531200  // 15분 후
}
```

#### 🔄 Refresh Token (갱신 토큰)

**역할**: Access Token 재발급

**특징:**
- ⏱️ **수명**: 7일 (김)
- 🔄 **사용 빈도**: 낮음 (15분마다 1번)
- 💾 **저장 위치**: DB + HttpOnly Cookie
- 🔒 **탈취 위험**: 낮음 (드물게 전송)

**내용:**
```json
{
  "userId": 123,
  "tokenId": "abc123",  // DB의 refresh_tokens 테이블 ID
  "exp": 1672531200     // 7일 후
}
```

#### 🔄 토큰 갱신 플로우

```
1. 로그인
   사용자 → 서버: email + password
   서버 → 사용자: Access Token (15분) + Refresh Token (7일)

2. API 호출 (13분 경과)
   사용자 → 서버: GET /api/posts + Access Token
   서버: ✅ 토큰 유효 → 200 OK

3. API 호출 (16분 경과, Access Token 만료)
   사용자 → 서버: GET /api/posts + Access Token (만료됨)
   서버: ❌ 401 Unauthorized

4. 토큰 갱신 요청
   사용자 → 서버: POST /api/auth/refresh + Refresh Token
   서버: Refresh Token 검증 → 새 Access Token 발급
   서버 → 사용자: 새 Access Token (15분)

5. 다시 API 호출
   사용자 → 서버: GET /api/posts + 새 Access Token
   서버: ✅ 성공!
```

#### 🗄️ Refresh Token DB 저장

**왜 DB에 저장?**
- 로그아웃 시 즉시 무효화 가능
- 여러 기기 로그인 관리 (PC, 모바일)
- 의심스러운 활동 감지 시 강제 만료

**refresh_tokens 테이블:**
```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**로그아웃 시:**
```java
public void logout(String refreshToken) {
    // DB에서 Refresh Token 삭제
    refreshTokenRepository.deleteByToken(refreshToken);

    // 이제 이 Refresh Token으로 Access Token 재발급 불가!
}
```

#### 📊 비교표

| 항목 | Access Token | Refresh Token |
|------|--------------|---------------|
| **수명** | 15분 | 7일 |
| **용도** | API 인증 | Access Token 재발급 |
| **전송 빈도** | 매 API 호출 | 15분마다 1번 |
| **저장 위치** | 메모리 | DB + HttpOnly Cookie |
| **탈취 위험** | 높음 | 낮음 |
| **즉시 무효화** | 불가능 | 가능 (DB 삭제) |

---

### 1.7 전체 인증 플로우

이제 모든 개념을 종합해서 전체 플로우를 봅시다.

#### 🔐 1. 회원가입 플로우

```
1. 사용자 입력
   ┌─────────────────────────┐
   │ 이메일: user@example.com│
   │ 비밀번호: password123    │
   │ 닉네임: 철수            │
   └─────────────────────────┘
              │
              ▼
2. Controller
   POST /api/v1/auth/signup
   @Valid SignUpRequest 검증
              │
              ▼
3. Service
   - 이메일 중복 체크
   - 비밀번호 암호화 (BCrypt)
   - User Entity 생성
              │
              ▼
4. Repository
   userRepository.save(user)
              │
              ▼
5. Database
   INSERT INTO users (email, password, username)
   VALUES ('user@example.com', '$2a$10$...', '철수')
              │
              ▼
6. 응답
   201 Created
   {
     "success": true,
     "message": "회원가입 성공"
   }
```

#### 🔑 2. 로그인 플로우

```
1. 사용자 입력
   ┌─────────────────────────┐
   │ 이메일: user@example.com│
   │ 비밀번호: password123    │
   └─────────────────────────┘
              │
              ▼
2. Controller
   POST /api/v1/auth/login
              │
              ▼
3. Service - 인증
   - DB에서 사용자 조회
   - 비밀번호 검증
     passwordEncoder.matches(입력, DB해시)
              │
              ▼ (일치!)
4. JWT 생성
   - Access Token 생성 (15분)
     Payload: { userId: 123, email: "...", role: "USER" }

   - Refresh Token 생성 (7일)
     Payload: { userId: 123, tokenId: "abc123" }
              │
              ▼
5. Refresh Token DB 저장
   INSERT INTO refresh_tokens (user_id, token, expires_at)
   VALUES (123, 'eyJhbGc...', '2026-02-15 12:00:00')
              │
              ▼
6. 응답
   200 OK
   {
     "success": true,
     "data": {
       "accessToken": "eyJhbGc...",
       "refreshToken": "dGVzdC...",
       "tokenType": "Bearer",
       "expiresIn": 900
     }
   }
```

#### 📡 3. API 호출 플로우

```
1. 클라이언트 요청
   GET /api/v1/posts
   Authorization: Bearer eyJhbGc...
              │
              ▼
2. JwtAuthenticationFilter
   - Authorization 헤더에서 토큰 추출
   - 토큰 검증
     • 서명 확인
     • 만료 시간 확인
              │
              ▼ (유효!)
3. SecurityContext 저장
   Authentication auth = new UsernamePasswordAuthenticationToken(
       userId: 123,
       authorities: [ROLE_USER]
   )
   SecurityContextHolder.getContext().setAuthentication(auth)
              │
              ▼
4. Controller
   @GetMapping("/api/v1/posts")
   public ApiResponse<List<PostDto>> getPosts() {
       // SecurityContext에서 현재 사용자 정보 조회 가능
       Long currentUserId = getCurrentUserId();
       ...
   }
              │
              ▼
5. 응답
   200 OK
   { "data": [...] }
```

#### 🔄 4. 토큰 갱신 플로우

```
1. Access Token 만료
   GET /api/v1/posts
   Authorization: Bearer eyJhbGc... (만료됨)
              │
              ▼
2. 401 Unauthorized
   {
     "success": false,
     "errorCode": "AUTH_001",
     "message": "Access Token이 만료되었습니다"
   }
              │
              ▼
3. 클라이언트 - 자동 갱신
   POST /api/v1/auth/refresh
   {
     "refreshToken": "dGVzdC..."
   }
              │
              ▼
4. Service
   - DB에서 Refresh Token 조회
   - 유효성 검증
     • DB에 존재?
     • 만료 안 됨?
              │
              ▼ (유효!)
5. 새 Access Token 발급
   String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
              │
              ▼
6. 응답
   200 OK
   {
     "accessToken": "eyJhbGc... (새로운)",
     "expiresIn": 900
   }
              │
              ▼
7. 클라이언트 - 재시도
   GET /api/v1/posts
   Authorization: Bearer eyJhbGc... (새로운)
              │
              ▼
8. 성공!
   200 OK
```

#### 🚪 5. 로그아웃 플로우

```
1. 클라이언트 요청
   POST /api/v1/auth/logout
   {
     "refreshToken": "dGVzdC..."
   }
              │
              ▼
2. Service
   DELETE FROM refresh_tokens
   WHERE token = 'dGVzdC...'
              │
              ▼
3. 클라이언트 - 토큰 삭제
   - Access Token 메모리에서 삭제
   - Refresh Token 쿠키 삭제
              │
              ▼
4. 응답
   200 OK
   {
     "success": true,
     "message": "로그아웃 성공"
   }
```

#### 🔐 보안 고려사항

**1. Access Token 탈취 대응**
```
탈취자: [Access Token으로 API 호출]
서버: ✅ (토큰이 유효하므로 허용)

하지만:
- 15분 후 자동 만료
- 피해 최소화
```

**2. Refresh Token 탈취 대응**
```
탈취자: [Refresh Token으로 새 Access Token 요청]
서버: ✅ (발급됨)

하지만:
- 사용자가 로그아웃 → DB에서 Refresh Token 삭제
- 탈취자의 Refresh Token도 무효화됨
```

**3. 두 토큰 모두 탈취 대응**
```
최악의 상황:
- Access Token으로 15분간 사용 가능
- Refresh Token으로 7일간 갱신 가능

대응:
1. 의심스러운 활동 감지 (다른 IP, 다른 기기)
2. 강제 로그아웃 (모든 Refresh Token 삭제)
3. 이메일 알림 "새로운 기기에서 로그인이 감지되었습니다"
```

---

### 1.8 Phase 1 진행 방식

#### 📅 일정표

| 일차 | 작업 내용 | 당신이 할 일 | 제가 할 일 |
|------|-----------|--------------|------------|
| **Day 8** | feature/auth 브랜치 생성 | 브랜치 생성 & 푸시 | 안내 |
| **Day 9** | User Entity 작성 | 코드 리뷰 & 테스트 | 파일 작성 |
| **Day 10** | RefreshToken Entity, Repository | 코드 리뷰 & 테스트 | 파일 작성 |
| **Day 11** | JwtTokenProvider 작성 | application.yml 설정 | 파일 작성 |
| **Day 12** | SecurityConfig 작성 | 테스트 실행 | 파일 작성 |
| **Day 13** | AuthService 작성 | Postman 테스트 | 파일 작성 |
| **Day 14** | AuthController 작성 | 전체 플로우 테스트 | 파일 작성 |

#### 🔄 진행 방식

**매일 반복:**

```
1. 제가 코드 작성
   ├─ Entity, Service, Controller 등
   └─ 주석으로 상세 설명 추가

2. 당신이 확인
   ├─ 코드 읽고 이해
   ├─ 궁금한 점 질문
   └─ 로컬에서 테스트

3. 함께 검증
   ├─ 단위 테스트 실행
   ├─ Postman으로 API 테스트
   └─ 문제 발생 시 함께 해결

4. Git 커밋
   ├─ git add .
   ├─ git commit -m "..."
   └─ git push origin feature/auth
```

#### 📝 커밋 메시지 규칙

```bash
# 형식
<타입>: <제목>

<본문>

# 타입
Feat: 새 기능
Fix: 버그 수정
Refactor: 리팩토링
Test: 테스트 추가
Docs: 문서 수정
Style: 코드 포맷팅

# 예시
Feat: Add User entity and repository

- User 엔티티 생성 (email, password, username, role)
- UserRepository 인터페이스 생성
- JPA Auditing 적용 (BaseTimeEntity)
```

#### 🧪 테스트 방법

**1. 단위 테스트 (JUnit)**
```bash
# 특정 테스트 실행
./gradlew test --tests "com.muti.domain.auth.service.AuthServiceTest"

# 전체 테스트
./gradlew test
```

**2. API 테스트 (Postman)**
```
1. Postman 설치
2. Collection 생성: MUTI v2.0
3. 요청 추가:
   - POST /api/v1/auth/signup
   - POST /api/v1/auth/login
   - POST /api/v1/auth/refresh
   - POST /api/v1/auth/logout
```

**3. 로그 확인**
```bash
# 애플리케이션 실행
./gradlew bootRun

# 로그 확인
tail -f logs/application.log
```

#### ❓ 막혔을 때

**문제 해결 프로세스:**

```
1. 에러 메시지 복사
   ├─ 전체 스택 트레이스 복사
   └─ 에러가 발생한 라인 확인

2. 저에게 공유
   ├─ 에러 메시지 붙여넣기
   ├─ 어떤 작업 중이었는지 설명
   └─ 스크린샷 (선택)

3. 함께 해결
   ├─ 원인 분석
   ├─ 해결 방법 설명
   └─ 수정 후 재시도
```

#### 📚 Phase 1 완료 기준

```
✅ 체크리스트:

□ User, RefreshToken Entity 생성
□ UserRepository, RefreshTokenRepository 생성
□ JwtTokenProvider 구현
□ SecurityConfig 설정
□ AuthService 구현 (signup, login, refresh, logout)
□ AuthController 구현
□ 단위 테스트 작성 (20개 이상)
□ Postman API 테스트 통과
□ feature/auth → dev PR 생성
□ 코드 리뷰 & 머지
```

---

## Phase 2: CRUD 게시판

**준비 중...**

(Phase 1 완료 후 작성 예정)

---

## Phase 3: React 프론트엔드

**준비 중...**

(Phase 2 완료 후 작성 예정)

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