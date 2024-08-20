package com.exam.hotelmanage1.Constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {


    CANCEL("cancel","취소"),
    WAITING("waiting","결제 대기 중"),
    COMPLETE("complete","결제 완료"),
    DELIVERY("delivery","배송 완료");

    private final String key;
    private final String value;
}
