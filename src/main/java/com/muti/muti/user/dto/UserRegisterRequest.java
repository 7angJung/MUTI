package com.muti.muti.user.dto;

import jakarta.validation.constraints.Email;      // 이메일 형식 검증 어노테이션
import jakarta.validation.constraints.NotBlank;  // null/빈 문자열/공백 불가 검증 어노테이션
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;      // 문자열 길이 검증 어노테이션
import lombok.Getter;                            // Lombok: 모든 필드에 대한 getter 메서드 자동 생성
import lombok.Setter;                            // Lombok: 모든 필드에 대한 setter 메서드 자동 생성

// 회원가입 요청 데이터를 담는 DTO 클래스
@Getter
@Setter
public class UserRegisterRequest {

    // username: 공백 불가, 최소 3자 ~ 최대 20자
    @NotBlank(message = "사용자 ID는 필수입니다.")
    @Size(min = 3, max = 20, message = "사용자 ID는 3자 이상 20자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "사용자 ID는 영어와 숫자로만 구성되어야 합니다.")
    private String userId;

    // email: 공백 불가, 이메일 형식인지 검증
    @NotBlank(message = "이메일은 비워둘 수 없습니다.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    // password: 공백 불가, 최소 8자 이상
    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상 입력해야 합니다.")
    private String password;
}

