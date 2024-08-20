package com.exam.hotelmanage1.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Swagger설정
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("호텔 프로젝트API")
                .description("보안로그인/계정 등록/호텔 관리/객실 관리/예약 관리/룸서비스 등등 기능을 구현")
                .version("1.0.0");

        return new OpenAPI()
                .info(info);
    }
}
