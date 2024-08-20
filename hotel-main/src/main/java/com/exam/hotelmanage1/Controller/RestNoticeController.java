package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.HotelDTO;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Service.AdminNoCheckService;
import com.exam.hotelmanage1.Service.AuthService;
import com.exam.hotelmanage1.Service.HotelService;
import com.exam.hotelmanage1.Service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Tag(name = "Rest 공지사항", description = "공지사항 Rest API")
@RestController
public class RestNoticeController {
    private final AuthService authService;
    private final StoreService storeService;
    private final AdminNoCheckService adminNoCheckService;
    private final HotelService hotelService;

    @Operation(summary = "속한 지역에 따른 호텔 조회 API",
            description = "")
    @GetMapping("/api/notice/hotels")
    @ResponseBody
    public List<HotelDTO> getHotelsByRegion(@RequestParam("sido") String sido) {

        // 현재 접속 중인 사용자가 관리자인지 확인

        List<HotelEntity> hotelEntities;

        if (authService.isAdmin()) {
            // 관리자인 경우, 지정된 지역의 모든 호텔을 조회
            hotelEntities = storeService.findHotelsBySido(sido);
        } else {
            // 관리자가 아닌 경우, 현재 접속 중인 관리자의 adminNo로 필터링하여 조회
            Optional<Long> currentAdminNo = authService.getCurrentAdminNo();
            hotelEntities = currentAdminNo.map(adminNo ->
                    storeService.findHotelsBySidoAndAdminNo(sido, adminNo)
            ).orElse(Collections.emptyList());
        }

        List<HotelDTO> hotelDTOs = hotelEntities.stream().map(entity -> {
            HotelDTO.HotelDTOBuilder builder = HotelDTO.builder()
                    .hotelNo(entity.getHotelNo())
                    .name(entity.getName())
                    .address(entity.getAddress())
                    .address2(entity.getAddress2())
                    .postNumber(entity.getPostNumber())
                    .tel(entity.getTel())
                    .hotelType(entity.getHotelType())
                    .sido(entity.getSido())
                    .regDate(entity.getRegDate())
                    .modDate(entity.getModDate());

            authService.getCurrentAdminNo().ifPresent(builder::adminNo); // 현재 관리자의 adminNo로 설정, 존재하는 경우에만

            return builder.build();
        }).collect(Collectors.toList());

        return hotelDTOs;
    }


    @Operation(summary = "지역 목록 조회 API",
            description = "")
    @GetMapping("/api/notice/regions")
    public ResponseEntity<List<String>> noticeSidos(Principal principal) {
        List<String> sidos = hotelService.HotelSidos(principal);
        return ResponseEntity.ok(sidos);
    }

}
