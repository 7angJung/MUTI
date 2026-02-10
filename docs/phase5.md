## Phase 5: CI/CD 파이프라인 구축

### 5.1 Phase 5 개요

**Phase 5 완료일**: 2026년 2월 10일 ✅

Phase 5에서는 **CI/CD 파이프라인**을 구축하여 개발 프로세스를 자동화합니다.

**주요 성과:**
- ✅ GitHub Actions 워크플로우 3개 작성
- ✅ 자동 테스트 (push/PR 시)
- ✅ Docker 이미지 자동 빌드/푸시
- ✅ GitHub Container Registry 연동

**상세 문서:**
- 📄 **PHASE5_PROGRESS.md**: Phase 5 전체 진행 상황 및 상세 내용
- 🔗 **GitHub Actions**: https://github.com/7angJung/MUTI/actions

---

### 5.2 CI/CD란?

**CI (Continuous Integration) - 지속적 통합:**
- 코드 변경 시 자동으로 빌드 및 테스트
- 빠른 피드백, 버그 조기 발견

**CD (Continuous Deployment) - 지속적 배포:**
- 테스트 통과 시 자동 배포
- 수동 작업 최소화, 안정적 배포

**비유:**
- **수동 배포**: 매일 직접 빵집에 가서 빵 구매
- **CI/CD**: 빵이 구워지면 자동으로 집으로 배달

---

### 5.3 작성한 워크플로우

#### 1. test.yml - 자동 테스트

**트리거:** main/dev 브랜치 push, Pull Request

**주요 단계:**
1. JDK 17 설정
2. Gradle 캐싱 (빌드 속도 향상)
3. 테스트 실행
4. 테스트 리포트 생성

**성능:**
- 첫 실행: 2~3분
- 캐시 사용 후: 1분

---

#### 2. docker.yml - Docker 빌드/푸시

**트리거:** main/dev 브랜치 push, 버전 태그

**주요 단계:**
1. Docker Buildx 설정 (멀티 플랫폼)
2. GitHub Container Registry 로그인
3. 이미지 빌드 (amd64 + arm64)
4. 자동 태그 생성 및 푸시

**이미지 주소:**
```
ghcr.io/7angjung/muti:latest
ghcr.io/7angjung/muti:dev
ghcr.io/7angjung/muti:main-<sha>
```

---

#### 3. ci-cd.yml - 통합 파이프라인

**트리거:** main 브랜치 push (프로덕션 전용)

**실행 순서:**
```
Job 1: test (테스트)
   ↓ 성공 시
Job 2: build-and-push (Docker 빌드/푸시)
   ↓ 성공 시
Job 3: notify (배포 알림)
```

**특징:**
- 테스트 실패 시 빌드 중단 (리소스 절약)
- 순차 실행 (test → build → notify)

---

### 5.4 GitHub Container Registry (GHCR)

**정의:** GitHub에서 제공하는 Docker 이미지 레지스트리

**장점:**
- ✅ GitHub Actions와 자동 연동
- ✅ 무료 (Public repo 무제한)
- ✅ 멀티 플랫폼 지원 (amd64, arm64)
- ✅ 별도 계정 불필요

**사용법:**
```bash
# 이미지 pull
docker pull ghcr.io/7angjung/muti:latest

# 로컬 실행
docker run -p 8080:8080 ghcr.io/7angjung/muti:latest
```

---

### 5.5 최적화 기법

**1. Gradle 캐싱:**
```yaml
- uses: actions/setup-java@v4
  with:
    cache: 'gradle'
```
- 의존성 다운로드 시간 절약 (1분 → 10초)

**2. Docker Layer 캐싱:**
```yaml
cache-from: type=gha
cache-to: type=gha,mode=max
```
- 변경되지 않은 레이어 재사용 (5분 → 2분)

**3. Concurrency 제어:**
```yaml
concurrency:
  cancel-in-progress: true
```
- 이전 빌드 자동 취소, 최신 빌드만 실행

---

### 5.6 트러블슈팅

**문제: Personal Access Token 권한 부족**

```
refusing to allow a Personal Access Token to create or update workflow
without `workflow` scope
```

**해결:**
1. GitHub Settings → Tokens
2. `workflow` scope 추가 ✅
3. 로컬 credential 초기화
4. 다시 push 시 새 토큰 입력

---

### 5.7 개발 워크플로우 개선

**이전 (수동):**
```
코드 수정 → 로컬 테스트 → Docker 빌드 → 수동 푸시
(30분)
```

**이후 (자동):**
```
코드 수정 → Git push → (자동으로 모든 작업 수행)
(5분 + 자동화 8분)
```

**효과:**
- ✅ 수동 작업 0%
- ✅ 테스트 누락 방지
- ✅ 일관된 빌드 환경
- ✅ 빠른 피드백

---

### 5.8 Phase 5 완료 체크리스트

- [x] `.github/workflows` 디렉토리 생성
- [x] test.yml 작성 (65줄)
- [x] docker.yml 작성 (68줄)
- [x] ci-cd.yml 작성 (117줄)
- [x] README.md에 CI/CD 배지 추가
- [x] Personal Access Token 설정
- [x] Git commit 및 push
- [x] PHASE5_PROGRESS.md 작성

---

### 5.9 다음 단계

**Phase 6 옵션:**

1. **실제 배포 (Railway/Fly.io)**
   - 클라우드 플랫폼 배포
   - 도메인 및 HTTPS 설정
   - CI/CD 자동 배포 연동

2. **프론트엔드 개발 (React)**
   - React + TypeScript 설정
   - CI/CD 파이프라인 확장
   - Vercel/Netlify 자동 배포

3. **추가 백엔드 기능**
   - Spotify API 통합
   - 음악 추천 알고리즘

**상세 내용은 PHASE5_PROGRESS.md를 참조하세요.**

---

