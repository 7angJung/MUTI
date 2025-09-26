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

    @NotBlank(message = "ID는 필수 입력 항목입니다.")
    @Size(min = 3, max = 20, message = "ID는 3자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "ID는 영문 또는 숫자만 가능합니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 6, message = "비밀번호는 6자 이상으로 입력해주세요.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;
}

