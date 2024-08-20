package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.CodeGroupEntity;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.NoticeEntity;
import com.exam.hotelmanage1.Repository.NoticeRepository;
import com.exam.hotelmanage1.Repository.NoticeSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*================

작업명:공지사항관리crud
작업자:김민주
작업일:2024-04-09
작업내용:crud기능구현부 정상동작

===============*/

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;
    private final  AuthService authService;

    //insert등록
    public void insert(NoticeDTO noticeDTO){
        NoticeEntity noticeEntity = modelMapper.map(noticeDTO,NoticeEntity.class);

        noticeRepository.save(noticeEntity);
    }



    //read상세
    public NoticeDTO read(Long noticeNo){
        Optional<NoticeEntity> temp = noticeRepository.findById(noticeNo);
        return modelMapper.map(temp,NoticeDTO.class);
    }


    //update수정
    public NoticeDTO update(NoticeDTO updateNoticeDTO){
        Optional<NoticeEntity> temp = noticeRepository.findById(updateNoticeDTO.getNoticeNo());
        if (temp.isPresent()){
            NoticeEntity noticeEntity = modelMapper.map(updateNoticeDTO,NoticeEntity.class);
            noticeRepository.save(noticeEntity);
            NoticeDTO noticeDTO = modelMapper.map(noticeEntity, NoticeDTO.class);

            return noticeDTO;
        }

        return null;
    }

    //delete삭제
    public void delete(Long noticeNo){
        noticeRepository.deleteById(noticeNo);
    }

    //list조회
    public PageResponseDTO<NoticeDTO> list(NoticeDTO noticeDTO, PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("noticeNo");

        // Specification 생성
        Specification<NoticeEntity> spec = Specification.where(null);

        if (noticeDTO.getTitle() != null){
            spec = spec.and(NoticeSpecification.equalsTitle(noticeDTO.getTitle()));
        }
        if (noticeDTO.getContent() != null){
            spec = spec.and(NoticeSpecification.equalsContent(noticeDTO.getContent()));
        }
        if (noticeDTO.getAdminNo() != null){
            spec = spec.and(NoticeSpecification.equalsAdminNo(noticeDTO.getAdminNo()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<NoticeEntity> noticeEntityPage = noticeRepository.findAll(spec,pageable);

            // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
            List<NoticeDTO> dtoList = noticeEntityPage.getContent().stream()
                            .map(entity -> {
                    String hotelName = Optional.ofNullable(entity.getHotelEntity())
                            .map(HotelEntity::getName)
                            .orElse(null);
                    return NoticeDTO.builder()
                            .noticeNo(entity.getNoticeNo())
                            .adminNo(entity.getAdminEntity().getAdminNo())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .title(entity.getTitle())
                            .content(entity.getContent())
                            .hotelName(hotelName)
                            .build();
                })
                    .collect(Collectors.toList());

            return PageResponseDTO.<NoticeDTO>withAll()
                    .pageRequestDTO(pageRequestDTO)
                    .dtoList(dtoList)
                    .total((int)noticeEntityPage.getTotalElements())
                    .build();

        }



    public List<NoticeDTO> getNoticeByRegion(String sido) {
        if(authService.isAdmin()){
            List<NoticeEntity> allEntity = noticeRepository.findAll();
            return allEntity.stream()
                    .map(entity -> modelMapper.map(entity, NoticeDTO.class))
                    .collect(Collectors.toList());

        }else{
            List<NoticeEntity> entities = noticeRepository.findByHotelEntity_Sido(sido);
            return entities.stream()
                    .map(entity -> modelMapper.map(entity, NoticeDTO.class))
                    .collect(Collectors.toList());
        }

    }






}//NoticeService
