package com.exam.hotelmanage1.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = "storeEntity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "store_img")
@SequenceGenerator(name = "storeImg_sql", sequenceName = "storeImg_sql",
        initialValue = 1,allocationSize = 1)
public class StoreImgEntity extends BaseEntity implements IImageEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "storeImg_sql")
    @Column(name="storeImg_sql")
    private Long storeImgNo;

    private String imgName; //이미지 파일명

    private String oriImgName;

//    private String uploadPath;

    private String repImgYn;    //대표이미지


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="store_store_no")
    private StoreEntity storeEntity;

    public void setMenuImg(String oriImgName, String imgName){  //이름 두개를 받아서 셋하는 용도
        this.oriImgName = oriImgName;
        this.imgName= imgName;
    }
    @Override
    public String getImgName() {
        return imgName;
    }

}
