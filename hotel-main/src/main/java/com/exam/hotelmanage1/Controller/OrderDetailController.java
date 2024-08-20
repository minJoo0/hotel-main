package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Tag(name = "주문 상세 내역",description = "주문 상세 내역 관리 API")
@Log4j2
public class OrderDetailController {

    private final UserService userService;
    private final AdminNoCheckService adminNoCheckService;
    private final OrderDetailService orderDetailService;
    private final HotelService hotelService;
    private final AuthService authService;

    @ModelAttribute("paymentStatus")
    public PaymentStatus[] paymentStatus(){
        return PaymentStatus.values();
    }

    @Operation(summary = "주문 상세 내역",
            description = "결제 내역 페이지로 이동")
    @GetMapping("/manager/order/list")
    public String orderListGet(Model model,
                               PageRequestDTO pageRequestDTO,
                               PaymentDTO paymentDTO,
                               Principal principal){
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            paymentDTO.setAdminNo(null);
        }else {
            paymentDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<PaymentDTO> paymentDTOS = orderDetailService.paymentItemListAdmin(paymentDTO,pageRequestDTO);
        model.addAttribute("paymentList",paymentDTOS);
        return "manager/order/list";
    }
}
