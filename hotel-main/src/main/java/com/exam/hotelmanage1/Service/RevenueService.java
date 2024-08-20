package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.RevenueEntity;
import com.exam.hotelmanage1.Repository.RevenueRepository;
import com.exam.hotelmanage1.Repository.RevenueSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class RevenueService {

    private final RevenueRepository revenueRepository;
    private final ModelMapper modelMapper;

    public PageResponseDTO<RevenueDTO> PageList(RevenueDTO revenueDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("revenueNo");

        // Specification 생성
        Specification<RevenueEntity> spec = Specification.where(RevenueSpecification.MenuNoIsNotNull());

        if (revenueDTO.getPaymentDate() != null) {
            spec = spec.and(RevenueSpecification.equalsPaymentDate(revenueDTO.getPaymentDate()));
        }
        if (revenueDTO.getEmail() != null) {
            spec = spec.and(RevenueSpecification.equalsEmail(revenueDTO.getEmail()));
        }
        if (revenueDTO.getUserName() != null) {
            spec = spec.and(RevenueSpecification.equalsUserName(revenueDTO.getUserName()));
        }
        if (revenueDTO.getHotelNo() != null) {
            spec = spec.and(RevenueSpecification.equalsItemHotelNo(revenueDTO.getHotelNo()));
        }
        if (revenueDTO.getStoreNo() != null) {
            spec = spec.and(RevenueSpecification.equalsItemStoreNo(revenueDTO.getStoreNo()));
        }
        if (revenueDTO.getAdminNo() != null) {
            spec = spec.and(RevenueSpecification.equalsItemAdminNo(revenueDTO.getAdminNo()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<RevenueEntity> revenueEntityPage = revenueRepository.findAll(spec, pageable);

        List<RevenueDTO> dtoList = revenueEntityPage.getContent().stream()
                .map(entity -> RevenueDTO.builder()
                        .RevenueNo(entity.getRevenueNo())
                        .depositPrice(entity.getDepositPrice())
                        .fee(entity.getFee())
                        .unitPrice(entity.getPaymentItemEntity().getUnitPrice())
                        .optionPrice(entity.getPaymentItemEntity().getOptionPrice())
                        .totalPrice(entity.getPaymentItemEntity().getTotalPrice())
                        .paymentCount(entity.getPaymentItemEntity().getPaymentCount())
                        .paymentNo(entity.getPaymentItemEntity().getPaymentEntity().getPaymentNo())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .email(entity.getPaymentItemEntity().getPaymentEntity().getUserEntity().getEmail())
                        .userName(entity.getPaymentItemEntity().getPaymentEntity().getUserEntity().getName())
                        .hotelName(entity.getPaymentItemEntity().getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().getName())
                        .storeName(entity.getPaymentItemEntity().getMenuEntity().getCategory1().getStoreEntity().getName())
                        .paymentDate(entity.getPaymentDate())
                        .build())
                .collect(Collectors.toList());

        return PageResponseDTO.<RevenueDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)revenueEntityPage.getTotalElements())
                .build();
    }

    public PageResponseDTO<RevenueDTO> PageRoomList(RevenueDTO revenueDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("revenueNo");

        // Specification 생성
        Specification<RevenueEntity> spec = Specification.where(RevenueSpecification.MenuNoIsNull());

        if (revenueDTO.getPaymentDate() != null) {
            spec = spec.and(RevenueSpecification.equalsPaymentDate(revenueDTO.getPaymentDate()));
        }
        if (revenueDTO.getEmail() != null) {
            spec = spec.and(RevenueSpecification.equalsEmail(revenueDTO.getEmail()));
        }
        if (revenueDTO.getUserName() != null) {
            spec = spec.and(RevenueSpecification.equalsUserName(revenueDTO.getUserName()));
        }
        if (revenueDTO.getHotelNo() != null) {
            spec = spec.and(RevenueSpecification.equalsRoomHotelNo(revenueDTO.getHotelNo()));
        }
        if (revenueDTO.getAdminNo() != null) {
            spec = spec.and(RevenueSpecification.equalsRoomAdminNo(revenueDTO.getAdminNo()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<RevenueEntity> revenueEntityPage = revenueRepository.findAll(spec, pageable);

//        List<RevenueDTO> dtoList = revenueEntityPage.getContent().stream()
//                .map(entity -> modelMapper.map(entity, RevenueDTO.class))
//                .collect(Collectors.toList());
        List<RevenueDTO> dtoList = revenueEntityPage.getContent().stream()
                .map(entity -> RevenueDTO.builder()
                        .RevenueNo(entity.getRevenueNo())
                        .paymentNo(entity.getPaymentItemEntity().getPaymentEntity().getPaymentNo())
                        .reserveNo(entity.getPaymentItemEntity().getPaymentEntity().getReserveEntity().getReserveNo())
                        .hotelNo(entity.getPaymentItemEntity().getPaymentEntity().getReserveEntity().getRoomEntity().getHotelEntity().getHotelNo())
                        .unitPrice(entity.getPaymentItemEntity().getUnitPrice())
                        .totalPrice(entity.getPaymentItemEntity().getTotalPrice())
                        .paymentCount(entity.getPaymentItemEntity().getPaymentCount())
                        .hotelName(entity.getPaymentItemEntity().getPaymentEntity().getReserveEntity().getRoomEntity().getHotelEntity().getName())
                        .depositPrice(entity.getDepositPrice())
                        .fee(entity.getFee())
                        .email(entity.getPaymentItemEntity().getPaymentEntity().getUserEntity().getEmail())
                        .userName(entity.getPaymentItemEntity().getPaymentEntity().getUserEntity().getName())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .paymentDate(entity.getPaymentDate())
                        .build())
                .collect(Collectors.toList());

        return PageResponseDTO.<RevenueDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)revenueEntityPage.getTotalElements())
                .build();
    }

    public void insertItem(List<RevenueDTO> revenueDTOList) {
        List<RevenueEntity> revenueEntityList = revenueDTOList.stream()
                .map(dto -> modelMapper.map(dto, RevenueEntity.class))
                .toList();
        revenueRepository.saveAll(revenueEntityList);
    }

    public void insertRoom(RevenueDTO revenueDTO) {
        RevenueEntity revenueEntity =  modelMapper.map(revenueDTO, RevenueEntity.class);
        revenueRepository.save(revenueEntity);
    }

    //specification 사용하지 않고 @Query 를 사용해서 검색
    public PageResponseDTO<DailyRevenueDTO> getDailyTotalFeeAndDepositPrice(DailyRevenueDTO dailyRevenueDTO,PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("paymentDate");
        //레포지토리로 찾은 object 객체들을 순서에 맞게 DTO에 삽입
        Page<Object[]> objects = revenueRepository.findSumFeeAndSumDepositPriceGroupByPaymentDateAndUserCount
                (dailyRevenueDTO.getStartDate(),dailyRevenueDTO.getEndDate(),dailyRevenueDTO.getHotelNo(),dailyRevenueDTO.getStoreNo(), dailyRevenueDTO.getAdminNo(), pageable);
        List<DailyRevenueDTO> dtoList = objects.getContent().stream()
                .map(entity -> DailyRevenueDTO.builder()
                        .revenueDate((LocalDate) entity[0])
                        .totalFee((Long) entity[1])
                        .totalDepositPrice((Long) entity[2])
                        .totalFeeWhereItem((Long) entity[3])
                        .totalDepositPriceWhereItem((Long) entity[4])
                        .totalFeeWhereRoom((Long) entity[5])
                        .totalDepositPriceWhereRoom((Long) entity[6])
                        .build())
                .toList();
        return PageResponseDTO.<DailyRevenueDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)objects.getTotalElements())
                .build();
    }

    public TotalRevenueDTO getTotalRevenue(DailyRevenueDTO dailyRevenueDTO) {
        List<Object[]> results = revenueRepository.findSumFeeAndSumDepositPriceGroupByPaymentDateAndUserCountToList(dailyRevenueDTO.getStartDate(), dailyRevenueDTO.getEndDate(), dailyRevenueDTO.getHotelNo(), dailyRevenueDTO.getStoreNo(),dailyRevenueDTO.getAdminNo());

        // 초기화
        long totalFee = 0;
        long totalDepositPrice = 0;
        long totalFeeWhereItem = 0;
        long totalDepositPriceWhereItem = 0;
        long totalFeeWhereRoom = 0;
        long totalDepositPriceWhereRoom = 0;

        // 결과를 순회하며 합계 계산
        for (Object[] result : results) {
            totalFee += (Long) result[1];
            totalDepositPrice += (Long) result[2];
            totalFeeWhereItem += (Long) result[3];
            totalDepositPriceWhereItem += (Long) result[4];
            totalFeeWhereRoom += (Long) result[5];
            totalDepositPriceWhereRoom += (Long) result[6];
        }

        // DTO에 합계 값 설정
        TotalRevenueDTO totalRevenueDTO = new TotalRevenueDTO();
        totalRevenueDTO.setTotalFee(totalFee);
        totalRevenueDTO.setTotalDepositPrice(totalDepositPrice);
        totalRevenueDTO.setTotalFeeWhereItem(totalFeeWhereItem);
        totalRevenueDTO.setTotalDepositPriceWhereItem(totalDepositPriceWhereItem);
        totalRevenueDTO.setTotalFeeWhereRoom(totalFeeWhereRoom);
        totalRevenueDTO.setTotalDepositPriceWhereRoom(totalDepositPriceWhereRoom);

        return totalRevenueDTO;
    }
    public List<Object[]> getTotalRevenueExcel(DailyRevenueDTO dailyRevenueDTO) {
        return revenueRepository.findSumFeeAndSumDepositPriceGroupByPaymentDateAndUserCountToList
                (dailyRevenueDTO.getStartDate(), dailyRevenueDTO.getEndDate(), dailyRevenueDTO.getHotelNo(),
                        dailyRevenueDTO.getStoreNo(), dailyRevenueDTO.getAdminNo());
    }

}
