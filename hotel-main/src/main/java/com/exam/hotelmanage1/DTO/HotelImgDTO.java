package com.exam.hotelmanage1.DTO;

import lombok.*;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelImgDTO implements Serializable {

    private Long hotelImgNo;
    private String imgName;
    private String oriImgName;
    private String repImgYn;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
