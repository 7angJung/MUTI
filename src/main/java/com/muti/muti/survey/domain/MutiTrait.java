package com.muti.muti.survey.domain;

/**
 * MUTI (Music User Type Indicator)의 8가지 기본 성향을 정의합니다.
 * 각 성향은 4개의 차원에서 서로 대립되는 쌍을 이룹니다.
 */
public enum MutiTrait {
    // 감성(E) vs 악기/구조(I)
    E, // Emotion: 가사, 멜로디, 감정선 중시
    I, // Instrument: 악기 구성, 사운드 질감, 곡의 구조 중시

    // 느림(S) vs 빠름(F)
    S, // Slow: 차분하고 느린 템포 선호
    F, // Fast: 빠르고 리드미컬한 템포 선호

    // 적극적 탐색(A) vs 수동적 수용(D)
    A, // Active: 새로운 음악을 적극적으로 찾아 듣는 탐색가형
    D, // Drift: 알고리즘이나 추천에 몸을 맡기는 표류형

    // 대중성(P) vs 비주류(U)
    P, // Popular: 대중적이고 익숙한 음악 선호
    U  // Underground: 독특하고 실험적인 음악 선호
}