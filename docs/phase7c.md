# Phase 7-C: 프론트엔드 개발 (React + TypeScript)

## 목차
- [7-C.1 개요](#7-c1-개요)
- [7-C.2 기술 스택 선택](#7-c2-기술-스택-선택)
- [7-C.3 기술 선택 이유](#7-c3-기술-선택-이유)
- [7-C.4 프로젝트 구조](#7-c4-프로젝트-구조)
- [7-C.5 주요 구현 내용](#7-c5-주요-구현-내용)
- [7-C.6 트러블슈팅](#7-c6-트러블슈팅)
- [7-C.7 새로운 개념 설명](#7-c7-새로운-개념-설명)
- [7-C.8 Spotify 테마 디자인](#7-c8-spotify-테마-디자인)
- [7-C.9 성능 최적화](#7-c9-성능-최적화)
- [7-C.10 배포 계획](#7-c10-배포-계획)
- [7-C.11 다음 단계](#7-c11-다음-단계)
- [7-C.12 학습 내용 정리](#7-c12-학습-내용-정리)

---

## 7-C.1 개요

**목표:**
- React 기반 프론트엔드 애플리케이션 구축
- Spotify 스타일의 다크 테마 적용
- JWT 기반 인증 시스템 구현
- 백엔드 API와 통합

**완료 시점:**
- 날짜: 2026-02-10
- 상태: 기본 구조 완료, 개발 진행 중

---

## 7-C.2 기술 스택 선택

### 선택한 기술 스택

```
프론트엔드 프레임워크: React 19.2.0
빌드 도구: Vite 7.3.1
언어: TypeScript 5.9.3
상태 관리: Zustand 5.0.11
라우팅: React Router DOM 7.13.0
HTTP 클라이언트: Axios 1.13.5
스타일링: Tailwind CSS 4.1.18
```

---

## 7-C.3 기술 선택 이유

### 1. React vs Vue vs Angular

**대안 비교:**

| 기준 | React | Vue | Angular |
|------|-------|-----|---------|
| 학습 곡선 | 중간 | 쉬움 | 어려움 |
| 생태계 | 매우 큼 | 큼 | 중간 |
| 채용 시장 | 가장 큼 | 중간 | 작음 |
| 번들 크기 | 작음 (45KB) | 매우 작음 (34KB) | 큼 (144KB) |
| TypeScript | 지원 좋음 | 지원 좋음 | 기본 내장 |
| 기업 지원 | Meta | Evan You | Google |

**React 선택 이유:**

1. **채용 시장 우위**: 국내외 기업 채용 공고의 70% 이상이 React 요구
2. **거대한 생태계**: npm에 React 관련 패키지 10만개 이상
3. **컴포넌트 재사용성**: 레고 블록처럼 조립해서 UI 구성
4. **React Hooks**: 함수형 컴포넌트로 코드 간결화
5. **커뮤니티**: StackOverflow 질문 300만개 이상 (문제 해결 쉬움)

**비유: 건축 자재 선택**

```
React = 철근 콘크리트 (표준, 어디서나 사용, 자재 구하기 쉬움)
Vue = 경량 철골 (가볍고 빠름, 소규모 건물에 적합)
Angular = 프리캐스트 콘크리트 (무겁지만 대형 건물에 견고함)

우리 선택: React
→ 취업 시장에서 가장 많이 요구하는 기술
→ 문제 발생 시 해결책 찾기 쉬움
→ 규모가 커져도 대응 가능
```

---

### 2. Vite vs Create React App vs Next.js

**대안 비교:**

| 기준 | Vite | CRA | Next.js |
|------|------|-----|---------|
| 개발 서버 속도 | 매우 빠름 ⚡ | 느림 | 빠름 |
| HMR 속도 | 즉시 | 느림 | 빠름 |
| 빌드 도구 | esbuild + Rollup | Webpack | Webpack/Turbopack |
| SSR 지원 | 플러그인 필요 | 없음 | 기본 내장 |
| 학습 곡선 | 쉬움 | 쉬움 | 중간 |
| 설정 복잡도 | 낮음 | 낮음 | 중간 |

**Vite 선택 이유:**

1. **개발 속도**: 서버 시작 0.3초 vs CRA 10초
2. **HMR (Hot Module Replacement)**: 코드 변경 시 즉시 반영 (< 50ms)
3. **최신 브라우저 최적화**: ES Module 네이티브 지원
4. **경량**: 불필요한 Webpack 설정 없음
5. **미래 지향**: React 공식 문서도 Vite 권장 (2024년부터)

**비유: 주방 환경**

```
Vite = 인덕션 (즉시 가열, 온도 조절 빠름)
CRA = 가스레인지 (예열 필요, 조절 느림)
Next.js = 전문 식당 주방 (복잡하지만 풀 코스 가능)

우리 선택: Vite
→ 단순한 SPA 개발에는 Next.js가 과함
→ 개발 중 코드 수정 → 브라우저 반영이 0.05초만에!
→ CRA는 2023년부터 React 팀이 권장 중단
```

---

### 3. TypeScript vs JavaScript

**대안 비교:**

| 기준 | TypeScript | JavaScript |
|------|-----------|-----------|
| 타입 안정성 | 있음 | 없음 |
| 에러 발견 시점 | 컴파일 시 | 런타임 시 |
| IDE 지원 | 자동완성 강력 | 제한적 |
| 학습 곡선 | 가파름 | 평탄함 |
| 코드 양 | 많음 | 적음 |
| 리팩토링 | 안전함 | 위험함 |

**TypeScript 선택 이유:**

1. **타입 안정성**: 실행 전에 버그 90% 사전 발견
2. **자동완성**: VSCode에서 API 구조 힌트 제공
3. **리팩토링 안전성**: 변수명 변경 시 자동으로 모든 곳 변경
4. **대규모 프로젝트**: 코드가 많아질수록 TypeScript가 유리
5. **취업 시장**: 대기업/스타트업 70% 이상 TypeScript 사용

**비유: 운전 보조 시스템**

```
TypeScript = 차선 이탈 경고 + 자동 브레이크
JavaScript = 수동 운전 (모든 걸 직접 판단)

예시:
// JavaScript (에러 발생 가능)
function greet(user) {
  return "Hello, " + user.name.toUpperCase(); // user가 null이면 크래시!
}

// TypeScript (에러 사전 차단)
function greet(user: User | null) {
  if (!user) return "Hello, Guest";
  return "Hello, " + user.name.toUpperCase(); // 안전함!
}

선택 이유:
→ "user.name이 존재하지 않을 수 있음" 경고를 개발 중에 받음
→ 배포 후 사용자가 에러를 만나지 않음
```

---

### 4. Zustand vs Redux vs Context API

**대안 비교:**

| 기준 | Zustand | Redux | Context API |
|------|---------|-------|-------------|
| 보일러플레이트 | 매우 적음 | 많음 | 적음 |
| 학습 곡선 | 매우 쉬움 | 어려움 | 쉬움 |
| 번들 크기 | 1.2KB | 12KB | 0KB (내장) |
| DevTools | 있음 | 강력함 | 없음 |
| 비동기 처리 | 간단 | 복잡 (thunk/saga) | 수동 |
| 성능 | 좋음 | 좋음 | 나쁨 (리렌더링) |

**Zustand 선택 이유:**

1. **간결함**: Redux 코드 100줄 → Zustand 10줄
2. **학습 용이**: 30분이면 마스터 가능
3. **경량**: 1.2KB (Redux의 1/10)
4. **Context API 문제 해결**: 불필요한 리렌더링 없음
5. **충분한 기능**: 작은 프로젝트에는 Redux가 과함

**비유: 창고 관리 시스템**

```
Context API = 공용 창고 (누가 물건 꺼내면 전체 알림 울림)
Redux = 대형 물류센터 (복잡하지만 체계적)
Zustand = 스마트 로커 (필요한 사람만 알림받음)

예시:
// Redux (복잡)
// 1. action.js
// 2. reducer.js
// 3. store.js
// 4. connect HOC
// = 최소 4개 파일, 100줄 이상

// Zustand (간단)
const useStore = create((set) => ({
  user: null,
  setUser: (user) => set({ user }),
}));
// = 1개 파일, 5줄

선택 이유:
→ 프로젝트 규모: 소형~중형 (Redux는 과함)
→ 개발 속도: 빠르게 개발하고 테스트
→ 유지보수: 코드가 간단해서 나중에 이해하기 쉬움
```

---

### 5. Tailwind CSS vs Styled-Components vs CSS Modules

**대안 비교:**

| 기준 | Tailwind | Styled-Comp | CSS Modules |
|------|----------|-------------|-------------|
| 학습 곡선 | 중간 | 중간 | 쉬움 |
| 번들 크기 | 작음 (PurgeCSS) | 증가 (JS) | 작음 |
| 개발 속도 | 매우 빠름 | 보통 | 느림 |
| 일관성 | 높음 | 중간 | 낮음 |
| 커스터마이징 | 쉬움 | 매우 쉬움 | 복잡 |
| 반응형 | 간편 | 수동 | 수동 |

**Tailwind CSS 선택 이유:**

1. **유틸리티 우선**: 클래스 조합으로 빠른 UI 구성
2. **일관성**: 디자인 토큰 (색상, 간격) 자동 적용
3. **PurgeCSS**: 사용하지 않는 CSS 자동 제거 (최종 파일 10KB 이하)
4. **반응형**: `md:`, `lg:` 접두사로 간편하게
5. **Spotify 스타일**: 다크 테마 색상 커스터마이징 용이

**비유: 옷 입는 방법**

```
CSS Modules = 양복 맞춤 제작 (시간 오래 걸림, 정확함)
Styled-Components = 옷 입고 재봉틀로 수선 (유연함)
Tailwind = 조합 가능한 액세서리 (모자+벨트+신발 조합)

예시:
// CSS Modules (느림)
.button {
  padding: 0.5rem 1rem;
  background-color: #1DB954;
  border-radius: 9999px;
  font-weight: 700;
  color: white;
}
<button className={styles.button}>클릭</button>

// Styled-Components (JS 번들 증가)
const Button = styled.button`
  padding: 0.5rem 1rem;
  background-color: #1DB954;
  border-radius: 9999px;
  font-weight: 700;
  color: white;
`;

// Tailwind (빠름)
<button className="px-4 py-2 bg-spotify-green rounded-full font-bold text-white">
  클릭
</button>

선택 이유:
→ 개발 속도: HTML 벗어나지 않고 스타일링
→ 일관성: spotify-green 한 번 정의하면 전체 적용
→ 최적화: 사용하지 않는 클래스는 빌드 시 제거
```

---

### 6. React Router vs TanStack Router

**대안 비교:**

| 기준 | React Router | TanStack Router |
|------|--------------|-----------------|
| 성숙도 | 매우 높음 (2014~) | 신생 (2023~) |
| 타입 안전성 | 보통 | 매우 강함 |
| 번들 크기 | 12KB | 14KB |
| 학습 자료 | 풍부 | 적음 |
| 커뮤니티 | 거대 | 작음 |

**React Router 선택 이유:**

1. **검증된 안정성**: 10년 이상 운영된 라이브러리
2. **풍부한 자료**: 문제 해결 시 예시 코드 쉽게 찾음
3. **호환성**: 대부분의 React 라이브러리와 잘 작동
4. **단순함**: 기본 라우팅에는 복잡한 기능 불필요

---

## 7-C.4 프로젝트 구조

```
frontend/
├── public/                    # 정적 파일
│   └── vite.svg
├── src/
│   ├── components/            # 재사용 가능한 컴포넌트
│   │   └── layout/           # 레이아웃 컴포넌트
│   │       ├── Header.tsx    # 상단 네비게이션
│   │       ├── Footer.tsx    # 하단 정보
│   │       └── Layout.tsx    # 전체 레이아웃 래퍼
│   ├── pages/                # 페이지 컴포넌트
│   │   ├── home/
│   │   │   └── HomePage.tsx  # 메인 페이지
│   │   └── auth/
│   │       ├── LoginPage.tsx # 로그인 페이지
│   │       └── SignupPage.tsx # 회원가입 페이지
│   ├── services/             # API 통신
│   │   ├── api.ts           # Axios 인스턴스
│   │   └── auth.service.ts  # 인증 API
│   ├── store/                # 상태 관리
│   │   └── authStore.ts     # 인증 상태 (Zustand)
│   ├── types/                # TypeScript 타입 정의
│   │   └── auth.ts          # 인증 관련 타입
│   ├── App.tsx              # 라우터 설정
│   ├── main.tsx             # 앱 진입점
│   └── index.css            # 글로벌 스타일
├── .env                      # 환경 변수
├── package.json             # 의존성 관리
├── tsconfig.json            # TypeScript 설정
├── vite.config.ts           # Vite 설정
├── tailwind.config.js       # Tailwind 설정
└── postcss.config.js        # PostCSS 설정
```

**폴더 구조 철학:**

```
비유: 도서관 정리

components/ = 재사용 가능한 도구 (계산기, 돋보기 등)
pages/ = 각 섹션 (소설, 과학, 역사 등)
services/ = 사서 (책 대출/반납 담당)
store/ = 대출 기록 (누가 무슨 책 빌렸는지)
types/ = 도서 분류 시스템 (ISBN, 카테고리 등)

원칙:
1. 컴포넌트는 작고 재사용 가능하게
2. 페이지는 컴포넌트를 조합해서 구성
3. 비즈니스 로직은 services에
4. 전역 상태는 store에만
```

---

## 7-C.5 주요 구현 내용

### 1. Axios 인터셉터 (JWT 자동 관리)

**파일: `src/services/api.ts`**

```typescript
import axios from 'axios';

// API 기본 URL 설정
const API_BASE_URL = import.meta.env.VITE_API_URL || 'https://muti-world.duckdns.org';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: 모든 요청에 JWT 토큰 자동 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: 401 에러 시 자동 로그아웃
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 토큰 만료 or 인증 실패
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

**개념 설명: Interceptor (인터셉터)**

```
비유: 공항 보안 검색대

출국장 (Request Interceptor):
├─ 모든 승객(요청)이 통과
├─ 자동으로 여권(JWT 토큰) 검사
└─ 없으면 추가, 있으면 그대로 통과

입국장 (Response Interceptor):
├─ 모든 도착 승객(응답) 확인
├─ 비자 문제(401 에러) 발견
└─ 자동으로 출입국 사무소(로그인 페이지)로 이동

장점:
→ 매번 수동으로 `headers: { Authorization: ... }` 안 써도 됨
→ 토큰 만료 시 자동으로 로그아웃 처리
→ 코드 중복 제거
```

**왜 이렇게 하나?**

```
인터셉터 없이 (나쁨):
// 모든 API 호출마다 반복
axios.get('/api/users', {
  headers: {
    Authorization: `Bearer ${localStorage.getItem('accessToken')}`
  }
});

axios.post('/api/posts', data, {
  headers: {
    Authorization: `Bearer ${localStorage.getItem('accessToken')}`
  }
});

인터셉터 사용 (좋음):
// 한 번만 설정
api.interceptors.request.use(...);

// 이후 모든 호출은 자동으로 토큰 추가
api.get('/api/users');
api.post('/api/posts', data);
```

---

### 2. Zustand 상태 관리

**파일: `src/store/authStore.ts`**

```typescript
import { create } from 'zustand';
import { User } from '../types/auth';

interface AuthState {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  setAuth: (user: User, accessToken: string, refreshToken: string) => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: localStorage.getItem('accessToken'),
  isAuthenticated: !!localStorage.getItem('accessToken'),

  setAuth: (user, accessToken, refreshToken) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    set({ user, accessToken, isAuthenticated: true });
  },

  clearAuth: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    set({ user: null, accessToken: null, isAuthenticated: false });
  },
}));
```

**개념 설명: Zustand Store**

```
비유: 호텔 프런트 데스크

전통적인 방법 (Props Drilling):
할아버지 컴포넌트
  └─ 아버지 컴포넌트 (user 전달)
      └─ 자식 컴포넌트 (user 전달)
          └─ 손자 컴포넌트 (user 사용!)

문제: 중간에 user 필요 없는데도 계속 전달해야 함

Zustand 방법:
할아버지, 아버지, 자식, 손자 모두
프런트 데스크(Store)에서 직접 정보 조회!

const { user } = useAuthStore(); // 어디서든 접근 가능
```

**언제 사용하나?**

```
Store에 저장할 것:
✅ 여러 컴포넌트에서 사용하는 데이터
   - 사용자 정보 (user)
   - 인증 상태 (isAuthenticated)
   - 테마 설정 (dark/light)
   - 장바구니 아이템

❌ Store에 저장하지 말 것:
- 한 컴포넌트에서만 쓰는 데이터 (useState 사용)
- 폼 입력값 (React Hook Form 사용)
- 페이지 스크롤 위치
```

---

### 3. React Router 설정

**파일: `src/App.tsx`**

```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import HomePage from './pages/home/HomePage';
import LoginPage from './pages/auth/LoginPage';
import SignupPage from './pages/auth/SignupPage';
import { useAuthStore } from './store/authStore';

// Protected Route: 로그인 필요한 페이지 보호
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuthStore();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 공개 페이지 */}
        <Route
          path="/"
          element={
            <Layout>
              <HomePage />
            </Layout>
          }
        />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />

        {/* 보호된 페이지 (추후 추가) */}
        {/*
        <Route
          path="/playlists"
          element={
            <ProtectedRoute>
              <Layout>
                <PlaylistsPage />
              </Layout>
            </ProtectedRoute>
          }
        />
        */}

        {/* 404 처리 */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}
```

**개념 설명: Protected Route**

```
비유: 회원제 헬스장

일반 Route = 무료 체험존 (누구나 입장)
  - 홈페이지
  - 로그인 페이지
  - 회원가입 페이지

Protected Route = 회원 전용 구역 (카드 태그 필요)
  - 개인 락커
  - PT 예약
  - 운동 기록

동작:
1. /playlists 접속 시도
2. ProtectedRoute가 회원증(JWT) 확인
3. 있으면: 입장 허용 ✅
4. 없으면: 로그인 페이지로 자동 이동 ❌
```

---

### 4. Tailwind CSS 커스터마이징

**파일: `tailwind.config.js`**

```javascript
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        spotify: {
          green: '#1DB954',           // Spotify 메인 그린
          'green-light': '#1ED760',   // 호버 시 밝은 그린
          black: '#191414',           // 배경색
          'gray-dark': '#282828',     // 카드 배경
          'gray-light': '#B3B3B3',    // 텍스트 보조색
          border: '#404040',          // 테두리
        }
      }
    }
  }
}
```

**사용 예시:**

```tsx
<button className="bg-spotify-green hover:bg-spotify-green-light">
  클릭
</button>
```

**개념 설명: 디자인 토큰**

```
비유: 회사 CI (Corporate Identity)

전통적인 방법 (나쁨):
<div style={{backgroundColor: '#1DB954'}}>버튼1</div>
<div style={{backgroundColor: '#1DB954'}}>버튼2</div>
<div style={{backgroundColor: '#1DB954'}}>버튼3</div>

문제: 색상 변경 시 100곳을 수정해야 함

Tailwind 방법 (좋음):
// 설정 파일에 한 번만 정의
spotify: { green: '#1DB954' }

// 사용
<div className="bg-spotify-green">버튼1</div>
<div className="bg-spotify-green">버튼2</div>
<div className="bg-spotify-green">버튼3</div>

// 색상 변경 시 설정 파일 1곳만 수정!
spotify: { green: '#FF5733' }
→ 모든 버튼 자동으로 색상 변경됨 ✨
```

---

### 5. 폼 검증 (로그인/회원가입)

**파일: `src/pages/auth/SignupPage.tsx`**

```typescript
const validateForm = () => {
  // 1. 필수 필드 확인
  if (!formData.email || !formData.nickname || !formData.password) {
    setError('모든 필드를 입력해주세요.');
    return false;
  }

  // 2. 비밀번호 길이 확인
  if (formData.password.length < 8) {
    setError('비밀번호는 최소 8자 이상이어야 합니다.');
    return false;
  }

  // 3. 비밀번호 일치 확인
  if (formData.password !== formData.confirmPassword) {
    setError('비밀번호가 일치하지 않습니다.');
    return false;
  }

  // 4. 이메일 형식 확인
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(formData.email)) {
    setError('올바른 이메일 형식이 아닙니다.');
    return false;
  }

  return true;
};
```

**개념 설명: 클라이언트 측 검증**

```
비유: 우편물 발송

클라이언트 검증 (프론트엔드):
├─ 우체국 가기 전에 집에서 확인
├─ 주소 작성했나? ✅
├─ 우표 붙였나? ✅
└─ 빠르고 편리함

서버 검증 (백엔드):
├─ 우체국에서 최종 확인
├─ 실제로 배송 가능한 주소인가?
└─ 보안 강화 (필수!)

중요: 두 곳 모두 검증해야 함!
→ 클라이언트: 사용자 경험 향상 (즉시 피드백)
→ 서버: 보안 (악의적인 요청 차단)
```

---

## 7-C.6 트러블슈팅

### 문제 1: PostCSS 설정 오류

**에러 메시지:**

```bash
[plugin:vite:css] Failed to load PostCSS config
Error: Loading PostCSS Plugin failed: Cannot find module '@tailwindcss/postcss'
```

**원인 분석:**

```
Tailwind CSS 버전 문제:
├─ 설치된 버전: Tailwind CSS v4.1.18
├─ v4에서 변경: PostCSS 플러그인이 별도 패키지로 분리
└─ v3 방식: `tailwindcss: {}` (작동 안 함)
```

**해결 과정:**

```bash
# 1차 시도: v3 방식으로 변경 (실패)
# postcss.config.js
export default {
  plugins: {
    tailwindcss: {},  // ❌ v4에서는 작동 안 함
    autoprefixer: {},
  },
};

# 2차 시도: @tailwindcss/postcss 설치 (성공)
npm install -D @tailwindcss/postcss

# postcss.config.js
export default {
  plugins: {
    "@tailwindcss/postcss": {},  // ✅ v4 방식
    autoprefixer: {},
  },
};
```

**교훈:**

```
메이저 버전 업그레이드 주의사항:
├─ Tailwind v3 → v4: 설정 방식 변경
├─ React v17 → v18: ReactDOM.render 변경
└─ Vue v2 → v3: Composition API 도입

대응 방법:
1. 공식 마이그레이션 가이드 확인
2. Breaking Changes 섹션 꼼꼼히 읽기
3. 에러 메시지를 정확히 읽기
```

---

### 문제 2: Vite HMR 캐시 문제

**증상:**

```
브라우저에서 다음 에러:
"The requested module '/src/types/auth.ts' does not provide an export named 'User'"

하지만 파일 확인 시:
export interface User { ... } // 정상적으로 export됨
```

**원인:**

```
Vite HMR 캐시 문제:
├─ 파일 변경 후 브라우저가 이전 버전 캐시
├─ 특히 새 파일 생성 시 발생
└─ 개발 서버 재시작 필요
```

**해결 방법:**

```bash
# 1. Vite 개발 서버 중지
Ctrl+C (또는 Command+C)

# 2. 개발 서버 재시작
npm run dev

# 3. 브라우저 하드 리프레시
Mac: Command + Shift + R
Windows: Ctrl + Shift + R
```

**개념 설명: HMR (Hot Module Replacement)**

```
비유: 방송국 생방송

HMR 없이 (과거):
├─ 코드 수정
├─ 서버 재시작 (10초)
├─ 페이지 새로고침
└─ 로그인 다시, 페이지 이동 다시

HMR 사용 (현재):
├─ 코드 수정
├─ 변경된 모듈만 교체 (0.05초)
├─ 상태 유지 (로그인 상태 그대로)
└─ 개발 속도 10배 향상 ⚡

단점:
가끔 캐시 문제 발생 → 재시작 필요
```

---

### 문제 3: npm 패키지 설치 위치 실수

**증상:**

```
MUTI 루트에 node_modules/ 생성됨
→ backend(Gradle) + frontend(Node.js) 혼재
```

**원인:**

```
터미널 위치 확인 안 함:
MUTI % npm install tailwindcss  # ❌ 루트에 설치
frontend % npm install tailwindcss  # ✅ frontend에 설치
```

**해결 방법:**

```bash
# 1. 잘못 설치된 파일 제거
cd /Users/peterj/Desktop/study/MUTI
rm -rf node_modules package.json package-lock.json

# 2. 올바른 위치에서 재설치
cd frontend
npm install
```

**교훈:**

```
모노레포(Monorepo) 관리:
MUTI/
├── backend/          # Gradle (Java)
│   ├── build.gradle
│   └── src/
├── frontend/         # npm (Node.js)
│   ├── package.json
│   ├── node_modules/
│   └── src/
└── .gitignore        # 루트 설정

원칙:
1. 명령 실행 전 현재 디렉토리 확인 (pwd)
2. backend: gradle 명령
3. frontend: npm 명령
4. 절대 섞지 말 것!
```

---

## 7-C.7 새로운 개념 설명

### 1. SPA (Single Page Application)

**전통적인 MPA vs 현대적인 SPA:**

```
MPA (Multi Page Application) - 과거 방식:
1. 링크 클릭
2. 서버에 새 HTML 요청
3. 전체 페이지 새로고침 (깜빡임)
4. 느림 (매번 서버 왕복)

예시:
/home → home.html (서버에서 다운로드)
/about → about.html (서버에서 다운로드)
/contact → contact.html (서버에서 다운로드)

SPA (Single Page Application) - 현재 방식:
1. 최초 1번만 HTML 다운로드
2. 이후 JavaScript로 화면 전환
3. 깜빡임 없음
4. 빠름 (서버는 JSON 데이터만 주고받음)

예시:
처음 접속: index.html + app.js (전체 다운로드)
/home → JavaScript로 화면 변경 (즉시)
/about → JavaScript로 화면 변경 (즉시)
/contact → JavaScript로 화면 변경 (즉시)
```

**비유: 영화관 vs 넷플릭스**

```
MPA = 극장 (매번 이동해야 함):
├─ 코미디 보고 싶음 → CGV 가기
├─ 액션 보고 싶음 → 롯데시네마 가기
└─ 공포 보고 싶음 → 메가박스 가기

SPA = 넷플릭스 (앱 한 번만 열기):
├─ 코미디 보고 싶음 → 클릭 (즉시 재생)
├─ 액션 보고 싶음 → 클릭 (즉시 재생)
└─ 공포 보고 싶음 → 클릭 (즉시 재생)
```

**언제 SPA를 쓰나?**

```
SPA 적합:
✅ 대시보드 (관리자 페이지)
✅ 웹 애플리케이션 (Gmail, Notion)
✅ 소셜 미디어 (Twitter, Instagram)
✅ 음악 스트리밍 (Spotify, YouTube Music)

MPA 적합:
✅ 블로그 (SEO 중요)
✅ 뉴스 사이트 (SEO 중요)
✅ 전자상품 쇼핑몰 (SEO 중요)

우리 프로젝트: SPA 선택
→ MUTI는 로그인 후 사용하는 서비스
→ SEO보다 사용자 경험이 중요
→ 페이지 전환이 많음 (플레이리스트 탐색)
```

---

### 2. JWT (JSON Web Token)

**개념:**

```
JWT = 디지털 회원증

구조 (3부분):
Header.Payload.Signature
xxxxx.yyyyy.zzzzz

실제 예시:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

디코딩하면:
{
  "sub": "1234567890",      # 사용자 ID
  "name": "John Doe",       # 사용자 이름
  "iat": 1516239022         # 발급 시간
}
```

**비유: 놀이공원 팔찌**

```
전통적인 세션 (과거):
1. 입장 시 티켓 받음
2. 서버가 "123번 고객이 입장했음" 기록 (DB 저장)
3. 매번 놀이기구 탈 때마다 서버에 확인
   "123번 고객 맞나요?"
4. 서버 부하 증가 (매번 DB 조회)

JWT 방식 (현재):
1. 입장 시 팔찌 받음 (JWT)
2. 팔찌에 이름, 입장 시간 등 정보 새겨져 있음
3. 놀이기구 탈 때 팔찌만 보여주면 됨
4. 직원이 팔찌 보고 직접 판단 (서버 부하 없음)

장점:
→ 서버가 "누가 로그인했는지" 기억 안 해도 됨
→ 확장성: 서버 여러 대 있어도 OK
→ 빠름: 매번 DB 조회 안 함

단점:
→ 한 번 발급하면 취소 불가 (만료시간까지)
→ 크기가 큼 (쿠키보다 큼)
```

**MUTI에서 JWT 흐름:**

```
1. 로그인:
User → POST /api/v1/auth/login
← accessToken (1시간 유효), refreshToken (14일 유효)

2. 토큰 저장:
localStorage.setItem('accessToken', token)

3. API 요청 (자동):
api.get('/api/v1/playlists')
→ Interceptor가 자동으로 추가:
   headers: { Authorization: 'Bearer eyJhbGc...' }

4. 토큰 만료 (401 에러):
← 401 Unauthorized
→ Interceptor가 자동으로 로그아웃
→ /login으로 리다이렉트

5. Refresh Token (나중에 구현 예정):
accessToken 만료 시 refreshToken으로 갱신
→ 사용자는 로그아웃 안 당함
```

---

### 3. 환경 변수 (Environment Variables)

**개념:**

```
환경 변수 = 비밀 정보를 코드 밖에 보관

나쁜 예 (하드코딩):
const API_URL = 'https://muti-world.duckdns.org';  // ❌ 코드에 직접
const DB_PASSWORD = 'mypassword123';                // ❌ 위험!

좋은 예 (환경 변수):
const API_URL = import.meta.env.VITE_API_URL;      // ✅ .env 파일에서 읽기
const DB_PASSWORD = process.env.DB_PASSWORD;       // ✅ 안전
```

**파일: `.env`**

```bash
VITE_API_URL=https://muti-world.duckdns.org
```

**Vite에서 사용:**

```typescript
// 자동으로 문자열로 변환됨
const apiUrl = import.meta.env.VITE_API_URL;

// 타입 안전성을 위한 기본값 설정
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
```

**비유: 집 주소 vs 우편함**

```
하드코딩 = 집 벽에 주소 페인트칠:
├─ 이사 가면? 벽을 다시 칠해야 함 (코드 수정)
├─ 모든 방문객이 주소를 봄 (보안 취약)
└─ 변경 어려움

환경 변수 = 우편함에 주소 표시:
├─ 이사 가면? 우편함만 교체 (.env만 수정)
├─ 필요한 사람만 확인 (Git에 안 올림)
└─ 변경 쉬움

.gitignore에 추가:
.env         # 개발용 비밀 정보
.env.local   # 로컬 전용 설정
```

**보안 주의사항:**

```
절대 Git에 올리면 안 되는 것:
❌ .env (환경 변수)
❌ API 키
❌ 데이터베이스 비밀번호
❌ JWT 시크릿

Git에 올려도 되는 것:
✅ .env.example (예시 파일, 실제 값 없음)
```

---

## 7-C.8 Spotify 테마 디자인

### 색상 팔레트

```css
/* 배경색 */
--spotify-black: #191414;        /* 메인 배경 */
--spotify-gray-dark: #282828;    /* 카드, 사이드바 */

/* 포인트 색상 */
--spotify-green: #1DB954;        /* CTA 버튼, 로고 */
--spotify-green-light: #1ED760;  /* 호버 효과 */

/* 텍스트 */
--white: #FFFFFF;                /* 제목, 중요 텍스트 */
--spotify-gray-light: #B3B3B3;   /* 보조 텍스트 */

/* 테두리 */
--spotify-border: #404040;       /* 구분선, 카드 테두리 */
```

### 타이포그래피

```css
/* 폰트 스택 (Spotify와 동일) */
font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto',
             'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans',
             'Helvetica Neue', sans-serif;

/* 크기 */
Hero Title: 4rem (64px)
Section Title: 2rem (32px)
Button Text: 1rem (16px)
Body Text: 0.875rem (14px)
```

### 간격 시스템 (8의 배수)

```
4px  = 0.5rem (xs)  → 작은 여백
8px  = 1rem (sm)    → 기본 여백
16px = 2rem (md)    → 중간 여백
24px = 3rem (lg)    → 큰 여백
32px = 4rem (xl)    → 섹션 간격
```

### 버튼 스타일

```tsx
// Primary Button (초록)
<button className="px-8 py-4 rounded-full bg-spotify-green text-white font-bold
                   hover:bg-spotify-green-light hover:scale-105
                   transition-all duration-200">
  무료로 시작하기
</button>

// Secondary Button (투명 + 테두리)
<button className="px-8 py-4 rounded-full bg-transparent border-2 border-white
                   text-white font-bold hover:bg-white hover:text-spotify-black
                   transition-all duration-200">
  로그인
</button>
```

### 카드 스타일

```tsx
<div className="bg-spotify-gray-dark p-8 rounded-lg
                hover:bg-opacity-80 transition-all duration-200">
  <h3 className="text-xl font-bold text-white mb-4">카드 제목</h3>
  <p className="text-spotify-gray-light">카드 설명</p>
</div>
```

---

## 7-C.9 성능 최적화

### 1. Code Splitting (코드 분할)

**나중에 구현 예정:**

```typescript
// 페이지를 필요할 때만 로드
const HomePage = lazy(() => import('./pages/home/HomePage'));
const PlaylistsPage = lazy(() => import('./pages/playlists/PlaylistsPage'));

<Suspense fallback={<Loading />}>
  <Routes>
    <Route path="/" element={<HomePage />} />
    <Route path="/playlists" element={<PlaylistsPage />} />
  </Routes>
</Suspense>
```

**효과:**

```
Code Splitting 전:
├─ 최초 로드: app.js (2MB) ← 모든 페이지 포함
└─ 로딩 시간: 5초

Code Splitting 후:
├─ 최초 로드: app.js (500KB) ← Home만
├─ Playlist 접속 시: playlists.js (300KB) ← 필요할 때만
└─ 로딩 시간: 1.5초 ✨
```

---

### 2. Tailwind PurgeCSS

**자동 적용됨:**

```javascript
// tailwind.config.js
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  // Vite 빌드 시 사용하지 않는 CSS 자동 제거
}
```

**효과:**

```
개발 환경:
├─ Tailwind CSS: 전체 4MB
└─ 빌드 시간: 빠름 (모든 클래스 사용 가능)

프로덕션 빌드:
├─ 사용한 클래스만 포함: 10KB
├─ 사용 안 한 클래스: 제거 (3.99MB 절약!)
└─ 로딩 속도: 400배 향상 ✨
```

---

## 7-C.10 배포 계획

### Vercel 배포 (추천)

```bash
# 1. Vercel CLI 설치
npm install -g vercel

# 2. 로그인
vercel login

# 3. 프로젝트 배포
cd frontend
vercel

# 자동으로 생성되는 URL:
# https://muti-frontend-xxx.vercel.app
```

### 환경 변수 설정

```
Vercel Dashboard → Settings → Environment Variables

VITE_API_URL = https://muti-world.duckdns.org
```

### 대안

- Netlify
- AWS Amplify
- GitHub Pages (SPA 지원)
- Cloudflare Pages

---

## 7-C.11 다음 단계

### 현재 완료
- [x] React + Vite + TypeScript 프로젝트 생성
- [x] Tailwind CSS 설정 (Spotify 테마)
- [x] React Router 설정
- [x] Zustand 상태 관리 설정
- [x] Axios 인터셉터 (JWT 자동 관리)
- [x] Layout 컴포넌트 (Header, Footer, Layout)
- [x] 페이지 컴포넌트 (Home, Login, Signup)
- [x] 폼 검증 (이메일, 비밀번호)

### 다음 작업

1. **백엔드 연동 테스트**
   - 회원가입 API 호출
   - 로그인 API 호출
   - JWT 토큰 저장 및 자동 전송
   - 401 에러 시 자동 로그아웃 테스트

2. **추가 페이지 개발**
   - 플레이리스트 목록 페이지
   - 플레이리스트 상세 페이지
   - 음악 검색 페이지
   - 프로필 페이지

3. **UI/UX 개선**
   - 로딩 스피너
   - 토스트 알림
   - 모달 다이얼로그
   - 반응형 디자인 (모바일 최적화)

4. **배포**
   - Vercel에 프론트엔드 배포
   - 도메인 연결
   - HTTPS 설정 (자동)

---

## 7-C.12 학습 내용 정리

### 핵심 개념

1. **SPA (Single Page Application)**
   - 페이지 전환 시 깜빡임 없음
   - JavaScript로 화면 동적 변경
   - 서버는 JSON API만 제공

2. **JWT (JSON Web Token)**
   - 서버가 상태를 저장하지 않음 (Stateless)
   - 클라이언트가 토큰을 보관
   - 매 요청마다 토큰을 헤더에 포함

3. **Axios Interceptor**
   - 모든 요청에 자동으로 JWT 추가
   - 401 에러 시 자동 로그아웃
   - 코드 중복 제거

4. **Zustand**
   - 전역 상태 관리
   - Redux보다 간단 (코드 1/10)
   - Props Drilling 해결

5. **Tailwind CSS**
   - 유틸리티 클래스 조합
   - 디자인 토큰 (색상, 간격 일관성)
   - PurgeCSS로 최적화

### 프로젝트 아키텍처

```
사용자
  ↓
React SPA (Frontend)
  ├─ React Router (페이지 전환)
  ├─ Zustand (상태 관리)
  ├─ Axios (HTTP 통신)
  └─ Tailwind CSS (스타일링)
  ↓
Spring Boot API (Backend)
  ├─ Spring Security (JWT 검증)
  ├─ REST Controller (API 엔드포인트)
  └─ PostgreSQL (데이터 저장)
```

---

**Phase 7-C 완료!** 🎉

**당신은 이제:**
- ✅ React + TypeScript로 SPA 개발 가능
- ✅ JWT 기반 인증 시스템 구현 경험
- ✅ Tailwind CSS로 반응형 UI 디자인 가능
- ✅ Axios 인터셉터로 API 통신 자동화
- ✅ 모던 프론트엔드 생태계 이해

**다음: 백엔드 API 연동 테스트 시작!**