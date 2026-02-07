# MUTI 빠른 시작 가이드 🚀

## 📋 사전 요구사항

- ✅ Java 17 이상
- ✅ VS Code (Live Server 확장 설치 권장)
- ✅ 웹 브라우저 (Chrome, Firefox, Safari 등)

## 🎯 3단계로 시작하기

### Step 1: 백엔드 서버 실행

터미널에서 프로젝트 루트 디렉토리로 이동 후:

```bash
# 로컬 환경 (H2 Database)
./gradlew bootRun

# 또는 프로덕션 환경 (Supabase)
./gradlew bootRun --args='--spring.profiles.active=prod'
```

**서버가 실행되면:**
```
Started MutiApplication in X.XXX seconds
```

**확인:**
브라우저에서 http://localhost:8080/api/v1/surveys/ping 접속
→ `{"success":true,"data":"pong"}` 응답이 나오면 성공!

### Step 2: 프론트엔드 실행

#### 방법 A: VS Code Live Server (추천) ⭐

1. VS Code에서 `frontend/index.html` 파일 열기
2. 파일 우클릭 → **"Open with Live Server"** 선택
3. 브라우저에서 자동으로 열림!

**Live Server 설치 방법:**
- VS Code 왼쪽 Extensions 아이콘 클릭
- "Live Server" 검색
- "Live Server" by Ritwick Dey 설치
- VS Code 재시작

#### 방법 B: Python 서버

```bash
cd frontend
python3 -m http.server 8000
```

브라우저에서 http://localhost:8000 접속

### Step 3: 테스트 시작! 🎵

1. **메인 페이지**에서 "테스트 시작하기" 버튼 클릭
2. **8개 질문**에 차례대로 답변
3. **결과 확인**: 나의 MUTI 타입 확인!

---

## 🖥️ 터미널 2개 사용 예시

### Terminal 1: 백엔드
```bash
cd /Users/peterj/Desktop/study/MUTI
./gradlew bootRun
```

### Terminal 2: 프론트엔드 (Python 사용 시)
```bash
cd /Users/peterj/Desktop/study/MUTI/frontend
python3 -m http.server 8000
```

---

## 📸 예상 화면

### 1. 메인 페이지
```
🎵 MUTI
MUsic Type Indicator

당신의 음악 취향은?
8개의 간단한 질문으로
당신의 음악 성향을 16가지 타입 중 하나로 분석합니다

⚡ 2분 소요  🎯 정확한 분석  🎁 무료

[테스트 시작하기]
```

### 2. 설문 페이지
```
진행률: ████████░░░░░░░░ 4 / 8

평소 즐겨 듣는 음악의 템포는?

□ 빠르고 역동적인 음악 (댄스, EDM, 업템포 록)
□ 느리고 차분한 음악 (발라드, 앰비언트, 재즈)

[← 이전]  [다음 →]
```

### 3. 결과 페이지
```
당신의 MUTI 타입은

ESAP
에너제틱 스토리텔러

빠르고 에너지 넘치는 음악을 좋아하며...

세부 점수:
E vs I  ████████░░  +5
S vs F  ██████░░░░  +3
A vs D  ████░░░░░░  +2
P vs U  ███████░░░  +4

[🏠 홈으로]  [🔄 다시 테스트]  [📤 결과 공유]
```

---

## 🐛 문제 해결

### 문제 1: 백엔드 서버가 실행되지 않아요

**증상:**
```
Error: Could not find or load main class com.muti.MutiApplication
```

**해결:**
```bash
./gradlew clean build
./gradlew bootRun
```

### 문제 2: 프론트엔드가 API를 못 찾아요

**증상:**
```
Failed to fetch
```

**확인:**
1. 백엔드가 실행 중인가? → Terminal 1 확인
2. http://localhost:8080/api/v1/surveys/ping 접속해보기
3. 브라우저 개발자 도구 Console 확인 (F12)

**해결:**
- 백엔드를 먼저 실행한 후 프론트엔드 접속

### 문제 3: CORS 에러가 나요

**증상:**
```
Access to fetch blocked by CORS policy
```

**해결:**
```bash
# 백엔드 재시작
Ctrl+C (서버 종료)
./gradlew bootRun
```

### 문제 4: 포트가 이미 사용 중이에요

**증상:**
```
Port 8080 is already in use
```

**해결:**
```bash
# macOS/Linux
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID번호> /F
```

---

## ✅ 체크리스트

실행 전 확인:
- [ ] Java 17 이상 설치됨
- [ ] 프로젝트를 최신 버전으로 pull 받음
- [ ] Terminal 2개 준비됨 (또는 VS Code Live Server 설치)

실행 후 확인:
- [ ] 백엔드 서버 정상 실행 (http://localhost:8080/api/v1/surveys/ping)
- [ ] 프론트엔드 접속 가능 (http://localhost:8000 또는 Live Server URL)
- [ ] 메인 페이지 정상 표시
- [ ] "테스트 시작하기" 버튼 작동
- [ ] 설문 질문 8개 정상 로드
- [ ] 결과 페이지 정상 표시

---

## 🎉 성공!

모든 것이 정상 작동한다면, 이제 MUTI 테스트를 즐기세요!

친구들과 공유하고 각자의 음악 취향을 비교해보세요! 🎵

---

## 📚 더 알아보기

- **백엔드 상세 문서**: `README.md`
- **프론트엔드 가이드**: `frontend/README.md`
- **Supabase 설정**: `SUPABASE_SETUP.md`
- **API 문서**: http://localhost:8080/swagger-ui/index.html (서버 실행 후)

---

**문의사항이나 버그는 GitHub Issues로 남겨주세요!** 😊