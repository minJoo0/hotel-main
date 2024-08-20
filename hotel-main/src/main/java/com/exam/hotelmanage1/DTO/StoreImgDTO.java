package com.exam.hotelmanage1.DTO;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreImgDTO implements Serializable {

    private Long storeImgNo;
    private String imgName;
    private String oriImgName;
    private String repImgYn;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
