package com.exam.hotelmanage1.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = "roomEntity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "room_img")
@SequenceGenerator(name = "roomImg_sql", sequenceName = "roomImg_sql",
        initialValue = 1,allocationSize = 1)
public class RoomImgEntity extends BaseEntity implements IImageEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "roomImg_sql")
    @Column(name="roomImg_sql")
    private Long roomImgNo;

    private String imgName; //이미지 파일명

    private String oriImgName;

//    private String uploadPath;

    private String repImgYn;    //대표이미지


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="room_no")
    private RoomEntity roomEntity;

    public void setMenuImg(String oriImgName, String imgName){  //이름 두개를 받아서 셋하는 용도
        this.oriImgName = oriImgName;
        this.imgName= imgName;
    }
}
