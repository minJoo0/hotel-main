package com.exam.hotelmanage1.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

//로그인 성공시처리
@Component
@Log4j2
public class CustomLoginSuccessHandlerForAdmin extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        HttpSession session = request.getSession();//클라이언트 정보읽기

        if (session != null) {//클라이언트가 서버연결이 되면
            String userid = authentication.getName();//로그인 아이디를 불러와
            String roles = getAuthorities(authentication).toString();

            log.info("roles=~~~~~~~~~~~~~~~~~~~~" + roles);
            session.setAttribute("roles", roles);
            session.setAttribute("userid", userid);

            // 각 권한에 따라 다른 세션 속성 부여

            if (roles.contains("ADMIN")) {
                session.setAttribute("adminSessionInfo", "관리자 전용 세션 정보"); // 관리자 전용 세션 속성
                super.setDefaultTargetUrl("/admin/main");
                super.onAuthenticationSuccess(request, response, authentication);
            } else if (roles.contains("COMPANY")) {
                session.setAttribute("companySessionInfo", "회사 계정 전용 세션 정보"); // 회사 계정 전용 세션 속성
                super.setDefaultTargetUrl("/manager/main");
                super.onAuthenticationSuccess(request, response, authentication);
            } else if (roles.contains("STORE")){
                session.setAttribute("storeSessionInfo", "매장 계정 전용 세션 정보"); // 회사 계정 전용 세션 속성
                super.setDefaultTargetUrl("/store/main");
                super.onAuthenticationSuccess(request, response, authentication);
            }

        }
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities();
    }
}