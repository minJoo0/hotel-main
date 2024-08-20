package com.exam.hotelmanage1.Entity;

import jakarta.persistence.*;
import lombok.*;
/*
프그램명 : 하위코드 CRUD
작성자 : 김준수
기능 : 코드 엔티티
작업내용 : 코드 그룹 entity와 join
todo 나중에 추가될 테이블들을 분류하기 위해 join 필요
 */

@Entity
@Getter
@Setter
@ToString(exclude = "codeGroupEntity")  //양방향 관계일 때 무한반복 오류 예방하기 위해 작성
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "code")
@SequenceGenerator(
        name = "code_sql",
        sequenceName = "code_sql",
        initialValue = 1, //시작값
        allocationSize = 1 //증가값
)
public class CodeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "code_sql")
    private Long codeNo;        //코드 번호

    private String name;    //코드 이름

    private String code;    //코드 값

    private String fullCode;

    //코드그룹엔티티와 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codeGroup_id")
    private CodeGroupEntity codeGroupEntity;

}
