# EC2 재시작 체크리스트

## 1단계: EC2 인스턴스 시작

### AWS Console
```
AWS Console → EC2 → 인스턴스
→ 인스턴스 선택
→ 인스턴스 상태 → 인스턴스 시작
→ 상태: running 대기 (2-3분)
```

## 2단계: SSH 접속 테스트

```bash
# Mac 터미널에서
ssh -i ~/.ssh/muti-key-pair.pem ubuntu@YOUR_ELASTIC_IP

# 성공 시:
# Welcome to Ubuntu 24.04 LTS
# ubuntu@ip-172-31-27-134:~$
```

## 3단계: Docker 컨테이너 확인

```bash
# EC2에서 실행
docker compose ps

# 예상 결과:
# NAME           STATUS         PORTS
# muti-backend   Up X minutes   0.0.0.0:8080->8080/tcp ✅
```

**만약 컨테이너가 실행 중이 아니라면:**

```bash
cd ~/muti-app
docker compose up -d
docker compose logs -f
```

## 4단계: Health Check

### 내부 테스트 (EC2에서)
```bash
curl http://localhost:8080/actuator/health
# {"status":"UP"} ✅
```

### 외부 테스트 (Mac에서)
```bash
curl http://YOUR_ELASTIC_IP:8080/actuator/health
# {"status":"UP"} ✅
```

### 브라우저 테스트
```
http://YOUR_ELASTIC_IP:8080/actuator/health
# {"status":"UP"} ✅
```

## 5단계: 문제 해결 (필요 시)

### 컨테이너가 시작 안 되면
```bash
# 로그 확인
docker compose logs --tail=100

# 재시작
docker compose restart

# 완전 재시작
docker compose down
docker compose up -d
```

### Health Check 실패 시
```bash
# 로그에서 에러 확인
docker compose logs | grep ERROR

# 데이터베이스 연결 확인
docker compose logs | grep "HikariPool"
```

---

## ✅ 모든 확인 완료 후

- [ ] EC2 인스턴스 실행 중
- [ ] SSH 접속 가능
- [ ] Docker 컨테이너 Up 상태
- [ ] Health Check 성공 (내부/외부)

**이제 개발 작업을 시작할 수 있습니다!**