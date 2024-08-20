package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.DTO.RevenueDTO;
import com.exam.hotelmanage1.Entity.ReserveEntity;
import com.exam.hotelmanage1.Entity.RevenueEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RevenueRepository extends JpaRepository<RevenueEntity,Long> , JpaSpecificationExecutor<RevenueEntity> {
    Page<RevenueEntity> findAll(Specification<RevenueEntity> spec, Pageable pageable);

    //LEFT 조인 사용. INNER 조인으로 할 경우 storeEntity를 조인하는 과정에서 교집합이 아닌 객실 부분의 값이 사라짐.
    @Query("SELECT r.paymentDate, " +
            "COALESCE(SUM(r.fee), 0) as totalFee, " +
            "COALESCE(SUM(r.depositPrice), 0) as totalDepositPrice, " +
            "COALESCE(SUM(CASE WHEN r.userCount IS NULL THEN r.fee ELSE 0 END), 0) as totalFeeWhereUserCountIsNull, " +
            "COALESCE(SUM(CASE WHEN r.userCount IS NULL THEN r.depositPrice ELSE 0 END), 0) as totalDepositPriceWhereUserCountIsNull, " +
            "COALESCE(SUM(CASE WHEN r.userCount IS NOT NULL THEN r.fee END), 0) as totalFeeWhereUserCountIsNotNull, " +
            "COALESCE(SUM(CASE WHEN r.userCount IS NOT NULL THEN r.depositPrice END), 0) as totalDepositPriceWhereUserCountIsNotNull " +
            "FROM RevenueEntity r " +
            "LEFT JOIN r.paymentItemEntity pi " +
            "LEFT JOIN pi.menuEntity m " +
            "LEFT JOIN m.category1 c1 " +
            "LEFT JOIN c1.storeEntity s " +
            "LEFT JOIN pi.paymentEntity p " +
            "LEFT JOIN p.reserveEntity res " +
            "LEFT JOIN res.roomEntity rm " +
            "LEFT JOIN rm.hotelEntity h " +
            "LEFT JOIN h.adminEntity a " +
            "WHERE (:startDate IS NULL OR r.paymentDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.paymentDate <= :endDate) " +
            "AND (:hotelNo IS NULL OR h.hotelNo = :hotelNo) " +
            "AND (:storeNo IS NULL OR s.storeNo = :storeNo) " +
            "AND (:adminNo IS NULL OR a.adminNo = :adminNo) " +
            "GROUP BY r.paymentDate " +
            "ORDER BY r.paymentDate desc")
    Page<Object[]> findSumFeeAndSumDepositPriceGroupByPaymentDateAndUserCount(LocalDate startDate,
                                                                              LocalDate endDate,
                                                                              Long hotelNo,
                                                                              Long storeNo,
                                                                              Long adminNo,
                                                                              Pageable pageable);

    @Query("SELECT r.paymentDate, " +
            "COALESCE(SUM(r.fee), 0) as totalFee, " +
            "COALESCE(SUM(r.depositPrice), 0) as totalDepositPrice, " +
            "COALESCE(SUM(CASE WHEN r.userCount IS NULL THEN r.fee ELSE 0 END), 0) as totalFeeWhereUserCountIsNull, " +
            "COALESCE(SUM(CASE WHEN r.userCount IS NULL THEN r.depositPrice ELSE 0 END), 0) as totalDepositPriceWhereUserCountIsNull, " +
            "COALESCE(SUM(CASE WHEN r.userCount IS NOT NULL THEN r.fee END), 0) as totalFeeWhereUserCountIsNotNull, " +
            "COALESCE(SUM(CASE WHEN r.userCount IS NOT NULL THEN r.depositPrice END), 0) as totalDepositPriceWhereUserCountIsNotNull " +
            "FROM RevenueEntity r " +
            "LEFT JOIN r.paymentItemEntity pi " +
            "LEFT JOIN pi.menuEntity m " +
            "LEFT JOIN m.category1 c1 " +
            "LEFT JOIN c1.storeEntity s " +
            "LEFT JOIN pi.paymentEntity p " +
            "LEFT JOIN p.reserveEntity res " +
            "LEFT JOIN res.roomEntity rm " +
            "LEFT JOIN rm.hotelEntity h " +
            "LEFT JOIN h.adminEntity a " +
            "WHERE (:startDate IS NULL OR r.paymentDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.paymentDate <= :endDate) " +
            "AND (:hotelNo IS NULL OR h.hotelNo = :hotelNo) " +
            "AND (:storeNo IS NULL OR s.storeNo = :storeNo) " +
            "AND (:adminNo IS NULL OR a.adminNo = :adminNo) " +
            "GROUP BY r.paymentDate " +
            "ORDER BY r.paymentDate desc")
    List<Object[]> findSumFeeAndSumDepositPriceGroupByPaymentDateAndUserCountToList(LocalDate startDate,
                                                                              LocalDate endDate,
                                                                              Long hotelNo,
                                                                              Long storeNo,
                                                                                    Long adminNo);
}

