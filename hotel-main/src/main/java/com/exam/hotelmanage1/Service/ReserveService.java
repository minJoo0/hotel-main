package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.Constant.ReservationStatus;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.DTO.PaymentDTO;
import com.exam.hotelmanage1.DTO.ReserveDTO;
import com.exam.hotelmanage1.Entity.ReserveEntity;
import com.exam.hotelmanage1.Entity.RoomEntity;
import com.exam.hotelmanage1.Entity.UserEntity;
import com.exam.hotelmanage1.Repository.ReserveRepository;
import com.exam.hotelmanage1.Repository.ReserveSpecification;
import com.exam.hotelmanage1.Repository.RoomRepository;
import com.exam.hotelmanage1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;


    //예약 삭제
    public void reserveDelete(Long reserveNo) {
        reserveRepository.deleteById(reserveNo);
    }

    //예약 상세 정보
    public ReserveDTO reserveRead(Long reserveNo) {
        //예약 조회
        Optional<ReserveEntity> reserveEntity = reserveRepository.findById(reserveNo);
        if (reserveEntity.isPresent()) {    //있다면
            //조회한 entity의 모든 값을 변수에 저장
            ReserveEntity entity = reserveEntity.get();
            log.info("serveiceRead의 entity : " + entity);
            //dto로 변환
            ReserveDTO reserveDTO = reserveEntity.map(mapper->modelMapper.map(entity, ReserveDTO.class))
                    .orElse(null);
            log.info("serveiceRead의 reserveDTO : " + reserveDTO);
            return reserveDTO;
        }
        return null;

    }


    //객실 예약(객실,유저의 no값이 필요)
    public ReserveDTO reserveInsert(ReserveDTO reserveDTO) {
        //예약할 시작일과 종료일을 이용해서 기존에 등록 여부를 조회한다.
        Optional<ReserveEntity> search = reserveRepository.revDate(reserveDTO.getStartDate(), reserveDTO.getEndDate(),reserveDTO.getRoomNo());

        if (search.isPresent()) {   //겹치는 날짜가 있으면
            // todo 날짜가 겹쳤을 때 오류 페이지 처리
            //  현재 유저 페이지에서 예약이 겹치는 날짜의 객실은 목록에서 나오지 않게 만듬
            return null;
        }

        //객실no 조회
        Optional<RoomEntity> roomEntity = roomRepository.findById(reserveDTO.getRoomNo());
        //유저no 조회
        Optional<UserEntity> userEntity = userRepository.findById(reserveDTO.getUserNo());

        //입력받은 DTO값을 entity로 변환
        ReserveEntity reserveEntity = modelMapper.map(reserveDTO, ReserveEntity.class);


        //entity에는 roomNo대신 roomEntity가 사용되었기 때문에 entity에 id 값을 저장
        reserveEntity.setRoomEntity(roomEntity.get());
        reserveEntity.setUserEntity(userEntity.get());
        //todo 현재는 결제를 만들지 않았기 때문에 예약이 실행되면 '예약됨'으로 하드코딩
        reserveEntity.setStatus(ReservationStatus.RESERVED);
        //db에 entity값을 저장
        ReserveEntity resultEntity = reserveRepository.save(reserveEntity);
        //다시 dto로 반환
        ReserveDTO dto = modelMapper.map(resultEntity, ReserveDTO.class);
        return dto;
    }


    //객실 예약날짜 수정
    public ReserveDTO reserveUpdate(ReserveDTO reserveDTO) {
        //수정할 예약이 있는지 조회
        Optional<ReserveEntity> reserveEntity = reserveRepository.findById(reserveDTO.getReserveNo());

        //예약이 있다면
        if (reserveEntity.isPresent()) {
            //db에 저장된 예약정보를 entity로 저장
            ReserveEntity reserve = reserveEntity.get();

            // 수정하고자 하는 시작일과 종료일로 기존 예약과 겹치는지 확인
            Optional<ReserveEntity> search = reserveRepository.revDate(reserveDTO.getStartDate(),
                    reserveDTO.getEndDate(),reserveDTO.getRoomNo());

            // 겹치는 예약이 있고, 그 예약의 ID가 현재 업데이트하려는 예약의 ID와 다를 경우(즉, 다른 예약과 겹침)
            if (search.isPresent() && !search.get().getReserveNo().equals(reserveDTO.getReserveNo())) {
                log.info("예약날짜가 겹쳐서 예약 불가능");
                throw new IllegalStateException("예약날짜가 겹쳐서 예약 불가능");
            }

            //겹치는 예약이 없다면 예약날짜 수정
            reserve.setStartDate(reserveDTO.getStartDate());
            reserve.setEndDate(reserveDTO.getEndDate());
            log.info("수정된 엔티티 : " + reserve);
            log.info("수정된 엔티티 룸 : " + reserve.getRoomEntity().getRoomNo());

            //수정된 엔티티 저장
            ReserveEntity updateReserve = reserveRepository.save(reserve);
            //수정된 엔티티를 DTO로 변환해 반환
            return modelMapper.map(updateReserve, ReserveDTO.class);

        } //예약이 없다면
        else {
            log.info("해당 ID를 가진 예약이 없습니다.");
            throw new IllegalArgumentException("해당 ID를 가진 예약이 없습니다.");
        }
    }





    // 그냥 목록
    public List<ReserveDTO> list() {
        List<ReserveEntity> reserveEntities = reserveRepository.findAll();
        List<ReserveDTO> reserveDTOS = Arrays.asList(modelMapper.map(reserveEntities, ReserveDTO[].class));
        return reserveDTOS;
    }

    //페이징처리 + 검색 목록
    public PageResponseDTO<ReserveDTO> PageList(ReserveDTO reserveDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("reserveNo");

        // Specification 생성
        Specification<ReserveEntity> spec = Specification.where(ReserveSpecification.paymentTypeIsRoom(PaymentType.ROOM));

        //호텔이름으로 검색
        if (reserveDTO.getHotelName() != null) {
            spec = spec.and(ReserveSpecification.hotelNameContains(reserveDTO.getHotelName()));
        }

        //객실 이름으로 검색
        if (reserveDTO.getRoomName() != null) {
            spec = spec.and(ReserveSpecification.roomNameContains(reserveDTO.getRoomName()));
        }

        //유저 이메일로 검색
        if (reserveDTO.getUserEmail() != null) {
            spec = spec.and(ReserveSpecification.useEmailContains(reserveDTO.getUserEmail()));
        }
        //예약 상태로 검색
        if (reserveDTO.getStatus() != null) {
            spec = spec.and(ReserveSpecification.equalsStatus(reserveDTO.getStatus()));
        }
//        //예약 시작일로 검색
//        if (reserveDTO.getStartDate() != null) {
//            spec = spec.and(ReserveSpecification.equalsStartDate(reserveDTO.getStartDate()));
//        }
//        //예약 종료일로 검색
//        if (reserveDTO.getEndDate() != null) {
//            spec = spec.and(ReserveSpecification.equalsEndDate(reserveDTO.getEndDate()));
//        }

        // 예약 기간을 설정해서 검색
        if (reserveDTO.getStartDate() != null || reserveDTO.getEndDate() != null) {
            if (reserveDTO.getStartDate() != null && reserveDTO.getEndDate() != null) {
                spec = spec.and(ReserveSpecification.isBetweenDates(reserveDTO.getStartDate(), reserveDTO.getEndDate()));
            } else if (reserveDTO.getStartDate() != null) {
                spec = spec.and(ReserveSpecification.isAfterStartDate(reserveDTO.getStartDate()));
            } else if (reserveDTO.getEndDate() != null) {
                spec = spec.and(ReserveSpecification.isBeforeEndDate(reserveDTO.getEndDate()));
            }
        }


        //userNo로 예약 목록을 필터링 하기 위해 작성
        if (reserveDTO.getUserNo() != null) {
            spec = spec.and(ReserveSpecification.equalsUserNo(reserveDTO.getUserNo()));
        }
        //hotelNo 예약 목록을 필터링
        if (reserveDTO.getHotelNo() != null) {
            spec = spec.and(ReserveSpecification.equalsHotelNo(reserveDTO.getHotelNo()));
        }
        if (reserveDTO.getAdminNo() != null) {
            spec = spec.and(ReserveSpecification.equalsAdminNo(reserveDTO.getAdminNo()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<ReserveEntity> reserveEntityPage = reserveRepository.findAll(spec, pageable);

//        List<ReserveDTO> dtoList = reserveEntityPage.getContent().stream()
//                .map(entity -> modelMapper.map(entity, ReserveDTO.class))
//                .collect(Collectors.toList());
        List<ReserveDTO> dtoList = reserveEntityPage.getContent().stream()
                .map(entity -> ReserveDTO.builder()
                        .reserveNo(entity.getReserveNo())
                        .status(entity.getStatus())
                        .startDate(entity.getStartDate())
                        .endDate(entity.getEndDate())
                        .regDate(entity.getRegDate())//예약한날짜를 출력하기위해 추가
                        .roomNo(entity.getRoomEntity().getRoomNo())
                        .roomName(entity.getRoomEntity().getName())
                        .userNo(entity.getUserEntity().getUserNo())
                        .userEmail(entity.getUserEntity().getEmail())
                        .hotelNo(entity.getRoomEntity().getHotelEntity().getHotelNo())
                        .hotelName(entity.getRoomEntity().getHotelEntity().getName())
                        .people(entity.getPeople())
                        .paymentDTOList(Optional.ofNullable(entity.getPaymentEntity())
                                .map(entities -> entities.stream()
                                        .filter(payment -> PaymentStatus.COMPLETE.equals(payment.getPaymentStatus())) // paymentStatus가 COMPLETE인 것만 필터링
                                        .map(payment -> {
                                            PaymentDTO dto = new PaymentDTO();
                                            dto.setPaymentNo(payment.getPaymentNo());
                                            dto.setGrandTotalPrice(payment.getGrandTotalPrice());
                                            dto.setPaymentType(payment.getPaymentType());
                                            dto.setPaymentStatus(payment.getPaymentStatus()); // paymentStatus 필드 추가
                                            return dto;
                                        }).collect(Collectors.toList()))
                                .orElse(Collections.emptyList()))
                        .build())
                .collect(Collectors.toList());

        return PageResponseDTO.<ReserveDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)reserveEntityPage.getTotalElements())
                .build();
    }

    public List<ReserveDTO> findByUserNoAndHotelNo(Long userNo, Long hotelNo){
        List<ReserveEntity> reserveEntityList = reserveRepository.findByUserNoAndHotelNo(userNo, hotelNo);
        List<ReserveDTO> reserveDTOList = reserveEntityList.stream()
                .map(reserveEntity -> ReserveDTO.builder()
                        .reserveNo(reserveEntity.getReserveNo())
                        .userNo(reserveEntity.getUserEntity().getUserNo())
                        .hotelNo(reserveEntity.getRoomEntity().getHotelEntity().getHotelNo())
                        .startDate(reserveEntity.getStartDate())
                        .endDate(reserveEntity.getEndDate())
                        .status(reserveEntity.getStatus())
                        .roomName(reserveEntity.getRoomEntity().getName())
                        .people(reserveEntity.getPeople())
                        .build())
                .collect(Collectors.toList());
        return reserveDTOList;
    }






}
