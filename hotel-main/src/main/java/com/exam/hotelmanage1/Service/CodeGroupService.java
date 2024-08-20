package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.CodeGroupDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.Entity.CodeGroupEntity;
import com.exam.hotelmanage1.Entity.RoomCodeEntity;
import com.exam.hotelmanage1.Repository.CodeGroupRepository;
import com.exam.hotelmanage1.Repository.CodeGroupSpecification;
import com.exam.hotelmanage1.Repository.RoomCodeRepository;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
프그램명 : 분류 코드 그룹 CRUD
작성자 : 김준수
기능 : 코드그룹 servicew
작업내용 :
 */

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CodeGroupService {
    private final CodeGroupRepository codeGroupRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final RoomCodeRepository roomCodeRepository;

    //등록
    public void register(CodeGroupDTO codeGroupDTO) {
        CodeGroupEntity codeGroupEntity = modelMapper.map(codeGroupDTO, CodeGroupEntity.class);
        codeGroupRepository.save(codeGroupEntity);
    }

    @Transactional
    public void update(CodeGroupDTO codeGroupDTO) {
        // CodeGroupEntity 찾기
        CodeGroupEntity codeGroupEntity = codeGroupRepository.findById(codeGroupDTO.getCodeGroupNo())
                .orElseThrow(() -> new EntityNotFoundException("CodeGroup not found"));

        // 이전 orderIdx 값 저장 (옵션)
        int previousOrderIdx = codeGroupEntity.getOrderIdx();
        log.info(previousOrderIdx);

        // CodeGroupEntity 업데이트
        modelMapper.map(codeGroupDTO, codeGroupEntity);
        codeGroupRepository.save(codeGroupEntity);

        // 변경된 orderIdx 확인
        int newOrderIdx = codeGroupEntity.getOrderIdx();
        log.info(newOrderIdx);

        // orderIdx가 변경되었는지 확인 (옵션)
        if (previousOrderIdx != newOrderIdx) {
            // 관련된 RoomCodeEntity들의 orderIdx 업데이트
            List<RoomCodeEntity> relatedRoomCodes = roomCodeRepository.findByCodeGroupEntityCodeGroupNo(codeGroupEntity.getCodeGroupNo());
            for (RoomCodeEntity roomCode : relatedRoomCodes) {
                roomCode.setOrderIdx(newOrderIdx);
            }
            roomCodeRepository.saveAll(relatedRoomCodes);
        }

        log.info("Updated CodeGroup No: {}", codeGroupDTO.getCodeGroupNo());
    }


    private void updateRelatedRoomCodeOrderIdx(Long codeGroupNo, int newOrderIdx) {
        // 관련된 RoomCodeEntity들 찾기
        List<RoomCodeEntity> relatedRoomCodes = roomCodeRepository.findByCodeGroupEntityCodeGroupNo(codeGroupNo);
        for (RoomCodeEntity roomCode : relatedRoomCodes) {
            roomCode.setOrderIdx(newOrderIdx);
        }
        roomCodeRepository.saveAll(relatedRoomCodes);
    }

    //삭제
    public void delete(Long id) {
        Optional<CodeGroupEntity> codeGroupEntity = codeGroupRepository.findById(id);
        codeGroupRepository.deleteById(id);
    }

    //상세
    public CodeGroupDTO read(Long id) {
        Optional<CodeGroupEntity> temp = codeGroupRepository.findById(id);
        return modelMapper.map(temp, CodeGroupDTO.class);
    }

    //코드그룹의 하위 코드 갯수 카운트 메소드
    public List<Object[]> countCodesPerGroup() {
        return codeGroupRepository.countCodesPerGroup();
    }

    public Long countByCodeGroupEntityCodeGroupNo(CodeGroupEntity codeGroupEntity){
        Long codeGroupNo = codeGroupEntity.getCodeGroupNo();
        return codeGroupRepository.countByCodeGroupEntityCodeGroupNo(codeGroupNo);
    }


    //목록만
    public List<CodeGroupDTO> codeGroupDTOList() {
        List<CodeGroupEntity> codeGroupEntities = codeGroupRepository.findAll();

        List<CodeGroupDTO> result = Arrays.asList(modelMapper.map(codeGroupEntities, CodeGroupDTO[].class));
        log.info("resultresultresultresult" + result);
        return result;
    }


    //목록+ 페이징
//    public Page<CodeGroupDTO> list(Pageable page) {
//        int currentPage = page.getPageNumber() - 1; //데이터 베이스 페이지 번호 변경
//        int PageLimit = 10; //한 화면에 출력할 데이터 갯수
//
//        Pageable pageable = PageRequest.of(currentPage, PageLimit,
//                Sort.by(Sort.Direction.DESC, "id"));
//
//        //전체 조회
//        Page<CodeGroupEntity> codeGroupEntities = codeGroupRepository.findAll(pageable);
//
//        Page<CodeGroupDTO> codeGroupDTOS = codeGroupEntities.map(data -> modelMapper.map(data, CodeGroupDTO.class));
//        return codeGroupDTOS;
//    }

    //코드그룹 목록+페이징+검색
    public PageResponseDTO<CodeGroupDTO> list(CodeGroupDTO codeGroupDTO,
                                              PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("codeGroupNo");

        Specification<CodeGroupEntity> spec = Specification.where(null);

        if (codeGroupDTO.getCodeGroup() != null){
            spec = spec.and(CodeGroupSpecification.equalsCodeGroup(codeGroupDTO.getCodeGroup()));
        }
        if (codeGroupDTO.getName() != null){
            spec = spec.and(CodeGroupSpecification.equalsName(codeGroupDTO.getName()));
        }
        if (codeGroupDTO.getAdminNo() != null){
            spec = spec.and(CodeGroupSpecification.equalsAdminNo(codeGroupDTO.getAdminNo()));
        }


        Page<CodeGroupEntity> codeGroupEntityPage = codeGroupRepository.findAll(spec,pageable);

        List<CodeGroupDTO> dtoList = codeGroupEntityPage.getContent().stream()
                .map(entity -> CodeGroupDTO.builder()
                        .codeGroupNo(entity.getCodeGroupNo())
                        .codeGroup(entity.getCodeGroup())
                        .name(entity.getName())
                        .orderIdx(entity.getOrderIdx())
                        .category(entity.getCategory())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .codeCount(entity.getCodeEntities() != null ? String.valueOf(entity.getCodeEntities().size()) : "0")
//                        .codeEntities(entity.getCodeEntities())
                        .build())
                .collect(Collectors.toList());

        return PageResponseDTO.<CodeGroupDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)codeGroupEntityPage.getTotalElements())
                .build();
    }

    public List<CodeGroupDTO> getCodeGroupsByAdminNo(Long adminNo) {
        if(authService.isAdmin()){
            List<CodeGroupEntity> allEntity = codeGroupRepository.findAll();
            return allEntity.stream()
                    .map(entity -> modelMapper.map(entity, CodeGroupDTO.class))
                    .collect(Collectors.toList());

        }else{
            List<CodeGroupEntity> entities = codeGroupRepository.findByAdminEntity_AdminNo(adminNo);
            return entities.stream()
                    .map(entity -> modelMapper.map(entity, CodeGroupDTO.class))
                    .collect(Collectors.toList());
        }

    }

    public List<String> findNameByCodeNoIn(List<Long> codeGroupNos){
        return codeGroupRepository.findNameByCodeNoIn(codeGroupNos);

    }

    public List<Integer> findOrderIdxByCodeNoIn(List<Long> codeGroupNos){

        return codeGroupRepository.findOrderIdxByCodeNoIn(codeGroupNos);


    }

    public String findCodeGroupByCodeGroupNo(Long codeGroupNo){

        return codeGroupRepository.findCodeGroupByCodeGroupNo(codeGroupNo);

    }






}
