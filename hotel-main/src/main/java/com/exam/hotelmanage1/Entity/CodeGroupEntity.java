package com.exam.hotelmanage1.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/*
프그램명 : 분류 코드 그룹 CRUD
작성자 : 김준수
기능 : 코드그룹 엔티티
작업내용 : 하위코드 entity와 join
 */

@Entity
@Getter
@Setter
@ToString(exclude = "adminEntity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "codeGroup")
@SequenceGenerator(
        name = "codeGroup_sql",
        sequenceName = "codeGroup_sql",
        initialValue = 1, //시작값
        allocationSize = 1 //증가값
       )
public class CodeGroupEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "codeGroup_sql")
    private Long codeGroupNo;    // 코드 그룹 번호

    @Column(name = "codeGroup")
    private String codeGroup;       //코드 그룹

    @Column(name = "name")
    private String name;            //코드 그룹명

    @Column(name = "orderIdx")
    private int orderIdx;           //코드순서

    @Column(name = "category")
    private String category;         //코드 구분

    @Transient
    private Long codeCount;     //하위 코드 갯수

    //하위 코드 엔티티와 join(하나의 코드그룹은 여러개의 하위 코드를 갖는다)
    @OneToMany(mappedBy = "codeGroupEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CodeEntity> codeEntities = new ArrayList<>();

    @OneToMany(mappedBy = "codeGroupEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<RoomCodeEntity> roomCodeEntityList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_no")
    private AdminEntity adminEntity;


}
