# GitHub Personal Access Token 생성 가이드

## 🎯 목적
EC2 서버에서 GitHub Container Registry(GHCR)로부터 Docker 이미지를 pull하기 위한 토큰 생성

---

## 📋 단계별 가이드

### Step 1: GitHub Settings 접속
1. GitHub 웹사이트 로그인: https://github.com
2. 우측 상단 프로필 클릭 → **Settings** 클릭

### Step 2: Developer settings 이동
1. 좌측 메뉴 맨 아래 **Developer settings** 클릭
2. **Personal access tokens** 확장
3. **Tokens (classic)** 클릭

### Step 3: 새 토큰 생성
1. **Generate new token** 버튼 클릭
2. **Generate new token (classic)** 선택

### Step 4: 토큰 설정
1. **Note** (토큰 이름):
   ```
   MUTI GHCR Access
   ```

2. **Expiration** (만료 기간):
   - 권장: **No expiration** (만료 없음)
   - 또는: **90 days** (3개월)

3. **Select scopes** (권한 선택):
   **필수로 체크해야 할 항목:**
   - ✅ `read:packages` - Download packages from GitHub Package Registry
   - ✅ `write:packages` - Upload packages to GitHub Package Registry (선택사항, 안전하게 체크)

   **체크하지 않아도 되는 항목:**
   - ❌ repo
   - ❌ workflow
   - ❌ admin:org
   - ❌ 기타 등등

### Step 5: 토큰 생성 및 복사
1. 페이지 맨 아래 **Generate token** 버튼 클릭
2. 생성된 토큰이 표시됨 (예: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`)
3. **즉시 복사하여 안전한 곳에 저장** (이 페이지를 벗어나면 다시 볼 수 없음!)

---

## ⚠️ 중요 사항

### 토큰 보안
- ✅ 토큰을 **절대로** GitHub 코드에 커밋하지 마세요
- ✅ 로컬 메모장/비밀번호 관리자에 안전하게 저장하세요
- ✅ 토큰이 노출되면 즉시 삭제하고 새로 생성하세요

### 토큰 특징
- **한 번만 표시됨**: 생성 직후에만 확인 가능
- **재발급 필요**: 잊어버리면 삭제하고 새로 생성
- **만료 가능**: 만료 기간을 설정한 경우 주기적으로 갱신 필요

---

## 🔄 토큰 사용 방법

### EC2 서버에서 사용
```bash
# 토큰으로 GHCR 로그인
echo YOUR_TOKEN_HERE | docker login ghcr.io -u 7angJung --password-stdin
```

### 예시
```bash
# 실제 토큰이 ghp_abc123이라면
echo ghp_abc123 | docker login ghcr.io -u 7angJung --password-stdin
```

**결과:**
```
Login Succeeded
```

---

## 🚨 문제 해결

### 로그인 실패 시
1. **토큰 권한 확인**
   - `read:packages` scope가 체크되어 있는지 확인
   - GitHub Settings → Developer settings → Tokens에서 확인

2. **토큰 만료 확인**
   - 만료된 토큰은 사용 불가
   - 새 토큰 생성 필요

3. **사용자 이름 확인**
   - `-u 7angJung`이 정확한 GitHub username인지 확인

---

**작성일**: 2026-02-10
**작성자**: Claude Sonnet 4.5