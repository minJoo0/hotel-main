package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.CategoryDTO;
import com.exam.hotelmanage1.DTO.MenuDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.Entity.CategoryEntity;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Tag(name = "Rest 매장 매니저", description = "매장 매니저 Rest API")
@RestController
public class RestStoreManagerController {

    private final CategoryService categoryService;
    private final AuthService authService;
    private final HotelRepository hotelRepository;
    private final MenuService menuService;
    private final AdminNoCheckService adminNoCheckService;
    private final MenuOptionService menuOptionService;

    @Operation(summary = "1차 카테고리 목록",
            description = "")
    @GetMapping("/api/store/categories")
    public ResponseEntity<PageResponseDTO<CategoryDTO>> getCategoryList(Principal principal, PageRequestDTO requestDTO, CategoryDTO categoryDTO) {
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        categoryDTO.setStoreNo(storeNo);
        PageResponseDTO<CategoryDTO> responseDTO = categoryService.list(categoryDTO,requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "2차 카테고리 목록",
            description = "")
    @GetMapping("/api/store/categories2")
    public ResponseEntity<PageResponseDTO<CategoryDTO>> getCategoryList2(Principal principal,PageRequestDTO requestDTO,CategoryDTO categoryDTO) {
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        categoryDTO.setStoreNo(storeNo);
        PageResponseDTO<CategoryDTO> responseDTO1 = categoryService.list2(categoryDTO,requestDTO);
        return ResponseEntity.ok(responseDTO1);
    }
    @Operation(summary = "검색 후 1차 카테고리 목록",
            description = "")
    @GetMapping("/api/store/list")
    public ResponseEntity<PageResponseDTO<CategoryDTO>> list(CategoryDTO categoryDTO, PageRequestDTO pageRequestDTO, Principal principal) {
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        categoryDTO.setStoreNo(storeNo);
        PageResponseDTO<CategoryDTO> pageResponseDTO = categoryService.list(categoryDTO, pageRequestDTO);
        return ResponseEntity.ok(pageResponseDTO);
    }
    @Operation(summary = "검색 후 2차 카테고리 목록",
            description = "")
    @GetMapping("/api/store/list2")
    public ResponseEntity<PageResponseDTO<CategoryDTO>> list2(CategoryDTO categoryDTO,PageRequestDTO pageRequestDTO,Principal principal) {
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        categoryDTO.setStoreNo(storeNo);
        PageResponseDTO<CategoryDTO> pageResponseDTO = categoryService.list2(categoryDTO, pageRequestDTO);
        return ResponseEntity.ok(pageResponseDTO);
    }
    @Operation(summary = "검색창 select 창 1차 카테고리 목록",
            description = "")
    @GetMapping("/api/store/category")
    @ResponseBody
    public List<CategoryDTO> getCategoryByStoreNo(Principal principal) {
        Long storeNo = adminNoCheckService.storeNocheck(principal);
        List<CategoryDTO> categoryDTOS = categoryService.findCategoryByStoreNoAndParentIsNull(storeNo);

        return categoryDTOS;
    }
    @Operation(summary = "검색창 select 창 2차 카테고리 목록",
            description = "")
    @GetMapping("/api/store/category3")
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
    @GetMapping("/api/store/category4")
    @ResponseBody
    public List<MenuDTO> findMenusByCategory2(@RequestParam("category2") Long categoryNo)  {
        List<MenuDTO> menuDTOS = menuService.findMenusByCategory2(categoryNo);
        return menuDTOS;
    }

    @Operation(summary = "메뉴 옵션 삭제",
            description = "메뉴 옵션을 삭제하고 결과를 반환한다.")
    @DeleteMapping("/store/menu/option/delete/{menuOptionNo}")
    public ResponseEntity<?> deleteMenuOption(@PathVariable("menuOptionNo") Long menuOptionNo) {
        log.info("메뉴 옵션 삭제 처리...");
        try {
            menuOptionService.delete(menuOptionNo);
            return ResponseEntity.ok().body("메뉴 옵션 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("메뉴 옵션 삭제 실패");
        }
    }
}
