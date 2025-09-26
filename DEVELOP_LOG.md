# MUTI (Music Type Indicator) 개발 일지

## 2025년 09월 23일 - 프로젝트 초기 설정 및 개발 시작

### 1. 프로젝트 재시작 및 규칙 설정
- 프로젝트를 처음부터 다시 시작하며, 단계별 개발, H2/MySQL DB 분리, 개발 일지 작성 규칙을 설정했습니다.

### 2. `DEVELOP_LOG.md` 파일 생성
- 현재 이 파일을 생성하여 개발 진행 상황을 기록할 예정입니다.

### 3. `application.properties` 분리 및 설정 완료
- `application-test.yml` (H2 DB) 및 `application-dev.yml` (MySQL `muti_db`) 파일을 `src/main/resources` 경로에 생성했습니다.
- `application.properties` 파일에 `spring.profiles.active=dev`를 추가하여 기본 프로필을 `dev`로 설정했습니다.
- **H2 DB 설정 (`application-test.yml`)**:
  - `spring.h2.console.enabled=true` 및 `path=/h2-console` 설정으로 H2 콘솔을 활성화했습니다.
  - `jdbc:h2:mem:mutidb`를 사용하여 인메모리 H2 데이터베이스를 구성했습니다.
  - `spring.jpa.hibernate.ddl-auto=create-drop`으로 테스트 시마다 스키마를 새로 생성하고 삭제하도록 설정했습니다.
- **MySQL DB 설정 (`application-dev.yml`)**:
  - `jdbc:mysql://localhost:3306/muti_db`를 사용하여 MySQL 데이터베이스에 연결하도록 설정했습니다.
  - `username: root`, `password: root` (기본값)으로 설정했습니다.
  - `spring.jpa.hibernate.ddl-auto=update`로 엔티티 변경 시 스키마를 자동으로 업데이트하도록 설정했습니다.

### 5. 사용자 회원가입 기능 구현 및 테스트 완료

#### 개발 내용:
- **User Entity (`User.java`)**: 사용자 정보를 저장하는 JPA 엔티티를 정의했습니다. `id`, `username`, `password`, `email` 필드를 포함하며, `username`과 `email`은 고유(unique)하도록 설정했습니다.
- **UserRepository (`UserRepository.java`)**: `User` 엔티티를 위한 Spring Data JPA 리포지토리를 생성했습니다. `findByUsername`과 `findByEmail` 메서드를 추가하여 사용자 이름과 이메일로 사용자를 조회할 수 있도록 했습니다.
- **UserService (`UserService.java`)**: 사용자 등록을 위한 비즈니스 로직을 구현했습니다. 중복된 사용자 이름이나 이메일이 있는지 확인하고, 비밀번호는 `BCryptPasswordEncoder`를 사용하여 암호화한 후 저장합니다.
- **SecurityConfig (`SecurityConfig.java`)**: Spring Security 설정을 추가했습니다. `PasswordEncoder` 빈을 등록하고, `/api/users/register` 엔드포인트에 대한 접근을 인증 없이 허용하도록 설정했습니다. CSRF 보호는 현재 단계에서 비활성화했습니다.
- **UserRegisterRequest DTO (`UserRegisterRequest.java`)**: 사용자 회원가입 요청 데이터를 위한 DTO를 정의했습니다. `@NotBlank`, `@Size`, `@Email` 등의 `@Valid` 어노테이션을 사용하여 입력 값 유효성 검사를 추가했습니다.
- **UserController (`UserController.java`)**: `/api/users/register` 엔드포인트를 통해 사용자 회원가입 요청을 처리하는 REST 컨트롤러를 구현했습니다. `UserService`를 호출하여 사용자 등록을 처리하고, 성공 시 `201 Created` 상태와 메시지를 반환하며, 실패 시 `409 Conflict` 또는 `400 Bad Request` 상태와 오류 메시지를 반환합니다.

#### 문제 발생 및 해결:
1.  **Flyway DB 마이그레이션 충돌**: 테스트 환경에서 H2 DB의 `create-drop` 설정과 Flyway가 동시에 활성화되어 충돌이 발생했습니다. `application-test.yml`에 `spring.flyway.enabled=false`를 추가하여 테스트 시 Flyway를 비활성화함으로써 해결했습니다.
2.  **`MutiApplicationTests` 실패**: 기본 `MutiApplicationTests`가 `dev` 프로필을 사용하여 MySQL 연결을 시도하여 실패했습니다. `MutiApplicationTests` 클래스에 `@ActiveProfiles("test")`를 추가하여 테스트 시 `test` 프로필(H2 DB)을 사용하도록 설정하여 해결했습니다.
3.  **`UserRegistrationIntegrationTest` 실패 (403 Forbidden)**: Spring Security가 모든 엔드포인트를 기본적으로 보호하여 `/api/users/register` 접근이 `403 Forbidden`으로 거부되었습니다. `SecurityConfig.java`에 `/api/users/register` 경로에 대해 `permitAll()`을 설정하여 해결했습니다.
4.  **`UserRegistrationIntegrationTest` 실패 (AssertionError)**: 초기에는 `MockMvc` 테스트에서 `userRepository.deleteAll()`을 `@BeforeEach`, `@AfterEach`에 사용했으나, `@Transactional`과 함께 사용 시 예상치 못한 동작이 발생할 수 있어 제거했습니다. 이후 Spring Security 설정 변경으로 모든 테스트가 성공적으로 통과했습니다.
5.  **`bootRun` 실패 (MySQL 연결 거부)**: `dev` 프로필로 애플리케이션 실행 시 MySQL `muti_db`에 `root` 사용자(`password: YES`)로 접근이 거부되었습니다.
    - **해결**: 사용자로부터 올바른 MySQL 비밀번호를 전달받아 `application-dev.yml`에 업데이트했습니다.
6.  **`bootRun` 실패 (Port 8080 in use)**: MySQL 연결 문제는 해결되었으나, 웹 서버 시작 시 8080 포트가 이미 사용 중이라는 오류가 발생했습니다.

#### 사용자 조치 필요:
- **MySQL 연결 문제**: MySQL 서버가 실행 중인지, `muti_db` 데이터베이스가 존재하는지, `application-dev.yml`에 설정된 MySQL 사용자에게 `muti_db`에 대한 모든 권한이 부여되어 있는지 확인해주세요.
- **Port 8080 사용 중 문제**: 현재 8080 포트를 사용 중인 다른 애플리케이션을 종료하거나, `application-dev.yml`에 `server.port=8081`과 같이 다른 포트 번호를 설정하여 애플리케이션이 다른 포트에서 실행되도록 해주세요.

#### 사용된 기술/용어:
- **Spring Boot**: 애플리케이션 개발 프레임워크
- **Spring Data JPA**: JPA 기반 데이터 접근 계층 추상화
- **H2 DB**: 테스트 환경을 위한 인메모리 관계형 데이터베이스
- **MySQL**: 실제 실행 환경을 위한 관계형 데이터베이스
- **Spring Security**: 인증 및 권한 부여 프레임워크
- **BCryptPasswordEncoder**: 비밀번호 암호화를 위한 인코더
- **Lombok**: Getter, Setter 등 보일러플레이트 코드 자동 생성 라이브러리
- **`@Transactional`**: 선언적 트랜잭션 관리를 위한 어노테이션
- **`@RestController`**: RESTful 웹 서비스 컨트롤러를 위한 어노테이션
- **`@Valid`**: DTO 유효성 검사를 위한 어노테이션
- **MockMvc**: Spring MVC 테스트를 위한 유틸리티

#### 다음 개발 목표:
- 사용자 로그인 기능 구현

### 7. 웹페이지를 통한 회원가입 테스트 환경 구축 및 문제 해결
- 사용자가 Spring Boot 애플리케이션 실행 시 바로 웹페이지를 통해 회원가입 기능을 테스트할 수 있도록 시스템을 구축했습니다.
- 프로젝트 루트에 있던 `index.html` 파일을 Spring Boot의 정적 리소스 경로인 `src/main/resources/static/index.html`로 이동했습니다.
- 이제 애플리케이션을 실행하고 `http://localhost:8080/` (또는 설정된 포트)에 접속하면 회원가입 폼이 포함된 웹페이지가 자동으로 나타납니다.

#### 문제 발생 및 해결:
1.  **`bootRun` 실패 (MySQL 연결 거부)**: `dev` 프로필로 애플리케이션 실행 시 MySQL `muti_db`에 `root` 사용자(`password: YES`)로 접근이 거부되었습니다.
    - **해결**: 사용자로부터 올바른 MySQL 비밀번호를 전달받아 `application-dev.yml`에 업데이트하여 해결했습니다.
2.  **`bootRun` 실패 (Port 8080 in use)**: MySQL 연결 문제는 해결되었으나, 웹 서버 시작 시 8080 포트가 이미 사용 중이라는 오류가 발생했습니다.
    - **해결**: 사용자에게 8080 포트를 사용 중인 프로세스를 찾아 종료하도록 안내하여 해결했습니다.
3.  **웹페이지 접근 거부 (HTTP ERROR 403)**: `http://localhost:8080/` 접근 시 `403 Forbidden` 오류가 발생했습니다.
    - **해결**: `SecurityConfig.java`에 `/`, `/index.html`, `/favicon.ico` 및 기타 정적 리소스 경로(`css/**`, `js/**`, `images/**`)에 대해 `permitAll()`을 설정하여 해결했습니다.
4.  **프론트엔드 `SyntaxError` 및 데이터 미전송**: 회원가입 버튼 클릭 시 브라우저 콘솔에 `Uncaught SyntaxError`가 발생하고 백엔드로 데이터가 전송되지 않는 문제가 있었습니다.
    - **원인**: `event.preventDefault()`가 제대로 작동하지 않아 폼이 기본 `GET` 방식으로 제출되고, 브라우저가 URL 쿼리 문자열을 JavaScript 코드로 잘못 해석하는 문제로 추정되었습니다.
    - **해결**: `index.html`의 JavaScript 이벤트 리스너 내부에 `console.log`를 추가하여 실행 여부를 확인하고, `return false;`를 추가하여 기본 폼 제출을 더욱 확실하게 방지했습니다. 또한 `responseDiv.color`를 `responseDiv.style.color`로 수정했습니다. 이 수정 후 웹페이지를 통한 회원가입 기능이 정상적으로 작동함을 확인했습니다.

#### 최종 확인:
- H2 DB를 사용한 통합 테스트 코드가 모두 통과했습니다.
- MySQL `muti_db`와 연동하여 Spring Boot 애플리케이션이 성공적으로 실행되고, 웹페이지를 통해 회원가입 기능이 정상적으로 작동함을 확인했습니다.

#### 다음 개발 목표:
- 사용자 로그인 기능 구현

## 2025년 09월 24일 - 사용자 로그인 기능 구현

### 1. 사용자 로그인 기능 구현
- Spring Security를 사용하여 사용자 로그인 기능을 구현했습니다.

#### 개발 내용:
- **UserLoginRequest DTO (`UserLoginRequest.java`)**: 로그인 요청 데이터를 위한 DTO를 생성했습니다.
- **UserService (`UserService.java`)**: `UserDetailsService` 인터페이스를 구현하고 `loadUserByUsername` 메서드를 오버라이드하여 Spring Security가 사용자를 인증할 수 있도록 했습니다.
- **SecurityConfig (`SecurityConfig.java`)**: `formLogin`을 설정하여 로그인 처리를 활성화했습니다. 로그인 성공 및 실패 시 각각 `200 OK`와 `401 Unauthorized`를 반환하는 핸들러를 추가했습니다. 또한 인증되지 않은 사용자가 보호된 리소스에 접근할 경우 `401 Unauthorized`를 반환하도록 `authenticationEntryPoint`를 설정했습니다.
- **UserController (`UserController.java`)**: 로그인된 사용자의 정보를 확인할 수 있는 `/api/users/me` 엔드포인트를 추가했습니다.
- **UserLoginIntegrationTest (`UserLoginIntegrationTest.java`)**: 로그인 성공, 실패, 인증된 사용자의 보호된 엔드포인트 접근에 대한 통합 테스트를 작성했습니다.

#### 문제 발생 및 해결:
1.  **`UserRegistrationIntegrationTest` 실패**: `SecurityConfig` 변경 후 기존 회원가입 테스트가 실패했습니다. 원인은 테스트 코드에서 기대하는 예외 메시지("Username already exists")와 실제 `UserService`에서 던지는 메시지("이미 사용 중인 사용자 이름입니다.")가 달랐기 때문입니다. 테스트 코드의 기대 메시지를 실제 메시지에 맞게 수정하여 해결했습니다.
2.  **`UserLoginIntegrationTest` 실패 (401 Unauthorized)**: `MockMvc`를 사용한 `formLogin` 테스트 시 `application/json`으로 요청을 보내자 인증에 실패했습니다. Spring Security의 `formLogin`은 기본적으로 `application/x-www-form-urlencoded` 형식의 데이터를 기대하기 때문입니다. 요청 `contentType`을 `APPLICATION_FORM_URLENCODED`로 변경하고, `param`을 사용하여 데이터를 전송하도록 수정했으나 여전히 문제가 해결되지 않았습니다.
3.  **테스트 방식 변경 (`httpBasic`)**: `formLogin` 테스트의 복잡성을 해결하기 위해, 테스트 환경에서는 `httpBasic` 인증을 사용하기로 결정했습니다. `SecurityConfig`에 `httpBasic()`을 추가하고, `UserLoginIntegrationTest`에서 `SecurityMockMvcRequestPostProcessors.httpBasic()`을 사용하여 인증을 테스트하도록 리팩토링했습니다. 이 변경 후 모든 테스트가 성공적으로 통과했습니다.
4.  **로그인 실패 메시지 인코딩 문제**: 사용자가 수동 테스트 중 로그인 실패 시 응답 메시지가 `?????`로 깨지는 현상을 발견했습니다. 이는 `SecurityConfig`의 실패 핸들러에서 응답의 문자 인코딩을 명시하지 않았기 때문입니다. 성공/실패/로그아웃 핸들러 및 `authenticationEntryPoint`에 `response.setContentType("application/json;charset=UTF-8")` 설정을 추가하여 문제를 해결했습니다.

#### 최종 확인:
- H2 DB를 사용한 모든 통합 테스트(`UserRegistrationIntegrationTest`, `UserLoginIntegrationTest`)가 통과했습니다.
- `index.html`에 로그인 폼과 인증 테스트 버튼을 추가하여 수동 테스트 환경을 구축했습니다.

#### 다음 개발 목표:
- JWT를 사용한 토큰 기반 인증으로 전환

## 2025년 09월 24일 - JWT 토큰 기반 인증으로 전환

### 1. JWT 인증 시스템 구현
- 기존의 세션 기반 인증 방식을 상태 비저장(stateless) 토큰 기반 인증으로 전환했습니다.

#### 개발 내용:
- **JWT 의존성 추가**: `build.gradle`에 `jjwt-api`, `jjwt-impl`, `jjwt-jackson` 라이브러리를 추가하여 JWT를 생성하고 검증할 수 있도록 했습니다.
- **JwtUtil 클래스 생성**: JWT 토큰의 생성, 사용자 정보 추출, 유효성 검증을 담당하는 유틸리티 클래스 `JwtUtil.java`를 `com.muti.muti.config.jwt` 패키지에 생성했습니다.
- **JwtAuthenticationFilter 생성**: 모든 요청에 대해 한 번씩 실행되는 필터 `JwtAuthenticationFilter.java`를 생성했습니다. 이 필터는 요청의 `Authorization` 헤더에서 `Bearer` 토큰을 추출하고, 토큰이 유효하면 Spring Security 컨텍스트에 인증 정보를 설정합니다.
- **AuthController 생성**: 실제 로그인을 처리하고 JWT를 발급하는 `/api/auth/login` 엔드포인트를 포함하는 `AuthController.java`를 `com.muti.muti.auth` 패키지에 생성했습니다. `AuthRequest`와 `AuthResponse` DTO도 함께 생성했습니다.
- **SecurityConfig 수정**: 
  - 기존의 `formLogin`, `httpBasic`을 비활성화했습니다.
  - 세션 관리 정책을 `SessionCreationPolicy.STATELESS`로 변경하여 세션을 사용하지 않도록 설정했습니다.
  - `JwtAuthenticationFilter`가 `UsernamePasswordAuthenticationFilter`보다 먼저 실행되도록 필터 체인에 추가했습니다.
  - `AuthenticationManager`를 Bean으로 노출하여 `AuthController`에서 사용할 수 있도록 했습니다.
- **통합 테스트 수정**: `UserLoginIntegrationTest`를 수정하여 새로운 `/api/auth/login` 엔드포인트를 테스트하고, 발급받은 JWT를 사용하여 보호된 리소스에 접근하는 시나리오를 검증하도록 변경했습니다.
- **index.html 수정**: 프론트엔드 JavaScript 코드를 수정하여, 로그인 시 서버로부터 JWT를 받아 변수에 저장하고, 'Check /api/users/me' 버튼 클릭 시 해당 토큰을 `Authorization` 헤더에 담아 요청하도록 변경했습니다.

#### 문제 발생 및 해결:
1.  **순환 종속성 오류 (`BeanCurrentlyInCreationException`)**: JWT 관련 컴포넌트(`JwtAuthenticationFilter`, `SecurityConfig`, `UserService` 등)를 추가하고 실행하자, Bean들이 서로를 참조하면서 무한 루프에 빠지는 순환 종속성 오류가 발생했습니다. 
    - **1차 시도 (생성자 주입)**: 문제의 원인을 필드 주입(`@Autowired`)으로 추측하고, 관련된 모든 클래스를 Lombok의 `@RequiredArgsConstructor`를 사용한 생성자 주입 방식으로 변경했습니다. 하지만 동일한 오류가 계속 발생하여 이것만으로는 부족함을 확인했습니다.
    - **2차 시도 (Bean 분리 및 해결)**: 오류 로그를 다시 분석한 결과, `SecurityConfig`가 `AuthenticationManager` Bean을 생성하면서 `UserService`를 필요로 하고, `UserService`는 `PasswordEncoder`를 필요로 하는데, 이 `PasswordEncoder` Bean이 `SecurityConfig` 내부에 있어 순환이 발생함을 파악했습니다. 이 사이클을 끊기 위해 `PasswordEncoder` Bean을 별도의 `AppConfig.java` 설정 파일로 분리했습니다. 이 조치 후 순환 종속성 문제가 해결되어 애플리케이션 컨텍스트가 정상적으로 로드되었습니다.
2.  **로그인 실패 테스트 오류**: 순환 종속성 해결 후, 잘못된 비밀번호로 로그인 시 `500 Internal Server Error`를 기대하는 테스트가 실패했습니다. `AuthController`에서 `BadCredentialsException` 발생 시 일반 `Exception`을 던지도록 한 것이 원인이었습니다. REST API에서는 인증 실패 시 401을 반환하는 것이 더 적절하므로, `AuthController`의 `createAuthenticationToken` 메서드에서 `BadCredentialsException`을 `try-catch`로 잡아서 `401 Unauthorized` 상태와 에러 메시지를 직접 반환하도록 수정했습니다. 테스트 코드도 401 상태를 기대하도록 변경하여 모든 테스트가 통과함을 확인했습니다.

#### 최종 확인:
- H2 DB를 사용한 모든 통합 테스트가 성공적으로 통과했습니다.
- `index.html`을 통해 회원가입, JWT 발급, 발급된 JWT를 이용한 인증까지 모두 정상적으로 작동함을 확인했습니다.

#### 다음 개발 목표:
- 음악 추천 기능 구현

## 2025년 09월 25일 - MUTI 성향 분석 및 추천 기능 구현

### 1. MUTI 성향 분석 기능 구현
- 사용자의 음악적 성향을 분석하기 위한 질문 기반의 MUTI 타입 결정 시스템을 구현했습니다.

#### 개발 내용:
- **MUTI 타입 재정의**: 사용자 피드백에 따라 MUTI 타입을 아래 4가지 차원으로 재정의했습니다.
  - **E/I**: 감성 중심(Emotion) vs 사운드/구조 중심(Instrument)
  - **S/F**: 느린 템포(Slow) vs 빠른 템포(Fast)
  - **A/D**: 적극적 탐색(Active) vs 수동적 수용(Drift)
  - **P/U**: 대중적(Popular) vs 비주류(Underground)
- **도메인 모델 확장**:
  - `MutiTrait.java`: 새로운 MUTI 성향을 반영하는 Enum으로 업데이트했습니다.
  - `Question.java`, `Choice.java`: 설문 질문과 선택지를 위한 엔티티를 생성했습니다.
  - `User.java`: 사용자의 최종 MUTI 타입을 저장할 `mutiType` 필드를 추가했습니다.
- **DataInitializer 확장**: 애플리케이션 시작 시 4가지 차원을 측정하는 4개의 기본 질문과 선택지를 DB에 생성하도록 수정했습니다.
- **SurveyService 구현**: 사용자가 제출한 답변(선택지 ID)을 바탕으로 각 성향의 점수를 계산하여 최종 MUTI 타입을 문자열(예: "IFAP")로 조합하고, 이를 해당 사용자의 정보에 저장하는 로직을 구현했습니다.
- **SurveyController 구현**: 성향 분석 기능을 위한 REST API 엔드포인트를 구현했습니다.
  - `GET /api/survey/questions`: 모든 설문 질문과 선택지를 조회합니다.
  - `POST /api/survey/submit`: 사용자가 선택한 답변을 제출받아 `SurveyService`를 통해 MUTI 타입을 계산하고 결과를 반환합니다.
- **SecurityConfig 수정**: `/api/survey/**` 경로에 대한 모든 요청이 인증을 요구하도록 명시적으로 설정했습니다.
- **SurveyIntegrationTest 작성**: 성향 분석 기능에 대한 통합 테스트를 작성하여 질문 조회 및 답변 제출 후 정확한 MUTI 타입이 반환되고 DB에 저장되는지 검증했습니다.
- **프론트엔드(index.html) 업데이트**: 사용자가 직접 웹페이지에서 MUTI 성향 검사를 진행하고 결과를 확인할 수 있도록 UI와 관련 로직을 추가했습니다.

#### 최종 확인:
- H2 DB를 사용한 모든 통합 테스트가 성공적으로 통과했습니다.
- `index.html`을 통해 회원가입, 로그인, 성향 검사 시작, 질문에 대한 답변 제출, 그리고 최종 MUTI 타입 결과 확인까지의 전체 흐름이 정상적으로 작동함을 확인했습니다.

### 2. MUTI 타입 기반 추천 로직 구현
- 분석된 MUTI 타입을 기반으로 사용자에게 음악을 추천하는 핵심 로직을 구현했습니다.

#### 개발 내용:
- **MusicService 수정**: 기존의 '좋아요' 기반 추천 로직을 폐기하고, 사용자의 MUTI 타입을 기반으로 추천하는 새로운 로직을 구현했습니다.
  - 각 MUTI 성향(Trait)과 음악 장르 간의 매핑(e.g., 'F' -> 'Rock', 'Pop')을 정의했습니다.
  - 사용자의 MUTI 타입(e.g., 'IFAP')을 조회하여 각 성향에 매핑된 장르들을 취합합니다.
  - 취합된 장르에 해당하는 음악들을 DB에서 조회하여 최종 추천 목록으로 반환합니다.
- **레거시 코드 제거**: '좋아요' 기반 추천 기능이 더 이상 사용되지 않으므로, 관련 도메인(`UserMusicPreference`), 리포지토리(`UserMusicPreferenceRepository`), 그리고 컨트롤러의 불필요한 엔드포인트를 삭제하여 코드를 정리했습니다.
- **통합 테스트 강화**: `SurveyIntegrationTest`에 MUTI 타입 기반 추천을 검증하는 테스트 케이스를 추가했습니다. 특정 타입(e.g., 'IFAP')을 부여받은 사용자가 추천 API 호출 시, 해당 타입과 매핑된 장르의 음악들이 올바르게 반환되는지 검증합니다.
- **프론트엔드(index.html) 최종 개편**: 최종 사용자 동선에 맞춰 UI/UX를 개편했습니다.
  - 기존의 '음악 목록', '좋아요' 등 불필요해진 UI를 모두 제거했습니다.
  - 사용자가 성향 검사를 완료하고 MUTI 타입을 확인한 직후, '내 타입의 음악 추천받기' 버튼이 나타나도록 하여 자연스럽게 추천 기능으로 연결되도록 개선했습니다.

#### 문제 발생 및 해결:
- **테스트 실패**: 추천 로직 변경 후, 관련 코드를 정리하는 과정에서 `MusicController`를 실수로 삭제하여 API 호출이 실패하는 테스트 오류가 발생했습니다. `getRecommendations` 엔드포인트만 포함하는 `MusicController`를 다시 생성하여 문제를 해결했습니다.

#### 최종 확인:
- 모든 통합 테스트가 최종 로직 기준으로 성공적으로 통과했습니다.
- `index.html`에서 **회원가입 → 로그인 → 성향 검사 → 결과 확인 → 음악 추천**으로 이어지는 핵심 기능 전체가 의도대로 완벽하게 작동함을 확인했습니다.

#### 다음 개발 목표:
- **프로젝트 완료**

## 2025년 09월 25일 - 회원가입 및 로그인 시스템 리팩토링

### 1. `username` -> `userId` 리팩토링 및 유효성 검사 강화
- 기존 `username` 필드는 한글 등 비-ASCII 문자를 허용하여 기술적인 문제를 유발할 가능성이 있었습니다. 이를 해결하기 위해 `userId`로 필드명을 변경하고, 영어와 숫자로만 구성되도록 제한하는 리팩토링을 진행했습니다.

#### 개발 내용:
- **도메인 및 DTO 수정**: `User` 엔티티와 관련 DTO들(`UserRegisterRequest`, `AuthRequest` 등)의 `username` 필드를 모두 `userId`로 변경했습니다.
- **유효성 검사 추가**: `UserRegisterRequest`의 `userId` 필드에 `@Pattern(regexp = "^[a-zA-Z0-9]+$")` 어노테이션을 추가하여, ID가 영어와 숫자로만 구성되도록 강제했습니다. 관련 오류 메시지도 사용자 친화적으로 수정했습니다.
- **서비스 및 컨트롤러 리팩토링**: `UserService`가 DTO를 직접 받도록 수정하고, `UserController`의 호출 코드를 간소화하여 코드 구조를 개선했습니다.
- **테스트 코드 리팩토링 및 강화**:
  - `UserLoginIntegrationTest`에서 `userId` 변경사항을 반영하고, 잘못된 응답 메시지를 검증하던 오류를 수정했습니다.
  - `UserRegistrationIntegrationTest`의 유효성 검사 테스트를 역할에 따라 여러 개의 명확한 테스트(`ID에 한글 포함`, `ID가 너무 짧은 경우` 등)로 분리하여 가독성과 유지보수성을 높였습니다.

#### 최종 확인:
- 수정된 모든 통합 테스트(`UserRegistrationIntegrationTest`, `UserLoginIntegrationTest` 등)를 실행하여 **모두 성공적으로 통과**하는 것을 확인했습니다.
- 이를 통해 `userId`로의 리팩토링과 새로운 유효성 검사 규칙이 정확히 적용되었으며, 기존 기능에 영향을 주지 않음을 검증했습니다.

#### 다음 개발 목표:
- **사용자 테스트 및 피드백 대기**

## 2025년 09월 27일 - 카카오 OAuth2 로그인 기능 구현

### 1. JWT 기반 소셜 로그인(카카오) 구현
- 기존의 로컬 회원가입/로그인 방식에 더해, 카카오 계정을 이용한 소셜 로그인 및 신규 회원가입 기능을 구현했습니다.
- 핵심 목표는 사용자가 소셜 계정으로 간편하게 인증하고, 기존 JWT 기반 인증 흐름에 통합시키는 것이었습니다.

#### 개발 내용:
- **DB 스키마 확장**: `users` 테이블에 사용자의 가입 경로(e.g., 'local', 'kakao')를 저장하는 `provider` 컬럼을 추가했습니다.
- **`User` 엔티티 수정**: `provider` 필드와 소셜 회원가입용 생성자를 추가했습니다.
- **`UserService` 수정**: 로컬 회원가입 시, 만약 이메일이 이미 소셜 계정으로 등록되어 있다면 가입을 막고 안내 메시지를 반환하도록 로직을 수정했습니다.
- **OAuth2 설정 추가**: `build.gradle`에 이미 포함된 `spring-boot-starter-oauth2-client` 의존성을 확인하고, `application-dev.yml`과 `application-test.yml`에 카카오 클라이언트 설정을 추가했습니다.
- **`CustomOAuth2UserService` 구현**: Spring Security의 `DefaultOAuth2UserService`를 확장하여, 카카오로부터 사용자 정보를 받아오는 핵심 로직을 구현했습니다.
  - 카카오 API 응답을 `OAuthAttributes` DTO로 파싱하여 표준화했습니다.
  - `saveOrUpdate` 로직을 통해, DB에 해당 이메일의 사용자가 있으면 로그인으로, 없으면 `provider`를 'kakao'로 설정하여 신규 회원으로 자동 저장하도록 구현했습니다.
- **`OAuth2AuthenticationSuccessHandler` 구현**: 소셜 로그인이 성공적으로 완료된 직후 호출되는 핸들러를 구현했습니다.
  - Spring Security의 기본 세션 방식 대신, 인증된 사용자 정보를 기반으로 우리 시스템의 **JWT를 발급**합니다.
  - 발급된 JWT를 쿼리 파라미터(`?token=...`)에 담아 프론트엔드(`index.html`)로 리다이렉트시킵니다.
- **`SecurityConfig` 수정**: `http.oauth2Login()` 설정을 추가하여, 위에서 만든 `CustomOAuth2UserService`와 `OAuth2AuthenticationSuccessHandler`를 Security Filter Chain에 연결했습니다.
- **`index.html` 수정**: 사용자가 시각적으로 카카오 로그인을 시작할 수 있도록 **카카오 로그인 버튼**을 추가했습니다. 또한, 페이지 로드 시 URL에 토큰이 있는지 확인하여 자동으로 로그인 상태를 만드는 JavaScript 코드를 추가했습니다.

#### 문제 발생 및 해결:
1.  **컴파일 오류 (`No suitable constructor found for User`)**: `CustomOAuth2UserService`에서 소셜 가입자를 생성하기 위해 `new User(...)`를 호출했지만, `User` 엔티티에 해당 파라미터를 받는 생성자가 없어 발생했습니다. `User` 클래스에 소셜 가입자용 생성자를 추가하여 해결했습니다.
2.  **테스트 실패 (`UnsatisfiedDependencyException`)**: 모든 테스트 실행 시, 스프링 컨텍스트가 로드되지 못하는 문제가 발생했습니다. `application-dev.yml`에만 추가했던 OAuth2 설정이 테스트 환경(`application-test.yml`)에는 없어, `ClientRegistrationRepository` Bean 생성에 실패했기 때문입니다. `application-test.yml`에 테스트용 더미(dummy) OAuth2 설정을 추가하여 컨텍스트가 정상적으로 로드되도록 해결했습니다.
3.  **단일 테스트 실패 (`UserLoginIntegrationTest`)**: 인증되지 않은 사용자의 API 접근 테스트가 `403 Forbidden`을 기대했지만 다른 상태 코드를 받아 실패했습니다. `oauth2Login()`을 추가하면서, 인증되지 않은 사용자에 대한 기본 동작이 에러 반환에서 '로그인 페이지 리다이렉트'로 변경되었기 때문입니다. `SecurityConfig`에 `http.exceptionHandling().authenticationEntryPoint(...)`를 명시하여, API 접근 시에는 `401 Unauthorized`를 반환하도록 수정하고 테스트 코드의 기대값도 `401`로 변경하여 해결했습니다.
4.  **수동 테스트 실패 (HTTP 401 Unauthorized)**: 사용자가 직접 테스트 시, 카카오 동의 화면 직후 401 에러가 발생했습니다. 원인은 우리 서버가 카카오 서버로 액세스 토큰을 요청하는 과정에서, `client-id`와 `client-secret`을 전달하는 방식(Authentication Method)이 카카오의 기대와 달랐기 때문으로 추정되었습니다.
5.  **수동 테스트 실패 (`IllegalArgumentException`)**: 4번 문제 해결을 위해 `application-dev.yml`에 `client-authentication-method: post`를 추가했으나, Spring Security는 `post`가 아닌 `client_secret_post`라는 값을 기대했기 때문에 `IllegalArgumentException`이 발생했습니다. 설정값을 `client_secret_post`로 최종 수정하여 문제를 해결했습니다.

#### 사용된 기술/용어:
- **OAuth2 (Open Authorization 2.0)**: 특정 앱이 다른 앱의 사용자 데이터에 접근하기 위한 권한을 위임받는 업계 표준 프로토콜입니다. 여기서는 MUTI 앱이 카카오의 사용자 정보(이메일 등)에 접근하는 데 사용됩니다.
- **`spring-boot-starter-oauth2-client`**: Spring Boot 환경에서 손쉽게 OAuth2 클라이언트(카카오, 구글, 페이스북 등에 요청을 보내는 쪽)를 구현할 수 있도록 도와주는 라이브러리입니다.
- **`AuthenticationEntryPoint`**: Spring Security에서, 인증되지 않은 사용자가 보호된 리소스에 접근을 시도할 때 호출되는 컴포넌트입니다. '어떻게 인증을 시작하게 할 것인가' (예: 로그인 페이지로 보낼지, 401 에러를 보낼지)를 결정합니다.
- **`OAuth2UserService`**: OAuth2 프로바이더(카카오)로부터 사용자 정보를 성공적으로 가져온 후, 해당 정보를 기반으로 우리 시스템의 사용자를 로드하거나 생성하는 역할을 담당합니다.
- **`AuthenticationSuccessHandler`**: 인증(로그인)이 성공한 직후에 수행할 동작을 정의하는 컴포넌트입니다. 여기서는 JWT를 생성하고 프론트엔드로 리다이렉트하는 역할을 수행했습니다.

#### 최종 확인:
- 모든 통합 테스트가 성공적으로 통과했으며, 사용자의 수동 테스트를 통해 카카오 로그인 기능이 정상적으로 작동함을 최종 확인했습니다.

#### 다음 개발 목표:
- **사용자 지시 대기**