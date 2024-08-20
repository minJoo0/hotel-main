package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.Entity.PaymentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,Long>
        , JpaSpecificationExecutor<PaymentEntity> {

    @Query("SELECT p.GrandTotalPrice FROM PaymentEntity p WHERE p.paymentNo = :paymentNo")
    Integer findGrandTotalPriceByPaymentNo(Long paymentNo);

    @Transactional
    @Modifying
    @Query("DELETE FROM PaymentEntity p WHERE p.userEntity.userNo = :userNo")
    void deleteByUserNo(Long userNo);

    @Transactional
    @Modifying
    @Query("DELETE FROM PaymentItemEntity pi WHERE pi.paymentEntity.userEntity.userNo = :userNo")
    void deletePaymentItemsByUserNo(Long userNo);


    Optional<PaymentEntity> findByUserEntityUserNo(Long userNo);

    Optional<PaymentEntity> findByPaymentNo(Long paymentNo);


    @Query("SELECT p FROM PaymentEntity p " +
            "JOIN p.reserveEntity r " +
            "WHERE r.reserveNo = :reserveNo AND p.paymentType = :paymentType AND p.paymentStatus = :paymentStatus")
    List<PaymentEntity> findByReserveNoAndPaymentTypeAndPaymentStatus(@Param("reserveNo") Long reserveNo,
                                                      @Param("paymentType") PaymentType paymentType,@Param("paymentStatus") PaymentStatus paymentStatus);



    @Query("SELECT p.paymentStatus FROM PaymentEntity p WHERE p.paymentNo = :paymentNo")
    PaymentStatus findPaymentStatusByPaymentNo(@Param("paymentNo") Long paymentNo);
}
