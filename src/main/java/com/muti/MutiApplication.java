package com.muti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing  // JPA Auditing 활성화: @CreatedDate, @LastModifiedDate 자동 관리
@SpringBootApplication
public class MutiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MutiApplication.class, args);
	}

}
