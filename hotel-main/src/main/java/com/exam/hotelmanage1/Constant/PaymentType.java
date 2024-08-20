package com.exam.hotelmanage1.Constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {

    ROOM("room","객실"),
    ITEM("item","메뉴");

    private final String key;
    private final String value;
}
