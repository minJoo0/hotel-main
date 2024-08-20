package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.PaymentItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItemEntity,Long>
        , JpaSpecificationExecutor<PaymentItemEntity> {

    List<PaymentItemEntity> findByPaymentEntity_PaymentNo(Long paymentNo);
}
