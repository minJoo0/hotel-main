package com.exam.hotelmanage1.DTO;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueDTO {
    private Long RevenueNo;
    private Long storeNo;
    private Long hotelNo;
    private Long adminNo;
    private Long reserveNo;
    private String hotelName;
    private String email;
    private String storeName;
    private String userName;
    private Long paymentNo;
    private Long paymentItemNo;
    private Integer unitPrice;
    private Integer optionPrice;
    private Integer totalPrice;
    private Integer paymentCount;
    private Integer fee;
    private Integer depositPrice;
    private Integer totalFee;
    private Integer totalDepositPrice;
    private Integer userCount;
    private LocalDate paymentDate;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
