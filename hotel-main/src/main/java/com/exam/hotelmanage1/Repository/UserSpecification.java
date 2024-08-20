package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.Entity.*;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@RequiredArgsConstructor
public class UserSpecification {

    public static Specification<StoreEntity> equalsHotelNo(Long hotelNo) {
        return (root, query, criteriaBuilder) -> {// StoreEntity와 HotelEntity 사이의 조인을 생성합니다.
            Join<StoreEntity, HotelEntity> hotelJoin = root.join("hotelEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(hotelJoin.get("hotelNo"), hotelNo);

        };
    }
    public static Specification<CartEntity> equalsUserNo(Long userNo) {
        return (root, query, criteriaBuilder) -> {//
            Join<CartEntity, UserEntity> UserJoin = root.join("userEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(UserJoin.get("userNo"), userNo);

        };
    }

    public static Specification<PaymentItemEntity> equalsPaymentItemUserNo(Long userNo) {
        return (root, query, criteriaBuilder) -> {//
            Join<PaymentItemEntity, PaymentEntity> PaymentJoin = root.join("paymentEntity");
            Join<PaymentEntity, UserEntity> UserJoin = PaymentJoin.join("userEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(UserJoin.get("userNo"), userNo);

        };
    }
    public static Specification<PaymentEntity> equalsPaymentUserNo(Long userNo) {
        return (root, query, criteriaBuilder) -> {//
            Join<PaymentEntity, UserEntity> UserJoin = root.join("userEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(UserJoin.get("userNo"), userNo);

        };
    }

    public static Specification<PaymentEntity> paymentTypeIsItem(PaymentType ITEM) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.equal(root.get("paymentType"),ITEM);
    }

    public static Specification<UserEntity> equalsName(String name) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("name"),"%" + name + "%");
    }

    public static Specification<UserEntity> equalsEmail(String email) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("email"),"%" + email + "%");
    }

    public static Specification<UserEntity> equalsPhone(String phone) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("phone"),"%" + phone + "%");
    }

    public static Specification<UserEntity> equalsBirth(LocalDate birth) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("birth"),"%" + birth + "%");
    }
}
