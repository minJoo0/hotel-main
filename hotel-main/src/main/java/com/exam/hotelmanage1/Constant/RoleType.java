package com.exam.hotelmanage1.Constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum RoleType {
    USER("user","일반사용자"),
    STORE("store","매장"),
    COMPANY("company","회사"),
    ADMIN("admin","관리자");


    private final String key;
    private final String value;

}
