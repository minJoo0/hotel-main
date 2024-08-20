package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.CodeDTO;
import com.exam.hotelmanage1.DTO.CodeGroupDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/*
프그램명 : 하위코드 CRUD
작성자 : 김준수
기능 : 코드 controller
작업내용 :
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "하위코드", description = "하위코드 관리 API")
@Log4j2
public class CodeController {
    private final CodeService codeService;
    private final CodeGroupService codeGroupService;
    private final AdminNoCheckService adminNoCheckService;
    private final AuthService authService;

    //코드 등륵
    @Operation(summary = "하위 코드 등록 폼",
            description = "코드그룹 목록 값을 보내고 등록페이지로 이동")
    @GetMapping("manager/code/lowcode/register")
    public String registerForm(Model model
    ,Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        List<CodeGroupDTO> codeGroupDTOS = codeGroupService.getCodeGroupsByAdminNo(adminNo);

        log.info("코드 리스트" + codeGroupDTOS);
        model.addAttribute("list", codeGroupDTOS);

        return "manager/code/lowcode/register";
    }

    @Operation(summary = "하위 코드 등록 처리",
            description = "하위 코드를 등록하고 하위코드 목록페이지로 이동")
    @PostMapping("/manager/code/lowcode/register")
    public String registerProc(@ModelAttribute CodeDTO codeDTO,
                               RedirectAttributes redirectAttributes) {
        String codeGroup = codeGroupService.findCodeGroupByCodeGroupNo(codeDTO.getCodeGroupNo());
        codeDTO.setCodeGroup(codeGroup);
        codeDTO.setFullCode(codeGroup + codeDTO.getCode());
        log.info(codeDTO);
        codeService.register(codeDTO);

        return "redirect:/manager/code/lowcode/list";
    }


    //코드 수정
    @Operation(summary = "하위 코드 수정 폼",
            description = "수정하려는 하위코드를 조회하고 하위코드의 정보를 수정페이지로 보냄, 수정하려는 하위코드가 없으면 하위코드 목록페이지로 이동")
    @GetMapping("/manager/code/lowcode/update/{codeNo}")
    public String updateForm(@PathVariable Long codeNo,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Principal principal) {

        Long adminNo = adminNoCheckService.adminNocheck(principal);
        //값이 있는지 조회
        CodeDTO codeDTO = codeService.read(codeNo);
        //값을 dto로 변환해 전달
        model.addAttribute("codeDTO", codeDTO);

        List<CodeGroupDTO> codeGroupDTOS = codeGroupService.getCodeGroupsByAdminNo(adminNo);
        model.addAttribute("list", codeGroupDTOS);

        //값이 존재하지 않으면
        if (codeDTO == null) {
            redirectAttributes.addFlashAttribute("processMessage", "자료를 읽기 실패하였습니다.");
            return "redirect:manager/code/lowcode/list";
        }
        return "manager/code/lowcode/update";
    }
    @Operation(summary = "하위 코드 수정 처리",
            description = "수정 성공/실패 시 메세지를 띄우고 하위코드 목록 페이지로 이동")
    @PostMapping("/manager/code/lowcode/update")
    public String updateProc(@ModelAttribute CodeDTO codeDTO,
                             RedirectAttributes redirectAttributes) {

        if (codeService.update(codeDTO) != null) {
            redirectAttributes.addFlashAttribute("processMessage", "수정하였습니다.");
        } else  {
            redirectAttributes.addFlashAttribute("processMessage", "수정을 실패하였습니다.");
        }

        return "redirect:/manager/code/lowcode/list";
    }

    //코드 삭제
    @Operation(summary = "하위 코드 삭제 처리",
            description = "하위 코드를 삭제하고 하위코드 목록페이지로 이동")
    @PostMapping("/manager/code/lowcode/delete")
    public String deleteForm(@RequestParam(value = "codeNo") Long codeNo) {
        log.info(codeNo);
        codeService.delete(codeNo);
        return "redirect:/manager/code/lowcode/list";
    }

    //코드 상세보기
    @Operation(summary = "하위 코드 상세 정보",
            description = "하위 코드 상세 정보 페이지로 이동")
    @GetMapping("/manager/code/lowcode/read/{codeNo}")
    public String readForm(@PathVariable Long codeNo,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        //있는지 조회
        CodeDTO codeDTO = codeService.read(codeNo);
        model.addAttribute("data", codeDTO);

        if (codeDTO == null) {  //존재하지 않는다면
            model.addAttribute("processMessage", "존재하지 않는 자료입니다");
            return "redirect:/manager/code/lowcode/list";
        }

        return "manager/code/lowcode/read";
    }

//    코드 목록 전체 조회
//    @GetMapping("/admin/lowcode/list")
//    public String listForm(Model model) {
//        List<CodeDTO> codeDTOS = codeService.list();
//        model.addAttribute("list", codeDTOS);
//
//
//        return "admin/lowcode/list";
//    }


    //코드 목록 +페이징 + 검색

    @Operation(summary = "하위 코드 목록",
            description = "하위 코드 페이징+검색 처리가 된 목록과 코드 그룹 값을 하위코드 목록페이지로 보낸 후 하위코드 목록페이지로 이동")
    @GetMapping("/manager/code/lowcode/list")
    public String pageForm(CodeDTO codeDTO, Principal principal,
                           PageRequestDTO pageRequestDTO,
                           Model model) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        log.info("adminNo" + adminNo);
        if(authService.isAdmin()){
            codeDTO.setAdminNo(null);
        }else {
            codeDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<CodeDTO> responseDTO = codeService.PageList(codeDTO, pageRequestDTO);

        model.addAttribute("list", responseDTO);

        //코드 그룹의 리스트
        List<CodeGroupDTO> codeGroupDTOS = codeGroupService.getCodeGroupsByAdminNo(adminNo);
        model.addAttribute("group", codeGroupDTOS);


        return "manager/code/lowcode/list";
    }
}
