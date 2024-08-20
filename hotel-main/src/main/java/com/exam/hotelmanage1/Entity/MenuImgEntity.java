package com.exam.hotelmanage1.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString(exclude = "menuEntity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "menu_img")
@SequenceGenerator(name = "menuImg_sql", sequenceName = "menuImg_sql",
        initialValue = 1,allocationSize = 1)
public class MenuImgEntity extends BaseEntity implements IImageEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "menuImg_sql")
    @Column(name="menu_img_no")
    private Long menuImgNo;

    private String imgName; //이미지 파일명

    private String oriImgName;

//    private String uploadPath;

    private String repImgYn;    //대표이미지


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="menu_menu_no")
    private MenuEntity menuEntity;

    public void setMenuImg(String oriImgName, String imgName){  //이름 두개를 받아서 셋하는 용도
        this.oriImgName = oriImgName;
        this.imgName= imgName;
    }
}
