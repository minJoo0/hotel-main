package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Entity.AdminEntity;
import com.exam.hotelmanage1.Entity.StoreEntity;
import com.exam.hotelmanage1.Repository.AdminRepository;
import com.exam.hotelmanage1.Repository.StoreRepository;
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
public class AdminLoginService implements UserDetailsService {


    private final AdminRepository adminRepository;
    private final StoreRepository storeRepository;


    @Override
    public UserDetails loadUserByUsername(String userid){
        //가져온 아이디로 존재여부 검색
        Optional<AdminEntity> adminEntity = adminRepository.findByUserid(userid);
        Optional<StoreEntity> storeEntity = storeRepository.findByUserid(userid);
        //관리자
        //지점

        if (adminEntity.isPresent()){
            return User.withUsername(adminEntity.get().getUserid())
                    .password(adminEntity.get().getPassword())
                    .roles(adminEntity.get().getRoleType().name())
                    .build();
        }else if(storeEntity.isPresent()) {
            return User.withUsername(storeEntity.get().getUserid())
                    .password(storeEntity.get().getPassword())
                    .roles(storeEntity.get().getRoleType().name())
                    .build();
        }else
            throw new UsernameNotFoundException("가입 되지 않은 아이디 입니다 : "+userid);
        }
    }



