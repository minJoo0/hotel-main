package com.exam.hotelmanage1.DTO;

import lombok.*;

import java.time.LocalDateTime;
/*================

작업명:공지사항관리crud
작업자:김민주
작업일:2024-04-09

===============*/

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeDTO {

    private Long adminNo;//회사번호
    private Long hotelNo;//호텔번호
    private String hotelName;//호텔이름

    private Long noticeNo;//공지번호

    private String title;//공지제목
    private String content;//공지내용

    private LocalDateTime regDate;
    private LocalDateTime modDate;


}
