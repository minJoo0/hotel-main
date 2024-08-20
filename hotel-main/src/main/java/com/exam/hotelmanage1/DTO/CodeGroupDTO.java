package com.exam.hotelmanage1.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/*
프그램명 : 분류 코드 그룹 CRUD
작성자 : 김준수
기능 : 코드그룹 DTO
작업내용 :
 */

@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CodeGroupDTO {
    private Long codeGroupNo;            // 코드 그룹 번호
    private Long adminNo;
    private String codeGroup;   //코드 그룹

    private String name;        //코드 그룹명

    private int orderIdx;       //코드 순서

    private String category;    //코드 구분
    private String codeCount; //하위코드 갯수

    private LocalDateTime regDate;  //등록일
    private LocalDateTime modDate;  //수정일

//    private List<CodeGroupDTO> codeEntities;    //하위코드 list


    //toString() 메서드에서 발생한 무한 재귀 호출 문제를 해결하기 위해
    //codeEntities 필드를 참조하는 부분을 수정
//    @Override
//    public String toString() {
//        return "CodeGroupDTO{" +
//                "id=" + id +
//                ", codeGroup='" + codeGroup + '\'' +
//                ", name='" + name + '\'' +
//                ", orderIdx='" + orderIdx + '\'' +
//                ", category='" + category + '\'' +
//                ", codeCount=" + codeCount +
//                ", regDate=" + regDate +
//                ", modDate=" + modDate +
//                ", codeEntities size=" + (codeEntities != null ? codeEntities.size() : 0) +
//                '}';
//    }

}
