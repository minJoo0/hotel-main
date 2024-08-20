package com.exam.hotelmanage1.Constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegisterType {

    KAKAO("kakao","카카오"),
    GOOGLE("google","구글"),
    NAVER("naver","네이버");


    private final String key;
    private final String value;


}
