package com.muti.domain.survey.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * AxisDirection 단위 테스트
 */
@DisplayName("AxisDirection 테스트")
class AxisDirectionTest {

    @Test
    @DisplayName("E 방향 정보 확인")
    void directionE() {
        // given
        AxisDirection direction = AxisDirection.E;

        // when & then
        assertThat(direction.getCode()).isEqualTo("Emotion");
        assertThat(direction.getName()).isEqualTo("감정선 중심");
        assertThat(direction.getDescription()).isNotBlank();
    }

    @Test
    @DisplayName("I 방향 정보 확인")
    void directionI() {
        // given
        AxisDirection direction = AxisDirection.I;

        // when & then
        assertThat(direction.getCode()).isEqualTo("Instrument");
        assertThat(direction.getName()).isEqualTo("연주·프로덕션 중심");
    }

    @Test
    @DisplayName("모든 방향이 정의되어 있음 (8개)")
    void allDirectionsAreDefined() {
        // when
        AxisDirection[] directions = AxisDirection.values();

        // then
        assertThat(directions).hasSize(8);
        assertThat(directions).containsExactly(
                AxisDirection.E, AxisDirection.I,
                AxisDirection.S, AxisDirection.F,
                AxisDirection.A, AxisDirection.D,
                AxisDirection.P, AxisDirection.U
        );
    }

    @Test
    @DisplayName("각 방향은 고유한 코드, 이름, 설명을 가짐")
    void eachDirectionHasUniqueProperties() {
        // given
        AxisDirection direction = AxisDirection.S;

        // when & then
        assertThat(direction.getCode()).isNotBlank();
        assertThat(direction.getName()).isNotBlank();
        assertThat(direction.getDescription()).isNotBlank();
    }
}