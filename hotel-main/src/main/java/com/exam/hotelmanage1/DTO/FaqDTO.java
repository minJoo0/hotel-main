package com.exam.hotelmanage1.DTO;

import lombok.*;

import java.time.LocalDateTime;
/*================

작업명:FAQ관리crud
작업자:김민주
작업일:2024-04-12
작업내용:DTO생성

===============*/

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FaqDTO {

    private Long adminNo;//회사번호
    private Long faqNo;//공지번호
    private String category;//분류
    private String title;//공지제목
    private String content;//공지내용

    private LocalDateTime regDate;
    private LocalDateTime modDate;


}
