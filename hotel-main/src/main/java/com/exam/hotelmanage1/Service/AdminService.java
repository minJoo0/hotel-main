package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.AdminDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.DTO.UserDTO;
import com.exam.hotelmanage1.Entity.AdminEntity;
import com.exam.hotelmanage1.Entity.UserEntity;
import com.exam.hotelmanage1.Repository.AdminRepository;
import com.exam.hotelmanage1.Repository.AdminSpecification;
import com.exam.hotelmanage1.Repository.UserRepository;
import com.exam.hotelmanage1.Repository.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;



    public void register(AdminDTO adminDTO){
        //존재하는 회원인지 확인
        Optional<AdminEntity> adminEntity = adminRepository
                .findByUserid(adminDTO.getUserid());
        if (adminEntity.isPresent()){
            throw new IllegalStateException("이미 가입된 아이디입니다");
        }

        //존재하지 않으면
        //1.암호화.복호화
        //2.DTO->Entity 변환
        AdminEntity temp = modelMapper.map(adminDTO, AdminEntity.class);
        //3.DTO에 없는 부분을 Entity에 추가(복호화된암호,권한...)
        temp.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        temp.setRoleType(adminDTO.getRoleType());
        //4.값저장
        adminRepository.save(temp);

    }

    //목록
    public void modify(AdminDTO adminDTO) {

        Optional<AdminEntity> result = adminRepository.findById(adminDTO.getAdminNo());
        AdminEntity adminEntity = result.orElseThrow(); //기존에 존재하던 값

        //수정될값
        adminEntity.setName(adminDTO.getName());
        adminEntity.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        adminEntity.setEmail(adminDTO.getEmail());
        adminEntity.setPhone(adminDTO.getPhone());
    }

    public PageResponseDTO<AdminDTO> list(AdminDTO adminDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("adminNo");

        // Specification 생성
        Specification<AdminEntity> spec = Specification.where(null);

        if (adminDTO.getName() != null) {
            spec = spec.and(AdminSpecification.equalsName(adminDTO.getName()));
        }
        if (adminDTO.getEmail() != null) {
            spec = spec.and(AdminSpecification.equalsEmail(adminDTO.getEmail()));
        }
        if (adminDTO.getPhone() != null) {
            spec = spec.and(AdminSpecification.equalsPhone(adminDTO.getPhone()));
        }
        if (adminDTO.getUserid() != null) {
            spec = spec.and(AdminSpecification.equalsUserid(adminDTO.getUserid()));
        }
        if (adminDTO.getRoleType() != null) {
            spec = spec.and(AdminSpecification.equalsRole(adminDTO.getRoleType()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<AdminEntity> adminEntityPage = adminRepository.findAll(spec,pageable);

        // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
        List<AdminDTO> dtoList = adminEntityPage.getContent().stream()
                .map(entity -> AdminDTO.builder()
                        .adminNo(entity.getAdminNo())
                        .userid(entity.getUserid())
                        .name(entity.getName())
                        .email(entity.getEmail())
                        .phone(entity.getPhone())
                        .roleType(entity.getRoleType())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .build())
                .collect(Collectors.toList());


        // PageResponseDTO 생성
        return PageResponseDTO.<AdminDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) adminEntityPage.getTotalElements())
                .build();
    }
    public AdminDTO readOne(Long adminNo) {

        Optional<AdminEntity> adminEntity = adminRepository.findById(adminNo);

        AdminEntity adminEntity1 = adminEntity.orElseThrow();

        return modelMapper.map(adminEntity1, AdminDTO.class);
    }
    public void remove(Long adminNo) {

        adminRepository.deleteById(adminNo);
    }

    public PageResponseDTO<UserDTO> userList(UserDTO userDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("userNo");

        // Specification 생성
        Specification<UserEntity> spec = Specification.where(null);

        if (userDTO.getName() != null && !userDTO.getName().isEmpty()) {
            spec = spec.and(UserSpecification.equalsName(userDTO.getName()));
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            spec = spec.and(UserSpecification.equalsEmail(userDTO.getEmail()));
        }
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
            spec = spec.and(UserSpecification.equalsPhone(userDTO.getPhone()));
        }
        if (userDTO.getBirth() != null) {
            spec = spec.and(UserSpecification.equalsBirth(userDTO.getBirth()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<UserEntity> userEntityPage = userRepository.findAll(spec,pageable);

        // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
        List<UserDTO> dtoList = userEntityPage.getContent().stream()
                .map(entity -> UserDTO.builder()
                        .userNo(entity.getUserNo())
                        .name(entity.getName())
                        .email(entity.getEmail())
                        .phone(entity.getPhone())
                        .birth(entity.getBirth())
                        .roleType(entity.getRoleType())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .build())
                .collect(Collectors.toList());


        // PageResponseDTO 생성
        return PageResponseDTO.<UserDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) userEntityPage.getTotalElements())
                .build();
    }





}
