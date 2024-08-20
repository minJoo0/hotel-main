package com.exam.hotelmanage1.Entity;

import com.exam.hotelmanage1.Constant.HotelType;
import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.Constant.StoreType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter @ToString(exclude = {"hotelEntity","paymentEntity"})
@AllArgsConstructor @NoArgsConstructor
@Builder
@Table(name = "store")
@SequenceGenerator(
        name = "store_sql",
        sequenceName = "store_sql",
        initialValue = 1,
        allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "storeNo")
public class StoreEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "store_sql")
    private Long storeNo;

    @Column(name = "userid", unique = true)
    private String userid;

    private String password;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(nullable = false)
    private String businessHours;

    @Column(nullable = false)
    private String tel;

    @Enumerated(EnumType.STRING)
    private StoreType storeType;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_no")
    private HotelEntity hotelEntity;
    @OneToMany(mappedBy = "storeEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryEntity> categoryEntity;

    @OneToMany(mappedBy = "storeEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<StoreImgEntity> storeImgEntity;

    @OneToMany(mappedBy = "storeEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PaymentEntity> paymentEntity;


}
