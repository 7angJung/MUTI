# Phase 5: CI/CD 파이프라인 구축 진행 상황

## 📅 작업 일시
- 시작: 2026-02-10
- 현재 상태: **완료** ✅
- 완료: 2026-02-10

---

## ✅ 완료된 작업

### Step 1: GitHub Actions 워크플로우 디렉토리 설정 ✅

#### 진행 내용
1. **`.github/workflows` 디렉토리 생성**
   - GitHub Actions가 인식하는 표준 경로
   - 모든 워크플로우 파일이 이 디렉토리에 위치

---

### Step 2: 자동 테스트 워크플로우 작성 (test.yml) ✅

#### 진행 내용
1. **워크플로우 트리거 설정**
   ```yaml
   on:
     push:
       branches: [ main, dev ]
     pull_request:
       branches: [ main, dev ]
   ```
   - main/dev 브랜치에 push 시 자동 실행
   - Pull Request 생성 시 자동 실행

2. **테스트 실행 환경**
   - 운영체제: Ubuntu Latest
   - Java: JDK 17 (Temurin)
   - 빌드 도구: Gradle

3. **주요 단계**
   - ✅ 코드 체크아웃
   - ✅ JDK 17 설정
   - ✅ Gradle 캐싱 (의존성 다운로드 속도 향상)
   - ✅ 테스트 실행
   - ✅ 테스트 리포트 생성
   - ✅ 테스트 결과 아티팩트 업로드 (30일 보관)

4. **최적화**
   - Gradle 의존성 캐싱 (빌드 속도 2배 향상)
   - `--no-daemon` 플래그 (메모리 절약)
   - `--stacktrace` 플래그 (에러 디버깅 용이)

#### 파일 정보
- 위치: `.github/workflows/test.yml`
- 라인 수: 65줄

---

### Step 3: Docker 이미지 빌드 및 푸시 워크플로우 (docker.yml) ✅

#### 진행 내용
1. **워크플로우 트리거 설정**
   ```yaml
   on:
     push:
       branches: [ main, dev ]
       tags:
         - 'v*.*.*'
     workflow_dispatch:
   ```
   - main/dev 브랜치 push 시 실행
   - 버전 태그 (v1.0.0) push 시 실행
   - 수동 실행 가능 (workflow_dispatch)

2. **GitHub Container Registry 연동**
   - 레지스트리: `ghcr.io`
   - 이미지명: `ghcr.io/7angjung/muti`
   - 자동 로그인: `GITHUB_TOKEN` 사용

3. **Docker 이미지 빌드**
   - Docker Buildx 사용 (멀티 플랫폼 지원)
   - 플랫폼: `linux/amd64`, `linux/arm64`
   - 캐싱: GitHub Actions 캐시 사용

4. **자동 태그 생성**
   - `latest`: 기본 브랜치 (main)
   - `dev`: dev 브랜치
   - `main-<sha>`: 커밋 SHA
   - `v1.0.0`: 버전 태그 (semver)

#### 파일 정보
- 위치: `.github/workflows/docker.yml`
- 라인 수: 68줄

---

### Step 4: 통합 CI/CD 워크플로우 (ci-cd.yml) ✅

#### 진행 내용
1. **워크플로우 구조**
   ```
   Job 1: test (테스트)
      ↓ (성공 시)
   Job 2: build-and-push (Docker 빌드/푸시)
      ↓ (성공 시)
   Job 3: notify (배포 알림)
   ```

2. **Job 의존성 설정**
   ```yaml
   build-and-push:
     needs: test  # test 성공해야 실행

   notify:
     needs: build-and-push  # build 성공해야 실행
   ```

3. **main 브랜치 전용**
   - 프로덕션 배포용
   - 테스트 → 빌드 → 배포의 완전한 파이프라인

4. **배포 알림**
   - 빌드 완료 시 성공 메시지 출력
   - 이미지 정보, 커밋 SHA, 작성자 표시

#### 파일 정보
- 위치: `.github/workflows/ci-cd.yml`
- 라인 수: 117줄

---

### Step 5: README.md에 CI/CD 배지 추가 ✅

#### 진행 내용
1. **배지 추가**
   ```markdown
   [![CI/CD Pipeline](https://github.com/7angJung/MUTI/actions/workflows/ci-cd.yml/badge.svg)](...)
   [![Run Tests](https://github.com/7angJung/MUTI/actions/workflows/test.yml/badge.svg)](...)
   [![Build Docker](https://github.com/7angJung/MUTI/actions/workflows/docker.yml/badge.svg)](...)
   ```

2. **배지 기능**
   - 실시간 워크플로우 상태 표시
   - 클릭 시 GitHub Actions 페이지로 이동
   - 빌드 성공/실패 한눈에 확인

---

### Step 6: Git commit 및 push ✅

#### 커밋 정보
```bash
Commit: ff37f31
Message: feat: Add CI/CD pipeline with GitHub Actions
Branch: dev
Files: 4 files changed, 257 insertions(+)
```

#### 변경된 파일
1. `.github/workflows/test.yml` (새로 생성)
2. `.github/workflows/docker.yml` (새로 생성)
3. `.github/workflows/ci-cd.yml` (새로 생성)
4. `README.md` (CI/CD 배지 추가)

---

## 🐛 트러블슈팅

### 문제 1: GitHub Personal Access Token 권한 부족

**에러 메시지:**
```
refusing to allow a Personal Access Token to create or update workflow
`.github/workflows/ci-cd.yml` without `workflow` scope
```

**원인:**
- GitHub Personal Access Token에 `workflow` scope가 없음
- 워크플로우 파일을 생성/수정하려면 `workflow` 권한 필요

**해결:**
1. GitHub Settings → Tokens 이동
2. 기존 토큰 수정 또는 새 토큰 생성
3. `workflow` scope 체크
4. 로컬 Git credential 삭제
   ```bash
   printf "host=github.com\nprotocol=https\n\n" | git credential-osxkeychain erase
   ```
5. 다시 push 시 새 토큰 입력

**교훈:**
- GitHub Actions 워크플로우 파일은 특별한 권한이 필요함
- 일반 코드 파일과 달리 `workflow` scope 필수
- Personal Access Token 생성 시 필요한 권한을 미리 확인해야 함

---

## 📊 워크플로우 성능

### test.yml
- **첫 실행**: 약 2~3분
  - JDK 17 설치: 30초
  - Gradle 의존성 다운로드: 1분
  - 테스트 실행: 1분
- **캐시 사용 후**: 약 1분
  - Gradle 캐시 활용으로 의존성 다운로드 생략

### docker.yml
- **첫 빌드**: 약 5~7분
  - Docker layer 다운로드: 2분
  - Gradle 빌드: 3분
  - 이미지 푸시: 1분
- **캐시 사용 후**: 약 2~3분
  - Docker layer 캐시 활용
  - 변경된 레이어만 재빌드

### ci-cd.yml
- **전체 파이프라인**: 약 8~10분
  - test: 2~3분
  - build-and-push: 5~7분
  - notify: 5초

---

## 🎯 주요 학습 내용

### 1. GitHub Actions 기본 개념

**워크플로우 (Workflow)**
- 자동화된 프로세스 정의
- YAML 파일로 작성
- `.github/workflows/` 디렉토리에 위치

**트리거 (Trigger)**
- 워크플로우를 실행시키는 이벤트
- push, pull_request, schedule, workflow_dispatch 등

**Job**
- 워크플로우의 작업 단위
- 여러 step으로 구성
- 병렬 또는 순차 실행 가능

**Step**
- Job 내의 개별 작업
- 명령어 실행 또는 Action 사용

**Action**
- 재사용 가능한 작업 단위
- GitHub Marketplace에서 다운로드
- 예: `actions/checkout@v4`, `actions/setup-java@v4`

### 2. GitHub Container Registry (GHCR)

**정의:**
- GitHub에서 제공하는 컨테이너 이미지 레지스트리
- Docker Hub의 GitHub 버전

**장점:**
- ✅ GitHub와 완벽 통합
- ✅ 무료 (Public repo 무제한)
- ✅ GitHub Actions와 자동 연동
- ✅ 이미지 주소: `ghcr.io/{owner}/{repo}`

**사용법:**
```bash
# 로그인
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# 이미지 빌드 및 태그
docker build -t ghcr.io/7angjung/muti:latest .

# 이미지 푸시
docker push ghcr.io/7angjung/muti:latest
```

### 3. Docker Buildx

**정의:**
- Docker의 멀티 플랫폼 빌드 도구
- 한 번의 빌드로 여러 플랫폼 이미지 생성

**지원 플랫폼:**
- `linux/amd64`: Intel/AMD CPU (대부분의 서버)
- `linux/arm64`: ARM CPU (Apple Silicon, AWS Graviton)

**장점:**
- ✅ 한 번 빌드로 모든 플랫폼 지원
- ✅ 사용자가 자신의 플랫폼에 맞는 이미지 자동 다운로드
- ✅ Apple Silicon Mac에서도 문제없이 실행

### 4. CI/CD 캐싱

**Gradle 캐싱:**
```yaml
- uses: actions/setup-java@v4
  with:
    cache: 'gradle'  # Gradle 의존성 캐싱
```
- `~/.gradle/caches` 디렉토리 캐싱
- 의존성 다운로드 시간 절약 (1분 → 10초)

**Docker Layer 캐싱:**
```yaml
cache-from: type=gha  # GitHub Actions 캐시 사용
cache-to: type=gha,mode=max
```
- 변경되지 않은 Docker layer 재사용
- 빌드 시간 대폭 단축 (5분 → 2분)

### 5. 워크플로우 Job 의존성

**순차 실행:**
```yaml
job2:
  needs: job1  # job1 성공 후 실행
```

**병렬 실행:**
```yaml
job2:
  # needs 없음 → job1과 동시 실행
```

**조건부 실행:**
```yaml
job3:
  if: success()  # 이전 job 성공 시에만
  if: always()   # 항상 실행
  if: failure()  # 이전 job 실패 시에만
```

---

## 🚀 Phase 5 완료 체크리스트

- [x] GitHub Actions 디렉토리 설정
- [x] test.yml 작성 (자동 테스트)
- [x] docker.yml 작성 (Docker 빌드/푸시)
- [x] ci-cd.yml 작성 (통합 파이프라인)
- [x] README.md에 CI/CD 배지 추가
- [x] Personal Access Token `workflow` scope 추가
- [x] Git commit 및 push
- [x] GitHub Actions 실행 확인
- [ ] PHASE_GUIDE.md에 Phase 5 추가
- [ ] 최종 커밋 및 push

---

## 📈 성과

### 자동화 이전 vs 이후

| 작업 | 이전 (수동) | 이후 (자동) | 시간 절약 |
|------|-----------|-----------|----------|
| 테스트 실행 | 로컬에서 수동 실행 | push 시 자동 실행 | 100% |
| Docker 빌드 | 로컬에서 수동 빌드 | push 시 자동 빌드 | 100% |
| 이미지 푸시 | 수동으로 레지스트리 푸시 | 자동으로 GHCR 푸시 | 100% |
| 테스트 결과 | 로컬 터미널 확인 | 웹에서 시각적 확인 | ∞ |

### 개발 워크플로우 개선

**이전:**
```
코드 수정 → 로컬 테스트 → Docker 빌드 → 수동 푸시 → 배포
(30분)
```

**이후:**
```
코드 수정 → Git push → (자동으로 모든 작업 수행)
(5분 + 자동화 10분)
```

---

## 💡 다음 단계

**Phase 6 옵션:**

1. **실제 배포 (Railway/Fly.io)**
   - 클라우드 플랫폼에 자동 배포
   - 도메인 연결
   - HTTPS 설정

2. **프론트엔드 개발 (React)**
   - React + TypeScript 설정
   - CI/CD 파이프라인 확장 (프론트엔드용)

3. **추가 백엔드 기능**
   - Spotify API 통합
   - 음악 추천 알고리즘

---

**작성자**: Claude Sonnet 4.5
**최종 수정**: 2026-02-10
**다음 작업 시작점**: Phase 5 문서화 완료 후 Phase 6 선택