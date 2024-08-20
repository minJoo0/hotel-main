package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.MenuDTO;
import com.exam.hotelmanage1.DTO.MenuOptionDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.Entity.CategoryEntity;
import com.exam.hotelmanage1.Entity.MenuEntity;
import com.exam.hotelmanage1.Entity.MenuOptionEntity;
import com.exam.hotelmanage1.Repository.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class MenuOptionService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final HotelRepository hotelRepository;
    private final AdminRepository adminRepository;
    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private final MenuImgRepository menuImgRepository;
    private final MenuOptionRepository menuOptionRepository;



    public PageResponseDTO<MenuOptionDTO> list(MenuOptionDTO menuOptionDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("menuOptionNo");

        // Specification 생성
        Specification<MenuOptionEntity> spec = Specification.where(null);

        if (menuOptionDTO.getName() != null) {
            spec = spec.and(MenuOptionSpecification.equalsName(menuOptionDTO.getName()));
        }
        if (menuOptionDTO.getName() != null) {
            spec = spec.and(MenuOptionSpecification.equalsMenuNo(menuOptionDTO.getMenuNo()));
        }
        if (menuOptionDTO.getStoreNo() != null) {
            spec = spec.and(MenuOptionSpecification.equalsStoreNo(menuOptionDTO.getStoreNo()));
        }
        if (menuOptionDTO.getHotelNo() != null) {
            spec = spec.and(MenuOptionSpecification.equalsHotelNo(menuOptionDTO.getHotelNo()));
        }
        if (menuOptionDTO.getAdminNo() != null) {
            spec = spec.and(MenuOptionSpecification.equalsAdminNo(menuOptionDTO.getAdminNo()));
        }
        if (menuOptionDTO.getCategory1() != null) {
            spec = spec.and(MenuOptionSpecification.equalsCategory1(menuOptionDTO.getCategory1()));
        }
        if (menuOptionDTO.getCategory2() != null) {
            spec = spec.and(MenuOptionSpecification.equalsCategory2(menuOptionDTO.getCategory2()));
        }


        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<MenuOptionEntity> menuOptionEntityPage = menuOptionRepository.findAll(spec, pageable);
        log.info(menuOptionEntityPage.getContent());



        // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
        List<MenuOptionDTO> dtoList = menuOptionEntityPage.getContent().stream()
                .map(entity -> MenuOptionDTO.builder()
                        .adminNo(entity.getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo())
                        .hotelNo(entity.getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().getHotelNo())
                        .storeNo(entity.getMenuEntity().getCategory1().getStoreEntity().getStoreNo())
                        .menuOptionNo(entity.getMenuOptionNo())
                        .storeName(entity.getMenuEntity().getCategory1().getStoreEntity().getName())
                        .menuNo(entity.getMenuEntity().getMenuNo())
                        .menuName(entity.getMenuEntity().getName())
                        .category1(entity.getMenuEntity().getCategory1().getCategoryNo())
                        .category2(entity.getMenuEntity().getCategory2().getCategoryNo())
                        .category1Name(entity.getMenuEntity().getCategory1().getName())
                        .category2Name(entity.getMenuEntity().getCategory2().getName())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .name(entity.getName())
                        .price(entity.getPrice())
                        .build())

                .collect(Collectors.toList());

        log.info(dtoList);


        // PageResponseDTO 생성
        return PageResponseDTO.<MenuOptionDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) menuOptionEntityPage.getTotalElements())
                .build();
    }
    @Transactional
    public MenuOptionEntity register(MenuOptionDTO menuOptionDTO) {
        log.info("menuOptionDTO"+menuOptionDTO);

        MenuOptionEntity menuOptionEntity = modelMapper.map(menuOptionDTO, MenuOptionEntity.class);
        log.info("menuOptionEntity"+menuOptionEntity);

        // Entity 저장
        MenuOptionEntity result = menuOptionRepository.save(menuOptionEntity);
        return result;

    }
    public void update(MenuOptionDTO menuOptionDTO) {
        Optional<MenuOptionEntity> search = menuOptionRepository.findById(menuOptionDTO.getMenuNo());
        MenuOptionEntity menuOptionEntity = search.orElseThrow();


        menuOptionEntity.setName(menuOptionDTO.getName());
        menuOptionEntity.setPrice(menuOptionDTO.getPrice());
        menuOptionEntity.getMenuEntity().getCategory2().setCategoryNo(menuOptionDTO.getCategory2());
        menuOptionEntity.getMenuEntity().getCategory1().setCategoryNo(menuOptionDTO.getCategory1());
        menuOptionEntity.getMenuEntity().getCategory1().getStoreEntity().setStoreNo(menuOptionDTO.getStoreNo());

    }
    public MenuOptionDTO read(Long menuOptionNo) {
        Optional<MenuOptionEntity> menuOptionEntity = menuOptionRepository.findById(menuOptionNo);
        log.info(menuOptionEntity);
        if (menuOptionEntity.isPresent()) {
            MenuOptionEntity entity = menuOptionEntity.get();
            MenuOptionDTO menuOptionDTO = modelMapper.map(entity, MenuOptionDTO.class);
            return menuOptionDTO;
        }
        return null;
    }
    public void delete(Long menuOptionNo) {
        Optional<MenuOptionEntity> menuOptionEntity = menuOptionRepository.findById(menuOptionNo);
        if (menuOptionEntity.isPresent()) {
            menuOptionRepository.deleteById(menuOptionNo);
        }
    }
}

