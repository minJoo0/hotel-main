package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.FaqDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.Entity.FaqEntity;
import com.exam.hotelmanage1.Repository.FaqRepository;
import com.exam.hotelmanage1.Repository.FaqSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*================

작업명:faq관리crud
작업자:김민주
작업일:2024-04-12
작업내용:crud기능구현부

===============*/

@Service
@RequiredArgsConstructor
@Transactional
public class FaqService {

    private final FaqRepository faqRepository;
    private final ModelMapper modelMapper;

    //insert등록
    public void insert(FaqDTO faqDTO){
        FaqEntity faqEntity = modelMapper.map(faqDTO,FaqEntity.class);
        faqRepository.save(faqEntity);
    }


    //list조회
    public PageResponseDTO<FaqDTO> list(FaqDTO faqDTO, PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("faqNo");

        Specification<FaqEntity> spec = Specification.where(null);

        if (faqDTO.getTitle() != null){
            spec = spec.and(FaqSpecification.equalsTitle(faqDTO.getTitle()));
        }
        if (faqDTO.getContent() != null){
            spec = spec.and(FaqSpecification.equalsContent(faqDTO.getContent()));
        }
        if (faqDTO.getCategory() != null){
            spec = spec.and(FaqSpecification.equalsCategory(faqDTO.getCategory()));
        }
        if (faqDTO.getAdminNo() != null){
            spec = spec.and(FaqSpecification.equalsAdminNo(faqDTO.getAdminNo()));
        }


        Page<FaqEntity> faqEntityPage = faqRepository.findAll(spec,pageable);

        List<FaqDTO> dtoList = faqEntityPage.getContent().stream()
                .map(entity -> FaqDTO.builder()
                        .faqNo(entity.getFaqNo())
                        .category(entity.getCategory())
                        .title(entity.getTitle())
                        .content(entity.getContent())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .adminNo(entity.getAdminEntity().getAdminNo())
                        .build())
                .collect(Collectors.toList());

        return PageResponseDTO.<FaqDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)faqEntityPage.getTotalElements())
                .build();


    }

    //read상세
    public FaqDTO read(Long faqNo){
        Optional<FaqEntity> temp = faqRepository.findById(faqNo);
        return modelMapper.map(temp,FaqDTO.class);
    }


    //update수정
    public FaqDTO update(FaqDTO updatefaqDTO){
        Optional<FaqEntity> temp = faqRepository.findById(updatefaqDTO.getFaqNo());
        if (temp.isPresent()){
            FaqEntity faqEntity = modelMapper.map(updatefaqDTO,FaqEntity.class);
            faqRepository.save(faqEntity);
            FaqDTO faqDTO = modelMapper.map(faqEntity, FaqDTO.class);

            return faqDTO;
        }

        return null;
    }

    //delete삭제
    public void delete(Long faqNo){
        faqRepository.deleteById(faqNo);
    }


}
