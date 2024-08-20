package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Constant.PaymentMethod;
import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.Constant.PaymentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long paymentNo;
    private Long userNo;
    private Long adminNo;
    private Long hotelNo;
    private Long storeNo;
    private Long roomNo;
    private Long reserveNo;
    private String hotelName;
    private String roomName;
    private String userEmail;
    private String userName;
    private String userNickName;
    private int completePayment;
    private int grandTotalPrice;
    private List<PaymentItemDTO> paymentItemDTO;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private PaymentStatus paymentStatus;
    private PaymentType paymentType;
}
