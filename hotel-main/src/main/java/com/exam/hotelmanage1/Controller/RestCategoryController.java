package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.CategoryEntity;
import com.exam.hotelmanage1.Entity.MenuEntity;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Service.AdminNoCheckService;
import com.exam.hotelmanage1.Service.AuthService;
import com.exam.hotelmanage1.Service.CategoryService;
import com.exam.hotelmanage1.Service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Rest 카테고리", description = "카테고리 Rest API")
@Log4j2
@RestController
public class RestCategoryController {

    private final CategoryService categoryService;
    private final AuthService authService;
    private final HotelRepository hotelRepository;
    private final MenuService menuService;
    private final AdminNoCheckService adminNoCheckService;

    @Operation(summary = "1차 카테고리 목록",
            description = "")
    @GetMapping("/categories")
    public ResponseEntity<PageResponseDTO<CategoryDTO>> getCategoryList(Principal principal,PageRequestDTO requestDTO,CategoryDTO categoryDTO) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            categoryDTO.setAdminNo(null);
        }else {
            categoryDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<CategoryDTO> responseDTO = categoryService.list(categoryDTO,requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "2차 카테고리 목록",
            description = "")
    @GetMapping("/categories2")
    public ResponseEntity<PageResponseDTO<CategoryDTO>> getCategoryList2(Principal principal,PageRequestDTO requestDTO,CategoryDTO categoryDTO) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            categoryDTO.setAdminNo(null);
        }else {
            categoryDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<CategoryDTO> responseDTO1 = categoryService.list2(categoryDTO,requestDTO);
        return ResponseEntity.ok(responseDTO1);
    }
    @Operation(summary = "검색 후 1차 카테고리 목록",
            description = "")
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<CategoryDTO>> list(CategoryDTO categoryDTO, PageRequestDTO pageRequestDTO, Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            categoryDTO.setAdminNo(null);
        }else {
            categoryDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<CategoryDTO> pageResponseDTO = categoryService.list(categoryDTO, pageRequestDTO);
        return ResponseEntity.ok(pageResponseDTO);
    }
    @Operation(summary = "검색 후 2차 카테고리 목록",
            description = "")
    @GetMapping("/list2")
    public ResponseEntity<PageResponseDTO<CategoryDTO>> list2(CategoryDTO categoryDTO,Long hotelNo,
                                                              Long storeNo, PageRequestDTO pageRequestDTO,Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            categoryDTO.setAdminNo(null);
        }else {
            categoryDTO.setAdminNo(adminNo);
        }
        categoryDTO.setHotelNo(hotelNo);
        categoryDTO.setStoreNo(storeNo);
        PageResponseDTO<CategoryDTO> pageResponseDTO = categoryService.list2(categoryDTO, pageRequestDTO);
        return ResponseEntity.ok(pageResponseDTO);
    }
    @Operation(summary = "검색창 select 창 1차 카테고리 목록",
            description = "")
    @GetMapping("/category")
    @ResponseBody
    public List<CategoryDTO> getCategoryByStoreNo(@RequestParam("storeNo") Long storeNo) {
        log.info("hotelNaneStr~~~~~" + storeNo);
        List<CategoryDTO> categoryDTOS = categoryService.findCategoryByStoreNoAndParentIsNull(storeNo);
        return categoryDTOS;
    }
    @Operation(summary = "검색창 select 창 2차 카테고리 목록",
            description = "")
    @GetMapping("/category3")
    @ResponseBody
    public List<CategoryDTO> findByParentCategoryNo(@RequestParam("category1") Long categoryNo)  {
        List<CategoryEntity> categoryEntities = categoryService.findByParentCategoryNo(categoryNo);

        // 조회된 StoreEntity 목록을 StoreDTO 목록으로 변환합니다.
        List<CategoryDTO> categoryDTOS = categoryEntities.stream().map(entity -> CategoryDTO.builder()
                .categoryNo(entity.getCategoryNo())
                .storeNo(entity.getStoreEntity().getStoreNo())
                .name(entity.getName())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build()).collect(Collectors.toList());
        return categoryDTOS;
    }

    @Operation(summary = "메뉴 옵션 생성창 2차 카테고리 선택 후 메뉴 선택 API",
            description = "")
    @GetMapping("/category4")
    @ResponseBody
    public List<MenuDTO> findMenusByCategory2(@RequestParam("category2") Long categoryNo)  {
        List<MenuDTO> menuDTOS = menuService.findMenusByCategory2(categoryNo);
        return menuDTOS;
    }
}
