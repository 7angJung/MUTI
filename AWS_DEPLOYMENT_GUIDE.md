# AWS 배포 완전 가이드 (프리티어 안전 버전)

## ⚠️ 비용 폭탄 방지 필수 체크리스트

배포 시작 전 **반드시** 확인하고 설정하세요!

---

## STEP 0: 비용 알림 설정 (가장 중요!)

### 1. AWS 계정 프리티어 확인

**프리티어 한도:**
- ✅ EC2 t2.micro: 750시간/월 (31일 = 744시간, 1개 인스턴스만 사용 가능)
- ✅ EBS (스토리지): 30GB까지 무료
- ✅ 데이터 전송: 15GB/월까지 무료
- ❌ Elastic IP: **실행 중인 인스턴스에 연결되어 있어야 무료**
  - 미연결 시: $0.005/시간 ($3.6/월)
- ❌ Route 53 (도메인): $0.50/월 (프리티어 없음, 무료 대안 사용 권장)

**주의사항:**
- t2.micro가 아닌 다른 인스턴스(t2.small, t3.medium 등)는 비용 발생!
- 2개 이상의 EC2 인스턴스는 비용 발생!
- Elastic IP를 인스턴스에 연결하지 않으면 비용 발생!

---

### 2. 결제 알림 설정 (필수!)

**이것만은 반드시 설정하세요!**

#### A. Billing Alerts 활성화

1. **AWS Console 로그인**
   - https://console.aws.amazon.com

2. **우측 상단 계정 이름 클릭 → "Billing and Cost Management"**

3. **좌측 메뉴 "Billing preferences" 클릭**

4. **Alert preferences 섹션에서 체크:**
   - ✅ "Receive Free Tier Usage Alerts"
     - 이메일 입력: [your-email@example.com]
   - ✅ "Receive Billing Alerts"

5. **"Save preferences" 클릭**

---

#### B. CloudWatch 비용 알람 설정

**무료 사용량 초과 시 즉시 알림!**

1. **CloudWatch로 이동**
   - 서비스 검색 → "CloudWatch" 입력

2. **좌측 메뉴 "Alarms" → "All alarms" 클릭**

3. **"Create alarm" 버튼 클릭**

4. **"Select metric" 클릭**

5. **"Billing" → "Total Estimated Charge" 선택**

6. **Currency: USD 선택 → "Select metric" 클릭**

7. **임계값 설정:**
   ```
   Threshold type: Static
   Whenever EstimatedCharges is: Greater (>)
   than: 1  (또는 5, 10 등 원하는 금액)
   ```
   - 예: $1 초과 시 알림 (프리티어는 $0이어야 함)

8. **"Next" 클릭**

9. **알림 설정:**
   ```
   Select an SNS topic: Create new topic
   Topic name: billing-alarm
   Email: [your-email@example.com]
   ```

10. **"Create topic" → "Next" 클릭**

11. **알람 이름:**
    ```
    Alarm name: BillingAlert-1USD
    ```

12. **"Next" → "Create alarm" 클릭**

13. **이메일 확인:**
    - AWS에서 발송한 이메일 열기
    - "Confirm subscription" 클릭 (필수!)

**설정 완료!** 이제 비용이 $1을 초과하면 이메일로 알림이 옵니다.

---

### 3. 프리티어 사용량 확인

**현재 얼마나 사용했는지 확인:**

1. **Billing and Cost Management → "Free Tier" 클릭**

2. **확인할 항목:**
   - EC2 사용 시간
   - EBS 스토리지 사용량
   - 데이터 전송량

3. **주의:**
   - EC2 t2.micro: 750시간/월 한도
   - 현재 사용량이 700시간 이상이면 추가 인스턴스 생성 금지!

---

## STEP 1: EC2 인스턴스 생성

### 1. EC2 대시보드로 이동

1. **AWS Console → 서비스 검색 → "EC2"**

2. **좌측 메뉴 "Instances" 클릭**

3. **"Launch instances" 버튼 클릭**

---

### 2. EC2 인스턴스 설정 (하나하나 따라하세요!)

#### A. Name and tags

```
Name: muti-backend
```

---

#### B. Application and OS Images (Amazon Machine Image)

**중요: 프리티어 선택!**

```
Quick Start: Ubuntu
Amazon Machine Image (AMI): Ubuntu Server 24.04 LTS (HVM), SSD Volume Type
  - ✅ "Free tier eligible" 표시 확인!

Architecture: 64-bit (x86)
```

**주의:**
- Ubuntu Server 24.04 LTS 선택 (최신 LTS)
- "Free tier eligible" 표시가 있는지 반드시 확인!

---

#### C. Instance type

**가장 중요한 설정!**

```
Instance type: t2.micro
  - ✅ "Free tier eligible" 표시 확인!

vCPUs: 1
Memory: 1 GiB
```

**절대 하지 말 것:**
- ❌ t2.small, t2.medium, t3.micro, t3.small 등 선택하지 말 것!
- ❌ t2.micro가 아니면 무조건 비용 발생!

**참고:**
- t2.micro는 성능이 낮지만 프리티어
- 우리 애플리케이션(Spring Boot)은 충분히 실행 가능

---

#### D. Key pair (login)

**SSH 접속용 키 생성:**

1. **"Create new key pair" 클릭**

2. **설정:**
   ```
   Key pair name: muti-backend-key
   Key pair type: RSA
   Private key file format: .pem (Mac/Linux) 또는 .ppk (Windows PuTTY)
   ```
   - Mac 사용자: .pem 선택
   - Windows 사용자: .ppk 선택

3. **"Create key pair" 클릭**

4. **파일 저장:**
   - 자동으로 `muti-backend-key.pem` 다운로드됨
   - **이 파일을 안전한 곳에 보관!** (분실 시 EC2 접속 불가)

5. **권한 설정 (Mac/Linux):**
   ```bash
   chmod 400 ~/Downloads/muti-backend-key.pem
   ```

---

#### E. Network settings

**보안 그룹 설정 (매우 중요!)**

1. **"Edit" 버튼 클릭**

2. **설정:**
   ```
   Auto-assign public IP: Enable
   ```

3. **Firewall (security groups): Create security group**

4. **Security group name:**
   ```
   Security group name: muti-backend-sg
   Description: Security group for MUTI backend
   ```

5. **Inbound security groups rules:**

   **Rule 1: SSH (포트 22)**
   ```
   Type: SSH
   Protocol: TCP
   Port range: 22
   Source type: My IP (권장) 또는 Anywhere (0.0.0.0/0)
   Description: SSH access
   ```
   - My IP: 내 IP에서만 접속 가능 (안전)
   - Anywhere: 모든 IP에서 접속 가능 (위험하지만 편리)

   **Rule 2: HTTP (포트 80)**
   ```
   Type: HTTP
   Protocol: TCP
   Port range: 80
   Source type: Anywhere (0.0.0.0/0)
   Description: HTTP access
   ```

   **Rule 3: HTTPS (포트 443)**
   ```
   Type: HTTPS
   Protocol: TCP
   Port range: 443
   Source type: Anywhere (0.0.0.0/0)
   Description: HTTPS access
   ```

   **Rule 4: Custom TCP (포트 8080)**
   ```
   Type: Custom TCP
   Protocol: TCP
   Port range: 8080
   Source type: Anywhere (0.0.0.0/0)
   Description: Spring Boot application
   ```

**최종 보안 그룹 규칙:**
| Type | Protocol | Port | Source | Description |
|------|----------|------|--------|-------------|
| SSH | TCP | 22 | My IP | SSH access |
| HTTP | TCP | 80 | 0.0.0.0/0 | HTTP access |
| HTTPS | TCP | 443 | 0.0.0.0/0 | HTTPS access |
| Custom TCP | TCP | 8080 | 0.0.0.0/0 | Spring Boot |

---

#### F. Configure storage

**스토리지 설정:**

```
Volume 1 (Root volume)
Size (GiB): 20 (기본값은 8GB, 20GB로 증가 권장)
Volume type: General Purpose SSD (gp3)
  - ✅ "Free tier eligible" 확인!

Delete on termination: ✅ (체크)
Encrypted: Not encrypted (암호화는 비용 발생)
```

**주의:**
- 프리티어는 30GB까지 무료
- 20GB면 충분 (OS + Docker + 애플리케이션)
- gp3가 gp2보다 성능 좋고 무료

---

#### G. Advanced details

**대부분 기본값 사용, 변경할 것만:**

```
IAM instance profile: None (일단 없이 시작)

Termination protection: Disable (실수로 삭제 방지하려면 Enable)

Stop - Hibernate behavior: Disable

Shutdown behavior: Stop (Stop 권장, Terminate는 위험)
```

**나머지는 기본값 사용**

---

### 3. 인스턴스 생성 검토 및 실행

1. **우측 "Summary" 패널 확인:**
   ```
   Instance type: t2.micro ✅
   Free tier eligible ✅
   Number of instances: 1 ✅
   ```

2. **"Launch instance" 버튼 클릭**

3. **성공 메시지:**
   ```
   Successfully initiated launch of instance (i-xxxxxxxxxxxxx)
   ```

4. **"View all instances" 클릭**

---

### 4. 인스턴스 상태 확인

**EC2 Instances 페이지에서:**

```
Name: muti-backend
Instance ID: i-xxxxxxxxxxxxx
Instance state: Running (초록색) ← 이렇게 될 때까지 대기 (1~2분)
Instance type: t2.micro
Status check: 2/2 checks passed ← 이렇게 될 때까지 대기 (2~3분)
Public IPv4 address: xx.xx.xx.xx ← 이 IP로 접속 가능
```

**주의:**
- Instance state가 "Running"이 되어야 접속 가능
- Status check가 "2/2 checks passed"가 되어야 정상 작동

---

## STEP 2: Elastic IP 할당 및 연결

### ⚠️ Elastic IP 주의사항

**Elastic IP란?**
- 고정 IP 주소
- 인스턴스를 재부팅해도 IP가 변경되지 않음

**비용:**
- ✅ 실행 중인 인스턴스에 연결되어 있으면: 무료
- ❌ 인스턴스에 연결되지 않거나, 인스턴스가 중지되면: $0.005/시간 (월 $3.6)

**프리티어로 사용하려면:**
- ✅ 반드시 실행 중인 인스턴스에 연결
- ✅ 사용하지 않을 때는 Elastic IP 해제 (Release)

---

### 1. Elastic IP 할당

1. **EC2 대시보드 → 좌측 메뉴 "Elastic IPs" 클릭**

2. **"Allocate Elastic IP address" 버튼 클릭**

3. **설정:**
   ```
   Network Border Group: [기본값 사용]
   Public IPv4 address pool: Amazon's pool of IPv4 addresses
   ```

4. **Tags (선택사항):**
   ```
   Key: Name
   Value: muti-backend-eip
   ```

5. **"Allocate" 버튼 클릭**

6. **성공 메시지:**
   ```
   Successfully allocated Elastic IP address: xx.xx.xx.xx
   ```
   - 이 IP가 고정 IP입니다! 기록해두세요!

---

### 2. Elastic IP를 인스턴스에 연결

**이 단계를 꼭 해야 비용이 발생하지 않습니다!**

1. **할당된 Elastic IP 선택 (체크박스)**

2. **"Actions" → "Associate Elastic IP address" 클릭**

3. **설정:**
   ```
   Resource type: Instance

   Instance: [muti-backend 인스턴스 선택]
     (i-xxxxxxxxxxxxx - muti-backend)

   Private IP address: [자동 선택됨]

   Reassociation: ✅ Allow this Elastic IP address to be reassociated
   ```

4. **"Associate" 버튼 클릭**

5. **성공 메시지:**
   ```
   Successfully associated Elastic IP address
   ```

---

### 3. 인스턴스 IP 확인

**EC2 Instances 페이지에서 인스턴스 클릭:**

```
Public IPv4 address: xx.xx.xx.xx (Elastic IP)
Public IPv4 DNS: ec2-xx-xx-xx-xx.ap-northeast-2.compute.amazonaws.com
```

**이제 이 IP로 접속 가능합니다!**

---

## STEP 3: EC2 접속 및 환경 설정

### 1. SSH로 EC2 접속

**Mac/Linux 터미널에서:**

```bash
# 키 파일 권한 설정 (처음 한 번만)
chmod 400 ~/Downloads/muti-backend-key.pem

# EC2 접속
ssh -i ~/Downloads/muti-backend-key.pem ubuntu@[Elastic IP]
```

**예시:**
```bash
ssh -i ~/Downloads/muti-backend-key.pem ubuntu@13.125.123.45
```

**첫 접속 시 나오는 메시지:**
```
The authenticity of host '13.125.123.45' can't be established.
Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
```
- "yes" 입력

**접속 성공:**
```
Welcome to Ubuntu 24.04 LTS (GNU/Linux 6.8.0-1018-aws x86_64)

ubuntu@ip-172-31-x-x:~$
```

---

### 2. 시스템 업데이트

**EC2 내부에서 실행:**

```bash
# 패키지 목록 업데이트
sudo apt update

# 설치된 패키지 업그레이드
sudo apt upgrade -y
```

**시간이 좀 걸립니다 (2~3분)**

---

### 3. Docker 설치

**EC2 내부에서 실행:**

```bash
# 1. 필수 패키지 설치
sudo apt install -y ca-certificates curl gnupg lsb-release

# 2. Docker GPG 키 추가
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# 3. Docker 저장소 추가
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 4. Docker 설치
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 5. Docker 버전 확인
docker --version
# 출력: Docker version 24.x.x, build xxxxx

# 6. 현재 사용자를 docker 그룹에 추가 (sudo 없이 docker 명령 사용)
sudo usermod -aG docker $USER

# 7. 변경사항 적용 (로그아웃 후 재접속 또는 아래 명령 실행)
newgrp docker

# 8. Docker 정상 작동 확인
docker ps
# 출력: CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
# (비어있음 - 정상)
```

**Docker 설치 완료!**

---

### 4. 필요한 도구 설치

```bash
# Git 설치 (필요 시)
sudo apt install -y git

# jq 설치 (JSON 파싱 도구)
sudo apt install -y jq

# htop 설치 (시스템 모니터링)
sudo apt install -y htop
```

---

## STEP 4: 애플리케이션 배포

### 1. 환경 변수 파일 생성

**EC2 내부에서:**

```bash
# 애플리케이션 디렉토리 생성
mkdir -p ~/muti-app
cd ~/muti-app

# .env 파일 생성
nano .env
```

**`.env` 파일 내용:**
```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Database (Supabase)
DB_URL=jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:6543/postgres?prepareThreshold=0
DB_USERNAME=postgres.qlnuleskbxqhpyadsoeo
DB_PASSWORD=cjhpeter9800*

# JWT Secret (256비트 이상)
JWT_SECRET=muti-secret-key-production-change-this-to-secure-random-string-minimum-256-bits
```

**저장:**
- `Ctrl + O` (저장)
- `Enter` (확인)
- `Ctrl + X` (종료)

---

### 2. docker-compose.yml 생성

```bash
# docker-compose.yml 생성
nano docker-compose.yml
```

**`docker-compose.yml` 내용:**
```yaml
services:
  muti-backend:
    image: ghcr.io/7angjung/muti:dev
    container_name: muti-backend
    ports:
      - "8080:8080"
    env_file:
      - .env
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 40s
```

**저장:**
- `Ctrl + O` → `Enter` → `Ctrl + X`

---

### 3. GitHub Container Registry 로그인

**GitHub Personal Access Token이 필요합니다.**

```bash
# GitHub 로그인
echo [YOUR_GITHUB_TOKEN] | docker login ghcr.io -u 7angJung --password-stdin
```

**예시:**
```bash
echo ghp_xxxxxxxxxxxxxxxxxxxx | docker login ghcr.io -u 7angJung --password-stdin
```

**성공 메시지:**
```
Login Succeeded
```

---

### 4. Docker 이미지 pull 및 실행

```bash
# 이미지 pull (처음 한 번만, 시간 걸림 2~3분)
docker pull ghcr.io/7angjung/muti:dev

# 컨테이너 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f
```

**성공 메시지 (로그에서):**
```
muti-backend  | Started MutiApplication in 5.xxx seconds
muti-backend  | Tomcat started on port(s): 8080 (http)
```

**로그 종료:**
- `Ctrl + C`

---

### 5. 애플리케이션 동작 확인

```bash
# Health check
curl http://localhost:8080/actuator/health

# 출력:
# {"status":"UP"}

# Survey API 테스트
curl http://localhost:8080/api/v1/surveys | jq

# Board API 테스트
curl http://localhost:8080/api/v1/boards | jq
```

**모두 정상 응답하면 성공!**

---

### 6. 외부에서 접속 확인

**로컬 컴퓨터(Mac)에서:**

```bash
# Health check
curl http://[Elastic IP]:8080/actuator/health

# Survey API
curl http://[Elastic IP]:8080/api/v1/surveys | jq
```

**예시:**
```bash
curl http://13.125.123.45:8080/actuator/health
```

**성공하면 배포 완료!** 🎉

---

## STEP 5: 도메인 연결 (무료 대안)

### 옵션 1: DuckDNS (추천)

**무료 DDNS 서비스**

1. **https://www.duckdns.org 접속**

2. **GitHub로 로그인**

3. **도메인 생성:**
   ```
   Sub domain: muti-backend
   Full domain: muti-backend.duckdns.org
   Current IP: [Elastic IP 입력]
   ```

4. **"Add domain" 클릭**

5. **이제 접속 가능:**
   ```
   http://muti-backend.duckdns.org:8080/api/v1/surveys
   ```

---

### 옵션 2: No-IP

**무료 DDNS 서비스 (30일마다 갱신 필요)**

1. **https://www.noip.com 접속**

2. **회원가입 (무료)**

3. **도메인 생성**

---

### 옵션 3: Freenom (무료 실제 도메인)

**무료 도메인 (.tk, .ml, .ga, .cf, .gq)**

1. **https://www.freenom.com 접속**

2. **도메인 검색 (예: muti-backend.tk)**

3. **무료로 등록 (최대 12개월)**

**주의:**
- 갱신 필요
- 안정성이 낮음

---

## STEP 6: HTTPS 설정 (Let's Encrypt)

### 1. Nginx 설치

**EC2 내부에서:**

```bash
# Nginx 설치
sudo apt install -y nginx

# Nginx 시작
sudo systemctl start nginx
sudo systemctl enable nginx

# 상태 확인
sudo systemctl status nginx
```

---

### 2. Nginx 리버스 프록시 설정

```bash
# Nginx 설정 파일 생성
sudo nano /etc/nginx/sites-available/muti-backend
```

**내용:**
```nginx
server {
    listen 80;
    server_name muti-backend.duckdns.org;  # 본인 도메인으로 변경

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

**저장 및 활성화:**
```bash
# 심볼릭 링크 생성
sudo ln -s /etc/nginx/sites-available/muti-backend /etc/nginx/sites-enabled/

# 기본 설정 삭제
sudo rm /etc/nginx/sites-enabled/default

# 설정 테스트
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx
```

**이제 포트 없이 접속 가능:**
```
http://muti-backend.duckdns.org/api/v1/surveys
```

---

### 3. Certbot 설치 (무료 SSL)

```bash
# Certbot 설치
sudo apt install -y certbot python3-certbot-nginx

# SSL 인증서 발급
sudo certbot --nginx -d muti-backend.duckdns.org
```

**질문에 답변:**
```
Enter email address: [your-email@example.com]
Agree to terms: Y
Share email: N
```

**성공 메시지:**
```
Successfully deployed certificate
Congratulations!
```

**이제 HTTPS로 접속 가능:**
```
https://muti-backend.duckdns.org/api/v1/surveys
```

---

### 4. 자동 갱신 설정

**Let's Encrypt 인증서는 90일 후 만료되므로 자동 갱신 필요:**

```bash
# 자동 갱신 테스트
sudo certbot renew --dry-run

# 성공하면 자동으로 cron에 등록됨
# 확인:
sudo systemctl status certbot.timer
```

---

## STEP 7: 모니터링 및 자동 재시작

### 1. Docker 자동 재시작 확인

**이미 설정되어 있음:**
```yaml
restart: unless-stopped
```

**의미:**
- EC2 재부팅 시 자동으로 컨테이너 시작
- 컨테이너 크래시 시 자동으로 재시작

---

### 2. 로그 확인

```bash
# 실시간 로그
docker-compose logs -f

# 최근 100줄
docker-compose logs --tail=100

# 특정 시간 이후 로그
docker-compose logs --since 1h
```

---

### 3. 시스템 리소스 모니터링

```bash
# CPU/메모리 사용률 (실시간)
htop

# Docker 컨테이너 리소스
docker stats
```

---

## STEP 8: 비용 관리 및 안전 수칙

### ✅ 프리티어 유지 체크리스트

**매일 확인:**
- [ ] EC2 인스턴스가 1개만 실행 중인가?
- [ ] 인스턴스 타입이 t2.micro인가?
- [ ] Elastic IP가 실행 중인 인스턴스에 연결되어 있는가?

**매주 확인:**
- [ ] Billing Dashboard에서 비용 $0 확인
- [ ] Free Tier 사용량 확인 (750시간 이내)

**사용하지 않을 때:**
```bash
# 인스턴스 중지 (비용 절약)
# AWS Console → EC2 → Instances → 인스턴스 선택
# → Instance state → Stop instance

# 주의: 중지 시 Elastic IP는 비용 발생!
# 장기간 사용 안 할 거면 Elastic IP도 Release하기
```

**완전히 삭제하려면:**
1. EC2 인스턴스 종료 (Terminate)
2. Elastic IP 해제 (Release)
3. 보안 그룹 삭제
4. 키페어 삭제

---

## 🎉 배포 완료!

**최종 확인:**
```
✅ EC2 t2.micro 인스턴스 실행 중
✅ Docker 컨테이너 실행 중
✅ 외부에서 API 접속 가능
✅ 도메인 연결 (선택사항)
✅ HTTPS 설정 (선택사항)
✅ 비용 알림 설정
```

**접속 URL:**
```
# Public IP로 접속
http://[Elastic IP]:8080/api/v1/surveys

# 도메인으로 접속 (설정한 경우)
http://muti-backend.duckdns.org/api/v1/surveys
https://muti-backend.duckdns.org/api/v1/surveys (SSL 설정 시)
```

---

## 📝 다음 단계

**프론트엔드 개발 시:**
```javascript
// React 프로젝트에서
const API_BASE_URL = 'https://muti-backend.duckdns.org';

const response = await fetch(`${API_BASE_URL}/api/v1/surveys`);
```

**완전 배포!** 🚀