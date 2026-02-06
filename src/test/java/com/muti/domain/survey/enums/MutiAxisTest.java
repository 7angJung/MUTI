package com.muti.domain.survey.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * MutiAxis 단위 테스트
 */
@DisplayName("MutiAxis 테스트")
class MutiAxisTest {

    @Test
    @DisplayName("E_I 축의 첫 번째 방향 추출")
    void getFirstDirection_EI() {
        // given
        MutiAxis axis = MutiAxis.E_I;

        // when
        String result = axis.getFirstDirection();

        // then
        assertThat(result).isEqualTo("E");
    }

    @Test
    @DisplayName("E_I 축의 두 번째 방향 추출")
    void getSecondDirection_EI() {
        // given
        MutiAxis axis = MutiAxis.E_I;

        // when
        String result = axis.getSecondDirection();

        // then
        assertThat(result).isEqualTo("I");
    }

    @Test
    @DisplayName("S_F 축의 방향 추출")
    void getDirections_SF() {
        // given
        MutiAxis axis = MutiAxis.S_F;

        // when
        String first = axis.getFirstDirection();
        String second = axis.getSecondDirection();

        // then
        assertThat(first).isEqualTo("S");
        assertThat(second).isEqualTo("F");
    }

    @Test
    @DisplayName("A_D 축의 방향 추출")
    void getDirections_AD() {
        // given
        MutiAxis axis = MutiAxis.A_D;

        // when
        String first = axis.getFirstDirection();
        String second = axis.getSecondDirection();

        // then
        assertThat(first).isEqualTo("A");
        assertThat(second).isEqualTo("D");
    }

    @Test
    @DisplayName("P_U 축의 방향 추출")
    void getDirections_PU() {
        // given
        MutiAxis axis = MutiAxis.P_U;

        // when
        String first = axis.getFirstDirection();
        String second = axis.getSecondDirection();

        // then
        assertThat(first).isEqualTo("P");
        assertThat(second).isEqualTo("U");
    }

    @Test
    @DisplayName("모든 축이 정의되어 있음")
    void allAxesAreDefined() {
        // when
        MutiAxis[] axes = MutiAxis.values();

        // then
        assertThat(axes).hasSize(4);
        assertThat(axes).containsExactly(
                MutiAxis.E_I,
                MutiAxis.S_F,
                MutiAxis.A_D,
                MutiAxis.P_U
        );
    }

    @Test
    @DisplayName("각 축은 이름과 설명을 가짐")
    void eachAxisHasNameAndDescription() {
        // given
        MutiAxis axis = MutiAxis.E_I;

        // when & then
        assertThat(axis.getName()).isEqualTo("Emotion vs Instrument");
        assertThat(axis.getDescription()).isEqualTo("감정선 중심 vs 연주·프로덕션 중심");
    }
}