package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.CategoryEntity;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.MenuEntity;
import com.exam.hotelmanage1.Entity.StoreEntity;
import com.exam.hotelmanage1.Repository.CategoryRepository;
import com.exam.hotelmanage1.Repository.CategorySpecification;
import com.exam.hotelmanage1.Repository.HotelSpecification;
import com.exam.hotelmanage1.Repository.MenuSpecification;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public PageResponseDTO<CategoryDTO> list(CategoryDTO categoryDTO, PageRequestDTO pageRequestDTO) {
    Pageable pageable = pageRequestDTO.getPageable("categoryNo");

    // Specification 생성
    Specification<CategoryEntity> spec = Specification.where(CategorySpecification.hasNotParent());

        if (categoryDTO.getHotelNo() != null) {
            spec = spec.and(CategorySpecification.equalsHotelNo(categoryDTO.getHotelNo()));
        }

        if (categoryDTO.getName() != null) {
            spec = spec.and(CategorySpecification.equalsName(categoryDTO.getName()));
        }

        if (categoryDTO.getStoreNo() != null) {
            spec = spec.and(CategorySpecification.equalsStoreNo(categoryDTO.getStoreNo()));
        }
        if (categoryDTO.getAdminNo() != null) {
            spec = spec.and(CategorySpecification.equalsAdminNo(categoryDTO.getAdminNo()));
        }

    // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
    Page<CategoryEntity> categoryEntityPage = categoryRepository.findAll(spec, pageable);

    // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
    List<CategoryDTO> dtoList = categoryEntityPage.getContent().stream()
            .map(entity -> CategoryDTO.builder()
                    .adminNo(entity.getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo())
                    .categoryNo(entity.getCategoryNo())
                    .storeNo(entity.getStoreEntity().getStoreNo())
                    .hotelName(entity.getStoreEntity().getHotelEntity().getName())
                    .storeName(entity.getStoreEntity().getName())
                    .regDate(entity.getRegDate())
                    .modDate(entity.getModDate())
                    .name(entity.getName())
                    .build())

            .collect(Collectors.toList());


    // PageResponseDTO 생성
    return PageResponseDTO.<CategoryDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) categoryEntityPage.getTotalElements())
            .build();
}
    public PageResponseDTO<CategoryDTO> list2(CategoryDTO categoryDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("categoryNo");

        // Specification 생성
        Specification<CategoryEntity> spec = Specification.where(CategorySpecification.hasParent());

        if (categoryDTO.getHotelNo() != null) {
            spec = spec.and(CategorySpecification.equalsHotelNo(categoryDTO.getHotelNo()));
        }

        if (categoryDTO.getName() != null) {
            spec = spec.and(CategorySpecification.equalsName(categoryDTO.getName()));
        }

        if (categoryDTO.getStoreNo() != null) {
            spec = spec.and(CategorySpecification.equalsStoreNo(categoryDTO.getStoreNo()));
        }
        if (categoryDTO.getAdminNo() != null) {
            spec = spec.and(CategorySpecification.equalsAdminNo(categoryDTO.getAdminNo()));
        }
        if (categoryDTO.getParentId() != null){
            spec = spec.and(CategorySpecification.equalsParentId(categoryDTO.getParentId()));
        }


        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<CategoryEntity> categoryEntityPage = categoryRepository.findAll(spec, pageable);

        // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
        List<CategoryDTO> dtoList = categoryEntityPage.getContent().stream()
                .map(entity -> CategoryDTO.builder()
                        .categoryNo(entity.getCategoryNo())
                        .adminNo(entity.getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo())
                        .parentId(entity.getParent().getCategoryNo())
                        .pName(entity.getParent().getName())
                        .storeNo(entity.getStoreEntity().getStoreNo())
                        .hotelName(entity.getStoreEntity().getHotelEntity().getName())
                        .storeName(entity.getStoreEntity().getName())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .name(entity.getName())
                        .build())

                .collect(Collectors.toList());


        // PageResponseDTO 생성
        return PageResponseDTO.<CategoryDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) categoryEntityPage.getTotalElements())
                .build();
    }
    public CategoryEntity insert(CategoryDTO categoryDTO) {
        // DTO로부터 Entity 매핑
        CategoryEntity categoryEntity = modelMapper.map(categoryDTO, CategoryEntity.class);

        // parentId가 null이 아니면, 해당하는 부모 카테고리를 찾아서 설정
        if (categoryDTO.getParentId() != null) {
            CategoryEntity parentCategory = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + categoryDTO.getParentId()));
            categoryEntity.setParent(parentCategory);
        }
        // Entity 저장
        CategoryEntity result = categoryRepository.save(categoryEntity);
        return result;
    }
    public CategoryDTO update(CategoryDTO categoryDTO) {
        Optional<CategoryEntity> search = categoryRepository.findById(categoryDTO.getCategoryNo());
        if (search.isPresent()) {
            CategoryEntity categoryEntity = modelMapper.map(categoryDTO, CategoryEntity.class);
            CategoryEntity result = categoryRepository.save(categoryEntity);
            return modelMapper.map(result, CategoryDTO.class);
        }
        return null;
    }
    public void delete(Long CategoryNo ) {
        Optional<CategoryEntity> search = categoryRepository.findById(CategoryNo);
        if (search.isPresent()) {
            categoryRepository.deleteById(CategoryNo);
        }

    }
    public List<CategoryDTO> findCategoryByStoreNo(Long storeNo) {
        List<CategoryEntity> categoryEntityList = categoryRepository.findCategoryByStoreNoAndParentIsNull(storeNo);

        List<CategoryDTO> dtoList = categoryEntityList.stream()
                .map(entity -> CategoryDTO.builder()
                        .categoryNo(entity.getCategoryNo())
                        .adminNo(entity.getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo())
                        .storeNo(entity.getStoreEntity().getStoreNo())
                        .hotelName(entity.getStoreEntity().getHotelEntity().getName())
                        .storeName(entity.getStoreEntity().getName())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .name(entity.getName())
                        .build())

                .collect(Collectors.toList());

        return dtoList;
    }

    public List<CategoryDTO> findCategoryByStoreNoAndParentIsNull(Long storeNo) {
        List<CategoryEntity> categoryEntityList = categoryRepository.findCategoryByStoreNoAndParentIsNull(storeNo);
        List<CategoryDTO> categoryDTOS = categoryEntityList.stream().map(entity -> CategoryDTO.builder()
                .categoryNo(entity.getCategoryNo())
                .storeNo(entity.getStoreEntity().getStoreNo())
                .name(entity.getName())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build()).collect(Collectors.toList());
        return categoryDTOS;
    }

    public List<CategoryEntity> findByParentCategoryNo(Long categoryNo) {
        return categoryRepository.findByParentCategoryNo(categoryNo);
    }
}
