# MUTI 프로젝트 문서

> **프로젝트**: 음악 기반 소셜 플랫폼
> **기술 스택**: Spring Boot + React + PostgreSQL
> **배포**: AWS EC2 + Nginx
> **문서 최종 업데이트**: 2026-02-11

---

## 📚 문서 목록

### 핵심 문서

- **[요구사항 및 가이드라인](./requirements.md)** ⭐
  - Claude 작업 시 필수 준수 사항
  - 문서화 요구사항
  - 작업 방식 및 워크플로우
  - 코드 작성 원칙
  - 커뮤니케이션 가이드라인

### Phase별 상세 문서

#### Backend 개발 Phase

- **[Phase 0: 프로젝트 초기 설정](./phase0.md)**
  - Spring Boot 프로젝트 생성
  - 기본 설정 및 구조

- **[Phase 1: 회원 관리](./phase1.md)**
  - User Entity 및 CRUD
  - Repository, Service, Controller 구조

- **[Phase 2: JWT 인증/인가](./phase2.md)**
  - Spring Security 설정
  - JWT 토큰 발급/검증
  - 회원가입/로그인 API

- **[Phase 3: 음악/플레이리스트](./phase3.md)**
  - Music, Playlist Entity
  - CRUD API 구현
  - 연관관계 매핑

- **[Phase 4: Supabase 연동](./phase4.md)**
  - Supabase PostgreSQL 연결
  - Connection Pool 설정
  - 트러블슈팅

- **[Phase 5: Flyway 마이그레이션](./phase5.md)**
  - Flyway 설정
  - 마이그레이션 스크립트 작성
  - 버전 관리

#### 배포 Phase

- **[Phase 6: AWS 배포](./phase6.md)**
  - EC2 인스턴스 생성
  - Docker 배포
  - GitHub Actions CI/CD

- **[Phase 7-A: 도메인 + HTTPS](./phase7a.md)**
  - DuckDNS 무료 도메인
  - Let's Encrypt SSL 인증서
  - Nginx 리버스 프록시

- **[Phase 7-B: 모니터링](./phase7b.md)**
  - Uptime Robot 설정
  - 알림 구성

#### Frontend 개발 Phase

- **[Phase 7-C: 프론트엔드 기본 구조](./phase7c.md)**
  - React + Vite + TypeScript
  - Tailwind CSS (Spotify 테마)
  - Zustand 상태 관리
  - React Router
  - Axios 인터셉터 (JWT)
  - Layout 및 페이지 컴포넌트

- **[Phase 7-C Survey: 설문 시스템 구현](./phase7c_survey.md)**
  - 설문 CRUD API
  - 8개 질문 + 16가지 MUTI 타입
  - 점수 계산 알고리즘
  - 결과 분석 시스템

- **[Phase 7-D: Likert Scale 전환](./phase7d_likert_scale.md)**
  - 이분법 → 5점 리커트 척도
  - 8개 질문, 40개 옵션
  - 대칭적 점수 시스템 (+5, +3, +1, -3, -5)
  - 반응형 UI (가로/세로 레이아웃)

- **[Phase 7-E: Nginx + Frontend 배포](./phase7e_nginx_frontend_deployment.md)** ✅ 최신
  - Nginx 리버스 프록시 설정
  - EC2에 프론트엔드 배포
  - SPA 라우팅 (try_files)
  - 정적 파일 캐싱 최적화
  - 단일 도메인 통합 (프론트+백엔드)

---

## 🎯 프로젝트 현황

### ✅ 완료된 작업

- Backend API 개발 (Phase 0~3)
- Supabase PostgreSQL 연동 (Phase 4)
- Flyway 마이그레이션 (Phase 5)
- AWS EC2 배포 (Phase 6)
- HTTPS 설정 (Phase 7-A)
- 모니터링 설정 (Phase 7-B)
- Frontend 기본 구조 (Phase 7-C)
- 설문 시스템 구현 (Phase 7-C Survey)
- Likert Scale 전환 (Phase 7-D)
- Nginx + Frontend 배포 (Phase 7-E)

### 🎉 현재 상태

**배포 완료:** http://muti-world.duckdns.org
- ✅ 프론트엔드: React SPA (Nginx 서빙)
- ✅ 백엔드: Spring Boot API
- ✅ 데이터베이스: PostgreSQL (Docker)
- ✅ 설문 시스템: 8개 질문, 5점 리커트 척도
- ✅ 16가지 MUTI 음악 성향 타입 분석

### 📅 다음 계획

1. HTTPS 적용 (Let's Encrypt)
2. Gzip 압축 최적화
3. Spotify API 통합
4. 음악 검색 및 추천 기능
5. 플레이리스트 생성/공유 기능

---

## 🛠 기술 스택

### Backend
```
Language: Java 21
Framework: Spring Boot 3.4.1
Database: PostgreSQL 17
Migration: Flyway
Authentication: JWT (jjwt 0.12.6)
Deployment: AWS EC2 + Docker
CI/CD: GitHub Actions
```

### Frontend
```
Framework: React 19.2.0
Build Tool: Vite 7.3.1
Language: TypeScript 5.9.3
State Management: Zustand 5.0.11
Routing: React Router DOM 7.13.0
HTTP Client: Axios 1.13.5
Styling: Tailwind CSS 4.1.18
```

### Infrastructure
```
Cloud: AWS EC2 (t2.micro)
Web Server: Nginx 1.24.0
Container: Docker + Docker Compose
Registry: GitHub Container Registry
Domain: muti-world.duckdns.org
SSL: HTTP (HTTPS 준비 중)
Monitoring: Uptime Robot
```

---

## 📖 문서 작성 규칙

모든 Phase 문서는 다음 구조를 따릅니다:

```markdown
# Phase {N}: {제목}

## 목차
...

## 개요
- 날짜
- 목표
- 완료 상태

## 기술 스택
...

## 기술 선택 이유
- 대안 기술 비교
- 선택 이유
- 비유를 통한 설명

## 구현 내용
...

## 트러블슈팅
- 문제
- 원인
- 해결 방법
- 교훈

## 새로운 개념
- 개념 설명
- 비유
- 언제 사용하는지
- 언제 사용하지 말아야 하는지

## 다음 단계
...
```

자세한 문서 작성 가이드는 [요구사항 문서](./requirements.md)를 참조하세요.

---

## 🔗 유용한 링크

### 프로젝트

- **서비스 URL**: http://muti-world.duckdns.org
- **Backend API**: http://muti-world.duckdns.org/api
- **GitHub**: https://github.com/7angJung/MUTI

### 외부 서비스

- **Supabase Dashboard**: https://supabase.com/dashboard
- **AWS Console**: https://console.aws.amazon.com
- **Uptime Robot**: https://uptimerobot.com
- **DuckDNS**: https://www.duckdns.org

### 공식 문서

- [Spring Boot](https://docs.spring.io/spring-boot/)
- [React](https://react.dev/)
- [Vite](https://vite.dev/)
- [Tailwind CSS](https://tailwindcss.com/)
- [PostgreSQL](https://www.postgresql.org/docs/)

---

## 📝 기여 가이드

### 새로운 Phase 시작 시

1. `docs/phase{N}.md` 파일 생성
2. 템플릿에 따라 작성 (requirements.md 참조)
3. README.md에 링크 추가
4. Phase 완료 후 문서 업데이트

### 트러블슈팅 추가 시

1. 해당 Phase 문서의 "트러블슈팅" 섹션에 추가
2. 에러 메시지 전체 포함
3. 원인 분석 및 해결 과정 상세 기록
4. 예방법 제시

### 개념 설명 추가 시

1. 일상 생활 비유 포함 필수
2. "언제 사용하는지" 명시
3. "언제 사용하지 말아야 하는지" 명시
4. 코드 예시 포함

---

## 📞 문의

프로젝트 관련 문의사항이 있으시면:

- **GitHub Issues**: (Repository URL)/issues
- **Email**: (Your Email)

---

**마지막 업데이트**: 2026-02-11

**다음 업데이트 예정**: Phase 7-F (HTTPS) 또는 Phase 8 (Spotify API) 완료 후