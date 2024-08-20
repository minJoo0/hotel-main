package com.exam.hotelmanage1.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;


import java.io.IOException;
//실패처리
//
//
@Configuration
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //try~catch 오류발생하면 클래스내에서 오류를 무시 -> 외부 클래스 오류정보 x
    //throws 오류발생하면 오류를 무시하고 오류정보를 호출한 곳에 반환
    @Override
    public void commence (HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException)throws IOException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"인증실패");
    }


}
