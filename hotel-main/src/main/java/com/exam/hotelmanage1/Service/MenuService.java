package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.Repository.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.PosixFileAttributes;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MenuService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final HotelRepository hotelRepository;
    private final AdminRepository adminRepository;
    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private final MenuImgRepository menuImgRepository;
    private final MenuImgService menuImgService;

    @Value("${project.uploadPath}")
    private String uploadPath;

    public PageResponseDTO<MenuDTO> list(MenuDTO menuDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("menuNo");

        // Specification 생성
        Specification<MenuEntity> spec = Specification.where(null);

        if (menuDTO.getName() != null) {
            spec = spec.and(MenuSpecification.equalsName(menuDTO.getName()));
        }
        if (menuDTO.getStoreNo() != null) {
            spec = spec.and(MenuSpecification.equalsStoreNo(menuDTO.getStoreNo()));
        }
        if (menuDTO.getHotelNo() != null) {
            spec = spec.and(MenuSpecification.equalsHotelNo(menuDTO.getHotelNo()));
        }
        if (menuDTO.getAdminNo() != null) {
            spec = spec.and(MenuSpecification.equalsAdminNo(menuDTO.getAdminNo()));
        }
        if (menuDTO.getCategory1() != null) {
            spec = spec.and(MenuSpecification.equalsCategory1(menuDTO.getCategory1()));
        }
        if (menuDTO.getCategory2() != null) {
            spec = spec.and(MenuSpecification.equalsCategory2(menuDTO.getCategory2()));
        }


        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<MenuEntity> menuEntityPage = menuRepository.findAll(spec, pageable);



        // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
        List<MenuDTO> dtoList = menuEntityPage.getContent().stream()
                .map(entity -> {
                    // 메뉴에 해당하는 이미지 URL을 얻음
                    List<MenuImgEntity> menuImgEntities = menuImgService.findByMenuEntityMenuNo(entity.getMenuNo());
                    log.info(menuImgEntities);
                    String imageUrl = "";
                    if (!menuImgEntities.isEmpty()) {
                        MenuImgEntity firstMenuImg = menuImgEntities.get(0);

                        imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

                    }
                    log.info(imageUrl);

                    return MenuDTO.builder()
                            .adminNo(entity.getCategory1().getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo())
                            .hotelNo(entity.getCategory1().getStoreEntity().getHotelEntity().getHotelNo())
                            .storeNo(entity.getCategory1().getStoreEntity().getStoreNo())
                            .storeName(entity.getCategory1().getStoreEntity().getName())
                            .menuNo(entity.getMenuNo())
                            .category1(entity.getCategory1().getCategoryNo())
                            .category2(entity.getCategory2().getCategoryNo())
                            .category1Name(entity.getCategory1().getName())
                            .category2Name(entity.getCategory2().getName())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .name(entity.getName())
                            .price(entity.getPrice())
                            .imageUrl(imageUrl) // imageUrl 설정
                            .build();
                })
                .collect(Collectors.toList());


        // PageResponseDTO 생성
        return PageResponseDTO.<MenuDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) menuEntityPage.getTotalElements())
                .build();
    }
    public PageResponseDTO<CategoryDTO> UserList(CategoryDTO categoryDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("categoryNo");

        // Specification 생성
        Specification<CategoryEntity> spec = Specification.where(CategorySpecification.hasNotParent());

        if (categoryDTO.getStoreNo() != null) {
            spec = spec.and(CategorySpecification.equalsStoreNo(categoryDTO.getStoreNo()));
        }



        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<CategoryEntity> categoryEntityPage = categoryRepository.findAll(spec, pageable);
        log.info(categoryEntityPage.getContent());



        List<CategoryDTO> dtoList = categoryEntityPage.getContent().stream()
                .map(entity -> {
                    CategoryDTO categoryDTO1 = CategoryDTO.builder()
                            .adminNo(entity.getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo())
                            .hotelNo(entity.getStoreEntity().getHotelEntity().getHotelNo())
                            .hotelName(entity.getStoreEntity().getHotelEntity().getName())
                            .storeNo(entity.getStoreEntity().getStoreNo())
                            .storeName(entity.getStoreEntity().getName())
                            .categoryNo(entity.getCategoryNo())
                            .pName(entity.getParent() != null ? entity.getParent().getName() : "")
                            .name(entity.getName())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .build();
                    List<MenuDTO> menuDTOs = menuRepository.findByCategory1_CategoryNo(entity.getCategoryNo())
                            .stream()
                            .map(menuEntity -> {
                                // 메뉴에 해당하는 이미지 URL을 얻음
                                List<MenuImgEntity> menuImgEntities = menuImgService.findByMenuEntityMenuNo(menuEntity.getMenuNo());
                                log.info(menuImgEntities);
                                String imageUrl = "";
                                if (!menuImgEntities.isEmpty()) {
                                    MenuImgEntity firstMenuImg = menuImgEntities.get(0);

                                    imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

                                }
                                log.info(imageUrl);

                                return MenuDTO.builder()
                                        .adminNo(menuEntity.getCategory1().getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo())
                                        .hotelNo(menuEntity.getCategory1().getStoreEntity().getHotelEntity().getHotelNo())
                                        .storeNo(menuEntity.getCategory1().getStoreEntity().getStoreNo())
                                        .storeName(menuEntity.getCategory1().getStoreEntity().getName())
                                        .menuNo(menuEntity.getMenuNo())
                                        .category1(menuEntity.getCategory1().getCategoryNo())
                                        .category2(menuEntity.getCategory2().getCategoryNo())
                                        .category1Name(menuEntity.getCategory1().getName())
                                        .category2Name(menuEntity.getCategory2().getName())
                                        .regDate(menuEntity.getRegDate())
                                        .modDate(menuEntity.getModDate())
                                        .name(menuEntity.getName())
                                        .price(menuEntity.getPrice())
                                        .imageUrl(imageUrl) // imageUrl 설정
                                        .build();
                            })
                            .collect(Collectors.toList());
                    List<CategoryDTO> children = categoryRepository.findByParentCategoryNo(entity.getCategoryNo())
                            .stream()
                            .map(categoryEntity -> {
                                return CategoryDTO.builder()
                                        .pName(categoryEntity.getParent() != null ? categoryEntity.getParent().getName() : "")
                                        .categoryNo(categoryEntity.getCategoryNo())
                                        .name(categoryEntity.getName())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    categoryDTO1.setMenuDTOList(menuDTOs);
                    categoryDTO1.setChildren(children);
                    return categoryDTO1;
                })
                .collect(Collectors.toList());


        // PageResponseDTO 생성
        return PageResponseDTO.<CategoryDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) categoryEntityPage.getTotalElements())
                .build();
    }

    public String makeDir() {
        //c드라이브에 년월일로 이루어진 폴더생성
        Date date = new Date(); //오늘
        // 240314
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String now = sdf.format(date);
        //저장경로
        String path = uploadPath + "\\" + now;
        File file = new File(path);//경로만 file에 넣는다
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }


    public MenuEntity register(MenuDTO menuDTO) {

        MenuEntity menuEntity = modelMapper.map(menuDTO, MenuEntity.class);

        // parentId가 null이 아니면, 해당하는 부모 카테고리를 찾아서 설정
        if (menuDTO.getCategory1() != null) {
            CategoryEntity parentCategory = categoryRepository.findById(menuDTO.getCategory1())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + menuDTO.getCategory1()));
            menuEntity.setCategory1(parentCategory);
        }
        if (menuDTO.getCategory2() != null) {
            CategoryEntity parentCategory = categoryRepository.findById(menuDTO.getCategory2())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + menuDTO.getCategory2()));
            menuEntity.setCategory2(parentCategory);
        }

        // Entity 저장
        MenuEntity result = menuRepository.save(menuEntity);
        return result;

    }
    public MenuDTO read(Long menuNo) {
        Optional<MenuEntity> menuEntity = menuRepository.findById(menuNo);
        log.info(menuEntity);
        if (menuEntity.isPresent()) {
            MenuEntity entity = menuEntity.get();
            MenuDTO menuDTO = modelMapper.map(entity, MenuDTO.class);
            log.info(menuDTO);
            return menuDTO;
        }
        return null;
    }

    public void update(MenuDTO menuDTO) {
        Optional<MenuEntity> search = menuRepository.findById(menuDTO.getMenuNo());
        MenuEntity menuEntity = search.orElseThrow();


        menuEntity.setName(menuDTO.getName());
        menuEntity.setPrice(menuDTO.getPrice());
        menuEntity.getCategory2().setCategoryNo(menuDTO.getCategory2());
        menuEntity.getCategory1().setCategoryNo(menuDTO.getCategory1());
        menuEntity.getCategory1().getStoreEntity().setStoreNo(menuDTO.getStoreNo());

    }

    public List<HotelDTO> findHotelsByAdminNo(Long adminNo) {
        List<HotelEntity> hotelEntityList;

        if (authService.isAdmin()) {
            hotelEntityList = hotelRepository.findAll(); // 모든 호텔 조회를 위한 메서드 호출
        } else {
            hotelEntityList = hotelRepository.findHotelsByAdminNo(adminNo);
        }

        return hotelEntityList.stream()
                .map(entity -> modelMapper.map(entity, HotelDTO.class))
                .collect(Collectors.toList());
    }

    public List<HotelDTO> findHotelsByAdminNo2(Long adminNo) {

        List<HotelEntity> hotelEntityList;

        if (authService.isAdmin()) {
            hotelEntityList = hotelRepository.findAll(); // 모든 호텔 조회를 위한 메서드 호출
        } else {
            hotelEntityList = hotelRepository.findHotelsByAdminNoAndCategoryParentIDIsNotNull(adminNo);
        }

        return hotelEntityList.stream()
                .map(entity -> modelMapper.map(entity, HotelDTO.class))
                .collect(Collectors.toList());
    }
    public void delete(Long menuNo) {
        Optional<MenuEntity> menuEntity = menuRepository.findById(menuNo);
        if (menuEntity.isPresent()) {
            menuRepository.deleteById(menuNo);
        }
    }
    public List<MenuDTO> findMenusByCategory2(Long categoryNo) {

        List<MenuDTO> menuDTOs = menuRepository.findByCategory2_CategoryNo(categoryNo)
                .stream()
                .map(menuEntity -> {
                    // 메뉴에 해당하는 이미지 URL을 얻음
                    List<MenuImgEntity> menuImgEntities = menuImgService.findByMenuEntityMenuNo(menuEntity.getMenuNo());
                    log.info(menuImgEntities);
                    String imageUrl = "";
                    if (!menuImgEntities.isEmpty()) {
                        MenuImgEntity firstMenuImg = menuImgEntities.get(0);

                        imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

                    }
                    log.info(imageUrl);

                    return MenuDTO.builder()
                            .adminNo(menuEntity.getCategory1().getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo())
                            .hotelNo(menuEntity.getCategory1().getStoreEntity().getHotelEntity().getHotelNo())
                            .storeNo(menuEntity.getCategory1().getStoreEntity().getStoreNo())
                            .storeName(menuEntity.getCategory1().getStoreEntity().getName())
                            .menuNo(menuEntity.getMenuNo())
                            .category1(menuEntity.getCategory1().getCategoryNo())
                            .category2(menuEntity.getCategory2().getCategoryNo())
                            .category1Name(menuEntity.getCategory1().getName())
                            .category2Name(menuEntity.getCategory2().getName())
                            .regDate(menuEntity.getRegDate())
                            .modDate(menuEntity.getModDate())
                            .name(menuEntity.getName())
                            .price(menuEntity.getPrice())
                            .imageUrl(imageUrl) // imageUrl 설정
                            .build();
                })
                .collect(Collectors.toList());
        return menuDTOs;
    }





}
