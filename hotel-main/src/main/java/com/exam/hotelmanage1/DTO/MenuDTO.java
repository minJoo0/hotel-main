package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.Entity.MenuImgEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {
    private Long menuNo;
    private Long storeNo;
    private Long hotelNo;
    private String hotelName;
    private Long adminNo;
    private String storeName;
    private Long category1;
    private Long category2;
    private String category1Name;
    private String category2Name;
    private String imageUrl;
    private String name;
    private String content;
    private int price;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private List<MenuImgEntity> menuImgEntity = new ArrayList<>();
}
