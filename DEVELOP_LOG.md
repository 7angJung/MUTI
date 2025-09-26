# MUTI (Music Type Indicator) 개발 일지

## 2025년 09월 23일 - 프로젝트 초기 설정 및 개발 시작

### 1. 프로젝트 재시작 및 규칙 설정
- 프로젝트를 처음부터 다시 시작하며, 단계별 개발, H2/MySQL DB 분리, 개발 일지 작성 규칙을 설정했습니다.

### 2. `DEVELOP_LOG.md` 파일 생성
- 현재 이 파일을 생성하여 개발 진행 상황을 기록할 예정입니다.

### 3. `application.properties` 분리 및 설정 완료
- `application-test.yml` (H2 DB) 및 `application-dev.yml` (MySQL `muti_db`) 파일을 `src/main/resources` 경로에 생성했습니다.
- `application.properties` 파일에 `spring.profiles.active=dev`를 추가하여 기본 프로필을 `dev`로 설정했습니다.

### 5. 사용자 회원가입 기능 구현 및 테스트 완료

#### 개발 내용:
- **User Entity (`User.java`)**: 사용자 정보를 저장하는 JPA 엔티티를 정의했습니다.
- **UserRepository (`UserRepository.java`)**: `User` 엔티티를 위한 Spring Data JPA 리포지토리를 생성했습니다.
- **UserService (`UserService.java`)**: 사용자 등록을 위한 비즈니스 로직을 구현했습니다.
- **SecurityConfig (`SecurityConfig.java`)**: Spring Security 설정을 추가했습니다.
- **UserRegisterRequest DTO (`UserRegisterRequest.java`)**: 사용자 회원가입 요청 데이터를 위한 DTO를 정의했습니다.
- **UserController (`UserController.java`)**: 사용자 회원가입 요청을 처리하는 REST 컨트롤러를 구현했습니다.

## 2025년 09월 24일 - 사용자 로그인 기능 구현

### 1. 사용자 로그인 기능 구현
- Spring Security를 사용하여 사용자 로그인 기능을 구현했습니다.

## 2025년 09월 24일 - JWT 토큰 기반 인증으로 전환

### 1. JWT 인증 시스템 구현
- 기존의 세션 기반 인증 방식을 상태 비저장(stateless) 토큰 기반 인증으로 전환했습니다.

## 2025년 09월 25일 - MUTI 성향 분석 및 추천 기능 구현

### 1. MUTI 성향 분석 기능 구현
- 사용자의 음악적 성향을 분석하기 위한 질문 기반의 MUTI 타입 결정 시스템을 구현했습니다.

### 2. MUTI 타입 기반 추천 로직 구현
- 분석된 MUTI 타입을 기반으로 사용자에게 음악을 추천하는 핵심 로직을 구현했습니다.

## 2025년 09월 25일 - 회원가입 및 로그인 시스템 리팩토링

### 1. `username` -> `userId` 리팩토링 및 유효성 검사 강화
- 기존 `username` 필드를 `userId`로 변경하고, 영어와 숫자로만 구성되도록 제한하는 리팩토링을 진행했습니다.

## 2025년 09월 27일 - 카카오 OAuth2 로그인 기능 구현

### 1. JWT 기반 소셜 로그인(카카오) 구현
- 카카오 계정을 이용한 소셜 로그인 및 신규 회원가입 기능을 구현하고, 기존 JWT 인증 흐름에 통합했습니다.

#### 개발 내용:
- **DB 스키마 확장**: `users` 테이블에 `provider` 컬럼을 추가했습니다.
- **`User` 엔티티 수정**: `provider` 필드와 소셜 회원가입용 생성자를 추가했습니다.
- **`UserService` 수정**: 로컬 회원가입 시 이메일이 소셜 계정으로 기등록된 경우 가입을 차단했습니다.
- **OAuth2 설정 추가**: `application-dev.yml`과 `application-test.yml`에 카카오 클라이언트 설정을 추가했습니다.
- **`CustomOAuth2UserService` 구현**: 카카오 사용자 정보를 가져와 신규 가입 또는 로그인을 처리하는 로직을 구현했습니다.
- **`OAuth2AuthenticationSuccessHandler` 구현**: 소셜 로그인 성공 시 JWT를 발급하여 프론트엔드로 리다이렉트하도록 구현했습니다.
- **`SecurityConfig` 수정**: `oauth2Login()` 설정을 추가하여 커스텀 서비스와 핸들러를 연결했습니다.
- **`index.html` 수정**: 카카오 로그인 버튼과 토큰 처리 스크립트를 추가했습니다.

#### 문제 발생 및 해결:
1.  **컴파일/테스트 오류**: 생성자 누락, 테스트 설정 파일에 OAuth2 설정 부재, 인증 로직 변경으로 인한 테스트 실패 등 다양한 문제를 단계적으로 해결했습니다.
2.  **수동 테스트 실패 (401 Unauthorized, `IllegalArgumentException`)**: 카카오와 통신하는 과정에서 `client-authentication-method` 설정값이 누락되거나 잘못되어 발생한 문제를 `client_secret_post`로 명시하여 최종 해결했습니다.

## 2025년 09월 27일 - 결과 PDF 다운로드 기능 구현

### 1. 클라이언트 사이드 PDF 생성 기능 구현
- 사용자가 자신의 MUTI 분석 결과와 추천받은 음악 목록을 PDF 파일로 다운로드할 수 있는 기능을 구현했습니다.

#### 개발 내용:
- **`jspdf` & `html2canvas` 라이브러리 추가**: `index.html`에 CDN 방식으로 라이브러리를 추가했습니다.
- **`index.html` 구조 수정 및 기능 추가**: PDF로 변환할 결과 영역을 특정 `div`로 감싸고, PDF 저장 버튼과 `downloadResultAsPDF` 함수를 추가했습니다.

## 2025년 09월 27일 - 사용자 흐름 개선: 결과 페이지 분리

### 1. 설문 완료 후 결과 페이지로 이동 기능 구현
- 사용자가 설문 조사를 완료하면, 즉시 추천 음악 목록까지 포함된 전체 결과를 새로운 전용 페이지에서 확인할 수 있도록 사용자 흐름을 개선했습니다.

#### 개발 내용:
- **백엔드 리팩토링 (API 통합)**:
  - **`SurveyResultWithMusicDto` 생성**: 기존의 설문 결과 DTO에 음악 추천 목록(`List<MusicDto>`)을 포함하는 새로운 통합 DTO를 생성했습니다.
  - **`MusicService` 수정**: `getRecommendationsByMutiType` 메서드를 새로 만들어, `mutiType` 문자열만으로 추천 목록을 조회할 수 있도록 리팩토링했습니다. 이를 통해 `SurveyService`에서 재사용이 가능해졌습니다.
  - **`SurveyService` 수정**: 설문 제출 처리(`calculateAndSaveMutiType`) 시, MUTI 타입 계산 후 곧바로 `MusicService`를 호출하여 음악 추천 목록까지 조회한 후, 통합 DTO(`SurveyResultWithMusicDto`)로 반환하도록 수정했습니다.
  - **`Controller` 수정**: `SurveyController`는 이제 통합 DTO를 반환하며, 더 이상 필요 없어진 `MusicController`의 `/api/music/**` 경로는 `SecurityConfig`에서 삭제했습니다.
- **프론트엔드 리팩토링 (페이지 분리)**:
  - **`result.html` 생성**: MUTI 타입, 설명, 추천 음악 목록, PDF 저장 버튼을 모두 포함하는 최종 결과 페이지를 새로 생성했습니다.
  - **`index.html` 수정**: 설문 제출(`submitSurvey`) 시, 백엔드로부터 받은 통합 결과 JSON을 브라우저의 `sessionStorage`에 임시로 저장한 후, `window.location.href`를 통해 `result.html`로 페이지를 완전히 이동시키도록 수정했습니다.
  - **`result.html` 기능 구현**: 페이지가 로드되면 `sessionStorage`에서 결과 데이터를 읽어와 화면에 동적으로 렌더링합니다. 데이터 사용 후에는 `sessionStorage.removeItem`으로 깨끗하게 정리합니다.

#### 문제 발생 및 해결:
- **컴파일 오류 (메서드 중복 및 생성자 부재)**: 리팩토링 과정에서 `MusicService`에 동일한 시그니처를 가진 메서드가 중복 정의되고, `SurveyResultWithMusicDto`의 생성자가 Lombok에 의해 제대로 생성되지 않는 문제가 발생했습니다. 메서드 이름을 변경하고 생성자를 직접 코드로 작성하여 해결했습니다.
- **수동 테스트 실패 (HTTP 401 Unauthorized)**: 설문 제출 후 `result.html` 페이지로 이동 시 401 에러가 발생했습니다. 원인은 새로 만든 `result.html`을 `SecurityConfig`의 `permitAll()` 목록에 추가하는 것을 누락하여, 인증된 사용자만 접근할 수 있는 상태였기 때문입니다. `SecurityConfig`에 `/result.html` 경로를 추가하여 문제를 해결했습니다.
- **PDF 다운로드 시 내용 잘림**: `result.html`에서 PDF 저장 시, 추천 음악 목록이 길어지면 한 페이지를 넘어가 내용이 잘리는 문제가 발견되었습니다. `downloadResultAsPDF` 함수 로직을 수정하여, `html2canvas`로 생성된 긴 이미지를 `jsPDF`의 페이지 높이에 맞춰 여러 조각으로 자른 후, 각 조각을 `addPage()`를 통해 새 페이지에 순서대로 추가하는 방식으로 해결했습니다.

#### 최종 확인:
- 백엔드 리팩토링 후 모든 통합 테스트가 성공적으로 통과하는 것을 확인했습니다.
- 새로운 사용자 흐름(설문 제출 → 결과 페이지 이동 → 결과 확인)에 대한 사용자 테스트를 기다리고 있습니다.

## 2025년 09월 27일 - 로그인 및 회원가입 UI/UX 개편

### 1. 로그인 중심의 사용자 흐름으로 개편 및 UI 개선
- 사용자가 처음 마주하는 페이지를 설문조사 페이지에서 로그인 전용 페이지(`index.html`)로 변경했습니다.
- 로그인 성공 시에만 설문조사 페이지(`survey.html`)로 이동하도록 흐름을 수정했습니다.
- 회원가입은 로그인 페이지 내의 레이어 팝업(Modal)을 통해 진행되도록 변경하여 사용자 경험을 개선했습니다.
- 카카오 로그인 버튼의 디자인을 개선하여 사용성을 높였습니다.

#### 개발 내용:
- **프론트엔드 리팩토링**:
  - **`index.html` 신규 생성 및 개선**: 로그인 폼, 회원가입 팝업, 스타일이 적용된 카카오 로그인 버튼을 포함하는 로그인 전용 페이지를 새로 작성하고 개선했습니다. 카카오 로그인 버튼은 기존 이미지를 대체하여 CSS로 직접 스타일링하고 SVG 아이콘을 내장했습니다.
  - **`survey.html`로 변경**: 기존 `index.html`은 `survey.html`로 이름을 변경하고, 로그인한 사용자만 접근할 수 있는 설문조사 전용 페이지로 역할을 변경했습니다.
  - **인증 로직 추가 (`survey.html`)**: 페이지 로드 시 `sessionStorage`에 저장된 JWT 토큰의 유무를 확인하여, 토큰이 없으면 로그인 페이지로 리다이렉트하는 가드(Guard) 로직을 추가했습니다.
- **백엔드 리팩토링 (`SecurityConfig.java`)**:
  - **접근 권한 재설정**: `/`, `/index.html`, `/survey.html` 등 프론트엔드 페이지 경로는 모두에게 허용(`permitAll`)하고, 페이지 내에서 호출되는 API(`/api/survey/**`)만 인증(`authenticated`)을 요구하도록 `SecurityConfig`를 업데이트했습니다.

#### 문제 발생 및 해결:
- **문제: 로그인 후 페이지 이동 시 401 에러 발생**: `SecurityConfig`에서 `/survey.html` 페이지 자체를 보호하여, 로그인 후 페이지를 로드하는 단계에서 인증 실패가 발생했습니다.
- **해결**: 페이지 자체(`survey.html`)는 `permitAll`로 열어주되, 페이지 내의 스크립트가 `sessionStorage`의 토큰 유무를 검사하여 인증되지 않은 사용자를 로그인 페이지로 리다이렉트하도록 프론트엔드와 백엔드 양단에서 인증 흐름을 명확히 분리하여 해결했습니다.
- **문제: 카카오 로그인 버튼 디자인 및 정렬 문제**: 초기 카카오 로그인 UI가 단순 이미지여서 미관상 좋지 않았고, 버튼으로 개선 후에는 다른 버튼과 정렬이 맞지 않았습니다.
- **해결**: CSS와 인라인 SVG를 사용하여 카카오 공식 디자인 가이드에 맞는 버튼을 제작했으며, `box-sizing: border-box` 속성을 적용하여 다른 버튼들과의 정렬을 일치시켰습니다.

#### 최종 확인:
- UI/UX 개편 후 모든 통합 테스트가 성공적으로 통과하는 것을 확인했습니다.
- 새로운 사용자 흐름(시작 → 로그인 → 설문조사)에 대한 사용자 테스트를 기다리고 있습니다.

#### 다음 개발 목표:
- **사용자 지시 대기**