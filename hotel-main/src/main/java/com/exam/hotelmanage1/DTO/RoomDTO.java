package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Constant.RoomType;
import com.exam.hotelmanage1.DTO.RoomCodeDTO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {

    private Long roomNo;        //일련 번호
    private String no;            //룸 번호

    private Long hotelNo;       //조인된 호텔 번호
    private String hotelName;   //조인된 호텔 이름

    private Long adminNo;

    private String name;        //룸 이름
    private String content;     //룸 설명
    private Integer price;          //룸 가격
    private String fullCodes;

    private List<RoomCodeDTO> roomCodeDTO;
    private List<ReserveDTO> reserveDTOList;
    private ReserveDTO reserveDTO;


    private RoomType roomType;  //룸 타입
    //룸 타입No
    private String codeFullName;  //룸 타입No
    private Long codeNo;
    private Long codeGroupNo;
    private String sido;
    private String imageUrl;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime regDate;  //등록 날짜
    private LocalDateTime modDate;  //수정 날짜

}
