package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.MenuImgEntity;
import com.exam.hotelmanage1.Entity.StoreEntity;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Repository.MenuImgRepository;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
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
public class StoreManagerController {



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
    private final MenuOptionService menuOptionService;
    private final RevenueService revenueService;
    private final OrderDetailService orderDetailService;

    @ModelAttribute("paymentStatus")
    public PaymentStatus[] paymentStatus(){
        return PaymentStatus.values();
    }




    @Operation(summary = "매장 메인페이지",
            description = "매장 메인페이지로 이동")
    @GetMapping("/store/main")
    public String storeMain(Model model){
        return "store/main";
    }



    @Operation(summary = "매장 메뉴 목록",
            description = "호텔 목록, 메뉴 목록 값을 보낸 후 메뉴 목록 페이지로 이동")
    @GetMapping("/store/menu/list")
    public String managerMenuList(Model model,
                                  PageRequestDTO pageRequestDTO,
                                  MenuDTO menuDTO,
                                  Principal principal){
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        List<CategoryDTO> categoryDTOList = categoryService.findCategoryByStoreNo(storeNo);
        model.addAttribute("categoryDTOList", categoryDTOList);
        menuDTO.setStoreNo(storeNo);
        PageResponseDTO<MenuDTO> responseDTO = menuService.list(menuDTO,pageRequestDTO);
        model.addAttribute("list",responseDTO);
        return "store/menu/list";
    }

    @Operation(summary = "매장 메뉴 등록폼",
            description = "호텔의 매장 목록 값을 보낸 후 메뉴 등록 페이지로 이동")
    @GetMapping("/store/menu/register")
    public String managerMenuRegister(Model model,
                                      PageRequestDTO pageRequestDTO,
                                      MenuDTO menuDTO,
                                      Principal principal){
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        List<CategoryDTO> categoryDTOList = categoryService.findCategoryByStoreNo(storeNo);
        model.addAttribute("categoryDTOList", categoryDTOList);
        return "store/menu/register";
    }

    @Operation(summary = "매장 메뉴 등록 처리",
            description = "메뉴, 메뉴 이미지 등록 후 메뉴 목록 페이지로 이동")
    @PostMapping("/store/menu/register")
    public String registerProc(@Valid MenuDTO menuDTO,
                               RedirectAttributes redirectAttributes,
                               @RequestParam("file") MultipartFile file,
                               Principal principal)
            throws Exception {
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        menuDTO.setStoreNo(storeNo);
        Long menuNo = menuService.register(menuDTO).getMenuNo();
        menuDTO.setMenuNo(menuNo);
        menuImgService.saveMenuImg(menuDTO,file);

        redirectAttributes.addFlashAttribute("result", menuNo);

        return "redirect:/store/menu/list";
    }

    @Operation(summary = "매장 메뉴 수정폼",
            description = "수정 할 메뉴 정보, 메뉴 목록, 이미지url 값을 보낸 후 메뉴 수정 페이지로 이동")
    @GetMapping("/store/menu/update/{menuNo}")
    public String managerMenuUpdate(Model model,
                                    PageRequestDTO pageRequestDTO,
                                    @PathVariable("menuNo") Long menuNo,
                                    @RequestParam("imageUrl") String imageUrl,
                                    Principal principal){
        MenuDTO menuDTO = menuService.read(menuNo);
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        List<CategoryDTO> categoryDTOList = categoryService.findCategoryByStoreNo(storeNo);
        model.addAttribute("categoryDTOList", categoryDTOList);
        model.addAttribute("menuDTO", menuDTO);
        model.addAttribute("imageUrl",imageUrl);

        return "store/menu/update";
    }

    @Operation(summary = "매장 메뉴 수정 처리",
            description = "메뉴 정보 수정 처리 후 메뉴 목록 페이지로 이동")
    @PostMapping("/store/menu/update")
    public String managerMenuUpdatePost(Model model,
                                        PageRequestDTO pageRequestDTO,
                                        @ModelAttribute MenuDTO menuDTO,
                                        @RequestParam(value = "file", required = false) MultipartFile file,
                                        Principal principal)
            throws Exception
    {
        List<MenuImgEntity> menuImgEntities = menuImgService.findByMenuEntityMenuNo(menuDTO.getMenuNo());
        if (file != null && !file.isEmpty()) {
            // 이미지 파일이 제공되었다면, 이미지 파일을 저장하고 관련 정보를 업데이트합니다.
            ftpService.deleteFiles(menuImgEntities);
            menuImgRepository.deleteAll(menuImgEntities);
            menuImgService.saveMenuImg(menuDTO,file);
        }
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        menuDTO.setStoreNo(storeNo);
        menuService.update(menuDTO);

        return "redirect:/store/menu/list";
    }

    @Operation(summary = "매장 메뉴 상세 정보 조회",
            description = "메뉴 상세 정보, 메뉴 이미지url의 값을 보낸 후 메뉴 상세 정보 조회 페이지로 이동")
    @GetMapping("/store/menu/read/{menuNo}")
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
        return "store/menu/read";
    }

    

    @Operation(summary = "매장 메뉴 삭제 처리",
            description = "매장 메뉴를 삭제하고 메뉴 목록 페이지로 이동")
    @PostMapping("/store/menu/delete/{menuNo}")
    public String deleteProc(@PathVariable("menuNo") Long menuNo
            , RedirectAttributes redirectAttributes) {
        log.info("호텔 삭제 Proc 처리...");
        menuService.delete(menuNo);
        redirectAttributes.addFlashAttribute("processMessage", "삭제하였습니다.");
        return "redirect:/store/menu/list";
    }


    @Operation(summary = "메뉴 옵션 목록",
            description = "호텔 목록, 메뉴 옵션 목록 값을 보내고 메뉴 옵션 리스트 페이지로 이동")
    @GetMapping("/store/menu/option/list")
    public String managerMenuOptionList(Model model,
                                        PageRequestDTO pageRequestDTO,
                                        MenuOptionDTO menuOptionDTO,
                                        Principal principal){
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        menuOptionDTO.setStoreNo(storeNo);
        List<CategoryDTO> categoryDTOList = categoryService.findCategoryByStoreNo(storeNo);
        model.addAttribute("categoryDTOList", categoryDTOList);
        PageResponseDTO<MenuOptionDTO> responseDTO = menuOptionService.list(menuOptionDTO,pageRequestDTO);
        model.addAttribute("list",responseDTO);
        return "store/menu/option/list";
    }

    @Operation(summary = "메뉴 옵션 등록폼",
            description = "로그인한 계정의 호텔 목록 값을 보내고 호텔 등록 페이지로 이동")
    @GetMapping("/store/menu/option/register")
    public String managerMenuOptionRegister(Model model,
                                            Principal principal){
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        List<CategoryDTO> categoryDTOList = categoryService.findCategoryByStoreNo(storeNo);
        model.addAttribute("categoryDTOList", categoryDTOList);
        return "store/menu/option/register";
    }

    @Operation(summary = "메뉴 옵션 등록 처리",
            description = "메뉴 옵션을 등록 후 메뉴 옵션 목록 페이지로 이동")
    @PostMapping("/store/menu/option/register")
    public String registerProc(@Valid MenuOptionDTO menuOptionDTO,
                               Principal principal) {
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        menuOptionDTO.setStoreNo(storeNo);
        menuOptionService.register(menuOptionDTO);
        return "redirect:/store/menu/option/list";
    }

    @Operation(summary = "메뉴 옵션 수정 폼",
            description = "수정 할 메뉴 정보와 메뉴옵션 목록의 값을 보낸 후 메뉴 옵션 수정 페이지로 이동")
    @GetMapping("/store/menu/option/update/{menuOptionNo}")
    public String managerMenuOptionUpdate(Model model,
                                          PageRequestDTO pageRequestDTO,
                                          @PathVariable("menuOptionNo") Long menuOptionNo) {
        MenuOptionDTO menuOptionDTO = menuOptionService.read(menuOptionNo);
        PageResponseDTO<MenuOptionDTO> responseDTO = menuOptionService.list(menuOptionDTO,pageRequestDTO);
        model.addAttribute("menuOptionDTO", menuOptionDTO);
        model.addAttribute("responseDTO",responseDTO);
        return "store/menu/option/update";
    }

    @Operation(summary = "메뉴 옵션 수정 처리",
            description = "메뉴 옵션 수정 후 메뉴 옵션 목록 페이지로 이동")
    @PostMapping("/store/menu/option/update")
    public String managerMenuOptionUpdatePost(Model model,
                                              PageRequestDTO pageRequestDTO,
                                              @ModelAttribute MenuOptionDTO menuOptionDTO){

        menuOptionService.update(menuOptionDTO);

        return "redirect:/store/menu/option/list";
    }

    @Operation(summary = "메뉴 카테고리 목록",
            description = "호텔 메뉴의 1차카테고리, 2차 카테고리 목록을 보여줌")
    @GetMapping("/store/menu/category/list")
    public String managerMenuCategoryList(Model model, PageRequestDTO pageRequestDTO,
                                          CategoryDTO categoryDTO,Principal principal){

        Long storeNo = adminNoCheckService.storeNocheck(principal);
        categoryDTO.setStoreNo(storeNo);
        List<CategoryDTO> categoryDTOList = categoryService.findCategoryByStoreNoAndParentIsNull(storeNo);
        model.addAttribute("categoryDTOList",categoryDTOList);

        return "store/menu/category/list";
    }

    @Operation(summary = "메뉴 카테고리 검색",
            description = "카테고리 목록에서 검색 과정을 처리")
    @PostMapping("/store/menu/category/list")
    public String managerMenuCategoryListPost(){
        return "redirect:/store/menu/category/list";
    }

    @Operation(summary = "메뉴 1차 카테고리 등록폼",
            description = "호텔의 목록을 보내고 카테고리 등록 페이지로 이동")
    @GetMapping("/store/menu/category/register")
    public String managerMenuCategoryRegister(Model model, PageRequestDTO pageRequestDTO,
                                              CategoryDTO categoryDTO, Principal principal){
        return "store/menu/category/register";
    }

    @Operation(summary = "메뉴 1차 카테고리 등록 처리",
            description = "카테고리를 등록 후 카테고리 목록 페이지로 이동")
    @PostMapping("/store/menu/category/register")
    public String managerMenuCategoryRegisterPost(CategoryDTO categoryDTO,Principal principal){
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        categoryDTO.setStoreNo(storeNo);
        categoryService.insert(categoryDTO);
        return "redirect:/store/menu/category/list";
    }

    @Operation(summary = "메뉴 2차 카테고리 등록폼",
            description = "호텔의 목록을 보내고 카테고리 등록 페이지로 이동")
    @GetMapping("/store/menu/category/register2")
    public String managerMenuCategory2Register(Model model, PageRequestDTO pageRequestDTO,
                                               CategoryDTO categoryDTO, Principal principal){
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        categoryDTO.setStoreNo(storeNo);
        List<CategoryDTO> categoryDTOList = categoryService.findCategoryByStoreNoAndParentIsNull(storeNo);
        model.addAttribute("categoryDTOList",categoryDTOList);
        return "store/menu/category/register2";
    }

    @Operation(summary = "메뉴 2차 카테고리 등록 처리",
            description = "카테고리를 등록 후 카테고리 목록 페이지로 이동")
    @PostMapping("/store/menu/category/register2")
    public String managerMenuCategory2RegisterPost(CategoryDTO categoryDTO,@RequestParam("parentId") Long parentId,Principal principal){
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        categoryDTO.setStoreNo(storeNo);
        categoryDTO.setParentId(parentId);
        categoryService.insert(categoryDTO);
        return "redirect:/store/menu/category/list";
    }


    @Operation(summary = "메뉴 카테고리 삭제",
            description = "카테고리를 삭제함")
    @DeleteMapping("/store/menu/category/delete/{categoryNo}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryNo) {
        categoryService.delete(categoryNo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "정산 목록",
            description = "호텔목록, 정산목록을 보낸 후 정산 목록 페이지로 이동")
    @GetMapping("/store/revenue/list")
    public String revenueItemList(Model model,
                                  RevenueDTO revenueDTO,
                                  Principal principal,
                                  PageRequestDTO pageRequestDTO) {
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        revenueDTO.setStoreNo(storeNo);
        PageResponseDTO<RevenueDTO> revenueDTO1 = revenueService.PageList(revenueDTO,pageRequestDTO);
        model.addAttribute("revenueList",revenueDTO1);
        return "/store/revenue/list";
    }


    @Operation(summary = "주문 상세 내역",
            description = "결제 내역 페이지로 이동")
    @GetMapping("/store/order/list")
    public String orderListGet(Model model,
                               PageRequestDTO pageRequestDTO,
                               PaymentDTO paymentDTO,
                               Principal principal){
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        paymentDTO.setStoreNo(storeNo);
        PageResponseDTO<PaymentDTO> paymentDTOS = orderDetailService.paymentItemListAdmin(paymentDTO,pageRequestDTO);
        model.addAttribute("paymentList",paymentDTOS);
        return "store/order/list";
    }


}
