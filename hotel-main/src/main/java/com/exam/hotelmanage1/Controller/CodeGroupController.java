package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.CodeGroupDTO;
import com.exam.hotelmanage1.DTO.HotelDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.Entity.CodeGroupEntity;
import com.exam.hotelmanage1.Service.AdminNoCheckService;
import com.exam.hotelmanage1.Service.AuthService;
import com.exam.hotelmanage1.Service.CodeGroupService;
import com.exam.hotelmanage1.Service.CodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
프그램명 : 분류 코드 그룹 CRUD
작성자 : 김준수
기능 : 코드그룹 controller
작업내용 :
 */

@Controller
@RequiredArgsConstructor
@Tag(name = "코드 그룹", description = "코드 그룹 관리 API")
@Log4j2
public class CodeGroupController {
    private final CodeGroupService codeGroupService;
    private final CodeService codeService;
    private final AdminNoCheckService adminNoCheckService;
    private final AuthService authService;

    //코드 그룹 등록
    @Operation(summary = "코드그룹 등록폼",
            description = "각 호텔의 관리 코드그룹 등록페이지로 이동")
    @GetMapping("/manager/code/register")
    public String registerForm(Model model) {
        model.addAttribute("CodeGroupDTO", new CodeGroupDTO());

        return "manager/code/register";
    }

    @Operation(summary = "코드그룹 등록처리",
            description = "코드그룹 등록 후 코드그룹 목록페이지로 이동")
    @PostMapping("/manager/code/register")
    public String registerProc(@Validated CodeGroupDTO codeGroupDTO,
                               BindingResult bindingResult,Principal principal) {

        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if (bindingResult.hasErrors()) {
            return "manager/code/register";
        }
        codeGroupDTO.setAdminNo(adminNo);
        codeGroupService.register(codeGroupDTO);
        log.info(codeGroupDTO);

        return "redirect:/manager/code/list";
    }


    //코드 그룹 수정
    @Operation(summary = "코드그룹 수정폼",
            description = "수정 하려는 코드그룹 정보를 코드그룹 수정페이지로 보낸 후 이동")
    @GetMapping("/manager/code/update/{codeGroupNo}")
    public String updateForm(@PathVariable Long codeGroupNo, Model model) {
        CodeGroupDTO codeGroupDTO = codeGroupService.read(codeGroupNo);
        model.addAttribute("codeGroupDTO", codeGroupDTO);
        return "manager/code/update";
    }

    @Operation(summary = "코드그룹 수정 처리",
            description = "에러가 발생하면 코드그룹 수정 페이지로 이동, 에러가 발생하지 않으면 코드그룹 정보를 수정")
    @PostMapping("/manager/code/update")
    public String updateProc(@Validated CodeGroupDTO codeGroupDTO,
                             BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "/manager/code/update";
        }
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        codeGroupDTO.setAdminNo(adminNo);
        codeGroupService.update(codeGroupDTO);
        return "redirect:/manager/code/list";
    }

    @Operation(summary = "코드 그룹 삭제 처리",
            description = "선택한 코드그룹을 삭제 후 코드 목록 페이지로 이동")
    //코드 그룹 삭제
    @PostMapping("/manager/code/delete")
    public String deleteForm(@RequestParam(value = "codeGroupNo") Long codeGroupNo) {
        codeGroupService.delete(codeGroupNo);
        return "redirect:/manager/code/list";
    }


    //코드 그룹 정보
    @Operation(summary = "코드그룹 상세 정보",
            description = "선택한 코드그룹 상세 정보 페이지로 이동")
    @GetMapping("/manager/code/read/{id}")
    public String readForm(@PathVariable Long id, Model model) {
        CodeGroupDTO codeGroupDTO = codeGroupService.read(id);
        model.addAttribute("codeGroupDTO", codeGroupDTO);
        return "manager/code/read";
    }


    //코드 그룹 목록
//    @GetMapping("/admin/code/list")
//    public String listForm(Model model) {
//        List<CodeGroupDTO> codeGroupDTOS = codeGroupService.codeGroupDTOList();
//        model.addAttribute("list", codeGroupDTOS);
//
//
//        // 코드그룹당 코드의 수를 가져와 모델에 추가
//        List<Object[]> codeCountsPerGroup = codeGroupService.countCodesPerGroup();
//        Map<Long, Long> codeCountsMap = new HashMap<>();
//        for (Object[] result : codeCountsPerGroup) {
//            Long groupId = (Long) result[0];
//            Long codeCount = (Long) result[1];
//            codeCountsMap.put(groupId, codeCount);
//        }
//        model.addAttribute("codeCountsMap", codeCountsMap);
//
//        return "admin/code/list";
//    }



    //코드 그룹 목록 (페이징+검색)
    @Operation(summary = "코드 그룹 목록",
            description = "페이징+검색 조건 처리 된 코드 그룹 목록 페이지로 이동")
    @GetMapping("manager/code/list")
    public String listForm(Model model,
                           CodeGroupDTO codeGroupDTO,
                           PageRequestDTO pageRequestDTO,
                           CodeGroupEntity codeGroupEntity,
                           Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            codeGroupDTO.setAdminNo(null);
        }else {
            codeGroupDTO.setAdminNo(adminNo);
        }

        PageResponseDTO<CodeGroupDTO> CodeGroupDTOs = codeGroupService.list(codeGroupDTO, pageRequestDTO);
        model.addAttribute("CodeGroupDTOs", CodeGroupDTOs);
        // 코드그룹당 코드의 수를 가져와 모델에 추가
        Long codeCount = codeGroupService.countByCodeGroupEntityCodeGroupNo(codeGroupEntity);

        log.info("codecount" +codeCount );

        model.addAttribute("codeCount", codeCount);

        return "manager/code/list";
    }

}
