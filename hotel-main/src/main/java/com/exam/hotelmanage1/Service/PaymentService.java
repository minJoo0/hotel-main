package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.DTO.PaymentDTO;
import com.exam.hotelmanage1.Entity.PaymentEntity;
import com.exam.hotelmanage1.Repository.PaymentRepository;
import com.exam.hotelmanage1.Repository.UserSpecification;
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
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    public PageResponseDTO<PaymentDTO> paymentList(PaymentDTO paymentDTO, PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("paymentNo");
// Specification 생성
        Specification<PaymentEntity> spec = Specification.where(null);
        if (paymentDTO.getUserNo() != null){
            spec = spec.and(UserSpecification.equalsPaymentUserNo(paymentDTO.getUserNo()));
        }


        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<PaymentEntity> paymentEntityPage = paymentRepository.findAll(spec,pageable);
        // NoticeEntity의 Stream을 NoticeDTO로 변환 후 List로 수집
        List<PaymentDTO> dtoList = paymentEntityPage.getContent().stream()
                .map(entity -> {
                    return PaymentDTO.builder()
                            .paymentNo(entity.getPaymentNo())
                            .userNo(entity.getUserEntity().getUserNo())
                            .userEmail(entity.getUserEntity().getEmail())
                            .userName(entity.getUserEntity().getName())
                            .grandTotalPrice(entity.getGrandTotalPrice())
                            .userNickName(entity.getUserEntity().getNickName())
                            .hotelNo(entity.getReserveEntity().getRoomEntity().getHotelEntity().getHotelNo())
                            .adminNo(entity.getReserveEntity().getRoomEntity().getHotelEntity().getAdminEntity().getAdminNo())
                            .roomNo(entity.getReserveEntity().getRoomEntity().getRoomNo())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .build();
                })
                .collect(Collectors.toList());
        return PageResponseDTO.<PaymentDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)paymentEntityPage.getTotalElements())
                .build();
    }
    public  List<PaymentDTO> findByReserveNoAndPaymentTypeAndPaymentStatus(Long reserveNo, PaymentType paymentType, PaymentStatus paymentStatus){
        List<PaymentEntity> paymentEntityList = paymentRepository.findByReserveNoAndPaymentTypeAndPaymentStatus(reserveNo,paymentType,paymentStatus);
        List<PaymentDTO> paymentDTOList = paymentEntityList.stream()
                .map(entity -> modelMapper.map(entity, PaymentDTO.class))
                .toList();
        return paymentDTOList;
    }
    //paymentStatus 를 배달 완료로 변경하는 서비스
    public boolean updatePaymentStatusToDelivery(Long paymentNo) {
        // paymentNo에 해당하는 PaymentEntity 찾기
        Optional<PaymentEntity> paymentEntityOptional = paymentRepository.findById(paymentNo);

        if (paymentEntityOptional.isPresent()) {
            PaymentEntity paymentEntity = paymentEntityOptional.get();
            if(paymentEntity.getPaymentStatus() == PaymentStatus.COMPLETE){
                paymentEntity.setPaymentStatus(PaymentStatus.DELIVERY);

                // 변경된 PaymentEntity 저장
                paymentRepository.save(paymentEntity);
                return true;

            }
            return false;
        } else {
            return false;
        }
    }


}
