package com.exam.hotelmanage1.Controller;


import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "예약", description = "예약 관리 API")
@Log4j2
public class ReserveController {

    private final ReserveService reserveService;
    private final RoomService roomService;
    private final UserNoCheckService userNoCheckService;
    private final AdminNoCheckService adminNoCheckService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final RevenueService revenueService;
    private final AuthService authService;

    @ModelAttribute
    public void myPage(Principal principal, Model model) {
        // Principal 객체가 null인 경우, 즉 사용자가 로그아웃한 상태인 경우,
        // 이 메소드의 나머지 부분을 실행하지 않습니다.
        if (principal != null) {
            Long userNo = userNoCheckService.userNocheck(principal);
            model.addAttribute("userNo", userNo);
        }
    }


    //유저의 객실 예약
    @Operation(summary = "예약 등록폼",
            description = "객실 정보와 userNo 값을 보낸 후 예약 등록 페이지로 이동")
    @GetMapping("/user/reserve/register/{roomNo}")
    public String reserveForm(@PathVariable(name = "roomNo") Long roomNo,
                              @RequestParam(name = "startDate", required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam(name = "endDate", required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                              Principal principal,
                              Model model) {
        //url의 roomNo를 통해 room의 정보를 읽어 온다.
        RoomDTO roomDTO = roomService.read(roomNo);
        roomDTO.setStartDate(startDate);
        roomDTO.setEndDate(endDate);
        log.info("룸....." + roomDTO);
        model.addAttribute("roomDTO", roomDTO);

        Long userNocheck = userNoCheckService.userNocheck(principal);
        log.info("유저...." + userNocheck);
        model.addAttribute("userNo", userNocheck);

        return "user/reserve/register";
    }

    @Operation(summary = "예약 등록처리",
            description = "예약 등록 결제 후 예약 상세 정보 페이지로 이동")
    @PostMapping("/user/reserve/register")
    public String reserveProc(@ModelAttribute ReserveDTO reserveDTO,
                              @RequestParam int grandTotalPrice,
                              @RequestParam String paymentStatus,
                              @RequestParam int paymentCount,
                              @RequestParam int price,
                              Principal principal) {
        log.info(grandTotalPrice);

        Long reserveNo = reserveService.reserveInsert(reserveDTO).getReserveNo();

        Long userNo = userNoCheckService.userNocheck(principal);


        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setGrandTotalPrice(grandTotalPrice);
        paymentDTO.setReserveNo(reserveNo);
        paymentDTO.setUserNo(userNo);
        paymentDTO.setPaymentStatus(PaymentStatus.valueOf(paymentStatus)); // 라디오 버튼 값 설정
        paymentDTO.setPaymentType(PaymentType.ROOM);




        Long paymentNo = userService.paymentSave(paymentDTO).getPaymentNo();

        PaymentItemDTO paymentItemDTO = new PaymentItemDTO();
        paymentItemDTO.setPaymentNo(paymentNo);
        paymentItemDTO.setPaymentCount(paymentCount);
        paymentItemDTO.setTotalPrice(grandTotalPrice);
        paymentItemDTO.setUnitPrice(price);


        Long paymentItemNo = userService.paymentItemSave(paymentItemDTO).getPaymentItemNo();

        RevenueDTO revenueDTO = new RevenueDTO();
        Integer fee = (int) (paymentDTO.getGrandTotalPrice() * 0.1);
        Integer depositPrice = paymentDTO.getGrandTotalPrice() - fee;
        revenueDTO.setPaymentNo(paymentNo);
        revenueDTO.setPaymentItemNo(paymentItemNo);
        revenueDTO.setDepositPrice(depositPrice);
        revenueDTO.setFee(fee);
        revenueDTO.setUserCount(reserveDTO.getPeople());

        if (paymentDTO.getPaymentStatus() == PaymentStatus.COMPLETE){
            revenueService.insertRoom(revenueDTO);
        }

        // 예약 후 상세 정보 페이지로
        return "redirect:/user/reserve/read/" + reserveNo;
    }

    //유저의 예약 날짜 수정
    @Operation(summary = "예약 날짜 수정폼",
            description = "예약 정보를 보낸 후 예약 수정페이지로 이동")
    @GetMapping("/user/reserve/update/{reserveNo}")
    public String updateForm(@PathVariable(name = "reserveNo") Long reserveNo, Model model) {
        ReserveDTO reserveDTO = reserveService.reserveRead(reserveNo);
        model.addAttribute("reserveDTO", reserveDTO);

        return "user/reserve/update";
    }

    @Operation(summary = "예약 수정 처리",
            description = "수정된 예약 정보를 저장하고 페이지로 이동")
    @PostMapping("/user/reserve/update")
    public String updateProc(@ModelAttribute ReserveDTO reserveDTO) {
        log.info("수정 처리중");
        reserveService.reserveUpdate(reserveDTO);
        return "redirect:/user/reserve/read/" + reserveDTO.getReserveNo();
    }

    //유저의 예약 목록(페이징, 검색x)
    @Operation(summary = "고객용 예약 목록",
            description = "현재 접속 한 계정의 예약 목록값을 보낸 후 예약 목록페이지로 이동")
    @GetMapping("/user/reserve/list")
    public String UserListForm(ReserveDTO reserveDTO,
                               PageRequestDTO pageRequestDTO,
                               Model model,
                               Principal principal) {
        //현재 접속되어 있는 유저의 userNo 값을 가져온다
        Long userNo = userNoCheckService.userNocheck(principal);
        //DTO에 userNo 저장
        ReserveDTO reserveDTO1 = new ReserveDTO();
        reserveDTO1.setUserNo(userNo);
        //dto, page 정보를 리스트에 담는다.
        PageResponseDTO<ReserveDTO> list = reserveService.PageList(reserveDTO1, pageRequestDTO);
        model.addAttribute("list", list);
        return "user/reserve/list";
    }


    //유저의 예약 상세 정보
    @Operation(summary = "고객용 예약 상세 정보",
            description = "예약정보를 보낸 후 예약 상세 정보 페이지로 이동")
    @GetMapping("/user/reserve/read/{reserveNo}")
    public String readForm(@PathVariable(name = "reserveNo") Long reserveNo, Model model) {
        log.info("예약 정보 Form 이동");
        ReserveDTO reserveDTO = reserveService.reserveRead(reserveNo);
        model.addAttribute("reserveDTO", reserveDTO);
        return "user/reserve/read";
    }

    //유저의 예약 삭제
    @Operation(summary = "예약 삭제폼",
            description = "예약 삭제 후 예약 목록 페이지로 이동")
    @GetMapping("/user/reserve/delete/{reserveNo}")
    public String deleteForm(@PathVariable(name = "reserveNo") Long reserveNo) {
        log.info("예약 삭제...");
        reserveService.reserveDelete(reserveNo);
        return "redirect:/user/reserve/list";
    }

    //관리자가 볼 수 있는 예약 목록(페이징, 검색x)
    @Operation(summary = "관리자용 예약 목록",
            description = "특정 기간동안 예약되어 있는 객실 목록의 값을 보낸 후 예약 목록 페이지로 이동")
    @GetMapping("/manager/reserve/list")
    public String CompanyListForm(ReserveDTO reserveDTO,
                                  @RequestParam(name = "startDateStr", required = false) String startDateStr,
                                  @RequestParam(name = "endDateStr", required = false) String endDateStr,
                                  @RequestParam(name = "hotelName", required = false) String hotelName,
                                  @RequestParam(name = "roomName", required = false) String roomName,
                                  @RequestParam(name = "userEmail", required = false) String userEmail,
                                  PageRequestDTO pageRequestDTO,
                                  Model model,
                                  Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            reserveDTO.setAdminNo(null);
        }else {
            reserveDTO.setAdminNo(adminNo);
        }
        // 날짜 파라미터가 빈 문자열인 경우 기본값으로 설정
        LocalDate startDate = (startDateStr == null || startDateStr.isEmpty()) ? LocalDate.parse("1900-01-01") : LocalDate.parse(startDateStr);
        LocalDate endDate = (endDateStr == null || endDateStr.isEmpty()) ? LocalDate.parse("9999-12-31") : LocalDate.parse(endDateStr);

        log.info("startDate......" + startDate);
        log.info("endDate......" + endDate);

        // 변환된 날짜를 reserveDTO에 설정
        reserveDTO.setStartDate(startDate);
        reserveDTO.setEndDate(endDate);

        PageResponseDTO<ReserveDTO> reserveDTOS = reserveService.PageList(reserveDTO, pageRequestDTO);
        log.info(reserveDTOS);
        model.addAttribute("reserveDTOS", reserveDTOS);

        return "manager/reserve/list";
    }



    //관리자가 볼 수 있는 예약상세 정보
    @Operation(summary = "관리자용 예약 상세 정보",
            description = "예약 상세정보 페이지로 이동")
    @GetMapping("/manager/reserve/read/{reserveNo}")
    public String companyReadReserveForm(@PathVariable(name = "reserveNo") Long reserveNo,
                                         Model model) {
        ReserveDTO reserveDTO = reserveService.reserveRead(reserveNo);
        List<PaymentDTO> paymentDTOList = paymentService.findByReserveNoAndPaymentTypeAndPaymentStatus(reserveNo,PaymentType.ROOM,PaymentStatus.COMPLETE);
        log.info("호텔 이름" + reserveDTO.getHotelName());
        log.info("객실 이름" + reserveDTO.getRoomName());
        model.addAttribute("reserveDTO", reserveDTO);
        if (!paymentDTOList.isEmpty()) {
            PaymentDTO paymentDTO = paymentDTOList.get(0);
            // 모델에 PaymentDTO 객체 추가
            model.addAttribute("paymentDTO", paymentDTO);
        } else {
            // 리스트가 비었을 경우의 처리. 예를 들어, 기본 PaymentDTO 객체를 추가하거나 에러 메시지를 설정할 수 있음
            // 예: model.addAttribute("paymentDTO", new PaymentDTO());
        }
        return "manager/reserve/read";
    }

}