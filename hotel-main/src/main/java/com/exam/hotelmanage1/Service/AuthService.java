package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Entity.AdminEntity;
import com.exam.hotelmanage1.Repository.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AuthService {

    private final AdminRepository adminRepository;

    // 사용자가 관리자 권한을 가지고 있는지 확인하는 메서드
    public boolean isAdmin() {
        // 현재 인증된 사용자의 인증 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication);

        // 사용자의 권한 중 "ROLE_ADMIN" 권한이 있는지 확인
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
    public Optional<Long> getCurrentAdminNo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName(); // 현재 접속 중인 사용자명

        // userid로 AdminEntity 조회
        Optional<AdminEntity> adminEntity = adminRepository.findByUserid(currentUserName);

        // AdminEntity가 존재하면 그의 adminNo를 반환
        return adminEntity.map(AdminEntity::getAdminNo);
    }
}
