package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Constant.ReservationStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveDTO {

    private Long reserveNo;     //예약 id 번호

    private ReservationStatus status;      //예약 상태

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;    //체크인
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;      //체크 아웃

    private Integer people;         // 예약 인원


    private Long roomNo;        // 조인된 room의 id값
    private String roomName;
    private Long userNo;        // 조인된 user의 id값
    private String userEmail;
    private String userName;
    private Long hotelNo;        // room에 조인된 hotel의 id값
    private Long adminNo;
    private String hotelName;
    private List<PaymentDTO> paymentDTOList;
    private Integer gradTotalPrice; //해당 객실의 총 합 가격

    private LocalDateTime regDate;//예약한날짜를 출력하기위해 추가
}
