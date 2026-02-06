package com.muti.domain.survey.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * MUTI 16가지 타입
 * 4개 축의 조합으로 만들어지는 음악 취향 유형
 */
@Getter
@RequiredArgsConstructor
public enum MutiType {

    // E-S 조합
    ESAP("감성적 잔잔한 어쿠스틱 대중", "감정선 중심의 잔잔한 어쿠스틱 대중 음악을 선호"),
    ESAU("감성적 잔잔한 어쿠스틱 실험", "감정선 중심의 잔잔한 어쿠스틱 실험 음악을 선호"),
    ESDP("감성적 잔잔한 디지털 대중", "감정선 중심의 잔잔한 디지털 대중 음악을 선호"),
    ESDU("감성적 잔잔한 디지털 실험", "감정선 중심의 잔잔한 디지털 실험 음악을 선호"),

    // E-F 조합
    EFAP("감성적 에너지 어쿠스틱 대중", "감정선 중심의 빠른 어쿠스틱 대중 음악을 선호"),
    EFAU("감성적 에너지 어쿠스틱 실험", "감정선 중심의 빠른 어쿠스틱 실험 음악을 선호"),
    EFDP("감성적 에너지 디지털 대중", "감정선 중심의 빠른 디지털 대중 음악을 선호"),
    EFDU("감성적 에너지 디지털 실험", "감정선 중심의 빠른 디지털 실험 음악을 선호"),

    // I-S 조합
    ISAP("연주 중심 잔잔한 어쿠스틱 대중", "연주 중심의 잔잔한 어쿠스틱 대중 음악을 선호"),
    ISAU("연주 중심 잔잔한 어쿠스틱 실험", "연주 중심의 잔잔한 어쿠스틱 실험 음악을 선호"),
    ISDP("연주 중심 잔잔한 디지털 대중", "연주 중심의 잔잔한 디지털 대중 음악을 선호"),
    ISDU("연주 중심 잔잔한 디지털 실험", "연주 중심의 잔잔한 디지털 실험 음악을 선호"),

    // I-F 조합
    IFAP("연주 중심 에너지 어쿠스틱 대중", "연주 중심의 빠른 어쿠스틱 대중 음악을 선호"),
    IFAU("연주 중심 에너지 어쿠스틱 실험", "연주 중심의 빠른 어쿠스틱 실험 음악을 선호"),
    IFDP("연주 중심 에너지 디지털 대중", "연주 중심의 빠른 디지털 대중 음악을 선호"),
    IFDU("연주 중심 에너지 디지털 실험", "연주 중심의 빠른 디지털 실험 음악을 선호");

    private final String typeName;
    private final String description;

    /**
     * 타입 코드에서 각 축의 방향 추출
     */
    public String getAxisValue(MutiAxis axis) {
        String code = this.name();
        return switch (axis) {
            case E_I -> code.substring(0, 1); // E or I
            case S_F -> code.substring(1, 2); // S or F
            case A_D -> code.substring(2, 3); // A or D
            case P_U -> code.substring(3, 4); // P or U
        };
    }

    /**
     * 4개 축 방향으로 타입 찾기
     */
    public static MutiType fromAxisValues(String ei, String sf, String ad, String pu) {
        String code = ei + sf + ad + pu;
        return MutiType.valueOf(code.toUpperCase());
    }
}