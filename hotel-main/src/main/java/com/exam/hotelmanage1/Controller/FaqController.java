package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.FaqDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Service.AdminNoCheckService;
import com.exam.hotelmanage1.Service.AuthService;
import com.exam.hotelmanage1.Service.FaqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

/*================

작업명:FAQ관리crud
작업자:김민주
작업일:2024-04-12
작업내용:
===============*/

@Controller
@RequiredArgsConstructor
@Tag(name = "FAQ", description = "FAQ 관리 API")
@Log4j2
public class FaqController {

    private final FaqService faqService;
    private final HotelRepository hotelRepository;
    private final AuthService authService;
    private final AdminNoCheckService adminNoCheckService;


    //list조회
    @Operation(summary = "FAQ 목록",
            description = "FAQ 목록 정보를 보내고 FAQ 목록 페이지로 이동")
    @GetMapping("/manager/faq/list")
    public String faqListGet(FaqDTO faqDTO, PageRequestDTO pageRequestDTO,Principal principal,Model model) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);

        if(authService.isAdmin()){
            faqDTO.setAdminNo(null);
        }else {
            faqDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<FaqDTO> FaqDTOS = faqService.list(faqDTO,pageRequestDTO);
        model.addAttribute("faqList",FaqDTOS);

        return "manager/faq/list";
    }


    //insert등록
    @Operation(summary = "FAQ 등록폼",
            description = "호텔관리자 값을 보내고 FAQ 등록 페이지로 이동")
    @GetMapping("/manager/faq/register")
    public String faqRegiGet(Principal principal,Model model) {
        String userId = principal.getName(); //"정의"
        Long adminNo = hotelRepository.findCompanyNoByUsername(userId);

        model.addAttribute("FaqDTO",new FaqDTO());
        model.addAttribute("adminNo",adminNo);

        return "manager/faq/register";
    }

    @Operation(summary = "FAQ 등록 처리",
            description = "FAQ를 등록하고 FAQ 목록 페이지로 이동")
    @PostMapping("/manager/faq/register")
    public String faqRegiPost(FaqDTO FaqDTO){
        faqService.insert(FaqDTO);

        return "redirect:/manager/faq/list";
    }

    //read상세조회
    @Operation(summary = "FAQ 상세 정보",
            description = "조회하려는 FAQ의 정보값을 보내고 FAQ 상세 정보 페이지로 이동")
    @GetMapping("/manager/faq/{faqNo}")
    public String faqReadGet(@PathVariable Long faqNo,Model model) {

        FaqDTO FaqDTO = faqService.read(faqNo);
        model.addAttribute("FaqDTO",FaqDTO);

        return "manager/faq/read";
    }


    //update수정
    @Operation(summary = "FAQ 수정폼",
            description = "수정하려는 FAQ의 정보를 보내고 FAQ 수정 페이지로 이동")
    @GetMapping("/manager/faq/update/{faqNo}")
    public String faqUpdateGet(@PathVariable Long faqNo, Model model) {
        FaqDTO FaqDTO = faqService.read(faqNo);
        model.addAttribute("faqDTO",FaqDTO);

        return "manager/faq/update";
    }
    @Operation(summary = "FAQ 수정처리",
            description = "FAQ를 수정 후 FAQ 목록 페이지로 이동")
    @PostMapping("/manager/faq/update")
    public String faqUpdatePost(FaqDTO FaqDTO,Model model) {
        faqService.update(FaqDTO);
        return "redirect:/manager/faq/list";
    }



    //delete삭제
    @Operation(summary = "FAQ 삭제 처리",
            description = "FAQ를 삭제 후 FAQ 목록 페이지로 이동")
    @PostMapping("/manager/faq/delete")
    public String faqDelete(@RequestParam(value = "faqNo")Long faqNo, Model model) {
        faqService.delete(faqNo);
        return "redirect:/manager/faq/list";
    }


}
