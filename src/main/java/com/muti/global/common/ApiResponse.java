package com.muti.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 통일된 API 응답 포맷
 * @param <T> 응답 데이터 타입
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;
    private final String errorCode;

    /**
     * 성공 응답 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    /**
     * 성공 응답 (데이터 없음)
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null, null);
    }

    /**
     * 성공 응답 (메시지 포함)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    /**
     * 실패 응답
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(false, null, message, errorCode);
    }

    /**
     * 실패 응답 (데이터 포함)
     */
    public static <T> ApiResponse<T> error(String errorCode, String message, T data) {
        return new ApiResponse<>(false, data, message, errorCode);
    }
}