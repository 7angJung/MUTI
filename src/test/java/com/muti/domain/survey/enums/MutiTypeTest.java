package com.muti.domain.survey.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

/**
 * MutiType 단위 테스트
 */
@DisplayName("MutiType 테스트")
class MutiTypeTest {

    @Test
    @DisplayName("축 값으로 MUTI 타입 생성 - 성공")
    void fromAxisValues_Success() {
        // given
        String ei = "E";
        String sf = "S";
        String ad = "A";
        String pu = "P";

        // when
        MutiType result = MutiType.fromAxisValues(ei, sf, ad, pu);

        // then
        assertThat(result).isEqualTo(MutiType.ESAP);
        assertThat(result.getTypeName()).isEqualTo("감성적 잔잔한 어쿠스틱 대중");
    }

    @ParameterizedTest
    @CsvSource({
            "I, F, D, U, IFDU",
            "E, F, A, P, EFAP",
            "I, S, A, U, ISAU",
            "E, S, D, P, ESDP"
    })
    @DisplayName("다양한 축 조합으로 MUTI 타입 생성")
    void fromAxisValues_VariousCombinations(String ei, String sf, String ad, String pu, String expected) {
        // when
        MutiType result = MutiType.fromAxisValues(ei, sf, ad, pu);

        // then
        assertThat(result.name()).isEqualTo(expected);
    }

    @Test
    @DisplayName("소문자 입력도 처리 가능")
    void fromAxisValues_LowerCase() {
        // given
        String ei = "e";
        String sf = "s";
        String ad = "a";
        String pu = "p";

        // when
        MutiType result = MutiType.fromAxisValues(ei, sf, ad, pu);

        // then
        assertThat(result).isEqualTo(MutiType.ESAP);
    }

    @Test
    @DisplayName("잘못된 축 값으로 타입 생성 실패")
    void fromAxisValues_InvalidAxis_Fail() {
        // given
        String ei = "X";
        String sf = "S";
        String ad = "A";
        String pu = "P";

        // when & then
        assertThatThrownBy(() -> MutiType.fromAxisValues(ei, sf, ad, pu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("타입에서 축 값 추출 - E_I")
    void getAxisValue_EI() {
        // given
        MutiType type = MutiType.ESAP;

        // when
        String result = type.getAxisValue(MutiAxis.E_I);

        // then
        assertThat(result).isEqualTo("E");
    }

    @Test
    @DisplayName("타입에서 축 값 추출 - S_F")
    void getAxisValue_SF() {
        // given
        MutiType type = MutiType.IFDU;

        // when
        String result = type.getAxisValue(MutiAxis.S_F);

        // then
        assertThat(result).isEqualTo("F");
    }

    @Test
    @DisplayName("타입에서 축 값 추출 - A_D")
    void getAxisValue_AD() {
        // given
        MutiType type = MutiType.ESDP;

        // when
        String result = type.getAxisValue(MutiAxis.A_D);

        // then
        assertThat(result).isEqualTo("D");
    }

    @Test
    @DisplayName("타입에서 축 값 추출 - P_U")
    void getAxisValue_PU() {
        // given
        MutiType type = MutiType.ISAU;

        // when
        String result = type.getAxisValue(MutiAxis.P_U);

        // then
        assertThat(result).isEqualTo("U");
    }

    @Test
    @DisplayName("모든 MUTI 타입이 정의되어 있음")
    void allTypesAreDefined() {
        // when
        MutiType[] types = MutiType.values();

        // then
        assertThat(types).hasSize(16);
    }

    @Test
    @DisplayName("각 타입은 고유한 이름과 설명을 가짐")
    void eachTypeHasUniqueNameAndDescription() {
        // when
        MutiType type = MutiType.ESAP;

        // then
        assertThat(type.getTypeName()).isNotBlank();
        assertThat(type.getDescription()).isNotBlank();
        assertThat(type.getTypeName()).isNotEqualTo(type.getDescription());
    }
}