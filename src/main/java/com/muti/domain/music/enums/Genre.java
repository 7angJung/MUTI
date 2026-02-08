package com.muti.domain.music.enums;

/**
 * 음악 장르
 */
public enum Genre {
    POP("팝"),
    ROCK("록"),
    HIPHOP("힙합"),
    RNB("알앤비"),
    JAZZ("재즈"),
    CLASSICAL("클래식"),
    ELECTRONIC("일렉트로닉"),
    FOLK("포크"),
    INDIE("인디"),
    BALLAD("발라드"),
    DANCE("댄스"),
    METAL("메탈"),
    ALTERNATIVE("얼터너티브"),
    SOUL("소울"),
    COUNTRY("컨트리"),
    REGGAE("레게"),
    BLUES("블루스"),
    AMBIENT("앰비언트"),
    EXPERIMENTAL("실험음악"),
    OTHER("기타");

    private final String description;

    Genre(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}