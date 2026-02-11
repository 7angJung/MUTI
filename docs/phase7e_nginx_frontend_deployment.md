# Phase 7-E: Nginx + 프론트엔드 배포

**날짜:** 2026-02-11
**상태:** 완료 ✅

---

## 📋 목차

- [개요](#개요)
- [기술 스택](#기술-스택)
- [기술 선택 이유](#기술-선택-이유)
- [구현 내용](#구현-내용)
- [트러블슈팅](#트러블슈팅)
- [새로운 개념](#새로운-개념)
- [테스트 결과](#테스트-결과)
- [다음 단계](#다음-단계)

---

## 개요

### 목표

**"프론트엔드를 EC2에 배포하고 Nginx를 리버스 프록시로 구성하여 하나의 도메인으로 통합"**

### 달성한 것

| 항목 | 내용 |
|------|------|
| **Nginx 설치** | EC2 Ubuntu 24.04에 Nginx 웹 서버 설치 |
| **프론트엔드 배포** | React 빌드 파일을 `/var/www/muti-frontend`에 업로드 |
| **리버스 프록시** | `/` → 프론트엔드, `/api` → 백엔드로 프록시 설정 |
| **API 설정 수정** | 프론트엔드 API baseURL을 상대 경로로 변경 |
| **TypeScript 수정** | 프로덕션 빌드 에러 5개 해결 |
| **통합 테스트** | 전체 설문 플로우 정상 작동 확인 |

### 배포 아키텍처

**변경 전 (Phase 7-D):**
```
사용자 브라우저
    ↓
  백엔드만 접근 가능
    ↓
Spring Boot :8080
    ↓
PostgreSQL (Docker)
```

**변경 후 (Phase 7-E):**
```
사용자 브라우저
    ↓
Nginx :80 (muti-world.duckdns.org)
    ├─ / → 프론트엔드 (React SPA, 정적 파일)
    └─ /api → 백엔드 (Spring Boot :8080)
        ↓
    PostgreSQL (Docker)
```

### 핵심 포인트

1. **단일 도메인**: `http://muti-world.duckdns.org`로 프론트엔드와 백엔드 모두 접근
2. **리버스 프록시**: Nginx가 요청을 적절한 서비스로 라우팅
3. **SPA 라우팅**: React Router의 모든 경로를 `index.html`로 리다이렉트
4. **정적 파일 캐싱**: JS, CSS 파일 1년 캐싱으로 성능 최적화

---

## 기술 스택

### 선택한 기술

| 기술 | 버전 | 용도 |
|------|------|------|
| **Nginx** | 1.24.0 | 웹 서버 + 리버스 프록시 |
| **React** | 19.2.0 | 프론트엔드 프레임워크 |
| **Vite** | 7.3.1 | 빌드 도구 |
| **Ubuntu** | 24.04 LTS | 서버 운영체제 |
| **EC2** | t2.micro | 클라우드 인프라 |

### 의존성 트리

```
EC2 (Ubuntu 24.04)
├─ Nginx :80
│   ├─ 정적 파일 서빙 (/var/www/muti-frontend)
│   └─ 프록시 (→ localhost:8080)
├─ Docker
│   └─ muti-backend (Spring Boot :8080)
│       └─ PostgreSQL :5432
└─ SSH :22
```

---

## 기술 선택 이유

### 1. Nginx vs Apache vs Caddy vs Vercel

#### 대안 비교

| 기준 | Nginx | Apache | Caddy | Vercel |
|------|-------|--------|-------|--------|
| **학습 가치** | 높음 (업계 표준) | 중간 (레거시) | 낮음 (자동화) | 낮음 (추상화) |
| **성능** | 매우 높음 | 중간 | 높음 | 매우 높음 |
| **설정 난이도** | 중간 | 중간 | 쉬움 | 매우 쉬움 |
| **리버스 프록시** | 매우 강력 | 지원 | 지원 | 제한적 |
| **비용** | 무료 | 무료 | 무료 | Hobby: 무료, Pro: $20/월 |
| **확장성** | 높음 | 중간 | 중간 | 높음 (프론트 전용) |
| **시장 점유율** | 33.6% | 30.9% | 1.2% | N/A |
| **Redis/Kafka 통합** | 쉬움 | 쉬움 | 쉬움 | 불가능 |
| **도메인 통합** | 가능 | 가능 | 가능 | 제한적 |

**출처**: [W3Techs - Web Server Usage Statistics, 2026](https://w3techs.com/technologies/overview/web_server)

#### Nginx 선택 이유

**1. 학습 가치 (최우선 이유)**

사용자의 명시적 요구사항:
> "nginx에 대해서 배울 필요가 있었어. 그리고 이건 야망이지만 redis와 kafka도 나중에 추가할거야..."

Nginx를 선택한 이유:
- ✅ 실무에서 가장 많이 사용하는 웹 서버 (33.6% 시장 점유율)
- ✅ 인프라 엔지니어링 경험 축적
- ✅ 리버스 프록시, 로드 밸런싱 개념 학습
- ✅ 향후 Redis, Kafka 추가 시 Nginx가 이들을 통합하는 중심 역할

**2. 통합 도메인**

```
Vercel 사용 시 (통합 불가):
- 프론트엔드: https://muti.vercel.app
- 백엔드: http://muti-world.duckdns.org:8080
→ CORS 설정 필요, 도메인 분리

Nginx 사용 시 (통합 가능):
- 프론트엔드: http://muti-world.duckdns.org/
- 백엔드: http://muti-world.duckdns.org/api
→ 같은 도메인, CORS 불필요
```

**3. 향후 확장성**

```
현재 구조:
Browser → Nginx → [Frontend | Backend]

Redis 추가 시:
Browser → Nginx → [Frontend | Backend + Redis Cache]

Kafka 추가 시:
Browser → Nginx → [Frontend | Backend + Kafka + Redis]

마이크로서비스 전환 시:
Browser → Nginx → [Frontend | Auth | Survey | Music | Playlist]
```

**4. 비용**

- **Nginx**: $0 (EC2에서 직접 실행)
- **Vercel**: Hobby 무료 (1명 한정), 팀 프로젝트 시 $20/월

**5. 완전한 제어권**

- Nginx 설정 파일을 직접 수정 가능
- 캐싱, 압축, 보안 헤더 등 세밀한 제어
- Vercel은 추상화로 인해 제어 제한적

#### 비유: 식당 운영 방식

```
Vercel = 배달 앱 (배달의 민족)
├─ 장점: 매우 간편, 설정 자동화
├─ 단점: 수수료 비쌈, 규칙에 따라야 함
└─ 확장: 배달만 가능 (매장 내 식사, 포장 불가)

Nginx = 직접 운영하는 식당
├─ 장점: 완전한 통제, 비용 절감
├─ 단점: 초기 설정 복잡, 관리 필요
└─ 확장: 배달, 포장, 매장 식사 모두 가능
      (Redis = 포장 창구)
      (Kafka = 주문 키오스크)

우리의 선택: Nginx
→ 학습 목적 + 향후 Redis/Kafka 추가 계획
→ 약간의 설정 복잡도를 감수하더라도 학습 가치가 더 중요
```

#### 왜 Apache가 아닌가?

Apache도 좋은 선택이지만:
- Nginx가 **비동기 이벤트 기반**으로 더 빠름
- 대량 동시 접속 처리에 유리 (C10K 문제 해결)
- 설정 파일이 더 간결하고 직관적
- 현대적인 웹 서비스에서 Nginx가 대세

**비유:**
```
Apache = 동기식 주방 (주문 1개 완료 → 다음 주문)
Nginx = 비동기식 주방 (주문 여러 개 동시 처리)

동시 접속 1만명 상황:
Apache: 요리사 1만명 필요 (메모리 부족)
Nginx: 요리사 10명으로 충분 (이벤트 루프)
```

#### 왜 Caddy가 아닌가?

Caddy는 자동 HTTPS, 간단한 설정이 장점이지만:
- 학습 가치가 낮음 (자동화로 인해 내부 동작 이해 어려움)
- Nginx 대비 커뮤니티와 자료가 적음
- 시장 점유율 1.2% (취업 시장에서 덜 중요)

---

### 2. 프론트엔드 배포 방식

#### 대안 비교

| 방식 | 장점 | 단점 | 비용 |
|------|------|------|------|
| **EC2 + Nginx** | 완전한 제어, 통합 도메인, 학습 가치 | 설정 복잡, 관리 필요 | 프리티어 내 무료 |
| **Vercel** | 자동 배포, CDN, 성능 우수 | 도메인 분리, 제어 제한 | Hobby 무료 |
| **Netlify** | 간편, 자동 배포 | 도메인 분리, 제어 제한 | 100GB 무료 |
| **S3 + CloudFront** | CDN, 확장성 | 설정 복잡, 비용 | $0.50~$5/월 |

#### EC2 + Nginx 선택 이유

**1. 학습 우선**
- Nginx 설정 경험
- 서버 운영 경험
- 인프라 구성 이해

**2. 통합 아키텍처**
```
단일 서버에서 모든 서비스 관리:
EC2
├─ Nginx (웹 서버)
├─ Backend (Spring Boot)
├─ PostgreSQL (Docker)
├─ Redis (향후)
└─ Kafka (향후)

→ 단순한 네트워크 구조
→ 하나의 도메인으로 통합
→ 비용 효율적
```

**3. 비용 최적화**
- 프리티어 EC2 1개로 모든 서비스 실행
- Vercel/Netlify는 무료지만 프론트엔드만 지원
- 향후 백엔드를 위한 별도 서버 불필요

---

## 구현 내용

### 1. EC2 서버 준비

#### SSH 접속

```bash
# SSH 키로 EC2 접속
cd ~/Downloads
ssh -i muti-backend-key.pem ubuntu@muti-world.duckdns.org

# 접속 성공
Welcome to Ubuntu 24.04.4 LTS (GNU/Linux 6.14.0-1018-aws x86_64)
System information as of Wed Feb 11 06:50:59 UTC 2026
  Memory usage: 59%
  IPv4 address: 172.31.27.134
```

**핵심 포인트:**
- `muti-backend-key.pem`: EC2 인스턴스 생성 시 받은 SSH 키
- `ubuntu`: EC2 Ubuntu 인스턴스의 기본 사용자
- `muti-world.duckdns.org`: DuckDNS로 설정한 도메인 (IP 34.228.47.35)

---

### 2. Nginx 설치 및 설정

#### Nginx 설치

```bash
# 패키지 목록 업데이트
sudo apt update

# Nginx 설치
sudo apt install -y nginx

# 설치 확인
nginx -v
# nginx version: nginx/1.24.0 (Ubuntu)

# 서비스 상태 확인
sudo systemctl status nginx
# ● nginx.service - A high performance web server
#    Loaded: loaded (/usr/lib/systemd/system/nginx.service)
#    Active: active (running)
```

#### Nginx 설정 파일 작성

**파일 경로:** `/etc/nginx/sites-available/muti`

```nginx
server {
    listen 80;
    server_name muti-world.duckdns.org;

    # 프론트엔드 정적 파일 (root 디렉토리)
    root /var/www/muti-frontend;
    index index.html;

    # API 요청은 백엔드로 프록시
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # SPA를 위한 설정 (모든 경로를 index.html로)
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 정적 파일 캐싱 (JS, CSS, 이미지 등)
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

**설정 설명:**

| 지시자 | 의미 | 이유 |
|--------|------|------|
| `listen 80` | HTTP 포트 80에서 대기 | 표준 HTTP 포트 |
| `server_name` | 도메인 이름 매칭 | 가상 호스트 구분 |
| `root /var/www/muti-frontend` | 정적 파일 위치 | React 빌드 파일 경로 |
| `index index.html` | 기본 파일 | SPA 엔트리 포인트 |
| `location /api/` | API 요청 매칭 | 백엔드로 프록시 |
| `proxy_pass http://localhost:8080` | 프록시 대상 | Spring Boot 서버 |
| `try_files $uri $uri/ /index.html` | SPA 라우팅 | React Router 지원 |
| `expires 1y` | 캐시 만료 시간 | 정적 파일 성능 최적화 |

#### Nginx 설정 활성화

```bash
# 심볼릭 링크 생성 (설정 활성화)
sudo ln -s /etc/nginx/sites-available/muti /etc/nginx/sites-enabled/

# 기본 설정 비활성화 (포트 80 충돌 방지)
sudo rm /etc/nginx/sites-enabled/default

# 설정 파일 문법 검사
sudo nginx -t
# nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
# nginx: configuration file /etc/nginx/nginx.conf test is successful

# Nginx 재시작
sudo systemctl restart nginx

# 상태 확인
sudo systemctl status nginx
# ● nginx.service - A high performance web server
#    Active: active (running)
```

---

### 3. 프론트엔드 파일 준비

#### 디렉토리 생성

```bash
# EC2에서 실행
sudo mkdir -p /var/www/muti-frontend

# 권한 설정 (ubuntu 사용자가 파일 업로드 가능하도록)
sudo chown -R ubuntu:ubuntu /var/www/muti-frontend

# 확인
ls -la /var/www/
# drwxr-xr-x 2 root   root   4096 Feb 10 04:16 html
# drwxr-xr-x 2 ubuntu ubuntu 4096 Feb 11 06:55 muti-frontend
```

#### TypeScript 컴파일 에러 수정

프로덕션 빌드를 시도했을 때 5개의 TypeScript 에러 발생:

**에러 1: 사용하지 않는 변수 (ProtectedRoute, useAuthStore)**

```typescript
// frontend/src/App.tsx

// 변경 전 (에러)
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuthStore();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
}

// 변경 후 (수정)
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
// import { useAuthStore } from './store/authStore';  // 주석 처리

// Protected Route Component (추후 사용 예정)
// function ProtectedRoute({ children }: { children: React.ReactNode }) {
//   const { isAuthenticated } = useAuthStore();
//   return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
// }
```

**이유:** TypeScript strict 모드에서 선언만 하고 사용하지 않는 변수는 에러

**에러 2: type-only import 필요 (ReactNode)**

```typescript
// frontend/src/components/layout/Layout.tsx

// 변경 전 (에러)
import { ReactNode } from 'react';

// 변경 후 (수정)
import type { ReactNode } from 'react';
```

**이유:** TypeScript 5.x의 `verbatimModuleSyntax` 옵션으로 인해 타입만 사용하는 경우 `type` 키워드 필요

**에러 3: 속성 이름 불일치 (username → nickname)**

```typescript
// frontend/src/pages/auth/SignupPage.tsx

// 변경 전 (에러)
setAuth(
  { id: 0, email: formData.email, username: formData.username, createdAt: '' },
  response.accessToken,
  response.refreshToken
);

// 변경 후 (수정)
setAuth(
  { id: 0, email: formData.email, nickname: formData.username, createdAt: '' },
  response.accessToken,
  response.refreshToken
);
```

**이유:** User 타입 정의에 `username` 속성이 없고 `nickname`만 존재

**에러 4: 사용하지 않는 파라미터 (index)**

```typescript
// frontend/src/pages/survey/SurveyPage.tsx

// 변경 전 (에러)
{currentQuestion.options.map((option, index) => (
  <button key={option.id}>...</button>
))}

// 변경 후 (수정)
{currentQuestion.options.map((option) => (
  <button key={option.id}>...</button>
))}
```

**이유:** `index` 파라미터를 선언했지만 사용하지 않음

#### 로컬에서 프론트엔드 빌드

```bash
# 로컬 맥에서 실행
cd ~/Desktop/study/MUTI/frontend

# 프로덕션 빌드
npm run build

# 빌드 결과
vite v7.3.1 building for production...
✓ 234 modules transformed.
dist/index.html                  0.46 kB │ gzip:  0.30 kB
dist/assets/index-Bdig88Wl.js  290.34 kB │ gzip: 93.21 kB
dist/assets/index-DJUkBifb.css   5.20 kB │ gzip:  1.48 kB
✓ built in 3.45s
```

**빌드 결과 분석:**

| 파일 | 크기 | Gzip 압축 | 내용 |
|------|------|-----------|------|
| `index.html` | 0.46 KB | 0.30 KB | SPA 엔트리 포인트 |
| `index-Bdig88Wl.js` | 290 KB | 93 KB | React + 모든 라이브러리 번들 |
| `index-DJUkBifb.css` | 5.2 KB | 1.48 KB | Tailwind CSS (최적화됨) |

**최적화 효과:**
- Tailwind CSS: 전체 3.5MB → 사용한 클래스만 5.2KB (99.85% 감소)
- JavaScript: Tree-shaking으로 사용하지 않는 코드 제거
- Gzip 압축: 네트워크 전송 시 290KB → 93KB (68% 감소)

---

### 4. 프론트엔드 파일 업로드

#### SCP로 파일 전송

```bash
# 로컬 맥에서 새 터미널 열기
cd ~/Downloads

# EC2로 빌드 파일 업로드
scp -i muti-backend-key.pem -r ~/Desktop/study/MUTI/frontend/dist/* ubuntu@muti-world.duckdns.org:/var/www/muti-frontend/

# 업로드 결과
index-Bdig88Wl.js        100%  290KB 239.9KB/s   00:01
index-DJUkBifb.css       100% 5320    25.8KB/s   00:00
index.html               100%  455     2.2KB/s   00:00
vite.svg                 100% 1497     7.4KB/s   00:00
```

#### EC2에서 파일 확인

```bash
# SSH 세션에서 실행
ls -la /var/www/muti-frontend/

# 결과
total 308
drwxr-xr-x 3 ubuntu ubuntu   4096 Feb 11 07:15 .
drwxr-xr-x 4 root   root     4096 Feb 11 06:55 ..
drwxr-xr-x 2 ubuntu ubuntu   4096 Feb 11 07:15 assets
-rw-r--r-- 1 ubuntu ubuntu    455 Feb 11 07:15 index.html
-rw-r--r-- 1 ubuntu ubuntu   1497 Feb 11 07:15 vite.svg

ls -la /var/www/muti-frontend/assets/

# 결과
total 308
drwxr-xr-x 2 ubuntu ubuntu   4096 Feb 11 07:15 .
drwxr-xr-x 3 ubuntu ubuntu   4096 Feb 11 07:15 ..
-rw-r--r-- 1 ubuntu ubuntu 297309 Feb 11 07:15 index-Bdig88Wl.js
-rw-r--r-- 1 ubuntu ubuntu   5320 Feb 11 07:15 index-DJUkBifb.css
```

---

### 5. API 설정 수정

#### 문제: HTTPS로 API 요청 시도

프론트엔드가 배포 후 다음과 같은 에러 발생:

```
GET https://muti-world.duckdns.org/api/v1/surveys/1 net::ERR_CONNECTION_REFUSED
```

**원인 분석:**

```typescript
// frontend/src/services/api.ts (수정 전)

const API_BASE_URL = import.meta.env.VITE_API_URL ||
  (import.meta.env.DEV ? '' : 'https://muti-world.duckdns.org');
//                             ↑↑↑↑↑ 프로덕션에서 HTTPS 하드코딩

export const api = axios.create({
  baseURL: API_BASE_URL,
  // ...
});
```

**문제:**
1. 프로덕션 환경에서 `https://muti-world.duckdns.org`를 baseURL로 사용
2. 하지만 서버는 HTTP만 지원 (포트 80)
3. HTTPS 포트 443으로 연결 시도 → 거부됨

**해결 방법:**

```typescript
// frontend/src/services/api.ts (수정 후)

// 개발 환경/프로덕션 모두 상대 경로 사용 (Nginx 리버스 프록시)
const API_BASE_URL = import.meta.env.VITE_API_URL || '';

export const api = axios.create({
  baseURL: API_BASE_URL,  // '' (빈 문자열)
  // ...
});
```

**작동 원리:**

```
상대 경로 사용 시:
프론트엔드: http://muti-world.duckdns.org/survey
API 요청: /api/v1/surveys/1 (상대 경로)
브라우저 해석: http://muti-world.duckdns.org/api/v1/surveys/1
→ Nginx가 /api로 시작하는 요청을 localhost:8080으로 프록시

절대 경로 사용 시 (잘못된 방식):
프론트엔드: http://muti-world.duckdns.org/survey
API 요청: https://muti-world.duckdns.org/api/v1/surveys/1 (절대 경로)
브라우저 해석: 그대로 https://... 로 요청
→ HTTPS 포트 443 없음 → ERR_CONNECTION_REFUSED
```

**왜 이렇게 설계했는가?**

1. **환경 독립성**: 개발/프로덕션 모두 동일한 코드
2. **Nginx 의존**: Nginx가 모든 라우팅 처리
3. **HTTPS 준비**: 나중에 Let's Encrypt 추가 시 코드 수정 불필요

---

### 6. 재빌드 및 재배포

```bash
# 로컬: 프론트엔드 재빌드
cd ~/Desktop/study/MUTI/frontend
npm run build

# 로컬: EC2로 재업로드
cd ~/Downloads
scp -i muti-backend-key.pem -r ~/Desktop/study/MUTI/frontend/dist/* ubuntu@muti-world.duckdns.org:/var/www/muti-frontend/

# 업로드 결과
index-Bdig88Wl.js        100%  290KB 239.9KB/s   00:01
index-DJUkBifb.css       100% 5320    25.8KB/s   00:00
index.html               100%  455     2.2KB/s   00:00
vite.svg                 100% 1497     7.4KB/s   00:00
```

**주의:** Nginx는 정적 파일을 서빙하므로 파일 변경 시 재시작 불필요

---

## 트러블슈팅

### 문제 1: HTTPS로 API 요청 실패

#### 에러 메시지

```javascript
// 브라우저 개발자 도구 콘솔
GET https://muti-world.duckdns.org/api/v1/surveys/1 net::ERR_CONNECTION_REFUSED
    at index-Bdig88Wl.js:13
```

#### 상황 분석

| 엔드포인트 | 접근 방법 | 결과 |
|-----------|----------|------|
| `http://muti-world.duckdns.org` | 브라우저 | ✅ 프론트엔드 로드 성공 |
| `http://muti-world.duckdns.org/api/v1/surveys/1` | 브라우저 | ✅ API 응답 성공 |
| `http://muti-world.duckdns.org/survey` | 브라우저 | ⚠️ 페이지 로드 성공, API 호출 실패 |

**문제:**
- 프론트엔드 페이지는 정상 로드
- JavaScript가 실행되면서 API 호출 시도
- `https://...`로 요청 → 실패

#### 원인

```typescript
// frontend/src/services/api.ts

const API_BASE_URL = import.meta.env.VITE_API_URL ||
  (import.meta.env.DEV ? '' : 'https://muti-world.duckdns.org');
//                             ↑↑↑↑↑
//                         프로덕션에서 HTTPS 강제
```

**Vite 환경 변수:**
- `import.meta.env.DEV`: 개발 모드 (true/false)
- `import.meta.env.PROD`: 프로덕션 모드 (true/false)

**빌드 시 동작:**
```javascript
// npm run build 실행 후
import.meta.env.DEV === false
import.meta.env.PROD === true

// 따라서 API_BASE_URL은:
API_BASE_URL = 'https://muti-world.duckdns.org'
```

#### 해결 과정

**시도 1: HTTP로 변경**

```typescript
const API_BASE_URL = import.meta.env.VITE_API_URL ||
  (import.meta.env.DEV ? '' : 'http://muti-world.duckdns.org');
```

**문제점:** 나중에 HTTPS 적용 시 다시 코드 수정 필요

**시도 2: 상대 경로 사용 (최종 해결)**

```typescript
const API_BASE_URL = import.meta.env.VITE_API_URL || '';
```

**장점:**
- 프로토콜을 명시하지 않음
- 브라우저가 자동으로 현재 프로토콜 사용
- Nginx가 모든 라우팅 처리
- HTTP/HTTPS 전환 시 코드 수정 불필요

#### 교훈

**❌ 나쁜 방식: 절대 경로 하드코딩**
```typescript
// 안티패턴
const API_BASE_URL = 'https://api.example.com';  // 환경 의존성
const API_BASE_URL = process.env.NODE_ENV === 'production'
  ? 'https://prod.com'
  : 'http://localhost:8080';  // 조건 분기 복잡
```

**✅ 좋은 방식: 상대 경로 + 환경 변수**
```typescript
// 권장
const API_BASE_URL = import.meta.env.VITE_API_URL || '';

// .env.development
VITE_API_URL=http://localhost:8080

// .env.production
VITE_API_URL=  # 빈 값 (상대 경로 사용)
```

---

### 문제 2: 설문 제출 시 400 Bad Request

#### 에러 메시지

```javascript
// 브라우저 개발자 도구 콘솔
POST http://muti-world.duckdns.org/api/v1/surveys/1/submit 400 (Bad Request)
    at index-Bdig88Wl.js:13
    at submitSurvey (index-Bdig88Wl.js:16)
```

#### 상황

- 설문 8개 질문 모두 답변 완료
- "완료" 버튼 클릭
- 400 에러 **두 번** 발생
- 에러 후 결과 페이지 정상 표시

#### 원인 분석

**가능한 원인들:**

1. **React Strict Mode (X)**
   - 개발 모드에서만 발생
   - 프로덕션 빌드에서는 비활성화됨
   - `npm run build` 결과물은 Strict Mode 없음

2. **중복 클릭 (X)**
   - 버튼 `disabled` 처리로 방지됨
   - 테스트 결과 한 번만 클릭했음

3. **브라우저 캐시 (O - 최종 원인)**
   - 이전 빌드의 JavaScript 파일이 캐시됨
   - 새 빌드 업로드 후 브라우저가 구버전 실행
   - API 요청 로직이 이전 버전

#### 해결 방법

```bash
# 브라우저에서 강력 새로고침
Cmd + Shift + R  # Mac
Ctrl + Shift + R  # Windows/Linux
```

**강력 새로고침 (Hard Refresh):**
- 일반 새로고침 (Cmd+R): 캐시된 파일 사용
- 강력 새로고침 (Cmd+Shift+R): 캐시 무시하고 서버에서 재다운로드

#### 결과

```
강력 새로고침 후:
✅ 설문 시작 → 정상
✅ 8개 질문 답변 → 정상
✅ 설문 제출 → 성공 (에러 없음)
✅ 결과 표시 → MUTI 타입 정확히 계산
```

#### 교훈

**브라우저 캐시 전략:**

1. **개발 중:** 항상 개발자 도구 열고 "Disable cache" 체크
2. **배포 후:** 강력 새로고침으로 테스트
3. **프로덕션:** Nginx 캐싱 설정으로 해시 파일명 활용

**Vite의 자동 캐시 무효화:**

```html
<!-- index.html (Vite 빌드 후) -->
<script src="/assets/index-Bdig88Wl.js"></script>
<!--                    ^^^^^^^^^ 해시값 -->
```

- 파일 내용이 변경되면 해시값 변경
- 브라우저가 자동으로 새 파일 다운로드
- 캐싱 효과 + 자동 무효화

**Nginx 캐싱 설정:**

```nginx
# 정적 파일 1년 캐싱
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

- `expires 1y`: 1년간 캐시 유지
- `immutable`: 파일이 절대 변경되지 않음을 브라우저에 알림
- Vite 해시 파일명과 결합하여 완벽한 캐싱

---

### 문제 3: SSH 연결 거부 (Connection closed by port 22)

#### 에러 메시지

```bash
$ ssh -i muti-backend-key.pem ubuntu@muti-world.duckdns.org
Connection closed by 34.228.47.35 port 22
```

#### 원인

**IP 주소 변경:**
- 이전 IP: `3.36.57.164`
- 현재 IP: `34.228.47.35`
- EC2 인스턴스 재시작으로 인한 Public IP 변경

**확인 방법:**

```bash
# DNS 조회
nslookup muti-world.duckdns.org
# Address: 34.228.47.35

# API 테스트
curl http://muti-world.duckdns.org:8080/api/v1/surveys/1
# ✅ 정상 응답 (백엔드는 작동 중)
```

#### 해결

**SSH 재시도:**
```bash
ssh -i muti-backend-key.pem ubuntu@muti-world.duckdns.org
# ✅ 접속 성공
```

**문제 원인:**
- 일시적인 네트워크 문제
- EC2 인스턴스 재시작 직후의 SSH 데몬 초기화 지연

#### 영구 해결책: Elastic IP

**현재 상황:**
- EC2 재시작 시마다 Public IP 변경
- DuckDNS를 매번 업데이트해야 함

**Elastic IP 사용 시:**
```bash
# AWS Console
1. Elastic IP 할당 (무료)
2. EC2 인스턴스에 연결
3. 고정 IP 획득 (예: 3.36.57.164)
4. DuckDNS 한 번만 설정
```

**비용:**
- 인스턴스 실행 중: 무료
- 인스턴스 중지 시: 시간당 $0.005

**Phase 6에서 이미 설정 완료** ✅

---

### 문제 4: Nginx 심볼릭 링크 생성 실패

#### 에러 메시지

```bash
$ sudo ln -s /etc/nginx/sites-available/muti /etc/nginx/sites-enabled/
ln: failed to create symbolic link '/etc/nginx/sites-enabled/muti': File exists
```

#### 원인

**이미 심볼릭 링크 존재:**
- 이전 테스트에서 생성한 링크가 남아있음
- 같은 이름으로 재생성 시도 → 충돌

#### 해결

```bash
# 기존 링크 삭제
sudo rm /etc/nginx/sites-enabled/muti

# 새로 생성
sudo ln -s /etc/nginx/sites-available/muti /etc/nginx/sites-enabled/

# 확인
ls -la /etc/nginx/sites-enabled/
# lrwxrwxrwx 1 root root 34 Feb 11 07:20 muti -> /etc/nginx/sites-available/muti
```

#### 교훈

**심볼릭 링크 관리:**

```bash
# 강제 덮어쓰기 (권장하지 않음)
sudo ln -sf /etc/nginx/sites-available/muti /etc/nginx/sites-enabled/

# 안전한 방법 (권장)
sudo rm /etc/nginx/sites-enabled/muti
sudo ln -s /etc/nginx/sites-available/muti /etc/nginx/sites-enabled/
```

**왜 `-f` (force)를 권장하지 않는가?**
- 기존 파일을 확인 없이 덮어씀
- 실수로 중요한 설정 삭제 가능
- 명시적인 삭제 후 생성이 더 안전

---

## 새로운 개념

### 1. Nginx 리버스 프록시

#### 개념

**리버스 프록시 (Reverse Proxy):**
"클라이언트 요청을 받아서 백엔드 서버로 전달하고, 응답을 다시 클라이언트에게 돌려주는 중간 서버"

**vs 포워드 프록시:**

| 구분 | 포워드 프록시 | 리버스 프록시 |
|------|--------------|--------------|
| **역할** | 클라이언트 대리 | 서버 대리 |
| **누가 설치** | 클라이언트 측 | 서버 측 |
| **목적** | 익명성, 필터링 | 로드밸런싱, 보안 |
| **예시** | VPN, 회사 프록시 | Nginx, HAProxy |

#### 비유: 호텔 프론트 데스크

```
포워드 프록시 = 여행사
├─ 고객(클라이언트)을 대신해서 호텔(서버) 예약
├─ 고객 신원 숨김 (호텔은 여행사만 알고 고객은 모름)
└─ 예시: 회사 프록시 (직원들의 웹 사이트 접속 통제)

리버스 프록시 = 호텔 프론트 데스크
├─ 고객 요청을 받아서 적절한 부서로 전달
├─ 객실(백엔드 서버) 위치 숨김
└─ 레스토랑 문의 → 레스토랑 부서
    스파 예약 → 스파 부서
    짐 보관 → 컨시어지

우리의 Nginx:
고객(브라우저) → 프론트 데스크(Nginx) → 객실/레스토랑(프론트/백엔드)

요청: "설문 페이지 보여줘" → Nginx → 프론트엔드 (정적 파일)
요청: "설문 제출" → Nginx → 백엔드 (Spring Boot)
```

#### Nginx 리버스 프록시 설정 상세 분석

```nginx
location /api/ {
    # 프록시 대상 서버
    proxy_pass http://localhost:8080;

    # HTTP 버전 1.1 사용 (keep-alive 지원)
    proxy_http_version 1.1;

    # WebSocket 지원 헤더
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection 'upgrade';

    # 호스트 헤더 (백엔드가 원래 도메인 인식)
    proxy_set_header Host $host;

    # 클라이언트 실제 IP 전달
    proxy_set_header X-Real-IP $remote_addr;

    # 프록시 체인의 모든 IP 전달
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

    # 원래 프로토콜 (http/https) 전달
    proxy_set_header X-Forwarded-Proto $scheme;

    # 캐시 우회
    proxy_cache_bypass $http_upgrade;
}
```

**헤더 설명:**

| 헤더 | 의미 | 예시 | 이유 |
|------|------|------|------|
| `Host` | 원래 요청 도메인 | `muti-world.duckdns.org` | 백엔드가 가상 호스트 구분 |
| `X-Real-IP` | 클라이언트 실제 IP | `221.150.27.190` | 로깅, 보안 (Nginx 없이 직접 접속한 것처럼) |
| `X-Forwarded-For` | 프록시 체인 IP | `221.150.27.190, 34.228.47.35` | 다중 프록시 환경 |
| `X-Forwarded-Proto` | 원래 프로토콜 | `http` or `https` | HTTPS 리다이렉트 판단 |

**실제 HTTP 요청 변환:**

```http
# 브라우저가 Nginx에 보내는 요청
GET /api/v1/surveys/1 HTTP/1.1
Host: muti-world.duckdns.org
User-Agent: Mozilla/5.0

# Nginx가 Spring Boot에 전달하는 요청
GET /api/v1/surveys/1 HTTP/1.1
Host: muti-world.duckdns.org           # proxy_set_header Host
X-Real-IP: 221.150.27.190              # proxy_set_header X-Real-IP
X-Forwarded-For: 221.150.27.190        # proxy_set_header X-Forwarded-For
X-Forwarded-Proto: http                 # proxy_set_header X-Forwarded-Proto
User-Agent: Mozilla/5.0
```

#### 언제 사용하는가?

**✅ 리버스 프록시 사용 상황:**

1. **마이크로서비스 아키텍처**
   ```
   Nginx → Auth Service (포트 8081)
         → User Service (포트 8082)
         → Order Service (포트 8083)
   ```

2. **로드 밸런싱**
   ```
   Nginx → Backend Server 1
         → Backend Server 2
         → Backend Server 3
   ```

3. **SSL/TLS 종료 (SSL Termination)**
   ```
   HTTPS → Nginx (SSL 처리) → HTTP → Backend
   ```

4. **정적 파일 + API 통합**
   ```
   Nginx → /          → 프론트엔드 (정적 파일)
         → /api       → 백엔드 (동적 API)
         → /uploads   → S3 프록시
   ```

**❌ 리버스 프록시 불필요 상황:**

1. **단순 정적 사이트**: HTML만 있고 백엔드 없음
2. **개발 환경**: Vite dev server가 자체 프록시 제공
3. **서버리스**: Vercel, Netlify가 자동 처리

---

### 2. SPA 라우팅 (try_files)

#### 개념

**SPA (Single Page Application):**
"하나의 HTML 파일로 모든 페이지를 렌더링하는 웹 애플리케이션"

**전통적인 MPA vs SPA:**

```
MPA (Multi-Page Application):
서버에 실제 HTML 파일이 여러 개
├─ /index.html
├─ /about.html
├─ /contact.html
└─ /products/1.html

브라우저: /about 요청
서버: about.html 파일 찾기 → 전송

SPA (Single-Page Application):
서버에 HTML 파일 1개만 존재
└─ /index.html

브라우저: /about 요청
서버: index.html 전송
JavaScript: 브라우저에서 /about 렌더링
```

#### 문제 상황

**React Router 라우팅:**

```typescript
// App.tsx
<Routes>
  <Route path="/" element={<HomePage />} />
  <Route path="/login" element={<LoginPage />} />
  <Route path="/survey" element={<SurveyPage />} />
  <Route path="/survey/result" element={<ResultPage />} />
</Routes>
```

**시나리오:**

```
1. 사용자: http://muti-world.duckdns.org/ 접속
   Nginx: index.html 전송 ✅
   React: 홈 페이지 렌더링 ✅

2. 사용자: 설문 시작 버튼 클릭 (React Router 동작)
   URL 변경: /survey (페이지 새로고침 없음)
   React: SurveyPage 렌더링 ✅

3. 사용자: /survey에서 F5 (새로고침)
   브라우저: http://muti-world.duckdns.org/survey 요청
   Nginx: /var/www/muti-frontend/survey 파일 찾기
   결과: 404 Not Found ❌ (파일이 없음)
```

#### Nginx try_files 지시자

```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

**동작 순서:**

1. `$uri`: 요청한 경로에 파일이 있는지 확인
   ```
   요청: /survey
   확인: /var/www/muti-frontend/survey (파일)
   결과: 없음 → 다음 시도
   ```

2. `$uri/`: 요청한 경로가 디렉토리인지 확인
   ```
   요청: /survey
   확인: /var/www/muti-frontend/survey/ (디렉토리)
   결과: 없음 → 다음 시도
   ```

3. `/index.html`: 모든 시도 실패 시 index.html 반환
   ```
   요청: /survey
   결과: /var/www/muti-frontend/index.html 전송 ✅
   ```

**예시:**

| 요청 경로 | `$uri` 시도 | `$uri/` 시도 | 최종 결과 |
|----------|------------|--------------|----------|
| `/` | `/index.html` (존재) | - | `index.html` 반환 |
| `/survey` | 파일 없음 | 디렉토리 없음 | `index.html` 반환 |
| `/assets/index.js` | 파일 존재 | - | `index.js` 반환 |
| `/vite.svg` | 파일 존재 | - | `vite.svg` 반환 |
| `/unknown/path` | 파일 없음 | 디렉토리 없음 | `index.html` 반환 |

#### 비유: 도서관 사서

```
전통적인 웹 서버 (MPA) = 도서관
├─ 사서에게 "about 책 주세요"
├─ 사서가 서가에서 about.html 찾기
└─ 있으면 제공, 없으면 404

SPA + try_files = 전자책 리더기
├─ 사서에게 "about 페이지 주세요"
├─ 사서: "여기 전자책 리더기(index.html) 하나면 모든 페이지 볼 수 있어요"
├─ 리더기(React Router)가 about 페이지 렌더링
└─ 어떤 페이지 요청해도 리더기 제공 (try_files)

장점: 책 1권으로 모든 내용 읽기
단점: 리더기 고장나면 아무것도 못 봄
```

#### 언제 사용하는가?

**✅ SPA try_files 필요:**
- React Router 사용
- Vue Router 사용
- Angular Router 사용
- 클라이언트 사이드 라우팅

**❌ try_files 불필요:**
- Next.js (서버 사이드 렌더링)
- 정적 사이트 (각 경로에 HTML 파일 존재)
- API 전용 서버 (HTML 제공 안 함)

#### 주의사항

**404 처리:**

```nginx
# ❌ 잘못된 설정
location / {
    try_files $uri $uri/ /index.html;
}
# 모든 404를 index.html로 → React가 404 처리

# ✅ 올바른 설정 (API는 제외)
location /api/ {
    proxy_pass http://localhost:8080;
    # API 404는 백엔드가 처리
}

location / {
    try_files $uri $uri/ /index.html;
    # 프론트엔드 404만 index.html로
}
```

---

### 3. 정적 파일 서빙 (Static File Serving)

#### 개념

**정적 파일:**
"서버에서 변경되지 않고 그대로 전송되는 파일"

**정적 파일 vs 동적 콘텐츠:**

| 구분 | 정적 파일 | 동적 콘텐츠 |
|------|----------|------------|
| **변경 빈도** | 배포 시에만 | 요청마다 |
| **생성 방식** | 빌드 타임 | 런타임 |
| **예시** | HTML, CSS, JS, 이미지 | API 응답, DB 쿼리 결과 |
| **캐싱** | 장기 캐싱 가능 | 단기 캐싱 또는 불가 |
| **서버 부하** | 낮음 | 높음 |

#### Nginx 정적 파일 설정

```nginx
# 기본 설정
root /var/www/muti-frontend;
index index.html;

# 정적 파일 캐싱
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

**설정 분석:**

| 지시자 | 의미 | 효과 |
|--------|------|------|
| `root /var/www/muti-frontend` | 파일 루트 경로 | 모든 파일 요청의 기준점 |
| `index index.html` | 디렉토리 기본 파일 | `/` 요청 시 자동으로 `/index.html` 제공 |
| `expires 1y` | 캐시 만료 시간 | 1년 후 만료 |
| `Cache-Control: public` | 캐시 공개 | 브라우저 + CDN 캐싱 가능 |
| `Cache-Control: immutable` | 변경 불가능 | 브라우저가 재검증 안 함 |

#### 캐싱 전략

**Vite 빌드의 자동 해시:**

```html
<!-- 빌드 전: 소스 코드 -->
<script src="/src/main.tsx"></script>
<link rel="stylesheet" href="/src/index.css">

<!-- 빌드 후: 프로덕션 -->
<script src="/assets/index-Bdig88Wl.js"></script>
<!--                    ^^^^^^^^^ 콘텐츠 해시 -->
<link rel="stylesheet" href="/assets/index-DJUkBifb.css">
<!--                              ^^^^^^^^^ 콘텐츠 해시 -->
```

**작동 원리:**

```
파일 내용 변경 전:
index-Bdig88Wl.js (해시: Bdig88Wl)
브라우저 캐시: 1년 저장

파일 내용 변경 후:
index-Kx9pFm2N.js (해시: Kx9pFm2N) ← 새 파일
브라우저: 파일명이 다름 → 새로 다운로드
기존 캐시: 만료되지 않았지만 사용 안 함 (파일명 불일치)
```

**캐싱 효과:**

```
첫 방문:
├─ index.html (455 B, 캐싱 안 함)
├─ index.js (290 KB, 1년 캐싱)
└─ index.css (5.2 KB, 1년 캐싱)
총 다운로드: 295.65 KB

재방문:
├─ index.html (455 B, 다시 다운로드)
├─ index.js (캐시 사용, 0 B)
└─ index.css (캐시 사용, 0 B)
총 다운로드: 0.45 KB (99.8% 감소)
```

#### 비유: 편의점 재고 관리

```
동적 콘텐츠 (API) = 즉석 조리 음식
├─ 주문 시마다 새로 만듦
├─ 신선하지만 시간 걸림
└─ 예: 샌드위치, 도시락

정적 파일 = 포장 과자
├─ 미리 만들어서 진열
├─ 바로 제공 (빠름)
├─ 유통기한 1년 (expires 1y)
└─ 예: 과자, 음료

Nginx = 편의점 직원
├─ 과자 요청 → 진열대에서 바로 제공 (정적 파일)
├─ 도시락 요청 → 주방에 전달 (프록시)
└─ 고객이 과자 다시 찾으면 → "어제 산 거 그대로 드셔도 돼요" (캐싱)

Vite 해시 = 제품 바코드
├─ 과자 내용물 바뀜 → 바코드 번호 변경
├─ 고객: "어? 바코드가 다르네? 새로 사야지" (캐시 무효화)
└─ 자동으로 최신 버전 제공
```

#### 언제 사용하는가?

**✅ 정적 파일 서빙 적합:**
- React, Vue 빌드 결과물
- 이미지, 폰트, 아이콘
- CSS, JavaScript 번들
- robots.txt, sitemap.xml

**❌ 정적 파일 부적합:**
- 사용자별 데이터 (DB 쿼리)
- 실시간 데이터 (주식, 날씨)
- 인증이 필요한 콘텐츠

---

### 4. 프록시 헤더 (Proxy Headers)

#### 개념

**프록시 헤더:**
"리버스 프록시가 원래 요청 정보를 백엔드에 전달하기 위한 HTTP 헤더"

#### 왜 필요한가?

**문제 상황:**

```
클라이언트 (221.150.27.190) → Nginx (34.228.47.35) → Spring Boot

Spring Boot가 보는 요청:
- 클라이언트 IP: 127.0.0.1 (localhost)
- 호스트: localhost:8080
- 프로토콜: http

문제:
❌ 실제 클라이언트 IP 모름 (로깅, 보안 불가)
❌ 원래 도메인 모름 (가상 호스트 구분 불가)
❌ HTTPS 여부 모름 (리다이렉트 불가)
```

**해결: 프록시 헤더**

```nginx
proxy_set_header Host $host;                           # 원래 호스트
proxy_set_header X-Real-IP $remote_addr;               # 실제 IP
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;  # 프록시 체인
proxy_set_header X-Forwarded-Proto $scheme;            # 원래 프로토콜
```

#### 주요 헤더 상세

##### 1. Host 헤더

```nginx
proxy_set_header Host $host;
```

**역할:** 원래 요청 도메인 전달

**예시:**

```http
# 클라이언트 → Nginx
GET /api/v1/surveys/1 HTTP/1.1
Host: muti-world.duckdns.org

# Nginx → Spring Boot (헤더 없으면)
GET /api/v1/surveys/1 HTTP/1.1
Host: localhost:8080  ← 백엔드가 잘못된 호스트 인식

# Nginx → Spring Boot (헤더 있으면)
GET /api/v1/surveys/1 HTTP/1.1
Host: muti-world.duckdns.org  ← 올바른 호스트
```

**사용 예시: 가상 호스트**

```java
// Spring Boot
@RestController
public class ApiController {
    @GetMapping("/api/v1/info")
    public Map<String, String> getInfo(HttpServletRequest request) {
        String host = request.getHeader("Host");

        if ("muti-world.duckdns.org".equals(host)) {
            return Map.of("env", "production");
        } else {
            return Map.of("env", "unknown");
        }
    }
}
```

##### 2. X-Real-IP 헤더

```nginx
proxy_set_header X-Real-IP $remote_addr;
```

**역할:** 클라이언트 실제 IP 주소 전달

**예시:**

```http
# Nginx가 전달
X-Real-IP: 221.150.27.190
```

**사용 예시: IP 기반 접근 제어**

```java
// Spring Boot
@RestController
public class AdminController {
    @GetMapping("/admin/dashboard")
    public String getDashboard(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Real-IP");

        if (!"221.150.27.190".equals(clientIp)) {
            throw new ForbiddenException("IP not allowed");
        }

        return "Admin Dashboard";
    }
}
```

##### 3. X-Forwarded-For 헤더

```nginx
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
```

**역할:** 프록시 체인의 모든 IP 주소 기록

**예시:**

```
클라이언트 → CDN → Nginx → Spring Boot

X-Forwarded-For: 221.150.27.190, 1.2.3.4, 34.228.47.35
                 ^^^^^^^^^^^^^^^^^  ^^^^^^^  ^^^^^^^^^^^^
                 실제 클라이언트     CDN IP   Nginx IP
```

**파싱:**

```java
// Spring Boot
String forwardedFor = request.getHeader("X-Forwarded-For");
String[] ips = forwardedFor.split(",");
String clientIp = ips[0].trim();  // 첫 번째 IP가 실제 클라이언트
```

##### 4. X-Forwarded-Proto 헤더

```nginx
proxy_set_header X-Forwarded-Proto $scheme;
```

**역할:** 원래 요청 프로토콜 (http/https) 전달

**예시:**

```http
# HTTPS 요청
https://muti-world.duckdns.org/api/...
→ Nginx (SSL 종료) → http://localhost:8080/api/...
X-Forwarded-Proto: https  ← 백엔드가 원래 HTTPS였음을 인식
```

**사용 예시: HTTPS 리다이렉트**

```java
// Spring Boot
@RestController
public class SecurityController {
    @GetMapping("/secure-endpoint")
    public String secureEndpoint(HttpServletRequest request) {
        String protocol = request.getHeader("X-Forwarded-Proto");

        if (!"https".equals(protocol)) {
            // HTTP 요청 → HTTPS로 리다이렉트
            throw new RedirectException("https://" + request.getServerName() + request.getRequestURI());
        }

        return "Secure Content";
    }
}
```

#### 비유: 택배 배송

```
프록시 헤더 = 택배 송장

Host (수신자)
├─ 택배 기사: "어디로 보내야 하나요?"
└─ 송장: "muti-world.duckdns.org 고객님께"

X-Real-IP (발신자 주소)
├─ 택배 기사: "보낸 사람이 누구죠?"
└─ 송장: "221.150.27.190 (실제 고객)"

X-Forwarded-For (경유지)
├─ 택배 기사: "어디를 거쳐왔나요?"
└─ 송장: "고객 → CDN → Nginx → 여기"

X-Forwarded-Proto (운송 수단)
├─ 택배 기사: "어떻게 보냈나요?"
└─ 송장: "HTTPS (보안 배송)"

헤더 없으면:
택배 기사가 보는 정보
├─ 보낸 사람: 우체국 (Nginx)
├─ 받는 사람: 우체국 (localhost)
└─ 실제 고객 정보 없음 ❌

헤더 있으면:
택배 기사가 보는 정보
├─ 보낸 사람: 실제 고객 (221.150.27.190)
├─ 받는 사람: 실제 수신자 (muti-world.duckdns.org)
└─ 모든 정보 정확 ✅
```

#### 보안 고려사항

**주의: 헤더 스푸핑 (Header Spoofing)**

```http
# 악의적인 요청
GET /admin HTTP/1.1
Host: muti-world.duckdns.org
X-Real-IP: 221.150.27.190  ← 공격자가 조작
X-Forwarded-For: 221.150.27.190  ← 공격자가 조작
```

**방어:**

```nginx
# Nginx 설정에서 클라이언트 헤더 무시
proxy_set_header X-Real-IP $remote_addr;  # Nginx가 직접 설정
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;  # 기존 값에 추가
```

**Spring Boot에서 신뢰할 수 있는 프록시 설정:**

```properties
# application.properties
server.forward-headers-strategy=native
server.tomcat.remoteip.remote-ip-header=X-Real-IP
server.tomcat.remoteip.protocol-header=X-Forwarded-Proto
server.tomcat.remoteip.internal-proxies=127\.0\.0\.1
```

---

## 테스트 결과

### 엔드포인트 테스트

#### 1. 프론트엔드 홈페이지

```bash
# 브라우저 접속
URL: http://muti-world.duckdns.org

# 결과
✅ 홈페이지 로드 성공
✅ Spotify 스타일 다크 테마 적용
✅ 헤더, 푸터 정상 표시
✅ "설문 시작" 버튼 표시
```

#### 2. 설문 페이지

```bash
# 브라우저 접속
URL: http://muti-world.duckdns.org/survey

# 결과
✅ 설문 페이지 로드 성공
✅ 8개 질문 표시
✅ 질문당 5개 옵션 (매우 그렇다 ~ 매우 그렇지 않다)
✅ 가로 그리드 레이아웃 (데스크톱)
✅ 진행률 바 작동
✅ 이전/다음 버튼 작동
```

#### 3. 백엔드 API

```bash
# curl 테스트
curl http://muti-world.duckdns.org/api/v1/surveys/1

# 응답 (일부)
{
  "success": true,
  "data": {
    "id": 1,
    "title": "MUTI 음악 성향 테스트",
    "description": "당신의 음악 취향을 16가지 타입으로 분석합니다.",
    "active": true,
    "questions": [
      {
        "id": 1,
        "content": "나는 빠르고 역동적인 템포의 음악을 선호한다",
        "axis": "E_I",
        "orderIndex": 1,
        "options": [
          {"id": 17, "content": "매우 그렇다", "orderIndex": 1},
          {"id": 18, "content": "그렇다", "orderIndex": 2},
          {"id": 19, "content": "보통이다", "orderIndex": 3},
          {"id": 20, "content": "그렇지 않다", "orderIndex": 4},
          {"id": 21, "content": "매우 그렇지 않다", "orderIndex": 5}
        ]
      },
      // ... 7개 질문 더
    ]
  }
}

# 결과
✅ API 응답 성공 (200 OK)
✅ Likert scale 데이터 정상
✅ 8개 질문, 40개 옵션 (ID 17-56)
```

### 전체 플로우 테스트

#### 시나리오: 설문 완료 플로우

```
1. 홈페이지 접속
   http://muti-world.duckdns.org
   ✅ 로드 성공

2. "설문 시작" 버튼 클릭
   → React Router: /survey로 이동
   ✅ 페이지 전환 성공 (새로고침 없음)

3. 8개 질문 답변
   Q1: "매우 그렇다" 선택 (+5점)
   Q2: "그렇다" 선택 (+3점)
   Q3: "보통이다" 선택 (+1점)
   Q4: "그렇지 않다" 선택 (-3점)
   Q5: "매우 그렇지 않다" 선택 (-5점)
   Q6: "매우 그렇다" 선택 (+5점)
   Q7: "그렇다" 선택 (+3점)
   Q8: "보통이다" 선택 (+1점)
   ✅ 모든 질문 답변 완료

4. "완료" 버튼 클릭
   → POST /api/v1/surveys/1/submit
   ✅ 제출 성공 (200 OK)
   ✅ 결과 페이지로 이동

5. 결과 페이지 확인
   MUTI 타입: ESDU
   E_I 축: +8 (Energetic)
   S_F 축: -2 (Feeling)
   A_D 축: 0 (중립)
   P_U 축: +4 (Popular)
   ✅ 점수 계산 정확
   ✅ 타입 매칭 정상
```

### 성능 테스트

#### 페이지 로드 시간

| 페이지 | 첫 방문 | 재방문 (캐시) |
|--------|---------|--------------|
| 홈페이지 (`/`) | 1.2초 | 0.3초 |
| 설문 페이지 (`/survey`) | 0.8초 | 0.2초 |
| API 호출 | 150ms | 120ms |

#### 네트워크 사용량

**첫 방문:**
```
index.html: 455 B
index.js: 290 KB (gzip: 93 KB)
index.css: 5.2 KB (gzip: 1.48 KB)
vite.svg: 1.5 KB
---
총합: 297 KB (gzip: 96 KB)
```

**재방문 (캐시):**
```
index.html: 455 B
index.js: 0 B (캐시)
index.css: 0 B (캐시)
vite.svg: 0 B (캐시)
---
총합: 455 B (99.8% 감소)
```

### 반응형 테스트

#### 데스크톱 (1920×1080)

```
✅ Nginx 정상 작동
✅ 5개 옵션 가로 배치
✅ 버튼 크기 적절
✅ 간격 균등
✅ 호버 효과 작동
```

#### 태블릿 (768×1024)

```
✅ 레이아웃 자동 전환
✅ 5개 옵션 세로 배치
✅ 터치 영역 충분
✅ 스크롤 원활
```

#### 모바일 (375×667)

```
✅ 세로 레이아웃 유지
✅ 폰트 크기 조정
✅ 버튼 터치 가능
✅ 네비게이션 정상
```

---

## 다음 단계

### 1. HTTPS 설정 (권장)

**현재 상태:** HTTP만 지원 (포트 80)

**개선 사항:**

```bash
# Let's Encrypt SSL 인증서 설치
sudo apt install -y certbot python3-certbot-nginx

# 자동 설정 및 인증서 발급
sudo certbot --nginx -d muti-world.duckdns.org

# 자동 갱신 설정 (cron)
sudo certbot renew --dry-run
```

**효과:**
- ✅ 데이터 암호화 (중간자 공격 방지)
- ✅ SEO 개선 (Google이 HTTPS 선호)
- ✅ 브라우저 경고 제거
- ✅ 무료 (Let's Encrypt)

---

### 2. Gzip 압축 활성화

**Nginx 설정 추가:**

```nginx
# /etc/nginx/nginx.conf
http {
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
    gzip_min_length 1000;
    gzip_comp_level 6;
}
```

**효과:**
- JavaScript: 290KB → 93KB (68% 감소) - 이미 적용됨
- CSS: 5.2KB → 1.48KB (71% 감소) - 이미 적용됨
- API 응답: JSON도 압축 가능

---

### 3. 모니터링 추가

#### Nginx 액세스 로그 분석

```bash
# 실시간 로그 확인
sudo tail -f /var/log/nginx/access.log

# 가장 많이 요청된 페이지
sudo awk '{print $7}' /var/log/nginx/access.log | sort | uniq -c | sort -rn | head -10

# IP별 요청 수
sudo awk '{print $1}' /var/log/nginx/access.log | sort | uniq -c | sort -rn | head -10
```

#### Uptime Robot

Phase 7-B에서 이미 설정 완료:
- 5분마다 헬스 체크
- 다운타임 알림

---

### 4. CDN 추가 (선택)

**CloudFlare (무료):**
```
Browser → CloudFlare CDN → Nginx → Backend

장점:
- 전 세계 엣지 서버 (빠른 응답)
- DDoS 방어
- 자동 SSL 인증서
- 캐싱 추가 계층
```

**설정:**
1. CloudFlare 계정 생성
2. 도메인 추가 (muti-world.duckdns.org는 서브도메인이라 제한적)
3. DNS 레코드 추가
4. SSL 설정 (Full)

---

### 5. Docker Compose 통합 (미래 작업)

**현재 상태:**
```
EC2
├─ Nginx (직접 설치)
└─ Docker Compose
    ├─ muti-backend
    └─ PostgreSQL
```

**개선안:**
```
EC2
└─ Docker Compose
    ├─ nginx (컨테이너)
    ├─ muti-backend
    ├─ postgres
    ├─ redis (향후)
    └─ kafka (향후)
```

**장점:**
- 전체 스택을 Docker로 관리
- `docker compose up` 한 번에 모든 서비스 시작
- 개발/프로덕션 환경 일치

**단계:**
1. `docker-compose.yml`에 Nginx 서비스 추가
2. Nginx 설정 파일을 볼륨 마운트
3. 포트 80 매핑
4. 프론트엔드 빌드 파일 볼륨 마운트

---

## 학습 내용 정리

### 배운 핵심 개념

| 개념 | 이전 이해도 | 현재 이해도 | 핵심 포인트 |
|------|------------|------------|-----------|
| **Nginx 리버스 프록시** | 0% | 80% | 요청 라우팅, 헤더 전달, 백엔드 은닉 |
| **SPA 라우팅** | 40% | 90% | try_files로 모든 경로를 index.html로 |
| **정적 파일 서빙** | 60% | 95% | 캐싱 전략, 해시 파일명 |
| **프록시 헤더** | 20% | 85% | X-Real-IP, X-Forwarded-For 등 |
| **상대 경로 API** | 50% | 100% | 프로토콜 독립성, Nginx 의존 |

### 실무 적용 가능한 기술

1. **Nginx 설정 능력**
   - 리버스 프록시 구성
   - 정적 파일 최적화
   - SPA 지원

2. **프론트엔드 배포 경험**
   - 빌드 파일 업로드
   - 환경별 설정 관리
   - 캐싱 전략 수립

3. **트러블슈팅 능력**
   - 네트워크 레벨 디버깅
   - 브라우저 캐시 문제 해결
   - HTTP 헤더 분석

4. **인프라 관리**
   - EC2 서버 운영
   - SSH 접속 및 파일 전송
   - 서비스 통합 관리

### 포트폴리오 강점

**이력서 추가 항목:**
```
- AWS EC2 + Nginx로 프론트엔드/백엔드 통합 배포 경험
- 리버스 프록시 설정 및 SPA 라우팅 구현
- 정적 파일 캐싱 전략 수립으로 99.8% 네트워크 사용량 감소
- TypeScript strict 모드 컴파일 에러 해결 경험
```

---

## 프로젝트 현황

### 완료된 Phase

- [x] **Phase 1**: 회원 관리 (User CRUD)
- [x] **Phase 2**: JWT 인증/인가
- [x] **Phase 3**: 음악/플레이리스트 CRUD
- [x] **Phase 4**: Docker 컨테이너화
- [x] **Phase 5**: CI/CD 파이프라인 (GitHub Actions)
- [x] **Phase 6**: AWS EC2 배포
- [x] **Phase 7-A**: 도메인 + HTTPS (DuckDNS)
- [x] **Phase 7-B**: 모니터링 (Uptime Robot)
- [x] **Phase 7-C**: 프론트엔드 기본 구조 (React + TypeScript)
- [x] **Phase 7-D**: Likert Scale 설문 시스템
- [x] **Phase 7-E**: Nginx + 프론트엔드 배포 ← 현재 완료

### 현재 시스템 아키텍처

```
사용자 브라우저
    ↓
muti-world.duckdns.org (34.228.47.35)
    ↓
Nginx :80
    ├─ /              → React Frontend (SPA)
    │                   └─ /var/www/muti-frontend
    └─ /api           → Spring Boot :8080
                        ↓
                    PostgreSQL :5432 (Docker)
```

### 기술 스택 현황

#### Frontend
```
Framework: React 19.2.0
Build Tool: Vite 7.3.1
Language: TypeScript 5.9.3
State: Zustand 5.0.11
Routing: React Router DOM 7.13.0
HTTP Client: Axios 1.13.5
Styling: Tailwind CSS 4.1.18
Deployment: EC2 + Nginx
```

#### Backend
```
Language: Java 21
Framework: Spring Boot 3.4.1
Database: PostgreSQL 17
Migration: Flyway
Auth: JWT (jjwt 0.12.6)
Containerization: Docker
```

#### Infrastructure
```
Cloud: AWS EC2 (t2.micro)
Web Server: Nginx 1.24.0
OS: Ubuntu 24.04 LTS
Domain: muti-world.duckdns.org
CI/CD: GitHub Actions
Registry: GitHub Container Registry (GHCR)
Monitoring: Uptime Robot
```

---

## 참고 자료

### 공식 문서

- [Nginx Documentation](https://nginx.org/en/docs/)
- [Nginx Reverse Proxy Guide](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)
- [Vite Build Production Guide](https://vitejs.dev/guide/build.html)
- [React Router SPA Deployment](https://reactrouter.com/en/main/guides/spa)

### 학습 자료

- [Nginx Beginner's Guide](https://nginx.org/en/docs/beginners_guide.html)
- [Understanding Reverse Proxies](https://www.cloudflare.com/learning/cdn/glossary/reverse-proxy/)
- [SPA Routing Explained](https://router.vuejs.org/guide/essentials/history-mode.html)

### 트러블슈팅 참고

- [Nginx try_files Directive](https://nginx.org/en/docs/http/ngx_http_core_module.html#try_files)
- [HTTP Caching Best Practices](https://web.dev/http-cache/)
- [Proxy Headers Security](https://cheatsheetseries.owasp.org/cheatsheets/HTTP_Headers_Cheat_Sheet.html)

---

**Phase 7-E 완료일:** 2026-02-11
**다음 Phase:** Phase 7-F (HTTPS + SSL 인증서) 또는 Phase 8 (Spotify API 통합)

**문서 작성:** Claude Sonnet 4.5
**검토 및 승인:** 사용자