# MUTI 프로젝트 요구사항 및 가이드라인

> **작성일**: 2026-02-10
> **목적**: Claude가 프로젝트 작업 시 반드시 준수해야 할 원칙과 사용자 요구사항 정리

---

## 목차

1. [문서화 요구사항](#1-문서화-요구사항)
2. [작업 방식 및 워크플로우](#2-작업-방식-및-워크플로우)
3. [코드 작성 원칙](#3-코드-작성-원칙)
4. [커뮤니케이션 가이드라인](#4-커뮤니케이션-가이드라인)
5. [Phase 완료 시 체크리스트](#5-phase-완료-시-체크리스트)
6. [Git 및 버전 관리](#6-git-및-버전-관리)
7. [에러 처리 및 트러블슈팅](#7-에러-처리-및-트러블슈팅)
8. [프로젝트 현황](#8-프로젝트-현황)

---

## 1. 문서화 요구사항

### 1.1 Phase 완료 시 필수 문서화

**각 Phase가 완료될 때마다 `docs/phase{N}.md` 파일에 다음 내용을 반드시 작성:**

#### ✅ 필수 포함 항목

1. **무엇을 했는지 (What)**
   - Phase의 목표
   - 구현한 기능 목록
   - 변경된 파일 목록
   - 실행한 명령어

2. **어떤 기술 스택을 썼는지 (Tech Stack)**
   - 사용한 라이브러리/프레임워크 버전
   - 주요 의존성
   - 개발 도구

3. **대안 기술들과 비교 (Alternatives)**
   - 최소 2~3개의 대안 기술 나열
   - 각 대안의 장단점을 표로 정리
   - 객관적인 비교 (학습 곡선, 성능, 커뮤니티, 번들 크기 등)

4. **기술 선택 이유 (Why This Tech)**
   - 왜 이 기술을 채택했는지 자세히 설명
   - 프로젝트 요구사항과의 연관성
   - 장기적인 유지보수 고려사항
   - 채용 시장 트렌드 (해당되는 경우)

5. **발생한 문제와 트러블슈팅 (Issues & Solutions)**
   - 발생한 모든 에러 메시지 (전체 스택 트레이스)
   - 에러 원인 분석
   - 시도한 해결 방법들 (실패한 것도 포함)
   - 최종 해결 방법
   - 교훈 및 예방법

6. **새로운 개념 설명 (Concepts)**
   - Phase에서 처음 등장한 기술/개념
   - 해당 개념이 무엇인지 자세히 설명
   - **언제 사용하는지** (Use Cases)
   - **언제 사용하지 말아야 하는지** (Anti-patterns)
   - **비유를 통한 쉬운 설명** (일상 생활 예시)

#### ✅ 비유 작성 가이드

**좋은 비유의 조건:**
- 일상 생활에서 쉽게 접할 수 있는 것
- 개념의 핵심을 정확히 전달
- 장단점까지 비유로 설명

**예시:**

```markdown
❌ 나쁜 비유:
JWT는 토큰입니다.

✅ 좋은 비유:
JWT = 놀이공원 팔찌

전통적인 세션 (과거):
1. 입장 시 티켓 받음
2. 서버가 "123번 고객이 입장했음" 기록 (DB 저장)
3. 매번 놀이기구 탈 때마다 서버에 확인 "123번 고객 맞나요?"
4. 서버 부하 증가 (매번 DB 조회)

JWT 방식 (현재):
1. 입장 시 팔찌 받음 (JWT)
2. 팔찌에 이름, 입장 시간 등 정보 새겨져 있음
3. 놀이기구 탈 때 팔찌만 보여주면 됨
4. 직원이 팔찌 보고 직접 판단 (서버 부하 없음)

장점: 서버가 기억 안 해도 됨, 확장성 좋음
단점: 한 번 발급하면 취소 불가
```

---

### 1.2 문서 구조 템플릿

```markdown
# Phase {N}: {제목}

## 목차
- [개요](#개요)
- [기술 스택](#기술-스택)
- [기술 선택 이유](#기술-선택-이유)
- [구현 내용](#구현-내용)
- [트러블슈팅](#트러블슈팅)
- [새로운 개념](#새로운-개념)
- [다음 단계](#다음-단계)

## 개요
- 날짜:
- 목표:
- 완료 상태:

## 기술 스택
| 기술 | 버전 | 용도 |
|------|------|------|
| ... | ... | ... |

## 기술 선택 이유

### 1. {기술명} vs {대안1} vs {대안2}

**대안 비교:**

| 기준 | {기술명} | {대안1} | {대안2} |
|------|----------|---------|---------|
| ... | ... | ... | ... |

**선택 이유:**
1. ...
2. ...

**비유:**
```
...
```

## 구현 내용
...

## 트러블슈팅

### 문제 1: {문제명}

**에러 메시지:**
```
[전체 에러 메시지]
```

**원인:**
...

**해결 방법:**
```
[해결 코드/명령어]
```

**교훈:**
...

## 새로운 개념

### 1. {개념명}

**개념:**
...

**비유:**
```
...
```

**언제 사용:**
✅ ...
✅ ...

**언제 사용 안 함:**
❌ ...
❌ ...

## 다음 단계
- [ ] ...
- [ ] ...
```

---

## 2. 작업 방식 및 워크플로우

### 2.1 기본 원칙

#### ✅ Claude가 직접 해야 할 것

1. **파일 생성/수정**
   - Write, Edit 도구 사용
   - 사용자에게 승인 요청 후 실행
   - 코드, 설정 파일, 문서 등 모든 파일

2. **일회성 명령 실행**
   - 빌드 명령 (gradle build)
   - 테스트 실행 (gradle test)
   - Git 명령 (add, commit, push)
   - 패키지 설치 (npm install)

3. **정보 조회**
   - 파일 읽기 (Read)
   - 파일 검색 (Glob, Grep)
   - 시스템 상태 확인

#### ❌ 사용자가 해야 할 것

1. **지속적인 프로세스**
   - 개발 서버 실행 (npm run dev)
   - Docker 컨테이너 실행 (docker compose up)
   - 로그 모니터링

2. **브라우저 작업**
   - 웹페이지 접속
   - UI 확인
   - 수동 테스트

3. **외부 서비스 작업**
   - AWS Console 작업
   - GitHub 웹 UI 작업
   - 도메인 설정 등

### 2.2 작업 승인 프로세스

```
1. Claude: "다음 파일을 생성/수정하겠습니다:"
   - [파일 목록 및 변경 내용 요약]

2. User: "승인" 또는 피드백

3. Claude: 작업 실행

4. Claude: 결과 보고
```

### 2.3 Vite 개발 서버 재시작이 필요한 경우

**자동으로 재시작하지 않고, 사용자에게 안내:**

```markdown
**Vite 개발 서버 재시작이 필요합니다:**

1. 터미널에서 `Ctrl+C` (또는 `Cmd+C`)로 서버 중지
2. `npm run dev` 명령으로 재시작
3. 브라우저에서 `Cmd+Shift+R` (하드 리프레시)

**이유:** {캐시 문제, 새 파일 추가 등}
```

---

## 3. 코드 작성 원칙

### 3.1 보안

#### ✅ 항상 지켜야 할 것

1. **XSS 방지**
   - 사용자 입력 검증
   - HTML 이스케이프
   - React의 경우 dangerouslySetInnerHTML 사용 금지

2. **SQL Injection 방지**
   - PreparedStatement 사용 (JPA/Hibernate 사용 시 자동)
   - 직접 쿼리 작성 금지

3. **인증/인가**
   - 민감한 엔드포인트는 JWT 검증 필수
   - Spring Security의 permitAll() 최소화

4. **환경 변수 사용**
   - 비밀번호, API 키는 절대 코드에 하드코딩 금지
   - .env 파일 사용
   - .gitignore에 .env 포함 필수

#### ❌ 절대 하지 말 것

```java
// ❌ 나쁜 예
String password = "mypassword123";
String apiKey = "sk_live_abc123";
String query = "SELECT * FROM users WHERE name = '" + userName + "'";
```

```java
// ✅ 좋은 예
String password = System.getenv("DB_PASSWORD");
String apiKey = System.getenv("API_KEY");
// JPA 사용 (자동으로 안전한 쿼리 생성)
userRepository.findByName(userName);
```

### 3.2 코드 스타일

#### Java (Spring Boot)

```java
// ✅ 좋은 예
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
}
```

#### TypeScript (React)

```typescript
// ✅ 좋은 예
interface Props {
  title: string;
  onSubmit: () => void;
}

export function MyComponent({ title, onSubmit }: Props) {
  return (
    <div>
      <h1>{title}</h1>
      <button onClick={onSubmit}>Submit</button>
    </div>
  );
}
```

### 3.3 에러 처리

#### Backend (Spring Boot)

```java
// ✅ 좋은 예
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            404,
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

#### Frontend (React)

```typescript
// ✅ 좋은 예
try {
  const response = await authService.login(data);
  setAuth(response.user, response.accessToken, response.refreshToken);
  navigate('/');
} catch (error: any) {
  setError(error.response?.data?.message || '로그인에 실패했습니다.');
}
```

---

## 4. 커뮤니케이션 가이드라인

### 4.1 응답 스타일

#### ✅ 해야 할 것

1. **간결하고 명확하게**
   - 핵심만 전달
   - 불필요한 장황한 설명 제거

2. **이모지 사용 금지**
   - 사용자가 명시적으로 요청한 경우만 사용
   - 문서 작성 시 이모지 제외

3. **파일 경로 명시**
   - 코드 참조 시 `file_path:line_number` 형식
   - 예: `src/main/java/com/muti/UserController.java:25`

4. **에러 메시지 전체 포함**
   - 트러블슈팅 시 에러의 전체 스택 트레이스 포함
   - 일부만 발췌하지 말 것

#### ❌ 하지 말아야 할 것

1. **추측 금지**
   - "아마도...", "~일 것 같습니다" 사용 금지
   - 확실하지 않으면 확인 후 답변

2. **과도한 칭찬 금지**
   - "완벽합니다!", "훌륭합니다!" 등 불필요
   - 사실만 전달

3. **시간 예측 금지**
   - "10분 정도 걸릴 것 같습니다" 사용 금지
   - 작업 완료 후 소요 시간 기록은 OK

### 4.2 응답 예시

#### ✅ 좋은 응답

```markdown
PostCSS 설정 오류가 발생했습니다.

**원인:**
Tailwind CSS v4에서는 `@tailwindcss/postcss` 패키지가 필요합니다.

**해결 방법:**
`npm install -D @tailwindcss/postcss`를 실행하세요.

이후 `npm run dev`로 서버를 재시작하면 정상 작동합니다.
```

#### ❌ 나쁜 응답

```markdown
오! 이런 에러가 발생했네요! 😱

아마도 PostCSS 설정이 잘못된 것 같아요~ 🤔

걱정 마세요! 제가 완벽한 해결책을 알고 있답니다! ✨

한 10분 정도면 고칠 수 있을 거예요~ 💪
```

---

## 5. Phase 완료 시 체크리스트

### 5.1 Phase 완료 전 확인

- [ ] 모든 코드가 정상 작동하는지 테스트
- [ ] 에러가 발생하지 않는지 확인
- [ ] 문서 작성 완료 (`docs/phase{N}.md`)
- [ ] Git 커밋 및 푸시 (사용자 승인 후)
- [ ] 다음 Phase 계획 수립

### 5.2 문서 작성 체크리스트

- [ ] 무엇을 했는지 명확히 기록
- [ ] 기술 스택 목록 및 버전 기재
- [ ] 최소 2~3개의 대안 기술과 비교
- [ ] 기술 선택 이유를 3가지 이상 작성
- [ ] 발생한 모든 문제와 해결 과정 기록
- [ ] 새로운 개념은 비유를 포함해서 설명
- [ ] "언제 사용하는지" 명시
- [ ] 코드 예시 포함 (주요 구현)
- [ ] 다음 단계 제시

---

## 6. Git 및 버전 관리

### 6.1 Git 작업 원칙

#### ✅ 허용되는 Git 명령

1. **읽기 작업 (항상 OK)**
   ```bash
   git status
   git log
   git diff
   git branch
   ```

2. **안전한 쓰기 작업 (사용자 승인 후)**
   ```bash
   git add [파일명]
   git commit -m "메시지"
   git push origin [브랜치]
   git checkout -b [새브랜치]
   git merge [브랜치]
   ```

#### ❌ 절대 사용하면 안 되는 Git 명령

```bash
git push --force          # 위험! 히스토리 덮어쓰기
git reset --hard          # 위험! 작업 내용 손실
git clean -f              # 위험! 파일 삭제
git checkout .            # 위험! 변경사항 취소
git restore .             # 위험! 변경사항 취소
```

### 6.2 커밋 메시지 규칙

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅 (기능 변경 없음)
refactor: 리팩토링
test: 테스트 추가
chore: 빌드 설정 등

예시:
feat: Add user authentication with JWT
fix: Resolve CORS issue in API gateway
docs: Update Phase 3 documentation
```

### 6.3 브랜치 전략

```
main              # 프로덕션 (안정 버전)
  └─ dev          # 개발 브랜치
      └─ feature/{기능명}  # 기능별 브랜치

예시:
feature/user-auth
feature/playlist-crud
feature/music-search
```

---

## 7. 에러 처리 및 트러블슈팅

### 7.1 에러 발생 시 프로세스

1. **에러 메시지 전체 복사**
   - 전체 스택 트레이스 포함
   - 컨텍스트 정보 (어떤 작업 중이었는지)

2. **에러 원인 분석**
   - 에러 메시지 정확히 읽기
   - 관련 파일 확인
   - 최근 변경 사항 확인

3. **해결 방법 시도**
   - 공식 문서 확인
   - 이전 유사한 문제 해결 사례 참조
   - 단계별 시도 (한 번에 여러 방법 시도 금지)

4. **문서화**
   - 에러 → 원인 → 해결 과정 전체 기록
   - 예방법 제시

### 7.2 자주 발생하는 문제와 해결

#### 문제 1: Port Already in Use

```bash
# 에러
Error: Port 8080 is already in use

# 해결
lsof -i :8080          # 포트 사용 프로세스 찾기
kill -9 [PID]          # 프로세스 종료

# 또는 Docker의 경우
docker compose down    # 컨테이너 종료
```

#### 문제 2: Cannot Find Module

```bash
# 에러
Error: Cannot find module 'axios'

# 해결
npm install            # package.json 기준 재설치
# 또는
npm install axios      # 특정 패키지 설치
```

#### 문제 3: CORS Error

```javascript
// Backend (Spring Boot)
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}
```

---

## 8. 프로젝트 현황

### 8.1 완료된 Phase

- [x] **Phase 0**: 프로젝트 초기 설정
- [x] **Phase 1**: 회원 관리 (User CRUD)
- [x] **Phase 2**: JWT 인증/인가
- [x] **Phase 3**: 음악/플레이리스트 CRUD
- [x] **Phase 4**: Supabase PostgreSQL 연동
- [x] **Phase 5**: Flyway 마이그레이션
- [x] **Phase 6**: AWS EC2 배포
- [x] **Phase 7-A**: 도메인 + HTTPS (DuckDNS, Let's Encrypt)
- [x] **Phase 7-B**: 모니터링 (Uptime Robot)
- [x] **Phase 7-C**: 프론트엔드 기본 구조

### 8.2 진행 중인 작업

- [ ] **Phase 7-C**: 프론트엔드 개발 (백엔드 API 연동)
  - 기본 구조 완료 (Layout, Pages, Router)
  - 백엔드 API 테스트 필요
  - 추가 페이지 개발 예정

### 8.3 다음 계획

1. **Phase 7-C 완료**
   - 회원가입/로그인 API 연동 테스트
   - 에러 처리 개선
   - UI/UX 개선

2. **Phase 7-D: 프론트엔드 배포**
   - Vercel 배포
   - 환경 변수 설정
   - 도메인 연결

3. **Phase 8: 추가 기능**
   - Spotify API 통합 (OAuth 2.0)
   - 음악 검색
   - 플레이리스트 공유

### 8.4 기술 스택 현황

#### Backend
```
Language: Java 21
Framework: Spring Boot 3.5.10
Database: PostgreSQL (Supabase)
Migration: Flyway
Authentication: JWT (jjwt 0.12.6)
Deployment: AWS EC2 + Docker
Domain: muti-world.duckdns.org (HTTPS)
```

#### Frontend
```
Framework: React 19.2.0
Build Tool: Vite 7.3.1
Language: TypeScript 5.9.3
State Management: Zustand 5.0.11
Routing: React Router DOM 7.13.0
HTTP Client: Axios 1.13.5
Styling: Tailwind CSS 4.1.18
Deployment: (예정) Vercel
```

#### DevOps
```
CI/CD: GitHub Actions
Container: Docker + Docker Compose
Registry: GitHub Container Registry (GHCR)
Monitoring: Uptime Robot
SSL: Let's Encrypt (자동 갱신)
```

---

## 9. 사용자 선호도 및 특이사항

### 9.1 사용자 요청 사항

1. **문서화 스타일**
   - 자세하고 구체적으로 작성
   - 비유를 통한 쉬운 설명 필수
   - 이해하기 쉽게 구조화

2. **작업 방식**
   - Claude가 할 수 있는 것은 직접 수행
   - 실행 전 승인 요청
   - 사용자는 지속적인 프로세스와 브라우저 작업만

3. **디자인**
   - Spotify 다크 테마
   - 16personalitylab.ai 레이아웃 스타일

4. **코드 품질**
   - 최신 버전 사용 (다운그레이드 지양)
   - 보안 우선
   - 간결하고 유지보수 쉽게

### 9.2 사용자 피드백

1. **긍정적 피드백**
   - "최신 버전으로 잘 해왔잖아"
   - 안정성을 중요하게 생각함
   - Swagger보다 시스템 안정성 우선

2. **개선 요청**
   - 터미널 명령을 직접 실행해달라는 요청
   - 문서가 너무 길어서 Phase별로 분리
   - 요구사항을 정리한 문서 필요

---

## 10. 금지 사항 (Must NOT)

### 10.1 절대 하지 말아야 할 것

1. **보안 위험 행위**
   - [ ] 비밀번호/API 키 하드코딩
   - [ ] SQL Injection 가능한 쿼리 작성
   - [ ] XSS 취약점 만들기
   - [ ] .env 파일 Git에 커밋

2. **파괴적인 Git 명령**
   - [ ] `git push --force`
   - [ ] `git reset --hard`
   - [ ] `git clean -f`
   - [ ] `git checkout .`

3. **시스템 위험 명령**
   - [ ] `rm -rf /` 또는 유사한 명령
   - [ ] `docker system prune` (승인 없이)
   - [ ] 프로덕션 DB 직접 수정

4. **코드 품질 저하**
   - [ ] 에러를 숨기기 (try-catch로 무시)
   - [ ] 테스트 없이 배포
   - [ ] 하드코딩된 값 사용
   - [ ] 중복 코드 대량 생성

5. **문서화 누락**
   - [ ] Phase 완료 후 문서화 생략
   - [ ] 트러블슈팅 기록 누락
   - [ ] 비유 없이 개념만 설명
   - [ ] 대안 기술 비교 생략

---

## 11. 참고 자료

### 11.1 공식 문서

- [Spring Boot](https://docs.spring.io/spring-boot/index.html)
- [React](https://react.dev/)
- [Vite](https://vite.dev/)
- [Tailwind CSS](https://tailwindcss.com/)
- [Zustand](https://zustand-demo.pmnd.rs/)
- [React Router](https://reactrouter.com/)

### 11.2 프로젝트 문서

- `docs/README.md`: 전체 Phase 인덱스
- `docs/phase{N}.md`: 각 Phase 상세 문서
- `PHASE4_PROGRESS.md`: Phase 4 진행 기록
- `.env.example`: 환경 변수 예시

---

## 12. 업데이트 이력

| 날짜 | 내용 | 작성자 |
|------|------|--------|
| 2026-02-10 | 초기 문서 생성 | Claude |
| | | |
| | | |

---

**이 문서는 프로젝트 진행 중 지속적으로 업데이트됩니다.**

**마지막 업데이트**: 2026-02-10