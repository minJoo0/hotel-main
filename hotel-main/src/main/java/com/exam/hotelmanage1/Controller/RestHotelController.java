package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.CodeDTO;
import com.exam.hotelmanage1.DTO.CodeGroupDTO;
import com.exam.hotelmanage1.DTO.HotelDTO;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Tag(name = "Rest 호텔", description = "호텔 Rest API")
@RestController
public class RestHotelController {

    private final HotelService hotelService;
    private final AuthService authService;
    private final StoreService storeService;
    private final CodeGroupService codeGroupService;
    private final AdminNoCheckService adminNoCheckService;
    private final CodeService codeService;
    @Operation(summary = "",
            description = "")
    @PostMapping("/admin/hotel/register")
    public ResponseEntity<?> saveHotel(@RequestBody HotelDTO hotelDTO) {
        try {
            hotelService.insert(hotelDTO);
            return new ResponseEntity<>("호텔 정보가 성공적으로 저장되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("호텔 정보 저장 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "지역에 따른 호텔 선택 select 문 api",
            description = "")
    @GetMapping("/api/hotels")
    @ResponseBody
    public List<HotelDTO> getHotelsByRegion(@RequestParam("sido") String sido) {

        // 현재 접속 중인 사용자가 관리자인지 확인

        List<HotelEntity> hotelEntities;

        if (authService.isAdmin()) {
            // 관리자인 경우, 지정된 지역의 모든 호텔을 조회
            hotelEntities = storeService.findHotelsBySido(sido);
            log.info(hotelEntities);
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
    @Operation(summary = "adminNo로 CodeGroup을 찾는 API",
            description = "")
    @GetMapping("/api/codeGroups")
    public List<CodeGroupDTO> getCodeGroups(Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        // codeGroupService를 통해 코드 그룹 목록을 조회합니다.
        // 여기서 CodeGroup은 코드 그룹 정보를 담는 모델 클래스입니다.
        return codeGroupService.getCodeGroupsByAdminNo(adminNo);
    }
    @Operation(summary = "상위코드 No로 하위코드를 찾는 API",
            description = "")
    @GetMapping("/api/codeGroups/subCodes")
    public List<CodeDTO> getCode(@RequestParam("codeGroupNo") Long codeGroupNo) {
        // codeGroupService를 통해 코드 그룹 목록을 조회합니다.
        // 여기서 CodeGroup은 코드 그룹 정보를 담는 모델 클래스입니다.
        return codeService.findByCodeGroupEntityCodeGroupNo(codeGroupNo);
    }
}

