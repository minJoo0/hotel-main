package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.DTO.PaymentDTO;
import com.exam.hotelmanage1.DTO.PaymentItemDTO;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.PaymentEntity;
import com.exam.hotelmanage1.Entity.PaymentItemEntity;
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
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderDetailService {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final NoticeRepository noticeRepository;
    private final HotelImgService hotelImgService;
    private final CartRepository cartRepository;
    private final MenuImgService menuImgService;
    private final PaymentRepository paymentRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final HotelService hotelService;

    public PageResponseDTO<PaymentDTO> paymentItemListAdmin(PaymentDTO paymentDTO, PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("paymentNo");
        // Specification 생성
        Specification<PaymentEntity> spec = Specification.where(OrderDetailSpecification.equalsPaymentType(PaymentType.ITEM));
        if (paymentDTO.getAdminNo() != null){
            spec = spec.and(OrderDetailSpecification.equalsAdminNo(paymentDTO.getAdminNo()));
        }
        if (paymentDTO.getStoreNo() != null){
            spec = spec.and(OrderDetailSpecification.equalsStoreNo(paymentDTO.getStoreNo()));
        }
        if (paymentDTO.getPaymentStatus() != null){
            spec = spec.and(OrderDetailSpecification.equalsPaymentStatus(paymentDTO.getPaymentStatus()));
        }
        if (paymentDTO.getHotelName() != null){
            spec = spec.and(OrderDetailSpecification.equalsHotelName(paymentDTO.getHotelName()));
        }
        if (paymentDTO.getUserName() != null){
            spec = spec.and(OrderDetailSpecification.equalsUserName(paymentDTO.getUserName()));
        }
        if (paymentDTO.getRoomName() != null){
            spec = spec.and(OrderDetailSpecification.equalsRoomName(paymentDTO.getRoomName()));
        }
        if (paymentDTO.getUserEmail() != null){
            spec = spec.and(OrderDetailSpecification.equalsUserEmail(paymentDTO.getUserEmail()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<PaymentEntity> paymentEntityPage = paymentRepository.findAll(spec,pageable);
        // NoticeEntity의 Stream을 NoticeDTO로 변환 후 List로 수집
        List<PaymentDTO> dtoList = paymentEntityPage.getContent().stream()
                .map(entity -> {
                    PaymentDTO paymentDTO1 = PaymentDTO.builder()
                            .adminNo(entity.getReserveEntity().getRoomEntity().getHotelEntity().getAdminEntity().getAdminNo())
                            .hotelNo(entity.getReserveEntity().getRoomEntity().getHotelEntity().getHotelNo())
                            .hotelName(entity.getReserveEntity().getRoomEntity().getHotelEntity().getName())
                            .userEmail(entity.getUserEntity().getEmail())
                            .userName(entity.getUserEntity().getName())
                            .userNickName(entity.getUserEntity().getNickName())
                            .roomNo(entity.getReserveEntity().getRoomEntity().getRoomNo())
                            .roomName(entity.getReserveEntity().getRoomEntity().getName())
                            .paymentNo(entity.getPaymentNo())
                            .grandTotalPrice(entity.getGrandTotalPrice())
                            .userNo(entity.getUserEntity().getUserNo())
                            .paymentStatus(entity.getPaymentStatus())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .build();
                    List<PaymentItemDTO> paymentItemDTOs = paymentItemRepository.findByPaymentEntity_PaymentNo(entity.getPaymentNo())
                            .stream()
                            .map(paymentItemEntity -> {
                                return PaymentItemDTO.builder()
                                        .paymentNo(paymentItemEntity.getPaymentEntity().getPaymentNo())
                                        .paymentCount(paymentItemEntity.getPaymentCount())
                                        .menuNo(paymentItemEntity.getMenuEntity().getMenuNo())
                                        .unitPrice(paymentItemEntity.getUnitPrice())
                                        .totalPrice(paymentItemEntity.getTotalPrice())
                                        .menuName(paymentItemEntity.getMenuEntity().getName())
                                        .storeNo(paymentItemEntity.getMenuEntity().getCategory1().getStoreEntity().getStoreNo())
                                        .storeName(paymentItemEntity.getMenuEntity().getCategory1().getStoreEntity().getName())
                                        .regDate(paymentItemEntity.getRegDate())
                                        .modDate(paymentItemEntity.getModDate())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    paymentDTO1.setPaymentItemDTO(paymentItemDTOs);
                    log.info(paymentDTO1);
                    return paymentDTO1;
                })
                .collect(Collectors.toList());
        return PageResponseDTO.<PaymentDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)paymentEntityPage.getTotalElements())
                .build();
    }
    public PageResponseDTO<PaymentDTO> paymentRoomListAdmin(PaymentDTO paymentDTO, PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("paymentNo");
        // Specification 생성
        Specification<PaymentEntity> spec = Specification.where(OrderDetailSpecification.equalsPaymentType(PaymentType.ROOM));
        if (paymentDTO.getAdminNo() != null){
            spec = spec.and(OrderDetailSpecification.equalsAdminNo(paymentDTO.getAdminNo()));
        }
        if (paymentDTO.getPaymentStatus() != null){
            spec = spec.and(OrderDetailSpecification.equalsPaymentStatus(paymentDTO.getPaymentStatus()));
        }
        if (paymentDTO.getHotelName() != null){
            spec = spec.and(OrderDetailSpecification.equalsHotelName(paymentDTO.getHotelName()));
        }
        if (paymentDTO.getUserName() != null){
            spec = spec.and(OrderDetailSpecification.equalsUserName(paymentDTO.getUserName()));
        }
        if (paymentDTO.getRoomName() != null){
            spec = spec.and(OrderDetailSpecification.equalsRoomName(paymentDTO.getRoomName()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<PaymentEntity> paymentEntityPage = paymentRepository.findAll(spec,pageable);
        // NoticeEntity의 Stream을 NoticeDTO로 변환 후 List로 수집
        List<PaymentDTO> dtoList = paymentEntityPage.getContent().stream()
                .map(entity -> {
                    PaymentDTO paymentDTO1 = PaymentDTO.builder()
                            .adminNo(entity.getReserveEntity().getRoomEntity().getHotelEntity().getAdminEntity().getAdminNo())
                            .hotelNo(entity.getReserveEntity().getRoomEntity().getHotelEntity().getHotelNo())
                            .hotelName(entity.getReserveEntity().getRoomEntity().getHotelEntity().getName())
                            .userEmail(entity.getUserEntity().getEmail())
                            .userName(entity.getUserEntity().getName())
                            .userNickName(entity.getUserEntity().getNickName())
                            .roomNo(entity.getReserveEntity().getRoomEntity().getRoomNo())
                            .roomName(entity.getReserveEntity().getRoomEntity().getName())
                            .paymentNo(entity.getPaymentNo())
                            .grandTotalPrice(entity.getGrandTotalPrice())
                            .userNo(entity.getUserEntity().getUserNo())
                            .paymentStatus(entity.getPaymentStatus())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .build();
                    List<PaymentItemDTO> paymentItemDTOs = paymentItemRepository.findByPaymentEntity_PaymentNo(entity.getPaymentNo())
                            .stream()
                            .map(paymentItemEntity -> {
                                return PaymentItemDTO.builder()
                                        .paymentNo(paymentItemEntity.getPaymentEntity().getPaymentNo())
                                        .paymentCount(paymentItemEntity.getPaymentCount())
                                        .menuNo(paymentItemEntity.getMenuEntity().getMenuNo())
                                        .unitPrice(paymentItemEntity.getUnitPrice())
                                        .totalPrice(paymentItemEntity.getTotalPrice())
                                        .menuName(paymentItemEntity.getMenuEntity().getName())
                                        .storeNo(paymentItemEntity.getMenuEntity().getCategory1().getStoreEntity().getStoreNo())
                                        .storeName(paymentItemEntity.getMenuEntity().getCategory1().getStoreEntity().getName())
                                        .regDate(paymentItemEntity.getRegDate())
                                        .modDate(paymentItemEntity.getModDate())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    paymentDTO1.setPaymentItemDTO(paymentItemDTOs);
                    log.info(paymentDTO1);
                    return paymentDTO1;
                })
                .collect(Collectors.toList());
        return PageResponseDTO.<PaymentDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)paymentEntityPage.getTotalElements())
                .build();
    }
}
