package com.muti.muti;

import org.junit.jupiter.api.Test;                     // JUnit 5 테스트 어노테이션
import org.springframework.boot.test.context.SpringBootTest;
// 스프링 부트 애플리케이션 전체를 로드해서 테스트 실행
import org.springframework.test.context.ActiveProfiles;
// 특정 프로필(test)을 활성화해서 실행

// @SpringBootTest: 실제 애플리케이션처럼 스프링 컨텍스트를 모두 로드하여 테스트
@SpringBootTest
// @ActiveProfiles("test"): 테스트 실행 시 application-test.yml 설정(H2 DB 등)을 사용
@ActiveProfiles("test")
class MutiApplicationTests {

	// 기본 생성된 테스트 메서드
	// contextLoads(): 스프링 애플리케이션 컨텍스트가 문제없이 로딩되는지 확인
	@Test
	void contextLoads() {
		// 아무 동작도 없음
		// 단, 스프링 컨텍스트가 정상적으로 뜨지 않으면 이 테스트가 실패함
	}
}