package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Entity.MenuEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionDTO {
    private Long menuOptionNo;
    private Long menuNo;
    private Long storeNo;
    private Long hotelNo;
    private String hotelName;
    private Long adminNo;
    private String storeName;
    private String menuName;
    private Long category1;
    private Long category2;
    private String category1Name;
    private String category2Name;
    private String name;
    private String content;
    private int price;
    private String imageUrl;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
