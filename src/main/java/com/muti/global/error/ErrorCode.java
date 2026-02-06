package com.muti.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "요청한 리소스를 찾을 수 없습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "타입이 올바르지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근 권한이 없습니다."),

    // Survey
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "설문을 찾을 수 없습니다."),
    SURVEY_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "S002", "이미 완료된 설문입니다."),
    INVALID_SURVEY_RESPONSE(HttpStatus.BAD_REQUEST, "S003", "설문 응답이 올바르지 않습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."),

    // Recommendation
    RECOMMENDATION_NOT_AVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "R001", "추천 서비스를 사용할 수 없습니다."),

    // Share
    SHARE_LINK_NOT_FOUND(HttpStatus.NOT_FOUND, "SH001", "공유 링크를 찾을 수 없습니다."),
    SHARE_LINK_EXPIRED(HttpStatus.GONE, "SH002", "만료된 공유 링크입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}