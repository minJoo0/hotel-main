package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.CodeEntity;
import com.exam.hotelmanage1.Entity.CodeGroupEntity;
import com.exam.hotelmanage1.Repository.CodeGroupRepository;
import com.exam.hotelmanage1.Repository.CodeRepository;
import com.exam.hotelmanage1.Repository.CodeSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/*
프그램명 : 하위코드 CRUD
작성자 : 김준수
기능 : 코드 service
작업내용 :
 */
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CodeService {
    private final ModelMapper modelMapper;
    private final CodeRepository codeRepository;
    private final CodeGroupRepository codeGroupRepository;


    //등록
    public CodeEntity register(CodeDTO codeDTO) {
        log.info("코드DTO는 이것임"+codeDTO);
        CodeEntity codeEntity = modelMapper.map(codeDTO, CodeEntity.class);

        CodeEntity result = codeRepository.save(codeEntity);
        log.info("저장" + result);
        return result;
    }

    //수정
    public CodeDTO update(CodeDTO codeDTO) {
        //코드 조회
        log.info(codeDTO);
        Optional<CodeEntity> search = codeRepository.findById(codeDTO.getCodeNo());
        log.info("수정" + codeDTO.toString());

        if (search.isPresent()) {   //코드 이미 있으면 다시 등록
            //코드 그룹 조회
            CodeEntity codeEntity = modelMapper.map(codeDTO, CodeEntity.class);
            //조회한 코드그룹정보를 추가 저장
            CodeEntity result = codeRepository.save(codeEntity);
            //엔티티를 DTO로 만들어 보내줌
            return modelMapper.map(result, CodeDTO.class);
        }

        return null;
    }

    //삭제
    public void delete(Long codeNo) {
        log.info(codeNo);
        //삭제할 코드가 있는지 조회
        Optional<CodeEntity> codeEntity = codeRepository.findById(codeNo);

        if (codeEntity.isPresent()) {   //코드가 있으면 삭제
            codeRepository.deleteById(codeNo);
        }
    }

    //읽기
    public CodeDTO read(Long codeNo) {
        //요청 번호를 조회
        Optional<CodeEntity> search = codeRepository.findById(codeNo);
        //엔티티를 dto로 변환
        return modelMapper.map(search, CodeDTO.class);
    }


    //전체 조회
//    public List<CodeDTO> list() {
//        List<CodeEntity> codeEntities = codeRepository.findAll();
//
//        List<CodeDTO> codeDTOS = codeEntities.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//        return codeDTOS;
//    }

    //자식 dto에 부모 dto의 값을 연결하기 위해 필요!!
//    private CodeDTO convertToDTO(CodeEntity codeEntity) {
//        CodeDTO codeDTO = modelMapper.map(codeEntity, CodeDTO.class);
//
//        codeDTO.setCodeGroupDTO(modelMapper.map(codeEntity.getCodeGroupEntity(), CodeGroupDTO.class));
//        return codeDTO;
//    }


    //목록+페이지+검색

    public PageResponseDTO<CodeDTO> PageList(CodeDTO codeDTO,
                                             PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("codeNo");

        Specification<CodeEntity> spec = Specification.where(null);


        if (codeDTO.getName() != null) {
            spec = spec.and(CodeSpecification.equalsName(codeDTO.getName()));
        }
        if (codeDTO.getCodeGroupNo() != null) {
            spec = spec.and(CodeSpecification.equalsCodeGroupNo(codeDTO.getCodeGroupNo()));
        }
        if (codeDTO.getAdminNo() != null) {
            spec = spec.and(CodeSpecification.equalsAdminNo(codeDTO.getAdminNo()));
        }

        Page<CodeEntity> codeEntityPage = codeRepository.findAll(spec, pageable);

        List<CodeDTO> dtoList = codeEntityPage.getContent().stream()
                .map(entity -> CodeDTO.builder()
                        .codeNo(entity.getCodeNo())
                        .codeGroupNo(entity.getCodeGroupEntity().getCodeGroupNo())
                        .name(entity.getName())
                        .codeGroupName(entity.getCodeGroupEntity().getName())
                        .codeGroup(entity.getCodeGroupEntity().getCodeGroup())
                        .code(entity.getCode())
                        .fullCode(entity.getCodeGroupEntity().getCodeGroup() + entity.getCode())
                        .build())
                .collect(Collectors.toList());

        return PageResponseDTO.<CodeDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) codeEntityPage.getTotalElements())
                .build();
    }

    public List<CodeDTO> findByCodeGroupEntityCodeGroupNo(Long codeGroupNo) {
        List<CodeEntity> allEntity = codeRepository.findByCodeGroupEntity_CodeGroupNo(codeGroupNo);
        return allEntity.stream()
                .map(entity -> modelMapper.map(entity, CodeDTO.class))
                .collect(Collectors.toList());


    }

    public List<String> findNameByCodeNoIn(List<Long> codeNos){
        return codeRepository.findNameByCodeNoIn(codeNos);
    }

    public List<String> findFullCodeByCodeNoIn(List<Long> codeNos){

        return codeRepository.findFullCodeByCodeNoIn(codeNos);


    }

}