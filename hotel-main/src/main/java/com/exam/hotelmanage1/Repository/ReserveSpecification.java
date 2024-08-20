package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.Constant.ReservationStatus;
import com.exam.hotelmanage1.Entity.*;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@RequiredArgsConstructor
public class ReserveSpecification {

    //예약 상태에 따라 검색
    public static Specification<ReserveEntity> equalsStatus(ReservationStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    //시작일 이후를 검색
    public static Specification<ReserveEntity> isAfterStartDate(LocalDate startDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
    }

    //예약 종료일 이전 검색
    public static Specification<ReserveEntity> isBeforeEndDate(LocalDate endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate);
    }

    // 사용자가 지정한 시작일 이후, 종료일 이전의 예약을 검색
    public static Specification<ReserveEntity> isBetweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), endDate),
                criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), startDate)
        );
    }

    //호텔이름으로 예약 검색
    public static Specification<ReserveEntity> hotelNameContains(String hotelName) {
        return (root, query, criteriaBuilder) -> {
            // ReserveEntity와 RoomEntity 사이의 조인을 생성합니다.
            Join<ReserveEntity, RoomEntity> roomJoin = root.join("roomEntity");
            // 조인된 RoomEntity로부터 다시 HotelEntity로의 조인을 생성합니다.
            Join<RoomEntity, HotelEntity> hotelJoin = roomJoin.join("hotelEntity");

            // 조인된 HotelEntity의 name 속성을 기준으로 호텔 이름이 포함되는지 검색 조건을 생성합니다.
            // 호텔 이름이 대소문자를 구분하지 않도록 lower 함수를 사용합니다.
            return criteriaBuilder.like(criteriaBuilder.lower(hotelJoin.get("name")), "%" + hotelName.toLowerCase() + "%");
        };
    }

    //객실이름을 예약 검색
    public static Specification<ReserveEntity> roomNameContains(String roomName) {
        return (root, query, criteriaBuilder) -> {
            // ReserveEntity와 RoomEntity 사이의 조인을 생성합니다.
            Join<ReserveEntity, RoomEntity> roomJoin = root.join("roomEntity");

            return criteriaBuilder.like(criteriaBuilder.lower(roomJoin.get("name")), "%" + roomName + "%");
        };
    }

    //유저email로 검색
    public static Specification<ReserveEntity> useEmailContains(String userEmail) {
        return (root, query, criteriaBuilder) -> {
            // ReserveEntity와 RoomEntity 사이의 조인을 생성합니다.
            Join<ReserveEntity, UserEntity> userJoin = root.join("userEntity");

            return criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")), "%" + userEmail + "%");
        };
    }


    //객실 일련번호에 대한 예약 필터링
    public static Specification<ReserveEntity> equalsRoomNo(Long roomNo) {
        return (root, query, criteriaBuilder) -> {// ReserveEntity와 RoomEntity 사이의 조인을 생성합니다.
            Join<ReserveEntity, RoomEntity> roomJoin = root.join("roomEntity");

            // 조인된 RoomEntity의 roomNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(roomJoin.get("roomNo"), roomNo);
        };
    }

    //유저번호에 대한 예약 필터링
    public static Specification<ReserveEntity> equalsUserNo(Long userNo) {
        return (root, query, criteriaBuilder) -> {// ReserveEntity와 UserEntity 사이의 조인을 생성합니다.
            Join<ReserveEntity, UserEntity> userJoin = root.join("userEntity");

            // 조인된 UserEntity userNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(userJoin.get("userNo"), userNo);

        };
    }

    //호텔번호로 예약 목록 필터링
    public static Specification<ReserveEntity> equalsHotelNo(Long hotelNo) {
        return (root, query, criteriaBuilder) -> {// ReserveEntity와 RoomEntity 사이의 조인을 생성합니다.
            Join<ReserveEntity, RoomEntity> roomJoin = root.join("roomEntity");
            return criteriaBuilder.equal(roomJoin.get("hotelEntity").get("hotelNo"), hotelNo);
        };
    }
    public static Specification<ReserveEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> {// ReserveEntity와 RoomEntity 사이의 조인을 생성합니다.
            Join<ReserveEntity, RoomEntity> roomJoin = root.join("roomEntity");
            Join<RoomEntity, HotelEntity> hotelJoin = roomJoin.join("hotelEntity");
            return criteriaBuilder.equal(hotelJoin.get("adminEntity").get("adminNo"), adminNo);
        };
    }

    public static Specification<ReserveEntity> paymentTypeIsRoom(PaymentType paymentType) {
        return (root, query, criteriaBuilder) -> {
            Join<ReserveEntity, PaymentEntity> paymentJoin = root.join("paymentEntity");
            return criteriaBuilder.equal(paymentJoin.get("paymentType"), paymentType);
        };
    }
}
