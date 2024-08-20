package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.KakaoDTO;
import com.exam.hotelmanage1.Service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.attribute.PosixFileAttributes;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Log4j2
public class LoginController {

    private final KakaoService kakaoService;

//    @GetMapping("/user/register/kakao")
//    public String kakaoLoginCallback(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
//        try {
//            // 카카오 서버에서 access token 요청
//            String accessToken = kakaoService.getAccessToken(code);
//
//            // 사용자 정보 조회
//            KakaoDTO userInfo = kakaoService.getKakaoUserInfo(accessToken);
//
//            // 사용자 정보를 처리 (예: DB 저장)
//            // ...
//
//            // RedirectAttributes를 사용하여 데이터 전달
//            redirectAttributes.addFlashAttribute("userInfo", userInfo);
//
//            return "redirect:/user/register";
//        } catch (Exception e) {
//            return "카카오 로그인 실패";
//        }
//    }
}
