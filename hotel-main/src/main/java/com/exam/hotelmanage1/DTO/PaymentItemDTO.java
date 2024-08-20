package com.exam.hotelmanage1.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentItemDTO {

    private Long paymentItemNo;
    private Long paymentNo;
    private Long userNo;
    private Long menuNo;
    private Long menuOptionNo;
    private String menuName;
    private String menuOptionName;

    private Long storeNo;
    private String storeName;

    private int paymentCount;
    private int unitPrice;
    private int optionPrice;
    private int totalPrice;
    private int grandTotalPrice;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private String imageUrl;
}
