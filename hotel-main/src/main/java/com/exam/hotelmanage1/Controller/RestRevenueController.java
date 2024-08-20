package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Tag(name = "Rest 정산", description = "정산 Rest API")
@RestController
public class RestRevenueController {

    private final RevenueService revenueService;
    private final HotelService hotelService;
    private final MenuService menuService;
    private final AdminNoCheckService adminNoCheckService;
    private final AuthService authService;
    @Operation(summary = "일별 정산 조회 API",
            description = "")
    @GetMapping("/api/revenue/daily")
    public PageResponseDTO<DailyRevenueDTO> listDailyRevenue(DailyRevenueDTO dailyRevenueDTO, PageRequestDTO pageRequestDTO, Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            dailyRevenueDTO.setAdminNo(null);
        }else {
            dailyRevenueDTO.setAdminNo(adminNo);
        }
        log.info(revenueService.getDailyTotalFeeAndDepositPrice(dailyRevenueDTO,pageRequestDTO));
        return revenueService.getDailyTotalFeeAndDepositPrice(dailyRevenueDTO,pageRequestDTO);
    }
    @Operation(summary = "총 정산 조회 API",
            description = "")
    @GetMapping("/api/revenue/total")
    public TotalRevenueDTO listTotalRevenue(DailyRevenueDTO dailyRevenueDTO,Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            dailyRevenueDTO.setAdminNo(null);
        }else {
            dailyRevenueDTO.setAdminNo(adminNo);
        }
        log.info(revenueService.getTotalRevenue(dailyRevenueDTO));
        return revenueService.getTotalRevenue(dailyRevenueDTO);
    }
    @Operation(summary = "엑셀 출력용 정산 조회 API",
            description = "")
    @GetMapping("/api/revenue/excel")
    public List<Object[]> ExcelListTotalRevenue(DailyRevenueDTO dailyRevenueDTO,Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            dailyRevenueDTO.setAdminNo(null);
        }else {
            dailyRevenueDTO.setAdminNo(adminNo);
        }
        log.info(revenueService.getTotalRevenueExcel(dailyRevenueDTO));
        return revenueService.getTotalRevenueExcel(dailyRevenueDTO);
    }
}
