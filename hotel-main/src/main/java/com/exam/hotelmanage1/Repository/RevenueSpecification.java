package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Constant.ReservationStatus;
import com.exam.hotelmanage1.Entity.*;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@RequiredArgsConstructor
public class RevenueSpecification {

    public static Specification<RevenueEntity> MenuNoIsNotNull() {
        return (root, query, criteriaBuilder) -> {
            Join<RevenueEntity, PaymentItemEntity> paymentJoin = root.join("paymentItemEntity");
            return criteriaBuilder.isNotNull(paymentJoin.get("menuEntity").get("menuNo"));
        };
    }

    public static Specification<RevenueEntity> MenuNoIsNull() {
        return (root, query, criteriaBuilder) -> {
            Join<RevenueEntity, PaymentItemEntity> paymentJoin = root.join("paymentItemEntity");
            return criteriaBuilder.isNull(paymentJoin.get("menuEntity").get("menuNo"));
        };
    }

    public static Specification<RevenueEntity> equalsPaymentDate(LocalDate paymentDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paymentDate"), paymentDate);
    }
    public static Specification<RevenueEntity> equalsEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            Join<RevenueEntity, PaymentItemEntity> paymentItemJoin = root.join("paymentItemEntity");
            Join<PaymentItemEntity, PaymentEntity> paymentJoin = paymentItemJoin.join("paymentEntity");
            Join<PaymentEntity, UserEntity> userJoin = paymentJoin.join("userEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.like(userJoin.get("email"),"%" + email + "%");

        };
    }
    public static Specification<RevenueEntity> equalsUserName(String name) {
        return (root, query, criteriaBuilder) -> {
            Join<RevenueEntity, PaymentItemEntity> paymentItemJoin = root.join("paymentItemEntity");
            Join<PaymentItemEntity, PaymentEntity> paymentJoin = paymentItemJoin.join("paymentEntity");
            Join<PaymentEntity, UserEntity> userJoin = paymentJoin.join("userEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.like(userJoin.get("name"),"%" + name + "%");

        };
    }

    public static Specification<RevenueEntity> equalsItemHotelNo(Long hotelNo) {
        return (root, query, criteriaBuilder) -> {
            Join<RevenueEntity, PaymentItemEntity> paymentItemJoin = root.join("paymentItemEntity");
            Join<PaymentItemEntity, MenuEntity> menuJoin = paymentItemJoin.join("menuEntity");
            Join<MenuEntity, CategoryEntity> categoryJoin = menuJoin.join("category1");
            Join<CategoryEntity, StoreEntity> storeJoin = categoryJoin.join("storeEntity");
            Join<StoreEntity, HotelEntity> hotelJoin = storeJoin.join("hotelEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(hotelJoin.get("hotelNo"),hotelNo);

        };
    }
    public static Specification<RevenueEntity> equalsItemAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> {
            Join<RevenueEntity, PaymentItemEntity> paymentItemJoin = root.join("paymentItemEntity");
            Join<PaymentItemEntity, MenuEntity> menuJoin = paymentItemJoin.join("menuEntity");
            Join<MenuEntity, CategoryEntity> categoryJoin = menuJoin.join("category1");
            Join<CategoryEntity, StoreEntity> storeJoin = categoryJoin.join("storeEntity");
            Join<StoreEntity, HotelEntity> hotelJoin = storeJoin.join("hotelEntity");
            Join<HotelEntity, AdminEntity> adminJoin = hotelJoin.join("adminEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(adminJoin.get("adminNo"),adminNo);

        };
    }

    public static Specification<RevenueEntity> equalsItemStoreNo(Long storeNo) {
        return (root, query, criteriaBuilder) -> {
            Join<RevenueEntity, PaymentItemEntity> paymentItemJoin = root.join("paymentItemEntity");
            Join<PaymentItemEntity, MenuEntity> menuJoin = paymentItemJoin.join("menuEntity");
            Join<MenuEntity, CategoryEntity> categoryJoin = menuJoin.join("category1");
            Join<CategoryEntity, StoreEntity> storeJoin = categoryJoin.join("storeEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(storeJoin.get("storeNo"),storeNo);

        };
    }
    public static Specification<RevenueEntity> equalsRoomHotelNo(Long hotelNo) {
        return (root, query, criteriaBuilder) -> {
            Join<RevenueEntity, PaymentItemEntity> paymentItemJoin = root.join("paymentItemEntity");
            Join<PaymentItemEntity, PaymentEntity> paymentJoin = paymentItemJoin.join("paymentEntity");
            Join<PaymentEntity, ReserveEntity> menuJoin = paymentJoin.join("reserveEntity");
            Join<ReserveEntity, RoomEntity> categoryJoin = menuJoin.join("roomEntity");
            Join<RoomEntity, HotelEntity> storeJoin = categoryJoin.join("hotelEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(storeJoin.get("hotelNo"),hotelNo);

        };
    }
    public static Specification<RevenueEntity> equalsRoomAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> {
            Join<RevenueEntity, PaymentItemEntity> paymentItemJoin = root.join("paymentItemEntity");
            Join<PaymentItemEntity, PaymentEntity> paymentJoin = paymentItemJoin.join("paymentEntity");
            Join<PaymentEntity, ReserveEntity> menuJoin = paymentJoin.join("reserveEntity");
            Join<ReserveEntity, RoomEntity> categoryJoin = menuJoin.join("roomEntity");
            Join<RoomEntity, HotelEntity> storeJoin = categoryJoin.join("hotelEntity");
            Join<HotelEntity, AdminEntity> adminJoin = storeJoin.join("adminEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(adminJoin.get("adminNo"),adminNo);

        };
    }

    public static Specification<RevenueEntity> betweenRevenueDate(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("paymentDate"), startDate, endDate);
            } else {
                return criteriaBuilder.conjunction(); // 날짜가 제공되지 않으면 빈 컨젝션을 반환
            }
        };
    }

}
