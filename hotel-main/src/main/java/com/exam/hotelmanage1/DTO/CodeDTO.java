package com.exam.hotelmanage1.DTO;

import lombok.*;

import java.nio.file.attribute.PosixFileAttributes;
import java.time.LocalDateTime;
/*
프그램명 : 하위코드 CRUD
작성자 : 김준수
기능 : 코드 DTO
작업내용 :
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeDTO{

    private Long codeNo;        //코드 번호
    private Long codeGroupNo;
    private Long adminNo;

    private String name;    //코드 이름
    private String codeGroupName;

    private String codeGroup;

    private String code;    //코드 값
    private String fullCode;

    private LocalDateTime regDate;  //등록일
    private LocalDateTime modDate;  //수정일

    private CodeGroupDTO codeGroupEntity;   //코드그룹 정보(하위 코드 조회시 사용)
}
