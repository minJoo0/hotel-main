package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "정산", description = "정산 관리 API")
@Log4j2
public class RevenueController {


    private final RevenueService revenueService;
    private final HotelService hotelService;
    private final MenuService menuService;
    private final AdminNoCheckService adminNoCheckService;
    private final AuthService authService;

    @Operation(summary = "정산 목록",
            description = "호텔목록, 정산목록을 보낸 후 정산 목록 페이지로 이동")
    @GetMapping("/manager/revenue/list")
    public String revenueItemList(Model model,
                                  RevenueDTO revenueDTO,
                                  Principal principal,
                                  PageRequestDTO pageRequestDTO) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            revenueDTO.setAdminNo(null);
        }else {
            revenueDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<RevenueDTO> revenueDTO1 = revenueService.PageList(revenueDTO,pageRequestDTO);
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        model.addAttribute("revenueList",revenueDTO1);
        model.addAttribute("hotelDTOList",hotelDTOList);
        return "/manager/revenue/list";
    }

    @Operation(summary = "객실 정산",
            description = "호텔목록, 객실 정산 목록을 보낸 후 객실정산 페이지로 이동")
    @GetMapping("/manager/revenue/room")
    public String revenueRoomList(Model model,
                                  RevenueDTO revenueDTO,
                                  Principal principal,
                                  PageRequestDTO pageRequestDTO) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            revenueDTO.setAdminNo(null);
        }else {
            revenueDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<RevenueDTO> revenueDTO1 = revenueService.PageRoomList(revenueDTO,pageRequestDTO);
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        model.addAttribute("revenueList",revenueDTO1);
        model.addAttribute("hotelDTOList",hotelDTOList);

        return "/manager/revenue/room";
    }

    @Operation(summary = "일일 정산",
            description = "호텔 일일 정산 페이지로 이동")
    @GetMapping("/manager/revenue/daily")
    public String revenueDailyList(Model model,
                                   DailyRevenueDTO dailyRevenueDTO,
                                   Principal principal,
                                   PageRequestDTO pageRequestDTO) {
//        PageResponseDTO<DailyRevenueDTO> ObjectList = revenueService.getDailyTotalFeeAndDepositPrice(dailyRevenueDTO,pageRequestDTO);
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        model.addAttribute("hotelDTOList",hotelDTOList);

        return "/manager/revenue/daily";
    }
}
