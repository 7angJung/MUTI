## Phase 6: AWS EC2 클라우드 배포

### 6.1 Phase 6 개요

#### 🎯 목표

**"로컬 컴퓨터가 아닌 실제 클라우드 서버에 애플리케이션을 배포하여, 인터넷 어디서나 접속 가능하게 만들기"**

#### 📦 완료된 작업

| 항목 | 설명 | 도구 |
|------|------|------|
| **EC2 인스턴스 생성** | 가상 서버 생성 (t2.micro, 프리티어) | AWS EC2 |
| **Elastic IP 할당** | 고정 공인 IP 주소 할당 | AWS Elastic IP |
| **SSH 접속 설정** | 키 페어로 보안 접속 | SSH |
| **Docker 설치** | 컨테이너 런타임 설치 | Docker Engine |
| **GHCR 로그인** | GitHub Container Registry 인증 | docker login |
| **애플리케이션 배포** | Docker Compose로 컨테이너 실행 | Docker Compose V2 |
| **Health Check** | 내부/외부 접속 확인 | curl, 브라우저 |

#### ⏱️ 실제 소요 시간

```
총 소요 시간: 약 2시간
├─ EC2 인스턴스 생성: 10분
├─ Elastic IP 설정: 5분
├─ SSH 접속 및 Docker 설치: 20분
├─ GHCR 로그인 (토큰 생성 포함): 15분
├─ 애플리케이션 배포: 10분
└─ 트러블슈팅 및 테스트: 60분
```

#### 💰 비용

- **EC2 t2.micro**: 프리티어 (월 750시간 무료)
- **Elastic IP**: 인스턴스 연결 시 무료 (미연결 시 시간당 $0.005)
- **아웃바운드 트래픽**: 월 15GB 무료
- **총 비용**: **$0/월** (프리티어 기간 내)

---

### 6.2 왜 AWS를 선택했는가?

#### 클라우드 플랫폼 비교

| 플랫폼 | 장점 | 단점 | 프리티어 |
|--------|------|------|---------|
| **AWS EC2** | 완전한 제어권, 업계 표준, 학습 가치 높음 | 설정 복잡 | 12개월 |
| **Railway** | 매우 간단, 자동 배포 | 월 $5, 무료 500시간 | 제한적 |
| **Fly.io** | 간단, 글로벌 배포 | 무료 3개 앱 | 제한적 |
| **Heroku** | 매우 간단 | 무료 플랜 폐지 | 없음 |

#### 선택 이유

1. **완전한 학습 경험**: 서버 설정부터 배포까지 모든 과정을 직접 경험
2. **프리티어 기간**: 12개월 무료 사용
3. **업계 표준**: 실무에서 가장 많이 사용하는 클라우드 플랫폼
4. **확장성**: 나중에 RDS, S3, CloudFront 등 다양한 서비스 연동 가능
5. **포트폴리오**: "AWS 경험 있음"을 이력서에 추가 가능

---

### 6.3 핵심 개념 이해하기

#### 🖥️ AWS EC2란?

**정의:** Elastic Compute Cloud - Amazon의 가상 서버 서비스

**비유: 아파트 임대**

```
전통적인 서버 (물리 서버)
├─ 직접 건물(서버)을 구매
├─ 유지보수 직접 담당 (고장나면 수리)
├─ 초기 비용: 수백만원~수천만원
└─ 사용하지 않아도 비용 발생

AWS EC2 (가상 서버)
├─ 아파트를 임대하듯 서버 임대
├─ 유지보수는 집주인(AWS)이 담당
├─ 초기 비용: $0 (프리티어)
└─ 사용한 만큼만 비용 지불
```

**EC2의 핵심 개념:**

1. **인스턴스 (Instance)**
   - 실제로 실행되는 가상 서버
   - 예: "t2.micro" 인스턴스를 1개 실행 중

2. **인스턴스 타입**
   - 서버의 성능 (CPU, 메모리, 네트워크)
   - 예: t2.micro = 1 vCPU, 1GB RAM (프리티어)

3. **AMI (Amazon Machine Image)**
   - 서버의 운영체제 템플릿
   - 예: Ubuntu 24.04 LTS

4. **보안 그룹 (Security Group)**
   - 방화벽 규칙
   - 어떤 포트를 열고 닫을지 결정
   - 예: 8080 포트를 인터넷에 공개

**실생활 비유:**

```
EC2 인스턴스 = 아파트 한 채
├─ 인스턴스 타입 = 아파트 크기 (10평, 20평, 30평...)
├─ AMI = 내부 인테리어 스타일 (한식, 양식, 모던...)
├─ 보안 그룹 = 현관문 자물쇠 설정
│   ├─ 22번 포트 (SSH) = 집 열쇠 (주인만 출입)
│   └─ 8080번 포트 (HTTP) = 택배 박스 (누구나 물건 넣을 수 있음)
└─ 키 페어 = 실제 열쇠
```

---

#### 🌐 Elastic IP란?

**정의:** 고정된 공인 IP 주소

**비유: 전화번호 vs 주소**

```
일반 IP (동적 IP)
├─ 인스턴스를 재시작할 때마다 IP 변경
├─ 예: 오늘은 13.125.1.100, 내일은 13.125.2.200
└─ 비유: 일회용 전화번호 (매번 바뀜)

Elastic IP (고정 IP)
├─ 한 번 할당하면 계속 동일
├─ 예: 항상 13.125.123.45
└─ 비유: 내 집 주소 (평생 동일)
```

**왜 필요한가?**

```
시나리오 1: Elastic IP 없이
사용자: "앱 주소가 뭐죠?"
개발자: "13.125.1.100이에요"
[인스턴스 재시작]
사용자: "접속이 안 돼요!"
개발자: "아, IP가 13.125.2.200으로 바뀌었어요"
사용자: "???"

시나리오 2: Elastic IP 사용
사용자: "앱 주소가 뭐죠?"
개발자: "13.125.123.45에요"
[인스턴스 재시작 해도]
사용자: "잘 접속되네요!"
개발자: "당연하죠. 주소는 항상 동일해요"
```

**중요한 특징:**

1. **인스턴스 연결 시**: 무료 ✅
2. **인스턴스 미연결 시**: 시간당 $0.005 (1일 = $0.12) ⚠️
3. **해제 후 재할당**: IP 주소 변경됨

**교훈:**
- Elastic IP를 할당하면 **반드시 인스턴스에 연결**해야 함
- 사용하지 않으면 **즉시 해제**해야 불필요한 비용 방지

---

#### 🐳 Docker Compose V2

**정의:** Docker CLI에 통합된 최신 버전의 Docker Compose

**차이점:**

| 항목 | V1 (구버전) | V2 (최신) |
|------|-------------|-----------|
| **명령어** | `docker-compose` (하이픈 O) | `docker compose` (하이픈 X) |
| **설치** | Python 패키지로 별도 설치 | Docker CLI에 내장 |
| **성능** | 느림 | Go로 재작성 (2배 빠름) |
| **상태** | 레거시 | 공식 권장 |

**비유: 구형 vs 신형 자동차**

```
Docker Compose V1 (구형 자동차)
├─ 별도로 구매해야 함 (apt install docker-compose)
├─ 시동 걸 때 "docker-compose up"
└─ 느리지만 작동은 함

Docker Compose V2 (신형 자동차)
├─ 차량 구매 시 기본 포함 (Docker에 내장)
├─ 시동 걸 때 "docker compose up" (띄어쓰기)
└─ 더 빠르고 효율적
```

**실제 사용:**

```bash
# V1 (구버전) - 설치 필요
sudo apt install docker-compose
docker-compose up -d

# V2 (최신) - 이미 설치됨
docker compose up -d  # 바로 사용 가능
```

---

#### 🏥 Spring Boot Actuator

**정의:** 프로덕션 환경에서 애플리케이션을 모니터링하고 관리하는 도구

**제공하는 엔드포인트:**

| 엔드포인트 | 기능 | 프로덕션 사용 |
|-----------|------|--------------|
| `/actuator/health` | 애플리케이션 상태 확인 | ✅ 공개 가능 |
| `/actuator/info` | 애플리케이션 정보 | ✅ 공개 가능 |
| `/actuator/metrics` | CPU, 메모리 사용량 | ⚠️ 비공개 권장 |
| `/actuator/env` | 환경 변수 | ❌ 절대 비공개 |
| `/actuator/loggers` | 로그 레벨 변경 | ❌ 절대 비공개 |

**비유: 자동차 계기판**

```
Spring Boot 애플리케이션 = 자동차
Actuator = 계기판

/actuator/health = 엔진 경고등
├─ "UP" = 초록불 (정상)
└─ "DOWN" = 빨간불 (고장)

/actuator/metrics = 속도계, RPM, 연료 게이지
├─ CPU 사용률 = RPM
├─ 메모리 사용량 = 연료 잔량
└─ 응답 시간 = 속도

/actuator/env = 차량 설정 (에어컨, 히터, 시트 위치)
└─ 민감한 정보 포함 (비밀번호 등)
```

**Health Check 동작 원리:**

```
Docker → 30초마다 → /actuator/health 요청
                     ↓
              Spring Boot Actuator
                     ↓
              {"status": "UP"} 응답
                     ↓
           Docker: "컨테이너 정상"
```

**만약 응답 없으면:**

```
Docker → /actuator/health 요청
         ↓ (응답 없음)
      Docker: "컨테이너 비정상"
         ↓
      자동 재시작 시도
```

---

#### 🔒 Spring Security - permitAll() vs authenticated()

**정의:** Spring Security의 접근 제어 설정

**SecurityConfig.java 구조:**

```java
.authorizeHttpRequests(auth -> auth
    // 인증 없이 접근 가능 (화이트리스트)
    .requestMatchers(
        "/api/v1/auth/**",      // 회원가입, 로그인
        "/actuator/**",         // Health Check
        "/swagger-ui/**"        // Swagger UI
    ).permitAll()

    // 나머지는 모두 인증 필요
    .anyRequest().authenticated()
)
```

**비유: 테마파크 입장**

```
테마파크 = Spring Boot 애플리케이션
입장권 = JWT 토큰

permitAll() = 무료 입장 구역
├─ 티켓 부스 (/api/v1/auth/login)
├─ 안내 센터 (/actuator/health)
└─ 화장실 (/swagger-ui)
→ 누구나 자유롭게 출입 가능

authenticated() = 유료 입장 구역
├─ 놀이기구 (/api/v1/musics)
├─ 공연장 (/api/v1/playlists)
└─ VIP 라운지 (/api/v1/users)
→ 입장권(JWT) 필요
```

**실제 동작:**

```
요청 1: GET /actuator/health
├─ Spring Security: "permitAll() 목록에 있네요"
└─ 응답: 200 OK ✅

요청 2: GET /api/v1/users
├─ Spring Security: "authenticated() 필요하네요"
├─ JWT 토큰 확인
│   ├─ 토큰 없음 → 401 Unauthorized ❌
│   └─ 토큰 있음 → 200 OK ✅
```

**트러블슈팅 과정에서 발견:**

```
문제: curl http://localhost:8080/api/v1/health
응답: {"error": "Unauthorized", "message": "인증이 필요합니다"}

원인 분석:
/api/v1/health는 permitAll() 목록에 없음
→ anyRequest().authenticated() 규칙에 해당
→ JWT 토큰 필요

해결 방법:
1. /api/v1/health를 permitAll()에 추가 (코드 수정)
2. /actuator/health 사용 (이미 permitAll에 있음) ← 선택!
```

---

### 6.4 단계별 배포 과정

#### Step 1: EC2 인스턴스 생성

**목표:** Ubuntu 24.04 기반 t2.micro 인스턴스 생성

**설정 내용:**

```yaml
이름: MUTI-Backend-Server

AMI: Ubuntu 24.04 LTS (HVM), SSD Volume Type
- 운영체제: Ubuntu
- 버전: 24.04 (최신 LTS)
- 아키텍처: 64비트 (x86)

인스턴스 유형: t2.micro
- vCPU: 1개
- 메모리: 1GB
- 프리티어: ✅ 적격 (월 750시간 무료)

키 페어: muti-key-pair (새로 생성)
- 타입: RSA
- 형식: .pem (Mac/Linux용)
- 저장 위치: ~/Downloads/muti-key-pair.pem

네트워크 설정:
- VPC: 기본 VPC
- 서브넷: 자동 할당
- 퍼블릭 IP 자동 할당: 활성화 ✅

방화벽 (보안 그룹):
규칙 1: SSH (포트 22)
  - 소스: 내 IP (보안)

규칙 2: HTTP (포트 8080)
  - 소스: 0.0.0.0/0 (전체 공개)

스토리지: 8GB gp3
- 프리티어: ✅ 최대 30GB까지 무료

고급 설정:
- 종료 방지: 비활성화 (실수로 삭제 방지는 나중에 설정)
- 세부 CloudWatch 모니터링: 비활성화 (유료)
```

**비용 확인:**

```
예상 월간 비용: $0.00
- t2.micro 인스턴스: 프리티어 (750시간/월)
- 8GB 스토리지: 프리티어 (30GB/월)
- 아웃바운드 트래픽: 프리티어 (15GB/월)
```

**생성 완료:**

```
인스턴스 ID: i-0abc123def456789
상태: running ✅
퍼블릭 IPv4: 13.125.XXX.XXX (임시)
```

---

#### Step 2: Elastic IP 할당 및 연결

**목표:** 고정 IP 주소 할당으로 서버 주소 불변성 확보

**Step 2-1: Elastic IP 할당**

```
EC2 콘솔 → 네트워크 및 보안 → Elastic IP
→ Elastic IP 주소 할당
→ 할당 버튼 클릭

결과:
할당된 IP: 13.125.123.45 (예시)
```

**Step 2-2: 인스턴스에 연결**

```
할당된 IP 선택
→ 작업 → Elastic IP 주소 연결
→ 인스턴스: MUTI-Backend-Server 선택
→ 연결 버튼 클릭

결과:
✅ 연결됨
✅ 비용: $0/시간 (인스턴스 연결 시)
```

**확인:**

```bash
# 이전 (동적 IP)
ssh ubuntu@13.125.XXX.XXX  # 재시작하면 바뀜

# 이후 (Elastic IP)
ssh ubuntu@13.125.123.45   # 영구적으로 동일 ✅
```

---

#### Step 3: SSH 접속 설정

**목표:** 로컬 Mac에서 EC2 서버에 안전하게 접속

**Step 3-1: 키 페어 권한 설정**

```bash
# 키 파일을 안전한 위치로 이동
mv ~/Downloads/muti-key-pair.pem ~/.ssh/

# 권한 변경 (소유자만 읽기 가능)
chmod 400 ~/.ssh/muti-key-pair.pem
```

**왜 chmod 400이 필요한가?**

```
chmod 400 = 소유자만 읽기 가능
├─ 4 (소유자): 읽기 권한
├─ 0 (그룹): 권한 없음
└─ 0 (기타): 권한 없음

비유: 은행 금고 열쇠
├─ 권한 777 (모두 접근) = 열쇠를 광장에 놓음 ❌
├─ 권한 644 (그룹도 읽기) = 열쇠를 친구와 공유 ⚠️
└─ 권한 400 (본인만 읽기) = 열쇠를 금고에 보관 ✅
```

**Step 3-2: SSH 접속**

```bash
ssh -i ~/.ssh/muti-key-pair.pem ubuntu@13.125.123.45
```

**접속 성공:**

```
Welcome to Ubuntu 24.04 LTS (GNU/Linux 6.8.0-1018-aws x86_64)

ubuntu@ip-172-31-27-134:~$  # 프롬프트 표시 ✅
```

**SSH 접속 원리:**

```
로컬 Mac
├─ 개인키: muti-key-pair.pem (비밀)
└─ 공개키: [자동 생성]

EC2 서버
├─ 공개키: muti-key-pair.pub (저장됨)
└─ 개인키로 서명한 요청만 허용

접속 과정:
1. Mac: "저 접속하고 싶어요" (개인키로 서명)
2. EC2: [공개키로 검증] "OK, 들어오세요" ✅
```

---

#### Step 4: Docker 설치

**목표:** EC2 서버에 Docker Engine 설치

**공식 설치 스크립트 사용:**

```bash
# 편의성 스크립트 다운로드 및 실행
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 출력:
# Executing docker install script, commit: 3f6b0e2
# + sh -c apt-get update -qq >/dev/null
# + sh -c apt-get install docker-ce docker-ce-cli containerd.io...
# Docker version 28.4.0, build 99d02a3 ✅
```

**사용자 권한 설정:**

```bash
# ubuntu 사용자를 docker 그룹에 추가
sudo usermod -aG docker ubuntu

# 변경사항 적용 (재로그인 또는 newgrp)
newgrp docker

# 확인
docker --version
# Docker version 28.4.0, build 99d02a3 ✅
```

**왜 usermod가 필요한가?**

```
기본 상태:
├─ docker 명령어는 root 권한 필요
└─ 매번 sudo docker ... 입력해야 함 😫

usermod 후:
├─ ubuntu 사용자가 docker 그룹에 속함
└─ docker ... 바로 실행 가능 😊

비유: 회사 출입증
├─ 기본: 매번 경비실에서 임시 출입증 발급
└─ usermod 후: 정직원 출입증 발급 (자유롭게 출입)
```

---

#### Step 5: GitHub Container Registry (GHCR) 로그인

**목표:** GHCR에서 Docker 이미지를 pull하기 위한 인증

**Step 5-1: Personal Access Token 생성**

```
GitHub 웹사이트
→ Settings
→ Developer settings
→ Personal access tokens
→ Tokens (classic)
→ Generate new token (classic)

토큰 설정:
├─ Note: MUTI GHCR Access
├─ Expiration: No expiration (만료 없음)
└─ Scopes:
    ✅ read:packages (이미지 pull)
    ✅ write:packages (이미지 push, 선택)

토큰 생성:
ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
→ 즉시 복사 및 안전한 곳에 저장! ⚠️
```

**왜 토큰이 필요한가?**

```
GitHub = 비밀 창고
Docker 이미지 = 창고 안의 물건

토큰 없이:
사용자: "이미지 주세요"
GitHub: "누구세요? 신분증 보여주세요" ❌

토큰 사용:
사용자: "이미지 주세요 [토큰 제시]"
GitHub: "확인했습니다. 여기 있습니다" ✅
```

**Step 5-2: GHCR 로그인**

```bash
# EC2 서버에서 실행
echo ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx | docker login ghcr.io -u 7angJung --password-stdin

# 출력:
# Login Succeeded ✅
```

**명령어 분석:**

```bash
echo [토큰]                    # 토큰을 출력
|                              # 파이프 (출력을 다음 명령의 입력으로)
docker login ghcr.io           # GHCR에 로그인
-u 7angJung                    # 사용자 이름
--password-stdin               # 비밀번호를 표준 입력에서 받음
```

**보안 측면:**

```
안전한 방법 (사용):
echo TOKEN | docker login ... --password-stdin
→ 토큰이 커맨드 히스토리에 남지만, 화면에 노출 안 됨 ✅

위험한 방법 (사용 금지):
docker login ... -p TOKEN
→ 비밀번호가 프로세스 목록에 노출됨 ❌
→ history에 평문으로 저장됨 ❌
```

---

#### Step 6: 애플리케이션 디렉토리 구조 생성

**목표:** 배포에 필요한 파일 구조 생성

```bash
# 애플리케이션 디렉토리 생성
mkdir -p ~/muti-app
cd ~/muti-app

# 최종 구조:
~/muti-app/
├── .env                 # 환경 변수 (Supabase 연결 정보)
└── docker-compose.yml   # Docker Compose 설정
```

---

#### Step 7: 환경 변수 파일 생성 (.env)

**목표:** Supabase PostgreSQL 연결 정보 설정

**Step 7-1: Supabase 연결 정보 확인**

```
Supabase Dashboard
→ Project Settings
→ Database
→ Connection string
→ Transaction Pooler (포트 6543) ✅
```

**Step 7-2: .env 파일 생성**

```bash
# EC2에서 실행
cat > .env << 'EOF'
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://aws-0-ap-northeast-2.pooler.supabase.com:6543/postgres
SPRING_DATASOURCE_USERNAME=postgres.xxxxxxxxxxxxxxxxxxxx
SPRING_DATASOURCE_PASSWORD=your-database-password

# Flyway
SPRING_FLYWAY_ENABLED=true
SPRING_FLYWAY_LOCATIONS=classpath:db/migration
SPRING_FLYWAY_BASELINE_ON_MIGRATE=true

# JWT
JWT_SECRET=your-jwt-secret-key-at-least-32-characters-long
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=604800000
EOF
```

**환경 변수 설명:**

```
SPRING_DATASOURCE_URL
└─ PostgreSQL 서버 주소 및 데이터베이스 이름
   └─ Transaction Pooler 사용 (연결 풀링)

SPRING_DATASOURCE_USERNAME
└─ 데이터베이스 사용자 이름

SPRING_DATASOURCE_PASSWORD
└─ 데이터베이스 비밀번호 (절대 GitHub에 커밋하면 안 됨!)

JWT_SECRET
└─ JWT 서명에 사용하는 비밀 키 (32자 이상)
```

---

#### Step 8: docker-compose.yml 파일 생성

**목표:** 컨테이너 실행 설정 정의

```bash
cat > docker-compose.yml << 'EOF'
services:
  muti-app:
    image: ghcr.io/7angjung/muti:latest
    container_name: muti-backend
    ports:
      - "8080:8080"
    env_file:
      - .env
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
EOF
```

**설정 항목 분석:**

```yaml
services:
  muti-app:                              # 서비스 이름
    image: ghcr.io/7angjung/muti:latest  # Docker 이미지
    container_name: muti-backend         # 컨테이너 이름

    ports:
      - "8080:8080"                      # 포트 매핑
        # 왼쪽: 호스트(EC2) 포트
        # 오른쪽: 컨테이너 내부 포트

    env_file:
      - .env                             # 환경 변수 파일

    restart: unless-stopped              # 재시작 정책
      # always: 항상 재시작
      # unless-stopped: 수동 중지 전까지 재시작 ✅
      # on-failure: 에러 발생 시에만 재시작
      # no: 재시작 안 함

    healthcheck:                         # 헬스체크 설정
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
        # 30초마다 헬스체크 실행
        # 연속 3번 실패 시 컨테이너 unhealthy 판정
```

**Healthcheck 동작 원리:**

```
Docker Engine (30초마다)
   ↓
curl http://localhost:8080/actuator/health
   ↓
Spring Boot Actuator
   ↓
{"status": "UP"}
   ↓
Docker: "컨테이너 정상" ✅

만약 응답 없으면:
   ↓ (3번 연속 실패)
Docker: "컨테이너 비정상" ❌
   ↓
자동 재시작 (restart: unless-stopped)
```

**비유: 자동차 시동**

```
restart: unless-stopped = 자동 시동

시나리오 1: 엔진 꺼짐 (예기치 않은 종료)
Docker: "어? 꺼졌네. 다시 시동 걸어야지" → 재시작 ✅

시나리오 2: 운전자가 시동 끔 (docker compose down)
Docker: "운전자가 끈 거네. 가만히 있어야지" → 재시작 안 함 ✅

시나리오 3: EC2 서버 재부팅
Docker: "서버가 켜졌네. 자동차도 시동 걸어야지" → 재시작 ✅
```

---

#### Step 9: Docker 이미지 Pull 및 실행

**Step 9-1: 이미지 다운로드**

```bash
docker compose pull

# 출력:
# muti-app Pulling
# ace07af2f7ef Pulling fs layer
# ...
# Status: Downloaded newer image for ghcr.io/7angjung/muti:latest ✅
```

**다운로드 과정:**

```
GHCR (GitHub Container Registry)
├─ 이미지 크기: 약 300MB
│   ├─ Base layer (JRE): 200MB
│   ├─ Application layer (JAR): 80MB
│   └─ Configuration: 20MB
└─ 다운로드 시간: 약 2-3분 (네트워크 속도에 따라)

EC2 서버 로컬
└─ /var/lib/docker/overlay2/ 에 저장
```

**Step 9-2: 컨테이너 시작**

```bash
docker compose up -d

# 출력:
# [+] Running 1/1
# ✔ Container muti-backend  Started  0.5s ✅
```

**-d 옵션:**

```
-d = detached mode (백그라운드 실행)

docker compose up (foreground)
├─ 로그가 터미널에 계속 출력
├─ Ctrl+C로 종료하면 컨테이너도 종료
└─ SSH 접속 끊으면 컨테이너도 종료 ❌

docker compose up -d (background)
├─ 로그는 백그라운드에 저장
├─ Ctrl+C 해도 컨테이너 계속 실행
└─ SSH 접속 끊어도 컨테이너 계속 실행 ✅
```

---

#### Step 10: 컨테이너 상태 및 로그 확인

**상태 확인:**

```bash
docker compose ps

# 출력:
# NAME           IMAGE                          STATUS         PORTS
# muti-backend   ghcr.io/7angjung/muti:latest   Up 2 minutes   0.0.0.0:8080->8080/tcp ✅
```

**로그 확인:**

```bash
docker compose logs -f

# 출력:
# muti-backend  |
# muti-backend  |   .   ____          _            __ _ _
# muti-backend  |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
# muti-backend  | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
# muti-backend  |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
# muti-backend  |   '  |____| .__|_| |_|_| |_\__, | / / / /
# muti-backend  |  =========|_|==============|___/=/_/_/_/
# muti-backend  |
# muti-backend  | 2026-02-09 ... INFO ... Started MutiApplication in 8.245 seconds ✅
```

**로그에서 확인할 중요 사항:**

```
✅ Started MutiApplication in X seconds
   → Spring Boot 애플리케이션이 완전히 시작됨

✅ Tomcat started on port(s): 8080 (http)
   → 웹 서버가 8080 포트에서 대기 중

✅ Flyway migration completed successfully
   → 데이터베이스 마이그레이션 성공

✅ HikariPool-1 - Start completed
   → 데이터베이스 연결 풀 생성 완료
```

---

#### Step 11: Health Check 테스트

**Step 11-1: EC2 내부 테스트**

```bash
curl http://localhost:8080/actuator/health

# 응답:
# {"status":"UP"} ✅
```

**Step 11-2: 외부 접속 테스트 (로컬 Mac)**

```bash
# 로컬 Mac 터미널에서
curl http://13.125.123.45:8080/actuator/health

# 응답:
# {"status":"UP"} ✅
```

**Step 11-3: 브라우저 테스트**

```
http://13.125.123.45:8080/actuator/health

브라우저 화면:
{"status":"UP"} ✅
```

**네트워크 흐름:**

```
로컬 Mac 브라우저
   ↓ (HTTP 요청)
인터넷
   ↓
AWS Security Group (방화벽)
   ├─ 8080 포트 열림? ✅
   └─ 통과
   ↓
EC2 인스턴스 (13.125.123.45:8080)
   ↓
Docker (포트 매핑: 8080 → 8080)
   ↓
Spring Boot 컨테이너 (내부 포트 8080)
   ↓
/actuator/health 엔드포인트
   ↓
{"status":"UP"} 응답
   ↓
(역순으로 응답 전달)
   ↓
로컬 Mac 브라우저에 표시 ✅
```

---

### 6.5 트러블슈팅

#### 문제 1: docker-compose 명령어를 찾을 수 없음

**에러 메시지:**

```bash
ubuntu@ip-172-31-27-134:~/muti-app$ docker-compose pull
Command 'docker-compose' not found, but can be installed with:
sudo snap install docker          # version 28.4.0, or
sudo apt  install docker-compose  # version 1.29.2-6
```

**원인:**

```
Docker를 공식 스크립트로 설치하면:
├─ Docker Engine (docker): ✅ 설치됨
├─ Docker Compose V2 (docker compose): ✅ 설치됨 (내장)
└─ Docker Compose V1 (docker-compose): ❌ 설치 안 됨 (레거시)
```

**해결 방법:**

```bash
# 하이픈 없이 사용 (V2)
docker compose pull      ✅
docker compose up -d     ✅
docker compose ps        ✅
docker compose logs -f   ✅
```

**비교:**

| 명령어 | V1 (레거시) | V2 (최신) |
|--------|-------------|-----------|
| Pull | docker-compose pull | docker compose pull |
| Up | docker-compose up -d | docker compose up -d |
| Down | docker-compose down | docker compose down |
| Logs | docker-compose logs | docker compose logs |

**교훈:**
- Docker Compose V2는 Docker CLI에 내장되어 있음
- **하이픈 없이** `docker compose` 사용
- V1 설치는 불필요함

---

#### 문제 2: GitHub Personal Access Token 권한 문제

**상황:** 이전에 CI/CD 구축 시 workflow scope 토큰 생성

**새로운 문제:** GHCR 로그인을 위해 토큰 재생성 후, 로컬 Git push가 안 되는 문제

**에러 (예상):**

```bash
git push
# remote: Permission denied
# fatal: Authentication failed
```

**원인 분석:**

```
토큰 1 (이전): workflow scope
├─ 용도: GitHub Actions 워크플로우 파일 생성/수정
└─ 로컬 Git credential에 저장됨

토큰 2 (새로 생성): read:packages만
├─ 용도: GHCR에서 Docker 이미지 pull
└─ 토큰 1을 삭제하고 생성함 → 토큰 1 무효화 ❌

결과:
로컬 Git이 무효화된 토큰 1을 사용 중
→ Git push 실패 ❌
```

**해결 방법:**

**옵션 A: 범용 토큰 생성 (권장)**

```
GitHub → Settings → Developer settings → Tokens
→ Generate new token (classic)

선택할 Scopes:
✅ repo (코드 푸시)
✅ workflow (워크플로우 수정)
✅ read:packages (이미지 pull)
✅ write:packages (이미지 push)

→ 하나의 토큰으로 모든 작업 가능 ✅
```

**옵션 B: Git Credential 업데이트**

```bash
# 로컬 Mac에서
printf "host=github.com\nprotocol=https\n\n" | git credential-osxkeychain erase

# 다음 git push 시 새 토큰 입력
git push
Username: 7angJung
Password: [새 토큰 붙여넣기]
```

**교훈:**
- GitHub Token은 생성 시 **단 한 번만** 표시됨
- 여러 용도로 사용할 토큰은 **필요한 모든 권한을 한 번에** 부여
- 토큰을 안전한 곳에 저장 (비밀번호 관리자 추천)

---

#### 문제 3: /api/v1/health 인증 에러

**에러 메시지:**

```bash
curl http://localhost:8080/api/v1/health

# 응답:
{
  "path": "/api/v1/health",
  "error": "Unauthorized",
  "message": "인증이 필요합니다. 로그인 후 다시 시도해주세요.",
  "timestamp": "2026-02-09T17:29:02.021519432"
}
```

**원인 분석:**

```
Spring Security 설정 (SecurityConfig.java):

permitAll() 목록:
├─ /api/v1/auth/**        ✅ 인증 불필요
├─ /actuator/**           ✅ 인증 불필요
├─ /swagger-ui/**         ✅ 인증 불필요
└─ ...

anyRequest().authenticated()
└─ /api/v1/health         ❌ 인증 필요 (목록에 없음)
```

**해결 방법 1: /actuator/health 사용 (선택함)**

```bash
# EC2 내부
curl http://localhost:8080/actuator/health
# {"status":"UP"} ✅

# 외부 (로컬 Mac)
curl http://13.125.123.45:8080/actuator/health
# {"status":"UP"} ✅

# 브라우저
http://13.125.123.45:8080/actuator/health
# {"status":"UP"} ✅
```

**해결 방법 2: Security 설정 수정 (나중에 필요 시)**

```java
// SecurityConfig.java
.requestMatchers(
    "/api/v1/auth/**",
    "/api/v1/health",        // ← 추가
    "/actuator/**",
    "/swagger-ui/**",
    "/error"
).permitAll()
```

**비유: 테마파크 무료 입장 구역**

```
현재 상황:
├─ 티켓 부스 (/api/v1/auth) = 무료 입장 ✅
├─ 안내 센터 (/actuator/health) = 무료 입장 ✅
└─ 메인 헬스센터 (/api/v1/health) = 입장권 필요 ❌

해결:
안내 센터를 사용하면 됨!
(굳이 메인 헬스센터 갈 필요 없음)
```

**교훈:**
- Spring Security는 명시적으로 허용한 경로만 인증 없이 접근 가능
- `/actuator/**`는 이미 permitAll에 포함되어 있음
- 커스텀 헬스체크 엔드포인트 대신 **Spring Boot Actuator 사용 권장**

---

### 6.6 배포 아키텍처

**최종 구조:**

```
┌─────────────────────────────────────────────────────┐
│                   인터넷 사용자                        │
│              (전 세계 어디서나 접속 가능)                │
└────────────────────┬────────────────────────────────┘
                     │ HTTP 요청
                     ↓
┌─────────────────────────────────────────────────────┐
│              AWS EC2 (t2.micro)                      │
│        Public IP: 13.125.123.45 (Elastic IP)        │
│                                                      │
│  ┌───────────────────────────────────────────────┐  │
│  │        Ubuntu 24.04 LTS (운영체제)             │  │
│  │                                                │  │
│  │  ┌─────────────────────────────────────────┐  │  │
│  │  │       Docker Engine                      │  │  │
│  │  │                                          │  │  │
│  │  │  ┌────────────────────────────────────┐ │  │  │
│  │  │  │   Spring Boot Container            │ │  │  │
│  │  │  │   (muti-backend)                   │ │  │  │
│  │  │  │                                    │ │  │  │
│  │  │  │  ┌──────────────────────────────┐ │ │  │  │
│  │  │  │  │  Spring Boot Application     │ │ │  │  │
│  │  │  │  │  - Port: 8080                │ │ │  │  │
│  │  │  │  │  - JWT 인증/인가              │ │ │  │  │
│  │  │  │  │  - REST API                  │ │ │  │  │
│  │  │  │  │  - Actuator                  │ │ │  │  │
│  │  │  │  └──────────────────────────────┘ │ │  │  │
│  │  │  │                                    │ │  │  │
│  │  │  └────────────────────────────────────┘ │  │  │
│  │  │                                          │  │  │
│  │  └─────────────────────────────────────────┘  │  │
│  │                                                │  │
│  └───────────────────────────────────────────────┘  │
│                                                      │
└────────────────────┬────────────────────────────────┘
                     │ PostgreSQL 연결
                     ↓
┌─────────────────────────────────────────────────────┐
│              Supabase PostgreSQL                     │
│     (aws-0-ap-northeast-2.pooler.supabase.com)      │
│                                                      │
│  ┌───────────────────────────────────────────────┐  │
│  │  Transaction Pooler (포트 6543)                │  │
│  │  - 연결 풀링                                   │  │
│  │  - 자동 스케일링                                │  │
│  └───────────────────────────────────────────────┘  │
│                                                      │
│  ┌───────────────────────────────────────────────┐  │
│  │  PostgreSQL Database                          │  │
│  │  - users 테이블                                │  │
│  │  - refresh_tokens 테이블                       │  │
│  │  - musics 테이블                               │  │
│  │  - playlists 테이블                            │  │
│  └───────────────────────────────────────────────┘  │
│                                                      │
└─────────────────────────────────────────────────────┘
```

**데이터 흐름:**

```
1. 사용자 요청:
브라우저 → http://13.125.123.45:8080/api/v1/users

2. AWS 처리:
인터넷 → Elastic IP (13.125.123.45) → EC2 인스턴스

3. Docker 처리:
EC2:8080 → Docker 포트 매핑 → Container:8080

4. Spring Boot 처리:
컨테이너 → Spring Security (JWT 검증) → Controller → Service

5. 데이터베이스 쿼리:
Service → Repository → JDBC → Supabase Pooler → PostgreSQL

6. 응답 반환:
PostgreSQL → ... (역순) → 브라우저 화면에 JSON 표시
```

---

### 6.7 관리 명령어 모음

#### 컨테이너 관리

```bash
# 컨테이너 상태 확인
docker compose ps

# 실시간 로그 보기
docker compose logs -f

# 최근 로그 100줄만
docker compose logs --tail=100

# 컨테이너 재시작
docker compose restart

# 컨테이너 중지
docker compose down

# 컨테이너 중지 및 볼륨 삭제
docker compose down -v
```

#### 이미지 업데이트

```bash
# 최신 이미지 pull
docker compose pull

# 기존 컨테이너 중지 및 제거
docker compose down

# 새 이미지로 컨테이너 시작
docker compose up -d

# 로그 확인
docker compose logs -f
```

#### 시스템 리소스 확인

```bash
# 디스크 사용량
df -h

# 메모리 사용량
free -h

# Docker 디스크 사용량
docker system df

# 사용하지 않는 리소스 정리
docker system prune -a
```

#### 환경 변수 수정

```bash
# .env 파일 편집
nano ~/muti-app/.env

# 수정 후 재시작
docker compose restart
```

---

### 6.8 비용 최적화 팁

#### 프리티어 한도

```
EC2 t2.micro:
├─ 시간: 월 750시간 (24시간 × 31일 = 744시간)
├─ 1개 인스턴스 항상 실행: ✅ 무료
└─ 2개 인스턴스 실행: ⚠️ 초과 비용 발생

스토리지 (EBS):
├─ 크기: 월 30GB까지 무료
├─ 현재 사용: 8GB ✅
└─ 스냅샷: 월 1GB까지 무료

아웃바운드 트래픽:
├─ 크기: 월 15GB까지 무료
├─ 예상 사용량: 약 5GB/월 ✅
└─ 초과 시: GB당 $0.09

Elastic IP:
├─ 인스턴스 연결 시: 무료 ✅
├─ 미연결 시: 시간당 $0.005 ⚠️
└─ 교훈: 사용 안 하면 즉시 해제!
```

#### 비용 알림 설정

```
AWS Console → Billing → Budgets → Create budget

설정:
├─ Budget type: Cost budget
├─ Amount: $10
├─ Alert threshold: 80% ($8)
└─ Email: your-email@example.com

결과:
비용이 $8 도달 시 이메일 알림 ✅
```

#### 인스턴스 종료 방지 활성화

```
EC2 Console → 인스턴스 선택
→ 작업 → 인스턴스 설정
→ 종료 방지 변경
→ 활성화 ✅

효과:
실수로 인스턴스 삭제 방지
```

---

### 6.9 보안 권장 사항

#### SSH 키 관리

```bash
# 키 파일 권한 확인
ls -l ~/.ssh/muti-key-pair.pem
# -r-------- (400) ✅

# 잘못된 권한 (위험)
# -rw-r--r-- (644) ❌
# -rwxrwxrwx (777) ❌ 절대 금지!
```

#### 환경 변수 보안

```bash
# .env 파일 권한 설정
chmod 600 ~/muti-app/.env

# Git에 절대 커밋하지 말 것
# .gitignore에 추가:
.env
*.pem
```

#### 보안 그룹 설정

```
SSH (포트 22):
├─ 소스: 내 IP만 허용 ✅
└─ 소스: 0.0.0.0/0 (전체 허용) ❌ 위험!

HTTP (포트 8080):
├─ 소스: 0.0.0.0/0 ✅ (공개 API)
└─ 프로덕션: 추후 HTTPS(443)로 변경
```

#### 정기적인 업데이트

```bash
# 시스템 패키지 업데이트 (월 1회)
sudo apt update && sudo apt upgrade -y

# Docker 이미지 업데이트 (배포 시마다)
docker compose pull
docker compose up -d
```

---

### 6.10 Phase 6 완료 체크리스트

**인프라:**
- [x] AWS 계정 생성
- [x] EC2 인스턴스 생성 (t2.micro)
- [x] Elastic IP 할당 및 연결
- [x] SSH 키 페어 생성 및 권한 설정
- [x] 보안 그룹 설정 (22, 8080 포트)

**서버 설정:**
- [x] SSH 접속 확인
- [x] Docker Engine 설치
- [x] Docker 사용자 권한 설정

**배포:**
- [x] GitHub Personal Access Token 생성
- [x] GHCR 로그인
- [x] 애플리케이션 디렉토리 생성
- [x] .env 파일 생성
- [x] docker-compose.yml 파일 생성
- [x] Docker 이미지 pull
- [x] 컨테이너 실행

**테스트:**
- [x] 컨테이너 상태 확인
- [x] 로그 확인 (Started MutiApplication)
- [x] 내부 Health Check (EC2 내부)
- [x] 외부 Health Check (로컬 Mac)
- [x] 브라우저 접속 확인

**문서화:**
- [x] PHASE6_PROGRESS.md 작성 (예정)
- [x] PHASE_GUIDE.md 업데이트 (현재 진행 중)
- [x] 트러블슈팅 기록

---

### 6.11 성과 및 배운 점

#### 기술적 성과

```
배포 전 (로컬 개발 환경):
├─ 접속: localhost:8080 (본인만)
├─ 중단: 컴퓨터 끄면 서비스 중단
└─ 확장: 불가능

배포 후 (AWS EC2):
├─ 접속: 13.125.123.45:8080 (전 세계 누구나) ✅
├─ 중단: 24/7 무중단 운영 ✅
└─ 확장: 인스턴스 타입 변경으로 스케일업 가능 ✅
```

#### 학습 내용

**1. 클라우드 인프라:**
- AWS EC2 가상 서버 개념
- Elastic IP로 고정 IP 할당
- 보안 그룹 (방화벽) 설정
- SSH 키 기반 인증

**2. Docker 운영:**
- Docker Compose V2 사용
- 환경 변수 관리 (.env)
- 컨테이너 healthcheck 설정
- 재시작 정책 (unless-stopped)

**3. Spring Boot 프로덕션:**
- Spring Boot Actuator 활용
- Spring Security 접근 제어
- 환경별 설정 분리
- 데이터베이스 연결 풀링

**4. 트러블슈팅:**
- Docker Compose V1 vs V2
- GitHub Token 권한 관리
- Security permitAll 설정
- 네트워크 디버깅

---

### 6.12 다음 시작점

**현재 상태:**
```
✅ AWS EC2에 Spring Boot 애플리케이션 배포 완료
✅ 인터넷을 통해 전 세계 어디서나 접속 가능
✅ 24/7 무중단 운영 중
```

**다음 작업 옵션:**

#### 옵션 1: 도메인 및 HTTPS 설정 (Phase 7)
```
현재: http://13.125.123.45:8080
목표: https://muti-api.duckdns.org

단계:
1. DuckDNS 무료 도메인 등록
2. DNS A 레코드 설정 (Elastic IP 연결)
3. Let's Encrypt SSL 인증서 발급
4. Nginx 리버스 프록시 설정
5. HTTP → HTTPS 자동 리다이렉트
```

#### 옵션 2: 모니터링 및 알림 (Phase 7)
```
도구: Uptime Robot (무료)

기능:
- 5분마다 헬스체크
- 다운타임 발생 시 이메일 알림
- 가동 시간 통계 (99.9% uptime)
- 응답 시간 모니터링
```

#### 옵션 3: 프론트엔드 개발 (Phase 7)
```
기술 스택: React + Vite + TypeScript

주요 기능:
- 회원가입/로그인 UI
- 음악 검색 및 재생
- 플레이리스트 생성/관리
- JWT 토큰 관리
- 배포: Vercel (무료)
```

#### 옵션 4: 추가 백엔드 기능 (Phase 7)
```
1. Spotify API 통합
   - OAuth 2.0 인증
   - 음악 검색 API
   - 재생 제어

2. 음악 추천 시스템
   - 사용자 취향 분석
   - 협업 필터링
   - 개인화된 추천

3. 관리자 대시보드
   - 사용자 관리
   - 시스템 모니터링
   - 로그 조회
```

**권장 순서:**

```
1. Phase 7-A: 도메인 및 HTTPS (가장 중요) ⭐
   → 프로덕션 필수 요소

2. Phase 7-B: 모니터링 설정
   → 안정성 확보

3. Phase 7-C: 프론트엔드 개발
   → 사용자 경험 완성

4. Phase 7-D: 추가 백엔드 기능
   → 차별화된 기능 구현
```

---

**Phase 6 완료를 축하합니다! 🎉**

**당신은 이제:**
- ✅ AWS 클라우드 서비스 사용 경험 보유
- ✅ Docker 컨테이너 운영 능력 보유
- ✅ 프로덕션 환경 배포 경험 보유
- ✅ 포트폴리오에 "클라우드 배포 경험" 추가 가능
- ✅ 실제 운영 중인 API 서버 보유

**다음 작업을 시작할 준비가 되었습니다!**

---


