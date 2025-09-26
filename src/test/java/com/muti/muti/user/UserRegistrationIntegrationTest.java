package com.muti.muti.user;

import com.muti.muti.MutiApplication;                // 스프링 부트 메인 애플리케이션
import com.muti.muti.user.dto.UserRegisterRequest;   // 회원가입 요청 DTO
import com.muti.muti.user.repository.UserRepository; // User 엔티티용 JPA Repository
import org.junit.jupiter.api.DisplayName;            // 테스트 이름을 사람이 읽기 쉽게 표시
import org.junit.jupiter.api.Test;                   // JUnit 5 테스트 메서드 어노테이션
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// MockMvc 자동 설정
import org.springframework.boot.test.context.SpringBootTest;
// 스프링 부트 전체 컨텍스트 로딩해서 통합 테스트 실행
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
// test 프로필(H2 DB 등)을 사용하도록 지정
import org.springframework.test.web.servlet.MockMvc;  // 가짜 HTTP 요청/응답 테스트 도구
import org.springframework.transaction.annotation.Transactional;
// 테스트 후 DB 변경 자동 롤백
import com.fasterxml.jackson.databind.ObjectMapper;   // 객체 ↔ JSON 직렬화/역직렬화

import static org.assertj.core.api.Assertions.assertThat; // 가독성 좋은 단언문 제공
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // POST 요청 빌더
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // 상태코드 검증
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content; // 응답 본문 검증

// 스프링 부트 애플리케이션 전체를 로드해서 통합 테스트 실행
@SpringBootTest(classes = MutiApplication.class)
// MockMvc 자동 설정 → 실제 서버 안 띄우고 가짜 요청/응답 가능
@AutoConfigureMockMvc
// test 프로필(H2 DB)을 사용
@ActiveProfiles("test")
// 각 테스트 메서드 실행 후 DB 트랜잭션 자동 롤백
@Transactional
public class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;  // 가짜 HTTP 요청을 보낼 때 사용

    @Autowired
    private ObjectMapper objectMapper; // DTO ↔ JSON 변환용

    @Autowired
    private UserRepository userRepository; // 테스트 중 DB 상태 직접 확인용

    @Test
    @DisplayName("새로운 사용자가 성공적으로 등록되어야 한다")
    void shouldRegisterNewUserSuccessfully() throws Exception {
        userRepository.deleteAll(); // 테스트 시작 전에 DB 정리

        // 회원가입 요청 DTO 생성
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUserId("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // /api/users/register 로 POST 요청 보내기
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // DTO → JSON
                .andExpect(status().isCreated()); // 201 Created 기대

        // DB에 데이터가 실제로 저장됐는지 검증
        assertThat(userRepository.findByUserId("testuser")).isPresent();
        assertThat(userRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    @DisplayName("중복된 ID로 등록 시 CONFLICT 상태를 반환해야 한다")
    void shouldReturnConflictWhenUserIdAlreadyExists() throws Exception {
        userRepository.deleteAll(); // DB 초기화

        // 첫 번째 사용자 등록
        UserRegisterRequest firstRequest = new UserRegisterRequest();
        firstRequest.setUserId("existinguser");
        firstRequest.setEmail("existing@example.com");
        firstRequest.setPassword("password123");
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // 같은 ID로 다시 등록 시도
        UserRegisterRequest secondRequest = new UserRegisterRequest();
        secondRequest.setUserId("existinguser"); // ID 중복
        secondRequest.setEmail("another@example.com");
        secondRequest.setPassword("password456");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isConflict()) // 409 Conflict 기대
                .andExpect(content().string("이미 사용 중인 ID입니다.")); // 메시지 검증
    }

    @Test
    @DisplayName("중복된 이메일로 등록 시 CONFLICT 상태를 반환해야 한다")
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        userRepository.deleteAll(); // DB 초기화

        // 첫 번째 사용자 등록
        UserRegisterRequest firstRequest = new UserRegisterRequest();
        firstRequest.setUserId("userone");
        firstRequest.setEmail("duplicate@example.com");
        firstRequest.setPassword("password123");
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // 같은 email로 다시 등록 시도
        UserRegisterRequest secondRequest = new UserRegisterRequest();
        secondRequest.setUserId("usertwo");
        secondRequest.setEmail("duplicate@example.com"); // email 중복
        secondRequest.setPassword("password456");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isConflict()) // 409 Conflict 기대
                .andExpect(content().string("이미 사용 중인 이메일입니다.")); // 메시지 검증
    }

    @Test
    @DisplayName("ID에 한글이 포함된 경우 Bad Request를 반환해야 한다")
    void shouldReturnBadRequestWhenUserIdContainsKorean() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUserId("한글아이디");
        request.setEmail("valid@email.com");
        request.setPassword("validpassword");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ID가 너무 짧은 경우 Bad Request를 반환해야 한다")
    void shouldReturnBadRequestWhenUserIdIsTooShort() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUserId("ab"); // 3자 미만
        request.setEmail("valid@email.com");
        request.setPassword("validpassword");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 형식이 유효하지 않은 경우 Bad Request를 반환해야 한다")
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUserId("validuser");
        request.setEmail("invalid-email");
        request.setPassword("validpassword");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호가 너무 짧은 경우 Bad Request를 반환해야 한다")
    void shouldReturnBadRequestWhenPasswordIsTooShort() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUserId("validuser");
        request.setEmail("valid@email.com");
        request.setPassword("short"); // 6자 미만

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}