package com.exam.hotelmanage1.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMessage;
        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
        } else {
            errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
        }


        errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // 요청된 URL 또는 파라미터를 확인하여 리디렉트 URL 설정
        String refererUrl = request.getHeader("Referer");
        String failureUrl;

        if(refererUrl != null && refererUrl.contains("/admin/login")) {
            failureUrl = "/admin/login?error=true&exception=" + errorMessage;
        } else if(refererUrl != null && refererUrl.contains("/user/login")) {
            failureUrl = "/user/login?error=true&exception=" + errorMessage;
        } else {
            // 기본 실패 URL
            failureUrl = "/login?error=true&exception=" + errorMessage;
        }

        setDefaultFailureUrl(failureUrl);

        super.onAuthenticationFailure(request, response, exception);
    }
}
