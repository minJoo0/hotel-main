package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.CategoryEntity;
import com.exam.hotelmanage1.Repository.CategoryRepository;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Service.AdminNoCheckService;
import com.exam.hotelmanage1.Service.AuthService;
import com.exam.hotelmanage1.Service.CategoryService;
import com.exam.hotelmanage1.Service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Tag(name = "카테고리", description = "메뉴 카테고리 관리 API")
@Log4j2
public class CategoryController {
    private final CategoryService categoryService;
    private final HotelRepository hotelRepository;
    private final AuthService authService;
    private final MenuService menuService;
    private final AdminNoCheckService adminNoCheckService;

    @Operation(summary = "메뉴 카테고리 목록",
            description = "호텔 메뉴의 1차카테고리, 2차 카테고리 목록을 보여줌")
    @GetMapping("/manager/menu/category/list")
    public String managerMenuCategoryList(Model model, PageRequestDTO pageRequestDTO,
                                          CategoryDTO categoryDTO,Principal principal){
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            categoryDTO.setAdminNo(null);
        }else {
            categoryDTO.setAdminNo(adminNo);
        }
//        PageResponseDTO<CategoryDTO> responseDTO = categoryService.list(categoryDTO,pageRequestDTO);
//        PageResponseDTO<CategoryDTO> responseDTO2 = categoryService.list2(categoryDTO,pageRequestDTO);
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        List<HotelDTO> hotelDTOList2 = menuService.findHotelsByAdminNo2(adminNo);
//        model.addAttribute("list",responseDTO);
//        model.addAttribute("list2",responseDTO2);
        model.addAttribute("hotel",hotelDTOList);
        model.addAttribute("hotel2",hotelDTOList2);
        return "manager/menu/category/list";
    }

    @Operation(summary = "메뉴 카테고리 검색",
            description = "카테고리 목록에서 검색 과정을 처리")
    @PostMapping("/manager/menu/category/list")
    public String managerMenuCategoryListPost(){
        return "redirect:/manager/menu/category/list";
    }

    @Operation(summary = "메뉴 1차 카테고리 등록폼",
            description = "호텔의 목록을 보내고 카테고리 등록 페이지로 이동")
    @GetMapping("/manager/menu/category/register")
    public String managerMenuCategoryRegister(Model model, PageRequestDTO pageRequestDTO,
                                      CategoryDTO categoryDTO, Principal principal){
        String userId = principal.getName();
        Long adminNo = hotelRepository.findCompanyNoByUsername(userId);
        if(authService.isAdmin()){
            categoryDTO.setAdminNo(null);
        }else {
            categoryDTO.setAdminNo(adminNo);
        }
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        model.addAttribute("storeList", hotelDTOList);
        return "manager/menu/category/register";
    }

    @Operation(summary = "메뉴 1차 카테고리 등록 처리",
            description = "카테고리를 등록 후 카테고리 목록 페이지로 이동")
    @PostMapping("/manager/menu/category/register")
    public String managerMenuCategoryRegisterPost(CategoryDTO categoryDTO){
        categoryService.insert(categoryDTO);
        return "redirect:/manager/menu/category/list";
    }

    @Operation(summary = "메뉴 2차 카테고리 등록폼",
            description = "호텔의 목록을 보내고 카테고리 등록 페이지로 이동")
    @GetMapping("/manager/menu/category/register2")
    public String managerMenuCategory2Register(Model model, PageRequestDTO pageRequestDTO,
                                              CategoryDTO categoryDTO, Principal principal){
        String userId = principal.getName();
        Long adminNo = hotelRepository.findCompanyNoByUsername(userId);
        if(authService.isAdmin()){
            categoryDTO.setAdminNo(null);
        }else {
            categoryDTO.setAdminNo(adminNo);
        }
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        model.addAttribute("storeList", hotelDTOList);
        return "manager/menu/category/register2";
    }

    @Operation(summary = "메뉴 2차 카테고리 등록 처리",
            description = "카테고리를 등록 후 카테고리 목록 페이지로 이동")
    @PostMapping("/manager/menu/category/register2")
    public String managerMenuCategory2RegisterPost(CategoryDTO categoryDTO,@RequestParam("parentId") Long parentId){
        categoryDTO.setParentId(parentId);
        categoryService.insert(categoryDTO);
        return "redirect:/manager/menu/category/list";
    }


    @Operation(summary = "메뉴 카테고리 삭제",
            description = "카테고리를 삭제함")
    @DeleteMapping("/manager/menu/category/delete/{categoryNo}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryNo) {
        categoryService.delete(categoryNo);
        return ResponseEntity.ok().build();
    }


}
