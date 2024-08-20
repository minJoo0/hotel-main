package com.exam.hotelmanage1.Config;

import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.Service.AdminLoginService;
import com.exam.hotelmanage1.Service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminLoginService adminLoginService;
    private final UserLoginService userLoginService;
    private final AuthenticationFailureHandler CustomFailureHandler;
    private final CustomLoginSuccessHandlerForAdmin customLoginSuccessHandlerForAdmin;
    private final CustomLoginSuccessHandlerForUser customLoginSuccessHandlerForUser;
    private final CustomLogoutSuccessHandlerForAdmin customLogoutSuccessHandlerForAdmin;
    private final CustomLogoutSuccessHandlerForUser customLogoutSuccessHandlerForUser;





    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); //암호를 암호화 난수...복호화...
    }




    @Configuration
    @Order(1)
    public class AdminSecurityConfig{
        @Bean
        public SecurityFilterChain filterChainAdmin(HttpSecurity http) throws Exception {
            http
                    .securityMatcher("/admin/**","/manager/**","/api/**","/store/**")
                    .authorizeHttpRequests((admin) -> admin
                            .requestMatchers("/assets/**","/assetsforuser/**","/admin/login","/admin/register",
                                            //레이아웃 관련
                                            "/layouts/**","/fragments/**","/api/**",
                                            //swagger 관련
                                            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                            .requestMatchers("/manager/**","/menuimg/**","/user/**").hasAnyRole(RoleType.ADMIN.name(),RoleType.COMPANY.name())
                            .requestMatchers("/store/**").hasRole(RoleType.STORE.name())
                            .requestMatchers("/admin/**").hasRole(RoleType.ADMIN.name())
                    )
                    .sessionManagement(configurer -> configurer
                            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                            .sessionFixation()
                            .newSession()
                    )
                    .formLogin((admin) -> admin
                            .loginPage("/admin/login")
                            .usernameParameter("userid")
                            .passwordParameter("password")
                            .failureHandler(CustomFailureHandler)
                            .successHandler(customLoginSuccessHandlerForAdmin) //성공시 추가처리할 메소드
                            .permitAll()
                    )
                    .logout((logout) -> logout
                            .logoutUrl("/admin/logout")
                            .logoutSuccessHandler(customLogoutSuccessHandlerForAdmin) // 관리자 로그아웃 핸들러 적용
                            .logoutSuccessUrl("/admin/login")
                    )
                    .userDetailsService(adminLoginService)
                    .securityContext(context -> context
                            .securityContextRepository(new CustomSecurityContextRepository())
                    )
                    .csrf(AbstractHttpConfigurer::disable);



            return http.build();
        }
    }
    @Configuration
    @Order(2)
    public class MemberSecurityConfig{
        @Bean
        public SecurityFilterChain filterChainUser(HttpSecurity http) throws Exception {
            http
//                    .securityMatcher("/user/**","/api/**")
//                    .authorizeHttpRequests((user) -> user
//                            .requestMatchers("/assets/**", "/assetsforuser/**", "/layouts/**", "/fragments/**","/user/register","/user/login", "/user/main", "/error/**","/user/search/**").permitAll()
//                            .requestMatchers("/user/read","/user/cart","/user/order","/user/search","/user/menu/**","/user/notice","/user/info/**","/user/update/**","/user/read/**","/user/store/**","/user/order/**","/user/cart/**").hasAnyRole(RoleType.USER.name(),RoleType.ADMIN.name(),RoleType.MANAGER.name(),RoleType.COMPANY.name())// 수정: 모두에게 허용
//                    )
                    .securityMatcher("/user/**","/api/**")
                    .authorizeHttpRequests((user)-> user
                            .requestMatchers("/assets/**", "/assetsforuser/**", "/layouts/**", "/fragments**", "/user/register/**", "/user/logout/**", "/user/login/**", "/user/main", "/error/**", "/user/search/**", "/user/api/**").permitAll()
                            // 모든 /user/** 하위 경로에 대해 USER 역할을 가진 사용자만 접근할 수 있도록 설정
                            .requestMatchers("/user/**").hasRole(RoleType.USER.name())
                    )
                    .sessionManagement(configurer -> configurer
                            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                            .sessionFixation()
                            .newSession()
                    )

                    .formLogin((user) -> user
                            .loginPage("/user/login")
                            .loginProcessingUrl("/user/login")
                            .usernameParameter("email")
                            .passwordParameter("password")
                            .failureHandler(CustomFailureHandler)
                            .successHandler(customLoginSuccessHandlerForUser) //성공시 추가처리할 메소드
                            .permitAll()
                    )
                    .logout((logout) -> logout
                            .logoutUrl("/user/logout")
                            .logoutSuccessHandler(customLogoutSuccessHandlerForUser) // 사용자 로그아웃 핸들러 적용
                            .logoutSuccessUrl("/user/login")
                    )
                    .securityContext(context -> context
                            .securityContextRepository(new CustomSecurityContextRepository())
                    )

                    .userDetailsService(userLoginService)
                    .csrf(AbstractHttpConfigurer::disable);

            return http.build();
        }
    }

    @Configuration
    @Order(3)
    public class StoreSecurityConfig{
        @Bean
        public SecurityFilterChain filterChainStore(HttpSecurity http) throws Exception {
            http
                    .securityMatcher("/store/**","/api/**","/user/api/**")
                    .authorizeHttpRequests((store)-> store
                            .requestMatchers("/assets/**", "/assetsforuser/**", "/layouts/**", "/fragments**", "/store/register/**", "/store/logout/**", "/store/login/**", "/store/main", "/error/**", "/store/search/**", "/store/api/**").permitAll()
                            // 모든 /store/** 하위 경로에 대해 USER 역할을 가진 사용자만 접근할 수 있도록 설정
                            .requestMatchers("/store/**").hasRole(RoleType.STORE.name())
                    )
                    .sessionManagement(configurer -> configurer
                            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                            .sessionFixation()
                            .newSession()
                    )

                    .formLogin((admin) -> admin
                            .loginPage("/admin/login")
                            .usernameParameter("userid")
                            .passwordParameter("password")
                            .failureHandler(CustomFailureHandler)
                            .successHandler(customLoginSuccessHandlerForAdmin) //성공시 추가처리할 메소드
                            .permitAll()
                    )
                    .logout((logout) -> logout
                            .logoutUrl("/admin/logout")
                            .logoutSuccessHandler(customLogoutSuccessHandlerForAdmin) // 관리자 로그아웃 핸들러 적용
                            .logoutSuccessUrl("/admin/login")
                    )
                    .userDetailsService(adminLoginService)
                    .securityContext(context -> context
                            .securityContextRepository(new CustomSecurityContextRepository())
                    )
                    .csrf(AbstractHttpConfigurer::disable);



            return http.build();
        }
    }



}
