package com.exam.hotelmanage1.Service;


import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.RoomCodeEntity;
import com.exam.hotelmanage1.Entity.RoomEntity;
import com.exam.hotelmanage1.Entity.RoomImgEntity;
import com.exam.hotelmanage1.Repository.*;
//import com.exam.hotelmanage1.Repository.RoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Log4j2
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    private final RoomCodeRepository roomCodeRepository;
    private final RoomImgService roomImgService;

    //객실 등록
    public RoomEntity register(RoomDTO roomDTO) {
        RoomEntity roomEntity = modelMapper.map(roomDTO, RoomEntity.class);
        log.info(roomEntity);
        RoomEntity result = roomRepository.save(roomEntity);

        return result;
    }


    //객실 수정
    public RoomEntity update(RoomDTO roomDTO) {
        //수정할 호텔의 방번호 조회
        Optional<RoomEntity> search = roomRepository.findById(roomDTO.getRoomNo());

        if (search.isPresent()) {
            RoomEntity roomEntity = search.get();
            modelMapper.map(roomDTO, roomEntity); // 기존 인스턴스 업데이트
            RoomEntity result = roomRepository.save(roomEntity);
            return result;
        }
        return null;
    }

    //삭제
    public void delete(Long id) {
        Optional<RoomEntity> roomEntity = roomRepository.findById(id);
        if (roomEntity.isPresent()) {
            roomRepository.deleteById(id);
        }
    }

    //읽기
    public RoomDTO read(Long id) {
        //읽을 값을 id를 통해 찾는다.
        Optional<RoomEntity> roomEntity = roomRepository.findById(id);
        if (roomEntity.isPresent()) { //있다면
            RoomEntity entity = roomEntity.get();
            log.info(entity);
            RoomDTO result = roomEntity.map(mapper -> modelMapper.map(entity, RoomDTO.class))
                    .orElse(null);
            log.info(result);

            List<String> codeGroupAndCodeNos = entity.getRoomCodeEntity().stream()
                    .sorted(Comparator.comparingInt(RoomCodeEntity::getOrderIdx)) // orderIdx에 따라 정렬
                    .map(roomCodeEntity ->
                            roomCodeEntity.getCodeGroupName() + ":" + roomCodeEntity.getCodeName() + "  /  ")
                    .collect(Collectors.toList());
            String codeNoString = String.join("", codeGroupAndCodeNos).trim();
            result.setCodeFullName(codeNoString);

            List<RoomImgEntity> roomImgEntities = roomImgService.findByRoomEntityRoomNo(entity.getRoomNo());
            String imageUrl = "";
            if (!roomImgEntities.isEmpty()) {
                RoomImgEntity firstMenuImg = roomImgEntities.get(0);

                imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

                result.setImageUrl(imageUrl);
                log.info(result);
            }
            return result;
        }
        return null;
    }
    // 방 목록+페이징 처리
    public PageResponseDTO<RoomDTO> PageList(RoomDTO roomDTO, PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("no");

        // Specification 생성
        Specification<RoomEntity> spec = Specification.where(null);

        //호텔에 속한 객실 목록 검색
        if (roomDTO.getHotelNo() != null) {
            spec = spec.and(RoomSpecification.equalsHotelNo(roomDTO.getHotelNo()));
        }
        //
        if (roomDTO.getAdminNo() != null) {
            spec = spec.and(RoomSpecification.equalsAdminNo(roomDTO.getAdminNo()));
        }
        //예약 가능한 객실 목록 검색
        if (roomDTO.getStartDate() != null & roomDTO.getEndDate() != null) {
            spec = spec.and(RoomSpecification.isAvailable(roomDTO.getStartDate(), roomDTO.getEndDate()));
        }


        // 방 페이지 조회
        Page<RoomEntity> roomEntityPage = roomRepository.findAll(spec, pageable);

        // Entity를 DTO로 변환
//        List<RoomDTO> dtoList = roomEntityPage.getContent().stream()
//                .map(entity -> modelMapper.map(entity, RoomDTO.class))
//                .collect(Collectors.toList());
        List<RoomDTO> dtoList = roomEntityPage.getContent().stream()
                .map(entity -> {
                    // RoomEntity와 연결된 모든 RoomCodeEntity의 codeGroupNo와 codeNo를 묶어서 리스트로 수집
                    List<String> codeGroupAndCodeNos = entity.getRoomCodeEntity().stream()
                            .sorted(Comparator.comparingInt(RoomCodeEntity::getOrderIdx)) // orderIdx에 따라 정렬
                            .map(roomCodeEntity ->
                                    roomCodeEntity.getCodeGroupName() + ":" + roomCodeEntity.getCodeName() + "  /  ")
                            .collect(Collectors.toList());
                    String codeNoString = String.join("", codeGroupAndCodeNos).trim();
                    List<RoomImgEntity> roomImgEntities = roomImgService.findByRoomEntityRoomNo(entity.getRoomNo());
                    String imageUrl = "";
                    if (!roomImgEntities.isEmpty()) {
                        RoomImgEntity firstMenuImg = roomImgEntities.get(0);

                        imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

                    }
                    log.info(imageUrl);



                    return RoomDTO.builder()
                            .roomNo(entity.getRoomNo())
                            .no(entity.getFullCodes() + entity.getRoomNo()) // 이 부분은 예시로 남겨둔 것입니다. 실제 필요에 따라 수정하세요.
                            .hotelNo(entity.getHotelEntity().getHotelNo())
                            .hotelName(entity.getHotelEntity().getName())
                            .adminNo(entity.getHotelEntity().getAdminEntity().getAdminNo())
                            .name(entity.getName())
                            .content(entity.getContent())
                            .price(entity.getPrice())
                            .codeFullName(codeNoString)
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());

        // 페이지 응답 DTO 생성
        return PageResponseDTO.<RoomDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) roomEntityPage.getTotalElements())
                .build();
    }



    //호텔 별로 방이 몇개 있는지 계산
    public Long countRoom(Long hotelNo) {
        return roomRepository.countByHotelEntityHotelNo(hotelNo);
    }

    //룸 번호가 자동생성 후 저장되도록 설정
    public void saveRoom() {

    }

    public List<RoomDTO> findRoomsByHotelNo(Long hotelNo, LocalDate date){
        List<RoomEntity> roomEntities = roomRepository.findRoomsByHotelNo(hotelNo);
        log.info(roomEntities);
        List<RoomDTO> roomDTOS = roomEntities.stream()
                .map(entity -> {
                    List<ReserveDTO> reserveDTOS = entity.getReserveEntities().stream()
                            .map(reserveEntity -> {
                                // startDate와 endDate 사이에 date가 존재하는지 확인
                                boolean isDateWithin = (date.isAfter(reserveEntity.getStartDate()) || date.isEqual(reserveEntity.getStartDate()))
                                        && (date.isBefore(reserveEntity.getEndDate()) || date.isEqual(reserveEntity.getEndDate()));
                                return ReserveDTO.builder()
                                        .reserveNo(reserveEntity.getReserveNo())
                                        .status(isDateWithin ? reserveEntity.getStatus() : null) // 조건에 따라 status 설정
                                        .startDate(reserveEntity.getStartDate())
                                        .endDate(reserveEntity.getEndDate())
                                        .build();
                            })
                            .toList();
                    return RoomDTO.builder()
                            .roomNo(entity.getRoomNo())
                            .name(entity.getName())
                            .hotelNo(entity.getHotelEntity().getHotelNo())
                            .hotelName(entity.getHotelEntity().getName())
                            .adminNo(entity.getHotelEntity().getAdminEntity().getAdminNo())
                            .name(entity.getName())
                            .content(entity.getContent())
                            .price(entity.getPrice())
                            .reserveDTOList(reserveDTOS)
                            .build();

                })
                .collect(Collectors.toList());
        log.info(roomDTOS);
        return roomDTOS;

    }

    public RoomDTO findRoomByRoomNo(Long roomNo, LocalDate date) {
        RoomEntity roomEntity = roomRepository.findRoomByRoomNo(roomNo);
        List<ReserveDTO> reserveDTOS = roomEntity.getReserveEntities().stream()
                .filter(reserveEntity ->
                        // startDate와 endDate 사이에 date가 존재하는지 필터링
                        (date.isAfter(reserveEntity.getStartDate()) || date.isEqual(reserveEntity.getStartDate())) &&
                                (date.isBefore(reserveEntity.getEndDate()) || date.isEqual(reserveEntity.getEndDate()))
                )
                .map(reserveEntity -> ReserveDTO.builder()
                        .reserveNo(reserveEntity.getReserveNo())
                        .status(reserveEntity.getStatus()) // 필터링된 엔티티이므로 조건 확인 없이 상태 설정
                        .startDate(reserveEntity.getStartDate())
                        .endDate(reserveEntity.getEndDate())
                        .userEmail(reserveEntity.getUserEntity().getEmail())
                        .userName(reserveEntity.getUserEntity().getName())
                        .build()
                )
                .toList();
        RoomDTO roomDTO = RoomDTO.builder()
                .roomNo(roomEntity.getRoomNo())
                .name(roomEntity.getName())
                .reserveDTOList(reserveDTOS)
                .build();

        return roomDTO;
    }


}
