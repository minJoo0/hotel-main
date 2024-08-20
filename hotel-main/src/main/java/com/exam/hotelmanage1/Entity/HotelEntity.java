package com.exam.hotelmanage1.Entity;

import com.exam.hotelmanage1.Constant.HotelType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter @ToString(exclude = "adminEntity")
@AllArgsConstructor @NoArgsConstructor
@Builder
@Table(name = "hotel")
@SequenceGenerator(
        name = "hotel_sql",
        sequenceName = "hotel_sql",
        initialValue = 1,
        allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "hotelNo")
public class HotelEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "hotel_sql")
    private Long hotelNo;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String address2;

    private String roadAddress;

    @Column(nullable = false)
    private String postNumber;

    @Column(nullable = false)
    private String tel;

    private String sido;

    private String sigungu;


    @Enumerated(EnumType.STRING)
    private HotelType hotelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_no")
    private AdminEntity adminEntity;

    @OneToMany(mappedBy = "hotelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreEntity> storeEntities;
    @OneToMany(mappedBy = "hotelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomEntity> roomEntity;
    @OneToMany(mappedBy = "hotelEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<HotelImgEntity> hotelImgEntity;

    @OneToMany(mappedBy = "hotelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeEntity> noticeEntities;




}


