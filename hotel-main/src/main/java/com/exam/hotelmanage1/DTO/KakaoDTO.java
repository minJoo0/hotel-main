package com.exam.hotelmanage1.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoDTO {


    private Long userNo;
    private Long kakaoNo;
    private String connectedAt;
    private String registerType;
    private String kakaoNickname;
    private String kakaoEmail;
}
