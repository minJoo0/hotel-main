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
public class MenuImgDTO implements Serializable {

    private Long menuImgNo;
    private String imgName;
    private String oriImgName;
    private String repImgYn;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private static ModelMapper modelMapper = new ModelMapper();

    public static  MenuImgDTO of(MenuImgDTO menuImgDTO){
        return  modelMapper.map(menuImgDTO, MenuImgDTO.class);
    }
}
