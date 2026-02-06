package com.muti.domain.survey.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 각 축의 방향 (양극)
 */
@Getter
@RequiredArgsConstructor
public enum AxisDirection {

    // E_I 축
    E("Emotion", "감정선 중심", "보컬의 감정, 가사 서사, 분위기에 민감"),
    I("Instrument", "연주·프로덕션 중심", "연주, 편곡, 사운드 디자인, 믹싱 등 구조적 요소"),

    // S_F 축
    S("Slow", "잔잔함", "템포가 느리고 여운이 긴 음악 선호"),
    F("Fast", "에너지", "빠른 템포와 강한 비트 선호"),

    // A_D 축
    A("Acoustic", "자연스러운 질감", "어쿠스틱 악기, 라이브 감성"),
    D("Digital", "전자적 사운드", "신스, 샘플링, 이펙트 기반"),

    // P_U 축
    P("Popular", "대중성", "멜로디 중심, 접근성 좋은 음악"),
    U("Underground", "실험성", "실험적·비주류 음악, 씬 중심");

    private final String code;
    private final String name;
    private final String description;
}