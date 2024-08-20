package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "메뉴 옵션",description = "메뉴 옵션 관리 API")
@Log4j2
public class MenuOptionController {

    private final MenuService menuService;
    private final AuthService authService;
    private final AdminNoCheckService adminNoCheckService;
    private final MenuOptionService menuOptionService;


    @Operation(summary = "메뉴 옵션 목록",
            description = "호텔 목록, 메뉴 옵션 목록 값을 보내고 메뉴 옵션 리스트 페이지로 이동")
    @GetMapping("/manager/menu/option/list")
    public String managerMenuOptionList(Model model,
                                        PageRequestDTO pageRequestDTO,
                                        MenuOptionDTO menuOptionDTO,
                                        Principal principal){
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        if(authService.isAdmin()){
            menuOptionDTO.setAdminNo(null);
        }else {
            menuOptionDTO.setAdminNo(adminNo);
        }

        PageResponseDTO<MenuOptionDTO> responseDTO = menuOptionService.list(menuOptionDTO,pageRequestDTO);
        model.addAttribute("hotel",hotelDTOList);
        model.addAttribute("list",responseDTO);
        return "manager/menu/option/list";
    }

    @Operation(summary = "메뉴 옵션 등록폼",
            description = "로그인한 계정의 호텔 목록 값을 보내고 호텔 등록 페이지로 이동")
    @GetMapping("/manager/menu/option/register")
    public String managerMenuOptionRegister(Model model,
                                            Principal principal){
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        model.addAttribute("storeList", hotelDTOList);
        return "manager/menu/option/register";
    }

    @Operation(summary = "메뉴 옵션 등록 처리",
            description = "메뉴 옵션을 등록 후 메뉴 옵션 목록 페이지로 이동")
    @PostMapping("/manager/menu/option/register")
    public String registerProc(@Valid MenuOptionDTO menuOptionDTO) {

        menuOptionService.register(menuOptionDTO);
        return "redirect:/manager/menu/option/list";
    }

    @Operation(summary = "메뉴 옵션 수정 폼",
            description = "수정 할 메뉴 정보와 메뉴옵션 목록의 값을 보낸 후 메뉴 옵션 수정 페이지로 이동")
    @GetMapping("/manager/menu/option/update/{menuOptionNo}")
    public String managerMenuOptionUpdate(Model model,
                                          PageRequestDTO pageRequestDTO,
                                          @PathVariable("menuOptionNo") Long menuOptionNo) {
        MenuOptionDTO menuOptionDTO = menuOptionService.read(menuOptionNo);
        PageResponseDTO<MenuOptionDTO> responseDTO = menuOptionService.list(menuOptionDTO,pageRequestDTO);
        model.addAttribute("menuOptionDTO", menuOptionDTO);
        model.addAttribute("responseDTO",responseDTO);
        return "manager/menu/option/update";
    }

    @Operation(summary = "메뉴 옵션 수정 처리",
            description = "메뉴 옵션 수정 후 메뉴 옵션 목록 페이지로 이동")
    @PostMapping("/manager/menu/option/update")
    public String managerMenuOptionUpdatePost(Model model,
                                              PageRequestDTO pageRequestDTO,
                                              @ModelAttribute MenuOptionDTO menuOptionDTO){

        menuOptionService.update(menuOptionDTO);

        return "redirect:/manager/menu/option/list";
    }

//    @Operation(summary = "메뉴 옵션 삭제폼",
//            description = "메뉴 옵션을 삭제 후 메뉴 목록 페이지로 이동")
//    @GetMapping("/manager/menu/option/delete/{menuOptionNo}")
//    public String deleteProc(@PathVariable("menuOptionNo") Long menuOptionNo) {
//        log.info("호텔 삭제 Proc 처리...");
//        menuOptionService.delete(menuOptionNo);
//
//        return "redirect:/manager/menu/option/list";
//    }
}
