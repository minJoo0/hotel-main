package com.exam.hotelmanage1.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalRevenueDTO {

    private Long totalFee;
    private Long totalDepositPrice;
    private Long totalFeeWhereItem;
    private Long totalDepositPriceWhereItem;
    private Long totalFeeWhereRoom;
    private Long totalDepositPriceWhereRoom;
}
