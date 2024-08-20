package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.Entity.*;
import jakarta.persistence.criteria.Join;
import org.apache.catalina.User;
import org.springframework.data.jpa.domain.Specification;

public class OrderDetailSpecification {

    public static Specification<PaymentEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> {
            Join<PaymentEntity, ReserveEntity> ReserveJoin = root.join("reserveEntity");
            Join<ReserveEntity, RoomEntity> RoomJoin = ReserveJoin.join("roomEntity");
            Join<RoomEntity, HotelEntity> HotelJoin = RoomJoin.join("hotelEntity");
            Join<HotelEntity, AdminEntity> AdminJoin = HotelJoin.join("adminEntity");
            return criteriaBuilder.equal(AdminJoin.get("adminNo"), adminNo);

        };
    }
    public static Specification<PaymentEntity> equalsStoreNo(Long storeNo) {
        return (root, query, criteriaBuilder) -> {
            Join<PaymentEntity, StoreEntity> StoreJoin = root.join("storeEntity");
            return criteriaBuilder.equal(StoreJoin.get("storeNo"), storeNo);

        };
    }
    public static Specification<PaymentEntity> equalsPaymentStatus(PaymentStatus paymentStatus) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus);

        };
    }
    public static Specification<PaymentEntity> equalsHotelName(String name) {
        return (root, query, criteriaBuilder) -> {
            Join<PaymentEntity, ReserveEntity> ReserveJoin = root.join("reserveEntity");
            Join<ReserveEntity, RoomEntity> RoomJoin = ReserveJoin.join("roomEntity");
            Join<RoomEntity, HotelEntity> HotelJoin = RoomJoin.join("hotelEntity");
            return criteriaBuilder.like(HotelJoin.get("name"),"%" + name + "%");


        };
    }
    public static Specification<PaymentEntity> equalsRoomName(String name) {
        return (root, query, criteriaBuilder) -> {
            Join<PaymentEntity, ReserveEntity> ReserveJoin = root.join("reserveEntity");
            Join<ReserveEntity, RoomEntity> RoomJoin = ReserveJoin.join("roomEntity");
            return criteriaBuilder.like(RoomJoin.get("name"),"%" + name + "%");

        };
    }
    public static Specification<PaymentEntity> equalsUserName(String name) {
        return (root, query, criteriaBuilder) -> {
            Join<PaymentEntity, UserEntity> UserJoin = root.join("userEntity");
            return criteriaBuilder.like(UserJoin.get("name"),"%" + name + "%");

        };
    }
    public static Specification<PaymentEntity> equalsUserEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            Join<PaymentEntity, UserEntity> UserJoin = root.join("userEntity");
            return criteriaBuilder.like(UserJoin.get("email"),"%" + email + "%");

        };
    }
    public static Specification<PaymentItemEntity> equalsPaymentNo(Long paymentNo) {
        return (root, query, criteriaBuilder) -> {
            Join<PaymentItemEntity, PaymentEntity> PaymentJoin = root.join("paymentEntity");
            return criteriaBuilder.equal(PaymentJoin.get("paymentNo"), paymentNo);

        };
    }
    public static Specification<PaymentEntity> equalsPaymentType(PaymentType paymentType) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("paymentType"), paymentType);

        };
    }
}
