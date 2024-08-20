package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.Constant.StoreType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDTO {

    private Long storeNo;
    private Long hotelNo;
    private Long adminNo;

    private String userid;

    private String password;


    private String name;
    private String hotelName;

    private String address;

    private String businessHours;

    private String tel;

    private StoreType storeType;

    private RoleType roleType;

    private String imageUrl;


    private Long userNo;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
