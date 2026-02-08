package com.muti.domain.board.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 게시판 타입 Enum
 *
 * FREE: 자유게시판 (모든 사용자가 접근 가능)
 * MUTI_TYPE: MUTI 타입별 게시판 (특정 MUTI 타입을 가진 사용자들의 전용 공간)
 */
@Getter
@RequiredArgsConstructor
public enum BoardType {
    FREE("자유게시판", "모든 사용자가 자유롭게 이야기를 나눌 수 있는 공간"),
    MUTI_TYPE("MUTI 타입 게시판", "같은 MUTI 타입을 가진 사용자들끼리 소통하는 공간");

    private final String displayName;
    private final String description;
}