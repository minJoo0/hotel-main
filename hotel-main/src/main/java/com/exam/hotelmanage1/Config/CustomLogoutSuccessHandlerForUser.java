package com.exam.hotelmanage1.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//로그아웃 성공시처리
@Component
public class CustomLogoutSuccessHandlerForUser extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        HttpSession session = request.getSession();

        if (session != null) {
            // SPRING_SECURITY_CONTEXT_USER 속성만 제거하여 USER 관련 세션 정보만 삭제
            session.removeAttribute("SPRING_SECURITY_CONTEXT_USER");
        }

        super.setDefaultTargetUrl("/user/login"); // 사용자 로그아웃 후 리다이렉트될 URL
        super.onLogoutSuccess(request, response, authentication);
    }
}
