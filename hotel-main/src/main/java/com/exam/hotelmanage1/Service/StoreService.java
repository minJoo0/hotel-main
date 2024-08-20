package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.DTO.StoreDTO;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final StoreImgService storeImgService;
    private final PasswordEncoder passwordEncoder;


    // 삽입
    public StoreEntity insert(StoreDTO storeDTO) {
        Optional<StoreEntity> storeEntity = storeRepository
                .findByUserid(storeDTO.getUserid());
        if (storeEntity.isPresent()){
            throw new IllegalStateException("이미 가입된 아이디입니다");
        }

        StoreEntity storeEntity1 = modelMapper.map(storeDTO, StoreEntity.class);
        storeEntity1.setPassword(passwordEncoder.encode(storeDTO.getPassword()));
        storeEntity1.setRoleType(RoleType.STORE);
        StoreEntity result = storeRepository.save(storeEntity1);
        return result;
    }

    // 수정
    public void update(StoreDTO storeDTO) {
        Optional<StoreEntity> result = storeRepository.findById(storeDTO.getStoreNo());
        StoreEntity storeEntity = result.orElseThrow();

        storeEntity.setUserid(storeDTO.getUserid());
        storeEntity.setPassword(passwordEncoder.encode(storeDTO.getPassword()));
        storeEntity.setName(storeDTO.getName());
        storeEntity.setAddress(storeDTO.getAddress());
        storeEntity.setBusinessHours(storeDTO.getBusinessHours());
        storeEntity.setTel(storeDTO.getTel());

    }

    // 삭제
    public void delete(Long storeNo) {
        Optional<StoreEntity> storeEntity = storeRepository.findById(storeNo);
        if (storeEntity.isPresent()) {
            storeRepository.deleteById(storeNo);
        }
    }

    // 전체조회
    public PageResponseDTO<StoreDTO> list(StoreDTO storeDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("storeNo");

        // Specification 생성
        Specification<StoreEntity> spec = Specification.where(null);

        if (storeDTO.getName() != null) {
            spec = spec.and(StoreSpecification.equalsName(storeDTO.getName()));
        }
        if (storeDTO.getBusinessHours() != null) {
            spec = spec.and(StoreSpecification.equalsBusinessHours(storeDTO.getBusinessHours()));
        }
        if (storeDTO.getTel() != null) {
            spec = spec.and(StoreSpecification.equalsTel(storeDTO.getTel()));
        }
        if (storeDTO.getStoreType() != null) {
            spec = spec.and(StoreSpecification.equalsType(storeDTO.getStoreType()));
        }
        if (storeDTO.getAddress() != null) {
            spec = spec.and(StoreSpecification.equalsAddress(storeDTO.getAddress()));
        }
        if (storeDTO.getHotelNo() != null) {
            spec = spec.and(StoreSpecification.equalsHotelNo(storeDTO.getHotelNo()));
        }
        if (storeDTO.getHotelName() != null) {
            spec = spec.and(StoreSpecification.equalsHotelName(storeDTO.getHotelName()));
        }
        if (storeDTO.getAdminNo() != null) {
            spec = spec.and(StoreSpecification.equalsAdminNo(storeDTO.getAdminNo()));
        }
        //userNo, 지금 날짜를 이용해 이용가능한 매장 목록
        if (storeDTO.getUserNo() != null) {
            spec = spec.and(StoreSpecification.hasValidReservationsForToday(storeDTO.getUserNo(), LocalDate.now()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<StoreEntity> storeEntityPage = storeRepository.findAll(spec, pageable);

        // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
        List<StoreDTO> dtoList = storeEntityPage.getContent().stream()
                .map(entity -> {
                    // 상점 이미지를 찾기
                    List<StoreImgEntity> storeImgEntities = storeImgService.findByStoreEntityStoreNo(entity.getStoreNo());
                    String imageUrl = "";
                    if (!storeImgEntities.isEmpty()) {
                        StoreImgEntity firstMenuImg = storeImgEntities.get(0);

                        imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

                    }

                    // StoreDTO 객체에 imageUrl 추가
                    return StoreDTO.builder()
                            .storeNo(entity.getStoreNo())
                            .name(entity.getName())
                            .hotelName(entity.getHotelEntity().getName())
                            .address(entity.getAddress())
                            .businessHours(entity.getBusinessHours())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .storeType(entity.getStoreType())
                            .tel(entity.getTel())
                            .hotelNo(entity.getHotelEntity().getHotelNo())
                            .adminNo(entity.getHotelEntity().getAdminEntity().getAdminNo())
                            .imageUrl(imageUrl) // 추가된 이미지 URL
                            .build();
                })
                .collect(Collectors.toList());


        // PageResponseDTO 생성
        return PageResponseDTO.<StoreDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) storeEntityPage.getTotalElements())
                .build();
    }

    // 상세조회
    public StoreDTO read(Long storeNo) {
        Optional<StoreEntity> storeEntity = storeRepository.findById(storeNo);
        if (storeEntity.isPresent()) {
            StoreEntity entity = storeEntity.get();
            StoreDTO result = storeEntity.map(mapper -> modelMapper.map(entity, StoreDTO.class))
                    .orElse(null);
            return  result;
        }
        return null;
    }

    //호텔에 있는 매장의 갯수
    public Long countStoresByHotelNo(Long hotelNo) {
        return storeRepository.countByHotelEntityHotelNo(hotelNo);
    }

    public List<HotelEntity> findHotelsBySido(String sido) {
        return hotelRepository.findBySido(sido);
    }

    public List<HotelEntity> findHotelsBySidoAndAdminNo(String sido, Long adminNo) {
        return hotelRepository.findBySidoAndAdminEntity_AdminNo(sido, adminNo);
    }
    public List<StoreEntity> findStoresByHotelNo(Long hotelNo) {
        return storeRepository.findStoresByHotelNo(hotelNo);
    }


    public StoreDTO getStoreAndHotelName(Long storeNo) {
        StoreEntity storeEntity = storeRepository.findByStoreNoWithHotel(storeNo)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        // DTO 변환
        return StoreDTO.builder()
                .storeNo(storeEntity.getStoreNo())
                .hotelNo(storeEntity.getHotelEntity().getHotelNo())
                .name(storeEntity.getName())
                .hotelName(storeEntity.getHotelEntity().getName())
                .address(storeEntity.getAddress())
                .businessHours(storeEntity.getBusinessHours())
                .tel(storeEntity.getTel())
                .storeType(storeEntity.getStoreType())
                .regDate(storeEntity.getRegDate())
                .modDate(storeEntity.getModDate())
                // 이 부분은 필요에 따라 추가 정보를 설정할 수 있습니다.
                .build();
    }

}