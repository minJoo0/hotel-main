package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Entity.CategoryEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    private Long categoryNo;
    private Long adminNo;
    private Long hotelNo;
    private Long storeNo;
    private String hotelName;
    private String storeName;
    private String name;
    private Long parentId;
    private String pName;
    private List<CategoryDTO> children;
    private List<MenuDTO> menuDTOList;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
