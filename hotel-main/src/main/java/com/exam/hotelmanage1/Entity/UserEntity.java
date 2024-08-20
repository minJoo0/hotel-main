package com.exam.hotelmanage1.Entity;

import com.exam.hotelmanage1.Constant.RoleType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user")
@SequenceGenerator(
        name = "user_sql",
        sequenceName = "user_sql",
        initialValue = 1,
        allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userNo")
public class UserEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_sql")
    @Column(name="userNo")
    private Long userNo;

    @Column(name = "email", unique = true)
    private String email; //이메일
    @Column(name = "password")
    private String password;
    private String name;
    private String nickName;
    private String gender;
    private LocalDate birth;
    private String phone;
    private String registerType;
    //권한분류를 문자열로 데이터베이스에 저장
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartEntity> cartEntity;

    //예약테이블과 join
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReserveEntity> reserveEntities;
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PaymentEntity> paymentEntity;
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<RegisterTypeEntity> registerTypeEntity;
}
