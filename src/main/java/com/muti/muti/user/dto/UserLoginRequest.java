package com.muti.muti.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {

    @NotBlank(message = "사용자 ID는 비워둘 수 없습니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;
}
