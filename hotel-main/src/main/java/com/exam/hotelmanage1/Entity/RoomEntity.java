package com.exam.hotelmanage1.Entity;

import com.exam.hotelmanage1.Constant.RoomType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter @Setter @ToString(exclude = {"hotelEntity", "roomImgEntity"})
@Builder
@AllArgsConstructor
@Entity
@Table(name = "room")
@SequenceGenerator(
        name = "room_sql",
        sequenceName = "room_sql",
        initialValue = 1,
        allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "room_no")
public class RoomEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_sql")
    private Long roomNo;        //일련 번호

    private String no;        //룸 번호
    private String name;    //룸 이름
    private String content; //룸 설명
    private Integer price;      //룸 가격
    private String fullCodes;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;  //룸 타입


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_no")
    private HotelEntity hotelEntity;
    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<RoomCodeEntity> roomCodeEntity;
    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<RoomImgEntity> roomImgEntity;


    //예약 테이블과 join
    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ReserveEntity> reserveEntities;

    public RoomEntity() {
        this.no = "room" + roomNo;
    }

}


