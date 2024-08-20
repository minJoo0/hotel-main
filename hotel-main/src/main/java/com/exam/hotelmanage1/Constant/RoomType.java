package com.exam.hotelmanage1.Constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {

    SINGLE ("single", "싱글룸"),
    TWIN ("twin", "트윈룸"),
    DOUBLE ("double","더블룸"),
    SUITE("suite", "스위트룸");


    private final String key;
    private final String value;
}
