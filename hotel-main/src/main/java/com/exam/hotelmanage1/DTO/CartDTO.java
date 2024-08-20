package com.exam.hotelmanage1.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private Long cartNo;
    private Long userNo;
    private Long hotelNo;
    private String hotelName;
    private Long storeNo;
    private String storeName;
    private Long roomNo;
    private String roomName;
    private Long menuNo;
    private String menuName;
    private int price;
    private int optionPrice;
    private int cartCount;
    private Long menuOptionNo;
    private String menuOptionName;
    private String imageUrl;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
