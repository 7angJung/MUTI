# MUTI 프론트엔드

MUTI (MUsic Type Indicator) 음악 성향 테스트 웹 애플리케이션

## 🚀 빠른 시작

### 1. 백엔드 서버 실행

먼저 Spring Boot 백엔드 서버를 실행해야 합니다:

```bash
cd ..  # 프로젝트 루트로 이동
./gradlew bootRun --args='--spring.profiles.active=local'
```

백엔드 서버가 `http://localhost:8080`에서 실행됩니다.

### 2. 프론트엔드 실행

#### Option A: Live Server (VS Code 추천) ⭐

1. VS Code에서 `frontend/index.html` 파일 열기
2. 마우스 우클릭 → "Open with Live Server" 선택
3. 브라우저에서 자동으로 열림 (보통 `http://127.0.0.1:5500`)

**Live Server 설치:**
- VS Code Extensions에서 "Live Server" 검색
- "Live Server" by Ritwick Dey 설치

#### Option B: Python 간단 서버

```bash
cd frontend

# Python 3
python3 -m http.server 8000

# 또는 Python 2
python -m SimpleHTTPServer 8000
```

브라우저에서 `http://localhost:8000` 접속

#### Option C: Node.js http-server

```bash
# 전역 설치 (한 번만)
npm install -g http-server

# 실행
cd frontend
http-server -p 8000
```

브라우저에서 `http://localhost:8000` 접속

## 📁 파일 구조

```
frontend/
├── index.html          # 메인 랜딩 페이지
├── survey.html         # 설문 페이지 (8개 질문)
├── result.html         # 결과 페이지 (MUTI 타입 표시)
├── css/
│   └── style.css       # 전체 스타일시트
├── js/
│   ├── app.js          # 메인 페이지 로직
│   ├── survey.js       # 설문 페이지 로직
│   └── result.js       # 결과 페이지 로직
└── README.md           # 이 파일
```

## 🎨 페이지 설명

### 1. 메인 페이지 (index.html)
- MUTI 소개
- 테스트 시작 버튼
- 프로젝트 정보

### 2. 설문 페이지 (survey.html)
- 8개 질문 순차 표시
- 진행률 표시 바
- 이전/다음 버튼
- 실시간 답변 저장
- 자동 제출 및 결과 페이지 이동

### 3. 결과 페이지 (result.html)
- MUTI 타입 표시 (16가지 중 하나)
- 타입별 이름 및 설명
- 4개 축 점수 시각화 (E-I, S-F, A-D, P-U)
- 공유 기능
- 재테스트 버튼

## 🔧 환경 설정

### API 엔드포인트 변경

배포 환경에서는 각 JavaScript 파일의 `API_BASE_URL`을 변경하세요:

**app.js, survey.js:**
```javascript
// 로컬 개발
const API_BASE_URL = 'http://localhost:8080/api/v1';

// 프로덕션 (예시)
const API_BASE_URL = 'https://your-backend-domain.com/api/v1';
```

## 🎯 주요 기능

### ✅ 구현 완료
- [x] 반응형 디자인 (모바일, 태블릿, 데스크톱)
- [x] 설문 데이터 API 연동
- [x] 실시간 진행률 표시
- [x] 답변 로컬 저장 (새로고침 방지)
- [x] MUTI 타입 계산 및 표시
- [x] 4개 축 점수 시각화
- [x] 결과 공유 기능
- [x] 에러 핸들링
- [x] 로딩 상태 표시

### 🚀 향후 추가 가능 기능
- [ ] 사용자 인증 (로그인)
- [ ] 결과 히스토리 저장
- [ ] MUTI 타입별 음악 추천
- [ ] SNS 공유 (카카오톡, 페이스북, 트위터)
- [ ] 다국어 지원
- [ ] 다크 모드

## 🐛 트러블슈팅

### CORS 오류
```
Access to fetch at 'http://localhost:8080/api/v1/surveys' from origin 'http://127.0.0.1:5500' has been blocked by CORS policy
```

**해결:**
- 백엔드에 CORS 설정이 추가되었습니다 (WebConfig.java)
- 백엔드 서버를 재시작하세요

### API 연결 실패
```
Failed to fetch
```

**확인 사항:**
1. 백엔드 서버가 실행 중인가? (`http://localhost:8080`)
2. API 엔드포인트가 올바른가?
3. 네트워크 연결이 정상인가?

**테스트:**
```bash
curl http://localhost:8080/api/v1/surveys/ping
# 응답: {"success":true,"data":"pong"}
```

### 결과 페이지가 비어있음
```
결과 데이터를 찾을 수 없습니다
```

**원인:**
- 설문을 완료하지 않고 직접 result.html로 접근
- sessionStorage가 비어있음

**해결:**
- 설문을 처음부터 다시 진행

## 📱 반응형 디자인

지원 브레이크포인트:
- **Desktop**: 768px 이상
- **Tablet**: 480px ~ 768px
- **Mobile**: 480px 이하

모든 페이지가 다양한 화면 크기에 최적화되어 있습니다.

## 🎨 디자인 시스템

### 컬러 팔레트
- Primary: `#6366f1` (인디고)
- Secondary: `#8b5cf6` (보라)
- Success: `#10b981` (초록)
- Error: `#ef4444` (빨강)

### 폰트
- System Font Stack (최적의 가독성)

### 애니메이션
- Fade In Up: 페이지 전환
- Bounce: 결과 배지
- Progress Bar: 설문 진행

## 🌐 브라우저 지원

- ✅ Chrome (최신 2개 버전)
- ✅ Firefox (최신 2개 버전)
- ✅ Safari (최신 2개 버전)
- ✅ Edge (최신 2개 버전)
- ⚠️ IE11 (부분 지원)

## 📝 라이선스

이 프로젝트는 MUTI 프로젝트의 일부입니다.

## 👨‍💻 개발자

MUTI Project Team

---

**문의사항이나 버그 리포트는 GitHub Issues를 이용해주세요.**