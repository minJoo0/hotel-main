package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Rest 사용자", description = "사용자 Rest API")
@Log4j2
public class RestUserController {



    private final UserService userService;
    private final AuthService authService;
    private final StoreService storeService;
    private final ReserveService reserveService;
    private final MenuService menuService;


    @Operation(summary = "장바구니 Item 수량 설정 API",
            description = "")
    @PostMapping("/user/api/updateQuantity")
    public ResponseEntity<?> updateCartQuantity(@RequestBody CartDTO cartDTO) {
        try {
            userService.updateCartQuantity(cartDTO.getCartNo(), cartDTO.getCartCount());
            return ResponseEntity.ok().body("수량 업데이트 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(summary = "사용자용 지역에 따른 호텔 선택 API",
            description = "")
    @GetMapping("/user/api/hotels")
    @ResponseBody
    public List<HotelDTO> getHotelsByRegion(@RequestParam("sido") String sido) {

        List<HotelEntity> hotelEntities;


        hotelEntities = storeService.findHotelsBySido(sido);
        log.info(hotelEntities);


        return hotelEntities.stream().map(entity -> {
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
    }

    @Operation(summary = "사용자용 호텔에 따른 예약된 방 선택 API",
            description = "")
    @GetMapping("/user/api/rooms")
    @ResponseBody
    public List<ReserveDTO> getRoomsByHotel(@RequestParam("hotelNo") Long hotelNo, @RequestParam("userNo") Long userNo) {

        List<ReserveDTO> reserveDTOList = reserveService.findByUserNoAndHotelNo(userNo,hotelNo);

        return reserveDTOList;

    }

    @Operation(summary = "장바구니가 비어있는지 확인하는 API",
            description = "")
    @GetMapping("/user/api/{userNo}")
    @ResponseBody
    public boolean isCartEmpty(@PathVariable(name = "userNo") Long userNo) {
        List<CartDTO> cartDTOList = userService.findByUserEntityUserNo(userNo);
        return cartDTOList == null || cartDTOList.isEmpty();
    }
    @Operation(summary = "해당 메뉴의 옵션을 출력하는 API",
            description = "")
    @PostMapping("/user/api/menu")
    public List<MenuOptionDTO> userMenuPost(CartDTO cartDTO){
        List<MenuOptionDTO> menuOptionDTOS = userService.findAllByMenuNo(cartDTO.getMenuNo());
        log.info(menuOptionDTOS);
        return menuOptionDTOS;
    }

    @Operation(summary = "카테고리 선택에 따라 해당 카테고리에 속한 메뉴를 출력하는 API",
            description = "")
    @GetMapping("/user/api/category")
    public List<MenuDTO> userMenuCategory(Long category2){
        log.info(category2);
        List<MenuDTO> menuDTOS = menuService.findMenusByCategory2(category2);
        return menuDTOS;
    }
    @Operation(summary = "시도, 시군구, 거기에 속하는 호텔의 갯수를 지정하는 API",
            description = "")
    @GetMapping("/user/api/region")
    public List<RegionDTO> userRegionList(){
        List<RegionDTO> regions = userService.findRegion();
        return regions;
    }



    @Operation(summary = "회원가입 창에서 기재한 Email 이 중복 Email인지 확인하는 API",
            description = "")
    @PostMapping("/user/api/checkEmail")
    public boolean checkEmail(@RequestParam String email) {
        return userService.isEmailExists(email);
    }
}
