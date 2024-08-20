package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.DTO.AdminDTO;
import com.exam.hotelmanage1.Service.AdminService;
import com.exam.hotelmanage1.Service.AdminLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Tag(name = "회사",description = "회사 관리 API")
@Log4j2
public class CompanyController {

    private final AdminService adminService;

    @ModelAttribute("roleTypes")
    public RoleType[] roleTypes(){
        return RoleType.values();
    }


    @GetMapping("/manager/register")
    public String managerregisterForm(Model model){
        model.addAttribute("roleTypes", RoleType.values());
        return "manager/register";
    }

    @PostMapping("/manager/register")
    public String managerregisterProc(AdminDTO adminDTO){
        adminService.register(adminDTO);
        return "redirect:/manager/login";
    }

    @Operation(summary = "회사 관리 메인 페이지",
            description = "회사 관리 메인 페이지로 이동")
    @GetMapping("/manager/main")
    public String managermain(Model model){
        return "manager/main";
    }

}
