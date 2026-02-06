package com.muti.domain.survey.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * MUTI 4가지 축
 * 음악 취향을 구성하는 독립적인 4개의 관점
 */
@Getter
@RequiredArgsConstructor
public enum MutiAxis {

    /**
     * Emotion vs Instrument
     * 감정선 중심 ↔ 연주·프로덕션 중심
     */
    E_I("Emotion vs Instrument", "감정선 중심 vs 연주·프로덕션 중심"),

    /**
     * Slow vs Fast
     * 잔잔함 ↔ 에너지와 속도
     */
    S_F("Slow vs Fast", "잔잔함 vs 에너지와 속도"),

    /**
     * Acoustic vs Digital
     * 자연스러운 질감 ↔ 전자적 사운드
     */
    A_D("Acoustic vs Digital", "자연스러운 질감 vs 전자적 사운드"),

    /**
     * Popular vs Underground
     * 대중성 ↔ 실험성과 씬 중심
     */
    P_U("Popular vs Underground", "대중성 vs 실험성과 씬 중심");

    private final String name;
    private final String description;

    /**
     * 축의 첫 번째 방향 (E, S, A, P)
     */
    public String getFirstDirection() {
        return this.name().split("_")[0];
    }

    /**
     * 축의 두 번째 방향 (I, F, D, U)
     */
    public String getSecondDirection() {
        return this.name().split("_")[1];
    }
}