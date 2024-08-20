package com.exam.hotelmanage1.Constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CASH("cash","현금"),
    CARD("card","카드"),
    KAKAO("kakao","카카오페이"),
    NAVER("naver","네이버페이"),
    TOSS("toss","토스페이");


    private final String key;
    private final String value;
}
