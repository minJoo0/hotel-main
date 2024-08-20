package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.HotelDTO;
import com.exam.hotelmanage1.DTO.MenuDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.Repository.AdminRepository;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Repository.HotelSpecification;
import com.exam.hotelmanage1.Repository.MenuSpecification;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class HotelService {
    private final HotelRepository hotelRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final AdminNoCheckService adminNoCheckService;
    private final AuthService authService;
    private final HotelImgService hotelImgService;

    // 삽입
    public HotelEntity insert(HotelDTO hotelDTO) {
        HotelEntity hotelEntity = modelMapper.map(hotelDTO, HotelEntity.class);
        HotelEntity result = hotelRepository.save(hotelEntity);
        return result;
    }


    // 수정
    public void update(HotelDTO hotelDTO) {
        Optional<HotelEntity> result = hotelRepository.findById(hotelDTO.getHotelNo());
        HotelEntity hotelEntity = result.orElseThrow();

        hotelEntity.setName(hotelDTO.getName());
        hotelEntity.setAddress(hotelDTO.getAddress());
        hotelEntity.setAddress2(hotelDTO.getAddress2());
        hotelEntity.setPostNumber(hotelDTO.getPostNumber());
        hotelEntity.setTel(hotelDTO.getTel());

    }

    // 삭제
    public void delete(Long hotelNo) {
        Optional<HotelEntity> hotelEntity = hotelRepository.findById(hotelNo);

        if (hotelEntity.isPresent()) {
            hotelRepository.deleteById(hotelNo);
        }
    }

    // 전체조회
    public PageResponseDTO<HotelDTO> list(HotelDTO hotelDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("hotelNo");

        // Specification 생성
        Specification<HotelEntity> spec = Specification.where(null);

        if (hotelDTO.getName() != null) {
            spec = spec.and(HotelSpecification.equalsName(hotelDTO.getName()));
        }
        if (hotelDTO.getPostNumber() != null) {
            spec = spec.and(HotelSpecification.equalsPostNumber(hotelDTO.getPostNumber()));
        }
        if (hotelDTO.getTel() != null) {
            spec = spec.and(HotelSpecification.equalsTel(hotelDTO.getTel()));
        }
        if (hotelDTO.getHotelType() != null) {
            spec = spec.and(HotelSpecification.equalsType(hotelDTO.getHotelType()));
        }
        if (StringUtils.isNotEmpty(hotelDTO.getSido())) {
            spec = spec.and(HotelSpecification.equalsSido(hotelDTO.getSido()));
        }
        if (hotelDTO.getAdminNo() != null) {
            spec = spec.and(HotelSpecification.equalsAdminNo(hotelDTO.getAdminNo()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<HotelEntity> hotelEntityPage = hotelRepository.findAll(spec, pageable);

        // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
        List<HotelDTO> dtoList = hotelEntityPage.getContent().stream()
                .map(entity -> HotelDTO.builder()
                        .hotelNo(entity.getHotelNo())
                        .address(entity.getAddress())
                        .address2(entity.getAddress2())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .postNumber(entity.getPostNumber())
                        .tel(entity.getTel())
                        .hotelType(entity.getHotelType())
                        .name(entity.getName())
                        .sido(entity.getSido())
                        .sigungu(entity.getSigungu())
                        .adminNo(entity.getAdminEntity().getAdminNo())
                        .build())
                .collect(Collectors.toList());


        // PageResponseDTO 생성
        return PageResponseDTO.<HotelDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) hotelEntityPage.getTotalElements())
                .build();
    }


    // 상세조회
    public HotelDTO read(Long hotelNo) {
        Optional<HotelEntity> hotelEntity = hotelRepository.findById(hotelNo);

        HotelEntity hotelEntity1 = hotelEntity.orElseThrow();

        int lowestPrice = hotelEntity1.getRoomEntity().stream()
                .mapToInt(RoomEntity::getPrice)
                .min()
                .orElse(0);


        HotelDTO hotelDTO = modelMapper.map(hotelEntity1,HotelDTO.class);

        //이미지가져오기위해 수정- imgUrl 수동으로 설정 //
        List<HotelImgEntity> hotelImgEntityList = hotelImgService.findByHotelEntityHotelNo(hotelEntity1.getHotelNo());
        String imageUrl = "";
        if (!hotelImgEntityList.isEmpty()) {
            HotelImgEntity firstMenuImg = hotelImgEntityList.get(0);
            imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정
        }

        hotelDTO.setImageUrl(imageUrl);
        hotelDTO.setRowPrice(lowestPrice);

        return hotelDTO;
    }

    public HotelEntity saveHotel(HotelDTO hotelDTO) {
        HotelEntity hotelEntity = modelMapper.map(hotelDTO, HotelEntity.class);
        HotelEntity result = hotelRepository.save(hotelEntity);


        return result;
    }

    public List<String> HotelSidos (Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            return hotelRepository.findAllSidos();
        }else {
            return hotelRepository.findSidosByAdminNo(adminNo);

        }

    }
    public List<String> UserSidos(){
        return hotelRepository.findAllSidos();
    }






}