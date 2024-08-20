package com.exam.hotelmanage1.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = "hotelEntity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "hotel_img")
@SequenceGenerator(name = "hotelImg_sql", sequenceName = "hotelImg_sql",
        initialValue = 1,allocationSize = 1)
public class HotelImgEntity extends BaseEntity implements IImageEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "hotelImg_sql")
    @Column(name="hotelImg_sql")
    private Long hotelImgNo;

    private String imgName; //이미지 파일명

    private String oriImgName;

//    private String uploadPath;

    private String repImgYn;    //대표이미지


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="hotel_hotel_no")
    private HotelEntity hotelEntity;

    public void setMenuImg(String oriImgName, String imgName){  //이름 두개를 받아서 셋하는 용도
        this.oriImgName = oriImgName;
        this.imgName= imgName;
    }
}
