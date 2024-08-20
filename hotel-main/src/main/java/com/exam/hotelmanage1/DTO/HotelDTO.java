package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Constant.HotelType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {
    private Long hotelNo;
    private Long adminNo;

    private String name;

    private String address;

    private String address2;

    private String roadAddress;

    private String postNumber;

    private String sido;

    private String sigungu;

    private String tel;
    private int rowPrice;

    private HotelType hotelType;

    private String imageUrl;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
