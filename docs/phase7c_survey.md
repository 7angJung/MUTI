# Phase 7-C 추가: 설문 시스템 구현

**작성일**: 2026-02-10
**상태**: 완료 ✅

---

## 목차
- [1. 개요](#1-개요)
- [2. 설문 시스템 구현](#2-설문-시스템-구현)
- [3. 기술 스택 및 선택 이유](#3-기술-스택-및-선택-이유)
- [4. 주요 트러블슈팅](#4-주요-트러블슈팅)
- [5. 새로운 개념 설명](#5-새로운-개념-설명)
- [6. 배포 및 테스트](#6-배포-및-테스트)
- [7. 다음 단계](#7-다음-단계)

---

## 1. 개요

### 1.1 목표
MUTI의 핵심 기능인 **음악 성향 테스트(설문조사)** 프론트엔드 구현 및 백엔드 연동

### 1.2 완료된 기능
- ✅ 설문 진행 페이지 (SurveyPage.tsx)
- ✅ 결과 표시 페이지 (ResultPage.tsx)
- ✅ 16가지 MUTI 타입 분석 및 표시
- ✅ 백엔드 API 완전 연동
- ✅ 로컬 및 프로덕션 환경 테스트 완료

### 1.3 구현 범위
- **질문 수**: 8개
- **차원**: 4가지 (E/I, S/F, A/D, P/U)
- **결과 타입**: 16가지 (2^4 조합)
- **응답 방식**: 이진 선택 (Binary Choice) - 리커트 척도는 차기 버전에서 구현 예정

---

## 2. 설문 시스템 구현

### 2.1 파일 구조

```
frontend/src/
├── types/
│   └── survey.ts              # 설문 관련 TypeScript 타입 정의
├── services/
│   └── survey.service.ts      # 설문 API 통신 로직
└── pages/
    └── survey/
        ├── SurveyPage.tsx     # 설문 진행 페이지
        └── ResultPage.tsx     # 결과 표시 페이지
```

---

### 2.2 타입 정의

**파일: `src/types/survey.ts`**

```typescript
// 질문 선택지
export interface QuestionOption {
  id: number;
  content: string;
  score: number;
  dimensionType: 'E_I' | 'S_F' | 'A_D' | 'P_U';
  dimensionValue: 'E' | 'I' | 'S' | 'F' | 'A' | 'D' | 'P' | 'U';
}

// 질문
export interface Question {
  id: number;
  content: string;
  questionOrder: number;
  dimensionType: 'E_I' | 'S_F' | 'A_D' | 'P_U';
  options: QuestionOption[];
}

// 설문
export interface Survey {
  id: number;
  title: string;
  description: string;
  questions: Question[];
}

// 응답 데이터 (백엔드 형식에 맞춤)
export interface SurveyAnswer {
  questionId: number;
  optionId: number;  // 백엔드: selectedOptionId → optionId
}

// 제출 요청
export interface SurveySubmitRequest {
  surveyId: number;
  answers: SurveyAnswer[];  // 백엔드: responses → answers
  sessionId?: string;
  userId?: number;
}

// 결과 (백엔드 응답 형식)
export interface SurveyResult {
  id: number;
  surveyId: number;
  mutiType: string;  // 백엔드: musicType → mutiType
  mutiTypeName: string;
  mutiTypeDescription: string;
  axisScores: {
    E_I: number;
    S_F: number;
    A_D: number;
    P_U: number;
  };
  axisDirections: {  // 백엔드: dominantTraits → axisDirections
    E_I: string;
    S_F: string;
    A_D: string;
    P_U: string;
  };
  createdAt: string;
}
```

**주요 변경사항 (백엔드 형식에 맞춤)**:
- `responses` → `answers` (필드명 통일)
- `selectedOptionId` → `optionId` (필드명 간소화)
- `musicType` → `mutiType` (백엔드 명명 규칙)
- `dominantTraits` → `axisDirections` (명확한 의미 전달)

---

### 2.3 API 서비스

**파일: `src/services/survey.service.ts`**

```typescript
import api from './api';
import type { Survey, SurveySubmitRequest, SurveyResult } from '../types/survey';

export const surveyService = {
  /**
   * 설문 목록 조회
   */
  getSurveys: async (): Promise<Survey[]> => {
    const response = await api.get('/api/v1/surveys');
    return response.data;
  },

  /**
   * 설문 상세 조회 (질문 포함)
   */
  getSurvey: async (surveyId: number): Promise<Survey> => {
    const response = await api.get(`/api/v1/surveys/${surveyId}`);
    return response.data;
  },

  /**
   * 설문 응답 제출
   */
  submitSurvey: async (
    surveyId: number,
    submitData: SurveySubmitRequest
  ): Promise<SurveyResult> => {
    const response = await api.post(
      `/api/v1/surveys/${surveyId}/submit`,
      submitData
    );
    return response.data;
  },
};
```

---

### 2.4 설문 진행 페이지

**파일: `src/pages/survey/SurveyPage.tsx`**

**주요 기능**:
1. **진행률 표시**: 현재 질문 번호 / 전체 질문 수
2. **질문 표시**: 각 질문마다 2개의 선택지
3. **응답 저장**: 사용자가 선택한 옵션 저장
4. **이전/다음 버튼**: 질문 간 이동
5. **제출**: 마지막 질문에서 자동 제출

**핵심 로직**:

```typescript
// 1. 응답 저장 및 다음 질문 이동
const handleNext = () => {
  if (selectedOption === null) {
    alert('선택지를 선택해주세요!');
    return;
  }

  // 현재 응답 저장
  const newAnswer: SurveyAnswer = {
    questionId: currentQuestion.id,
    optionId: selectedOption,
  };

  const updatedAnswers = [...answers, newAnswer];
  setAnswers(updatedAnswers);
  setSelectedOption(null);

  // 마지막 질문이면 제출
  if (currentQuestionIndex === survey!.questions.length - 1) {
    submitSurvey(updatedAnswers);
  } else {
    setCurrentQuestionIndex(currentQuestionIndex + 1);
  }
};

// 2. 이전 질문으로 이동
const handlePrevious = () => {
  if (currentQuestionIndex > 0) {
    setCurrentQuestionIndex(currentQuestionIndex - 1);
    // 이전 응답 제거 및 복원
    const updatedAnswers = answers.slice(0, -1);
    setAnswers(updatedAnswers);
    const previousAnswer = answers[answers.length - 1];
    setSelectedOption(previousAnswer?.optionId || null);
  }
};

// 3. 설문 제출
const submitSurvey = async (finalAnswers: SurveyAnswer[]) => {
  try {
    const result = await surveyService.submitSurvey(survey!.id, {
      surveyId: survey!.id,
      answers: finalAnswers,
    });
    navigate('/survey/result', { state: { result } });
  } catch (err: any) {
    setError(err.response?.data?.message || '설문 제출에 실패했습니다.');
  }
};
```

---

### 2.5 결과 표시 페이지

**파일: `src/pages/survey/ResultPage.tsx`**

**주요 기능**:
1. **MUTI 타입 표시**: 16가지 타입 중 하나
2. **타입 설명**: 각 타입별 맞춤 설명
3. **4가지 차원 분석**: E/I, S/F, A/D, P/U 각각의 결과
4. **공유 기능**: 결과를 클립보드 복사 또는 공유
5. **CTA 버튼**: 회원가입, 홈으로 이동

**16가지 MUTI 타입**:

```typescript
const typeDescriptions: Record<string, { title: string; description: string; emoji: string }> = {
  ESAP: {
    title: '열정적인 파티 메이커',
    description: '활기차고 감각적이며 분석적인 당신! 파티의 중심에서 모두를 즐겁게 만드는 타입입니다.',
    emoji: '🎉',
  },
  // ... 16가지 타입 정의
  IFDU: {
    title: '자유로운 감성인',
    description: '독립적이고 감성적인 당신! 제약 없이 음악을 느끼고 즐깁니다.',
    emoji: '🦄',
  },
};
```

---

## 3. 기술 스택 및 선택 이유

### 3.1 Vite 프록시 (개발 환경)

**파일: `vite.config.ts`**

```typescript
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'https://muti-world.duckdns.org',  // 프로덕션
        // target: 'http://localhost:8080',         // 로컬 테스트
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
```

**왜 Vite 프록시를 사용하나?**

```
문제: CORS (Cross-Origin Resource Sharing) 에러
브라우저: http://localhost:5174 (프론트엔드)
서버: https://muti-world.duckdns.org (백엔드)
→ 다른 도메인이므로 브라우저가 요청 차단!

해결: Vite 프록시
1. 프론트엔드 → Vite 프록시 (/api)
2. Vite 프록시 → 백엔드 (https://muti-world.duckdns.org/api)
3. 브라우저 입장에서는 같은 도메인처럼 보임 ✅

비유: 외국 물건 직구
직접 구매 (CORS 에러):
  한국 고객 → 미국 사이트 (차단!)

프록시 사용 (성공):
  한국 고객 → 배송대행지(Vite) → 미국 사이트 ✅
```

**장점**:
- CORS 설정 불필요 (개발 환경)
- 로컬/프로덕션 전환 쉬움 (한 줄만 변경)
- API 호출 코드 동일 (`/api/v1/...`)

---

### 3.2 API 응답 Unwrapping

**문제**: 백엔드가 데이터를 이중 래핑

```json
// 백엔드 응답
{
  "success": true,
  "data": {
    "id": 1,
    "questions": [...]
  }
}

// 프론트엔드 기대
{
  "id": 1,
  "questions": [...]
}
```

**해결**: Axios Response Interceptor

```typescript
// src/services/api.ts
api.interceptors.response.use(
  (response) => {
    // {success: true, data: {...}} 형식이면 data 추출
    if (response.data && response.data.success !== undefined && response.data.data !== undefined) {
      return { ...response, data: response.data.data };
    }
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

**비유**:
```
배달 음식 포장
과도한 포장 (백엔드):
  종이백 → 비닐백 → 음식 → 실제 먹을 것

Unwrapping (프론트엔드):
  자동으로 종이백, 비닐백 제거 → 바로 음식!
```

---

## 4. 주요 트러블슈팅

### 4.1 TypeScript `verbatimModuleSyntax` 에러

**에러 메시지**:
```
Uncaught SyntaxError: The requested module '/src/types/survey.ts' does not provide an export named 'Survey'
```

**원인 분석**:
```
tsconfig.app.json 설정:
{
  "compilerOptions": {
    "verbatimModuleSyntax": true,  // ← 이 옵션 때문!
    "erasableSyntaxOnly": true
  }
}

TypeScript 5.0+ 기능:
- verbatimModuleSyntax: 타입 import에 명시적으로 'type' 키워드 요구
- 목적: 런타임 코드와 타입 코드 명확히 구분
```

**해결 과정**:

```typescript
// ❌ 에러 발생
import { Survey, SurveySubmitRequest } from '../types/survey';

// ✅ 해결
import type { Survey, SurveySubmitRequest } from '../types/survey';
```

**수정한 파일**:
1. `src/services/survey.service.ts:2`
2. `src/pages/survey/SurveyPage.tsx:4`
3. `src/pages/survey/ResultPage.tsx:2`

**왜 이런 설정을 하나?**
```
장점:
✅ 타입과 값의 명확한 구분
✅ 번들 크기 최적화 (타입 코드 완전 제거)
✅ 컴파일 속도 향상

단점:
❌ 모든 타입 import에 'type' 키워드 필요
❌ 학습 곡선 증가

비유: 승객과 화물 분리
일반 import = 승객과 화물을 같은 비행기에 (무거움)
type import = 승객은 여객기, 화물은 화물기 (효율적!)
```

---

### 4.2 CORS 설정 (localhost:5174)

**문제**:
```
POST http://localhost:5174/api/v1/surveys/1/submit 403 (Forbidden)
```

**원인**:
```java
// SecurityConfig.java
configuration.setAllowedOriginPatterns(List.of(
    "http://localhost:3000",
    "http://localhost:5173",  // Vite 기본 포트
    // "http://localhost:5174" 없음! ← 문제
));
```

Vite가 5173 포트 사용 중이면 자동으로 5174 사용
→ 백엔드 CORS 설정에 5174 포트 없어서 차단

**해결**:
```java
configuration.setAllowedOriginPatterns(List.of(
    "http://localhost:3000",
    "http://localhost:5173",
    "http://localhost:5174",  // ✅ 추가
    "http://127.0.0.1:3000",
    "https://*.vercel.app",
    "https://*.up.railway.app"
));
```

**교훈**:
```
CORS 설정 팁:
1. 개발 환경: localhost + 여러 포트 허용
2. 프로덕션: 실제 도메인만 엄격하게
3. 보안: 절대 "*" (모든 도메인 허용) 사용 금지

비유: 아파트 출입 시스템
개발 환경 = 공사 중 (작업자들 다양하게 출입)
프로덕션 = 입주 완료 (등록된 주민만 출입)
```

---

### 4.3 API 필드명 불일치

**문제**: 프론트엔드와 백엔드 필드명 다름

| 구분 | 프론트엔드 (초기) | 백엔드 (실제) |
|------|------------------|--------------|
| 응답 배열 | `responses` | `answers` |
| 선택지 ID | `selectedOptionId` | `optionId` |
| 음악 타입 | `musicType` | `mutiType` |
| 성향 | `dominantTraits` | `axisDirections` |

**해결**: 프론트엔드를 백엔드 형식에 맞춤

```typescript
// Before
export interface SurveyResponse {
  questionId: number;
  selectedOptionId: number;
}

// After (백엔드 형식)
export interface SurveyAnswer {
  questionId: number;
  optionId: number;
}
```

**왜 프론트엔드를 맞췄나?**
```
선택지:
1. 백엔드 수정 → 데이터베이스, API, 테스트 모두 변경
2. 프론트엔드 수정 → TypeScript 타입만 변경

결정: 프론트엔드 수정
이유: 백엔드는 이미 데이터가 있고, 다른 클라이언트도 사용 중일 수 있음

비유: 콘센트와 플러그
백엔드(벽의 콘센트) = 이미 설치됨, 변경 어려움
프론트엔드(플러그) = 어댑터 사용 쉬움
```

---

### 4.4 PostgreSQL Prepared Statement 에러

**에러 메시지 (EC2)**:
```
ERROR: prepared statement "S_1" already exists
```

**원인 분석**:
```
PostgreSQL Prepared Statement Cache 충돌:
1. 여러 커넥션이 동시에 같은 이름의 prepared statement 생성
2. HikariCP 커넥션 풀에서 커넥션 재사용 시 캐시 충돌
3. Supabase Pooler 사용 시 자주 발생
```

**해결**:
```bash
# .env 파일 수정 (EC2)
DB_URL=jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:6543/postgres?prepareThreshold=0

# 주의: 줄바꿈 없이 한 줄로!
# ❌ 잘못된 예:
DB_URL=jdbc:postgresql://...?
prepareThreshold=0

# ✅ 올바른 예:
DB_URL=jdbc:postgresql://...?prepareThreshold=0
```

**`prepareThreshold=0` 의미**:
```
PostgreSQL JDBC 설정:
- prepareThreshold > 0: Prepared Statement 사용 (기본값 5)
- prepareThreshold = 0: Prepared Statement 비활성화

장점 (기본값 5):
✅ SQL 재사용 시 성능 향상
✅ SQL 파싱 오버헤드 감소

단점:
❌ 커넥션 풀에서 충돌 가능

언제 0으로 설정?
- Supabase Pooler 사용 시
- 커넥션 풀링 환경
- "prepared statement already exists" 에러 발생 시

비유: 주방 조리기구
Prepared Statement ON = 미리 세팅된 조리기구 (빠름, 충돌 위험)
Prepared Statement OFF = 매번 새 기구 사용 (느림, 안전)
```

---

### 4.5 Vite HMR (Hot Module Replacement) 캐시

**문제**: 파일 수정 후에도 브라우저에서 이전 버전 로드

**해결 방법**:
```bash
# 1. Vite 캐시 삭제
rm -rf frontend/node_modules/.vite

# 2. Vite 재시작
npm run dev

# 3. 브라우저 하드 리프레시
# Mac: Cmd + Shift + R
# Windows: Ctrl + Shift + R
```

**언제 발생하나?**
- 새 파일 추가 시
- import/export 구조 변경 시
- TypeScript 타입 변경 시

---

## 5. 새로운 개념 설명

### 5.1 Vite Proxy

**개념**:
```
Vite 개발 서버가 중간에서 API 요청을 대신 전달
```

**동작 원리**:
```
1. 브라우저
   ↓ GET /api/v1/surveys/1
2. Vite Dev Server (localhost:5174)
   ↓ Origin 헤더 변경
3. 백엔드 (https://muti-world.duckdns.org)
   ↓ 응답
4. Vite Dev Server
   ↓ CORS 헤더 추가
5. 브라우저 ✅
```

**비유: 통역사**
```
한국인(브라우저) ↔ 통역사(Vite) ↔ 미국인(백엔드)

통역사가 하는 일:
1. 언어 변환 (Origin 헤더 변경)
2. 문화 차이 해결 (CORS 헤더)
3. 양쪽을 부드럽게 연결
```

---

### 5.2 TypeScript `type` Import

**일반 Import vs Type Import**:

```typescript
// 1. 일반 import (값 + 타입)
import { Survey } from './types';
→ 런타임에 Survey 변수 존재 (번들에 포함 가능)

// 2. Type-only import (타입만)
import type { Survey } from './types';
→ 컴파일 시에만 존재, 런타임에는 완전히 제거
```

**장점**:
```
번들 크기 최적화:
일반 import: 100KB (타입 코드 포함)
type import: 80KB (타입 코드 제거) ← 20% 절약!

비유: 설계도 vs 실제 건물
type import = 설계도 (건축 중에만 필요, 완공 후 제거)
일반 import = 건물 자재 (실제로 사용됨)
```

---

### 5.3 Response Interceptor (Unwrapping)

**개념**: API 응답 데이터 자동 추출

**Before (수동)**:
```typescript
const response = await api.get('/api/v1/surveys/1');
const survey = response.data.data;  // 매번 .data.data
```

**After (자동)**:
```typescript
const response = await api.get('/api/v1/surveys/1');
const survey = response.data;  // Interceptor가 자동 추출!
```

**구현**:
```typescript
api.interceptors.response.use((response) => {
  // {success: true, data: {...}} 형식 감지
  if (response.data?.success && response.data?.data) {
    return { ...response, data: response.data.data };
  }
  return response;
});
```

**비유: 택배 자동 개봉**
```
Before: 택배 받으면 박스 → 비닐 → 상품 (수동 개봉)
After: 자동으로 상품만 꺼내서 전달 (편리!)
```

---

## 6. 배포 및 테스트

### 6.1 로컬 테스트 (성공)

**환경**:
- 백엔드: Docker 컨테이너 (localhost:8080)
- 프론트엔드: Vite Dev Server (localhost:5174)
- 연결: Vite Proxy

**테스트 결과**:
```
✅ 설문 조회 (GET /api/v1/surveys/1)
✅ 설문 응답 (POST /api/v1/surveys/1/submit)
✅ 결과 표시 (16가지 MUTI 타입)
✅ 모든 데이터 정상 표시
```

---

### 6.2 프로덕션 배포 (EC2)

**배포 과정**:

```bash
# 1. 코드 커밋
git add .
git commit -m "fix: Add localhost:5174 to CORS allowed origins"
git push origin dev

# 2. GitHub Actions 빌드 (자동)
# - Docker 이미지 빌드
# - ghcr.io/7angjung/muti:dev 푸시

# 3. EC2 배포
cd ~/muti-app
docker compose down
docker compose pull
docker compose up -d

# 4. 로그 확인
docker compose logs -f muti-backend
```

**배포 결과**:
```
✅ Spring Boot 시작 성공
✅ Flyway 마이그레이션 완료
✅ Health check 200 OK
✅ PostgreSQL prepared statement 에러 해결
✅ 프론트엔드-백엔드 연동 성공
```

---

### 6.3 최종 테스트

**시나리오**:
1. localhost:5174/survey 접속
2. 8개 질문에 응답
3. 결과 페이지에서 MUTI 타입 확인
4. 공유 버튼 클릭

**결과**: ✅ 모든 기능 정상 작동

---

## 7. 다음 단계

### 7.1 즉시 진행 (Phase 7-D)

**리커트 척도 구현**:
```
현재: 이진 선택 (A or B)
변경: 5단계 리커트 척도
- 매우 그렇다
- 그렇다
- 보통이다
- 그렇지 않다
- 매우 그렇지 않다
```

**영향 범위**:
- ✅ 프론트엔드: UI 변경 (라디오 버튼 → 리커트 스케일 버튼)
- ⚠️ 백엔드: 질문 데이터 수정 (2개 → 5개 선택지)
- ⚠️ 데이터베이스: 기존 질문 데이터 마이그레이션

---

### 7.2 향후 계획

1. **프론트엔드 배포**:
   - Vercel/Netlify에 배포
   - 도메인 연결 (muti-app.vercel.app)

2. **UI/UX 개선**:
   - 로딩 애니메이션
   - 결과 페이지 그래프 추가
   - 반응형 디자인 (모바일 최적화)

3. **추가 기능**:
   - 플레이리스트 페이지
   - 음악 발견 페이지
   - 프로필 페이지

---

## 8. 학습 내용 정리

### 8.1 핵심 성과

**기술적 역량**:
- ✅ TypeScript 고급 기능 (verbatimModuleSyntax)
- ✅ Vite Proxy 설정 및 활용
- ✅ Axios Interceptor 커스터마이징
- ✅ CORS 이해 및 해결
- ✅ PostgreSQL 연결 풀링 트러블슈팅

**프로젝트 관리**:
- ✅ 프론트엔드-백엔드 인터페이스 설계
- ✅ 타입 안전성 유지하며 API 통합
- ✅ 로컬 및 프로덕션 환경 분리
- ✅ 체계적인 에러 해결 프로세스

---

### 8.2 트러블슈팅 패턴

**문제 해결 5단계**:
```
1. 에러 메시지 정확히 읽기
   → "The requested module does not provide export"

2. 원인 파악
   → tsconfig의 verbatimModuleSyntax 설정

3. 해결책 조사
   → TypeScript 5.0+ 문서 확인

4. 적용 및 테스트
   → import type { ... } 추가

5. 문서화
   → 다음에 같은 문제 빠르게 해결
```

---

### 8.3 아키텍처 이해

**전체 흐름**:
```
사용자
  ↓
브라우저 (http://localhost:5174/survey)
  ↓
Vite Dev Server (프록시)
  ↓ GET /api/v1/surveys/1
EC2 백엔드 (https://muti-world.duckdns.org)
  ↓ Spring Boot → PostgreSQL
  ↓ {success: true, data: {...}}
Response Interceptor (Unwrapping)
  ↓ {...} (data 추출)
React Component (SurveyPage)
  ↓ 렌더링
사용자 화면 표시 ✨
```

---

**Phase 7-C 추가 작업 완료!** 🎉

**다음**: Phase 7-D (리커트 척도 구현) 준비 완료