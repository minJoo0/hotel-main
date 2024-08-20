package com.exam.hotelmanage1.Constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

    CANCEL("cancel","취소"),
    RESERVED("reserved","예약됨"),
    PENDING("pending","결제 대기 중"),
    CHECKED_IN("checkedIn","체크인 완료"),
    CHECKED_OUT("checkedOut", "체크 아웃 완료");

    private final String key;
    private final String value;
}
