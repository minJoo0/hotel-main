package com.exam.hotelmanage1.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyRevenueDTO {

    private Long DailyRevenueNo;
    private LocalDate revenueDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long adminNo;
    private Long hotelNo;
    private Long storeNo;
    private Long totalFee;
    private Long totalDepositPrice;
    private Long totalSales;
    private Long totalFeeWhereItem;
    private Long totalDepositPriceWhereItem;
    private Long totalSalesWhereItem;
    private Long totalFeeWhereRoom;
    private Long totalDepositPriceWhereRoom;
    private Long totalSalesWhereRoom;

}
