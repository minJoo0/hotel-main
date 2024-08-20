package com.exam.hotelmanage1.Entity;

import com.exam.hotelmanage1.Constant.RoleType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"hotelEntity"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "admin")
@SequenceGenerator(name = "admin_sql", sequenceName = "admin_sql",
        initialValue = 1,allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "adminNo")
public class AdminEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "admin_sql")
    @Column(name="adminNo")
    private Long adminNo;

    @Column(name = "userid", unique = true)
    private String userid;
    @Column(name = "password")
    private String password;
    private String name;
    private String email;
    private String phone;
    //권한분류를 문자열로 데이터베이스에 저장
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @OneToMany(mappedBy = "adminEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelEntity> hotelEntity;

    @OneToMany(mappedBy = "adminEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodeGroupEntity> codeGroupEntity;

}
