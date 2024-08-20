package com.exam.hotelmanage1.Constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreType {
    DIRECT("direct","직영점"),
    FRANCHISEE("franchisee","가맹점");


    private final String key;
    private final String value;
}