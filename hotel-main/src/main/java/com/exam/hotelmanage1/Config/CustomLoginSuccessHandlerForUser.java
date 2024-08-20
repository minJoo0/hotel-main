package com.exam.hotelmanage1.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

//로그인 성공시처리
@Component
@RequiredArgsConstructor
@Log4j2
public class CustomLoginSuccessHandlerForUser extends SimpleUrlAuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        handleSuccess(request, response, authentication);
    }

    public void handleSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        HttpSession session = request.getSession();

        if (session != null) {
            String userid = authentication.getName();
            String roles = getAuthorities(authentication).toString();

            log.info("roles=~~~~~~~~~~~~~~~~~~~~" + roles);
            session.setAttribute("roles", roles);
            session.setAttribute("userid", userid);

            // 각 권한에 따라 다른 세션 속성 부여
            if (roles.contains("USER")) {
                session.setAttribute("userSessionInfo", "일반 사용자 전용 세션 정보");
                log.info("세션입니다~~~~~~~~~~~~~~~~~~~" + session);
                super.setDefaultTargetUrl("/user/main");
                super.onAuthenticationSuccess(request, response, authentication);
            }
        }
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities();
    }
}