# MUTI 배포 가이드

## 📋 배포 구성

```
Frontend:  Vercel (https://vercel.com)
Backend:   Railway (https://railway.app)
Database:  Supabase (이미 설정 완료)
```

## 🚀 배포 단계

---

# Phase 1: GitHub에 코드 푸시

## 1. 현재 변경사항 커밋

```bash
git add .
git commit -m "Add deployment configurations"
git push origin main
```

---

# Phase 2: 백엔드 배포 (Railway)

## 1. Railway 회원가입

1. https://railway.app 접속
2. **"Login"** 또는 **"Sign up"** 클릭
3. **GitHub 계정으로 로그인** (권장)

## 2. 새 프로젝트 생성

1. Dashboard에서 **"New Project"** 클릭
2. **"Deploy from GitHub repo"** 선택
3. **"7angJung/MUTI"** 저장소 선택
4. **"Deploy Now"** 클릭

## 3. 환경 변수 설정

Railway 프로젝트 페이지에서:

1. **"Variables"** 탭 클릭
2. 다음 환경 변수 추가:

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:5432/postgres?user=postgres.????
DB_USERNAME=postgres.????
DB_PASSWORD=????
PORT=8080
```

## 4. 배포 확인

1. **"Deployments"** 탭에서 배포 진행 상황 확인
2. 성공하면 **"View Logs"** 에서 "Started MutiApplication" 확인
3. **"Settings"** → **"Domains"** 에서 URL 확인
   - 예: `muti-production.up.railway.app`

## 5. API 테스트

브라우저에서:
```
https://your-app.up.railway.app/api/v1/surveys/ping
```

응답:
```json
{"success":true,"data":"pong"}
```

---

# Phase 3: 프론트엔드 배포 (Vercel)

## 1. Vercel 회원가입

1. https://vercel.com 접속
2. **"Sign Up"** 클릭
3. **GitHub 계정으로 로그인**

## 2. 새 프로젝트 생성

1. Dashboard에서 **"Add New..."** → **"Project"** 클릭
2. **"Import Git Repository"** 에서 **"7angJung/MUTI"** 선택
3. **"Import"** 클릭

## 3. 프로젝트 설정

**Configure Project** 화면에서:

1. **Framework Preset**: Other (그대로 둠)
2. **Root Directory**: `frontend` 입력 후 **"Edit"** 클릭
3. **Build and Output Settings**: 그대로 둠
4. **"Deploy"** 클릭

## 4. 배포 완료 확인

1. 배포 진행 상황 확인 (1-2분 소요)
2. 성공하면 **"Visit"** 버튼 클릭
3. URL 확인: 예) `muti.vercel.app`

## 5. Railway URL 업데이트 필요

프론트엔드가 백엔드 API를 찾을 수 있도록:

1. Railway에서 백엔드 URL 복사 (예: `https://muti-production.up.railway.app`)
2. `frontend/js/config.js` 파일 수정:

```javascript
production: 'https://your-railway-url.up.railway.app/api/v1'
```

3. Git에 커밋 후 푸시:
```bash
git add frontend/js/config.js
git commit -m "Update production API URL"
git push
```

4. Vercel이 자동으로 재배포됨 (1분 소요)

---

# Phase 4: 최종 테스트

## 1. 프론트엔드 접속

```
https://your-app.vercel.app
```

## 2. 전체 플로우 테스트

1. **메인 페이지** → "테스트 시작하기" 클릭
2. **설문 페이지** → 8개 질문 답변
3. **결과 페이지** → MUTI 타입 확인

## 3. 브라우저 개발자 도구 확인

F12 → Network 탭:
- GET `https://your-railway-url.up.railway.app/api/v1/surveys/1` → 200 OK
- POST `https://your-railway-url.up.railway.app/api/v1/surveys/1/submit` → 200 OK

---

# 🔧 문제 해결

## Railway 배포 실패

**증상:** Build failed

**확인:**
1. Railway Logs 확인
2. 환경 변수가 모두 설정되었는지 확인
3. Supabase DB 연결 확인

**해결:**
- Settings → Restart 클릭

## Vercel CORS 오류

**증상:** `Access-Control-Allow-Origin` 오류

**해결:**
- Railway Settings → Environment Variables에서 다음 추가:
```
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app
```

## API 연결 안 됨

**증상:** Failed to fetch

**확인:**
1. Railway 백엔드가 실행 중인지 확인
2. `config.js`의 production URL이 정확한지 확인

**해결:**
- `frontend/js/config.js`의 production URL 수정 후 재배포

---

# 🎉 성공!

이제 전 세계 어디서나 접속 가능한 MUTI 서비스가 완성되었습니다!

```
Frontend:  https://your-app.vercel.app
Backend:   https://your-railway-url.up.railway.app
Database:  Supabase PostgreSQL
```

친구들과 공유하세요! 🎵

---

# 📝 배포 후 업데이트 방법

## 코드 수정 후 배포

```bash
# 1. 코드 수정
# 2. Git 커밋
git add .
git commit -m "Update: 변경 내용"
git push

# 3. 자동 재배포됨!
# - Vercel: 자동 재배포 (1분)
# - Railway: 자동 재배포 (2-3분)
```

## 환경 변수 변경

1. **Railway:** Settings → Variables → Edit
2. **Vercel:** Settings → Environment Variables → Edit
3. 변경 후 Redeploy 클릭

---

# 💰 비용

```
✅ Vercel:   무료 (월 100GB 트래픽)
✅ Railway:  무료 ($5 크레딧/월, 충분함)
✅ Supabase: 무료 (500MB DB, 충분함)

총 비용: $0 / 월
```

---

# 📚 추가 자료

- Vercel Docs: https://vercel.com/docs
- Railway Docs: https://docs.railway.app
- Supabase Docs: https://supabase.com/docs

---

**문의사항은 GitHub Issues로 남겨주세요!**
