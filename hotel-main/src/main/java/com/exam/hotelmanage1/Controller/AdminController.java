package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.DTO.AdminDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.DTO.UserDTO;
import com.exam.hotelmanage1.Service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log4j2
@Tag(name = "ADMIN", description = "관리자 API")
public class AdminController {

    private final AdminService adminService;

    @ModelAttribute("roleTypes")
    public RoleType[] roleTypes(){
        return RoleType.values();
    }

    @Operation(summary = "관리자 로그인", description = "관리자 로그인이 성공하면 메인페이지로 이동")
    @GetMapping("/admin/login")
    public String adminlogin(Model model,
                             @RequestParam(value="error", required = false) String error,
                             @RequestParam(value = "exception", required = false) String exception,
                             HttpServletRequest request, HttpServletResponse response, Authentication authentication)throws Exception{
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        if (authentication != null && authentication.isAuthenticated()) {
            // 이미 인증된 사용자는 메인 페이지로 리다이렉트
            response.sendRedirect("/admin/main");
        }

        return "admin/login";
    }


    @Operation(summary = "관리자 등록폼",
                description = "권한값들을 보내고 관리자 등록 페이지로 이동")
    @GetMapping("/admin/register")
    public String adminregisterForm(Model model){
        model.addAttribute("roleTypes", RoleType.values());
        return "admin/register";
    }

    @Operation(summary = "관리자 등록 처리",
                description = "관리자 등록 페이지에서 입력받은 값들로 관리자 등록 후 로그인 페이지로 이동")
    @PostMapping("/admin/register")
    public String adminregisterProc(AdminDTO adminDTO){
        adminService.register(adminDTO);
        return "redirect:/admin/login";
    }

    @Operation(summary = "관리자 메인페이지",
                description = "관리자 메인페이지로 이동")
    @GetMapping("/admin/main")
    public String adminmain(Model model){
        return "admin/main";
    }


    @Operation(summary = "회사 등록폼",
                description = "권한값들을 보내고 회사 등록 페이지로 이동")
    @GetMapping("/admin/company/register")
    public String adminaddstore(Model model){
        model.addAttribute("roleTypes", RoleType.values());
        return "admin/company/register";
    }

    @Operation(summary = "회사 등록 처리",
                description = "회사 등록 페이지에서 입력받은 값들로 회사 등록 후 회사 목록 페이지로 이동")
    @PostMapping("/admin/company/register")
    public String adminaddstorePost(@ModelAttribute("adminDTO") AdminDTO adminDTO){
        adminService.register(adminDTO);
        return "redirect:/admin/company/list";


    }

    @Operation(summary = "회사 목록",
                description = "최고 관리자 및 회사 계정의 목록을 읽어옴")
    @GetMapping("/admin/company/list")
    public String companylist(Model model, PageRequestDTO pageRequestDTO,
                              AdminDTO adminDTO){
        PageResponseDTO<AdminDTO> responseDTO = adminService.list(adminDTO,pageRequestDTO);

        model.addAttribute("list",responseDTO);
        return "admin/company/list";
    }

    @Operation(summary = "회사 정보",
                description = "회사 정보 페이지로 이동")
    @GetMapping("/admin/company/read")
    public String companyread(Model model){
        return "admin/company/read";
    }

    @Operation(summary = "회사 정보 수정폼",
                description = "회사 정보를 읽어오고 권한, 회사정보, 페이지 정보를 수정 페이지로 보낸 후 이동")
    @GetMapping("/admin/company/update")
    public String companyupdate(Model model,
                                @RequestParam(value = "adminNo", required = false) Long adminNo,
                                PageRequestDTO pageRequestDTO) {
        AdminDTO adminDTO = adminService.readOne(adminNo);
        PageResponseDTO<AdminDTO> responseDTO = adminService.list(adminDTO,pageRequestDTO);
        model.addAttribute("roleTypes", RoleType.values());
        model.addAttribute("adminDTO", adminDTO);
        model.addAttribute("responseDTO", responseDTO);
        return "admin/company/update";
    }

    @Operation(summary = "회사 정보 수정 처리",
                description = "회사 정보 수정 페이지에서 입력받은 값들로 정보 수정 후 회사목록 페이지로 이동")
    @PostMapping("/admin/company/update")
    public String companyupdatePost(@Valid AdminDTO adminDTO,
                                    @RequestParam(value = "searchRoleType", required = false) RoleType searchRoleType,
                                    @RequestParam(value = "searchUserid", required = false) String searchUserid,
                                    @RequestParam(value = "searchName", required = false) String searchName,
                                    @RequestParam(value = "searchEmail", required = false) String searchEmail,
                                    @RequestParam(value = "searchPhone", required = false) String searchPhone,
                                    @RequestParam(value = "page", required = false) String page,
                                    RedirectAttributes redirectAttributes){
        adminService.modify(adminDTO);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("roleType", searchRoleType);
        redirectAttributes.addAttribute("userid", searchUserid);
        redirectAttributes.addAttribute("name", searchName);
        redirectAttributes.addAttribute("email", searchEmail);
        redirectAttributes.addAttribute("phone", searchPhone);

        return "redirect:/admin/company/list";

    }

    @Operation(summary = "회사 삭제 처리",
            description = "회사 정보를 삭제 후 회사 목록 페이지로 이동")
    @PostMapping("/admin/company/delete")
    public String companyremove(@RequestParam(value = "adminNo", required = false) Long adminNo){
        adminService.remove(adminNo);
        return "redirect:/admin/company/list";
    }

    @Operation(summary = "일반 유저 목록",
            description = "일반 사용자 권한을 가진 계정들의 목록을 가져옴")
    @GetMapping("/admin/user/list")
    public String userList(Model model, PageRequestDTO pageRequestDTO,
                              UserDTO userDTO){
        log.info(userDTO);
        PageResponseDTO<UserDTO> responseDTO = adminService.userList(userDTO,pageRequestDTO);

        model.addAttribute("list",responseDTO);
        return "admin/user/list";
    }

}
