package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Entity.AdminEntity;
import com.exam.hotelmanage1.Entity.UserEntity;
import com.exam.hotelmanage1.Repository.AdminRepository;
import com.exam.hotelmanage1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

//기본crud 따로
//로그인만 처리
@Service
@Log4j2
@RequiredArgsConstructor
public class UserLoginService implements UserDetailsService {


    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email){
        //가져온 아이디로 존재여부 검색
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        //관리자
        //지점

        if (userEntity.isPresent()){
            return User.withUsername(userEntity.get().getEmail())
                    .password(userEntity.get().getPassword())
                    .roles(userEntity.get().getRoleType().name())
                    .build();
        }else{
            throw new UsernameNotFoundException("가입 되지 않은 아이디 입니다 : "+email);
        }
    }


}
