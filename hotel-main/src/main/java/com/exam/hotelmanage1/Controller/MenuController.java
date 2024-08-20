package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Repository.MenuImgRepository;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Tag(name = "메뉴",description = "메뉴 관리 API")
@Log4j2
public class MenuController {

    private final MenuService menuService;
    private final HotelRepository hotelRepository;
    private final AuthService authService;
    private final StoreService storeService;
    private final HotelService hotelService;
    private final CategoryService categoryService;
    private final AdminNoCheckService adminNoCheckService;
    private final MenuImgService menuImgService;
    private final FTPService ftpService;
    private final MenuImgRepository menuImgRepository;

    @Operation(summary = "매장 메뉴 목록",
                description = "호텔 목록, 메뉴 목록 값을 보낸 후 메뉴 목록 페이지로 이동")
    @GetMapping("/manager/menu/list")
    public String managerMenuList(Model model,
                                  PageRequestDTO pageRequestDTO,
                                  MenuDTO menuDTO,
                                  Principal principal){
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        if(authService.isAdmin()){
            menuDTO.setAdminNo(null);
        }else {
            menuDTO.setAdminNo(adminNo);
        }

        PageResponseDTO<MenuDTO> responseDTO = menuService.list(menuDTO,pageRequestDTO);
        model.addAttribute("hotel",hotelDTOList);
        model.addAttribute("list",responseDTO);
        return "manager/menu/list";
    }

    @Operation(summary = "매장 메뉴 등록폼",
            description = "호텔의 매장 목록 값을 보낸 후 메뉴 등록 페이지로 이동")
    @GetMapping("/manager/menu/register")
    public String managerMenuRegister(Model model,
                                      PageRequestDTO pageRequestDTO,
                                      MenuDTO menuDTO,
                                      Principal principal){
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        model.addAttribute("storeList", hotelDTOList);
        return "manager/menu/register";
    }

    @Operation(summary = "매장 메뉴 등록 처리",
            description = "메뉴, 메뉴 이미지 등록 후 메뉴 목록 페이지로 이동")
    @PostMapping("/manager/menu/register")
    public String registerProc(@Valid MenuDTO menuDTO,
                               RedirectAttributes redirectAttributes,
                               @RequestParam("file")MultipartFile file)
            throws Exception
    {
        Long menuNo = menuService.register(menuDTO).getMenuNo();
        menuDTO.setMenuNo(menuNo);
        menuImgService.saveMenuImg(menuDTO,file);

        redirectAttributes.addFlashAttribute("result", menuNo);

        return "redirect:/manager/menu/list";
    }

    @Operation(summary = "매장 메뉴 수정폼",
            description = "수정 할 메뉴 정보, 메뉴 목록, 이미지url 값을 보낸 후 메뉴 수정 페이지로 이동")
    @GetMapping("/manager/menu/update/{menuNo}")
    public String managerMenuUpdate(Model model,
                                    PageRequestDTO pageRequestDTO,
                                    @PathVariable("menuNo") Long menuNo,
                                    @RequestParam("imageUrl") String imageUrl){
        MenuDTO menuDTO = menuService.read(menuNo);
        PageResponseDTO<MenuDTO> responseDTO = menuService.list(menuDTO,pageRequestDTO);
        model.addAttribute("menuDTO", menuDTO);
        model.addAttribute("responseDTO",responseDTO);
        model.addAttribute("imageUrl",imageUrl);

        return "manager/menu/update";
    }

    @Operation(summary = "매장 메뉴 수정 처리",
            description = "메뉴 정보 수정 처리 후 메뉴 목록 페이지로 이동")
    @PostMapping("/manager/menu/update")
    public String managerMenuUpdatePost(Model model,
                                        PageRequestDTO pageRequestDTO,
                                        @ModelAttribute MenuDTO menuDTO,
                                        @RequestParam(value = "file", required = false) MultipartFile file)
            throws Exception
    {
        List<MenuImgEntity> menuImgEntities = menuImgService.findByMenuEntityMenuNo(menuDTO.getMenuNo());
        if (file != null && !file.isEmpty()) {
            // 이미지 파일이 제공되었다면, 이미지 파일을 저장하고 관련 정보를 업데이트합니다.
            ftpService.deleteFiles(menuImgEntities);
            menuImgRepository.deleteAll(menuImgEntities);
            menuImgService.saveMenuImg(menuDTO,file);
        }

        menuService.update(menuDTO);

        return "redirect:/manager/menu/list";
    }

    @Operation(summary = "매장 메뉴 상세 정보 조회",
            description = "메뉴 상세 정보, 메뉴 이미지url의 값을 보낸 후 메뉴 상세 정보 조회 페이지로 이동")
    @GetMapping("/manager/menu/read/{menuNo}")
    public String readForm(@PathVariable(name = "menuNo") Long menuNo,
                           Model model) {

        MenuDTO menuDTO = menuService.read(menuNo);
        List<MenuImgEntity> menuImgList = menuImgService.findByMenuEntityMenuNo(menuNo);
        String imageUrl = "";
        if (!menuImgList.isEmpty()) {
            MenuImgEntity firstMenuImg = menuImgList.get(0);

                imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

        }
        log.info(imageUrl);

        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("data", menuDTO);
        return "manager/menu/read";
    }


    @Operation(summary = "호텔 매장 목록",
            description = "storeEntity 목록의 값을 dtoList로 변경")
    @GetMapping("/api/menus")
    @ResponseBody
    public List<StoreDTO> getHotelNameById(@RequestParam("hotelNo") Long hotelNo) {

        List<StoreEntity> storeEntities = storeService.findStoresByHotelNo(hotelNo);
        // 조회된 StoreEntity 목록을 StoreDTO 목록으로 변환합니다.
        List<StoreDTO> storeDTOs = storeEntities.stream().map(entity -> StoreDTO.builder()
                .storeNo(entity.getStoreNo())
                .name(entity.getName())
                .address(entity.getAddress())
                .tel(entity.getTel())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build()).collect(Collectors.toList());

        return storeDTOs;
    }

    @Operation(summary = "매장 메뉴 삭제 처리",
            description = "매장 메뉴를 삭제하고 메뉴 목록 페이지로 이동")
    @PostMapping("/manager/menu/delete/{menuNo}")
    public String deleteProc(@PathVariable("menuNo") Long menuNo
            , RedirectAttributes redirectAttributes) {
        log.info("호텔 삭제 Proc 처리...");
        menuService.delete(menuNo);
        redirectAttributes.addFlashAttribute("processMessage", "삭제하였습니다.");
        return "redirect:/manager/menu/list";
    }

    
}
