package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.NoticeDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Service.AdminNoCheckService;
import com.exam.hotelmanage1.Service.AuthService;
import com.exam.hotelmanage1.Service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/*================

작업명:공지사항관리crud
작업자:김민주
작업일:2024-04-09
작업내용:
crud 정상동작0409
페이징,검색0412

===============*/

@Controller
@RequiredArgsConstructor
@Tag(name = "공지사항", description = "공지사항 관리 API")
@Log4j2
public class NoticeController {

    private final NoticeService noticeService;
    private final HotelRepository hotelRepository;
    private final AuthService authService;
    private final AdminNoCheckService adminNoCheckService;


    //list조회
    @Operation(summary = "공지사항 목록",
            description = "공지사항 목록 값을 보낸 후 공지사항 목록 페이지로 이동")
    @GetMapping("/manager/notice/list")
    public String noticeListGet(NoticeDTO noticeDTO,
                                PageRequestDTO pageRequestDTO,
                                Principal principal,
                                Model model) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            noticeDTO.setAdminNo(null);
        }else {
            noticeDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<NoticeDTO> noticeDTOS = noticeService.list(noticeDTO,pageRequestDTO);
        model.addAttribute("noticeList",noticeDTOS);

        return "manager/notice/list";
    }


    //insert등록
    @Operation(summary = "공지사항 등록폼",
            description = "로그인 한 회사의 No값, 공지사항 객체를 보낸 후 공지사항 등록 페이지로 이동")
    @GetMapping("/manager/notice/register")
    public String noticeRegiGet(Principal principal,Model model) {
        String userId = principal.getName(); //"정의"
        Long adminNo = hotelRepository.findCompanyNoByUsername(userId);

        model.addAttribute("noticeDTO",new NoticeDTO());
        model.addAttribute("adminNo",adminNo);

        return "manager/notice/register";
    }

    @Operation(summary = "공지사항 등록처리",
            description = "공지사항 등록 후 공지사항 목록 페이지로 이동")
    @PostMapping("/manager/notice/register")
    public String noticeRegiPost(NoticeDTO noticeDTO){
        noticeService.insert(noticeDTO);
        return "redirect:/manager/notice/list";
    }

    //read상세조회
    @Operation(summary = "공지사항 상세정보",
            description = "공지사항 값을 보낸 후 공지 사항 상세 정보 페이지로 이동")
    @GetMapping("/manager/notice/{noticeNo}")
    public String noticeReadGet(@PathVariable Long noticeNo,Model model) {

        NoticeDTO noticeDTO = noticeService.read(noticeNo);
        model.addAttribute("noticeDTO",noticeDTO);

        return "manager/notice/read";
    }


    //update수정
    @Operation(summary = "공지사항 수정폼",
            description = "수정할 공지사항,공지사항 목록 값을 보낸 후 공지사항 수정 페이지로 이동")
    @GetMapping("/manager/notice/update/{noticeNo}")
    public String noticeUpdateGet(@PathVariable Long noticeNo,
                                  PageRequestDTO pageRequestDTO,
                                  Model model) {
        NoticeDTO noticeDTO = noticeService.read(noticeNo);
        PageResponseDTO<NoticeDTO> pageResponseDTO = noticeService.list(noticeDTO,pageRequestDTO);
        model.addAttribute("noticeDTO",noticeDTO);
        model.addAttribute("pageResponseDTO",pageResponseDTO);

        return "manager/notice/update";
    }

    @Operation(summary = "공지사항 수정 처리",
            description = "공지사항 수정 후 공지하상 목록 페이지로 이동")
    @PostMapping("/manager/notice/update")
    public String noticeUpdatePost(@ModelAttribute NoticeDTO noticeDTO, Model model) {
        noticeService.update(noticeDTO);
        return "redirect:/manager/notice/list";
    }



    //delete삭제
    @Operation(summary = "공지사항 삭제처리",
            description = "공지사항 삭제 후 공지사항 목록 페이지로 이동")
    @PostMapping("/manager/notice/delete")
    public String noticeDelete(@RequestParam(value = "noticeNo")Long noticeNo, RedirectAttributes redirectAttributes, Model model) {
        noticeService.delete(noticeNo);
        redirectAttributes.addFlashAttribute("successMessage",
                "삭제하였습니다.");

        return "redirect:/manager/notice/list";
    }


}
