package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Constant.HotelType;
import com.exam.hotelmanage1.Constant.StoreType;
import com.exam.hotelmanage1.Entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@RequiredArgsConstructor
public class StoreSpecification {
    public static Specification<StoreEntity> equalsName(String name) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("name"),"%" + name + "%");
    }
    public static Specification<StoreEntity> equalsAddress(String address) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("address"),"%" + address + "%");
    }
    public static Specification<StoreEntity> equalsBusinessHours(String businessHours) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("businessHours"),"%" + businessHours + "%");
    }
    public static Specification<StoreEntity> equalsTel(String tel) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("tel"),"%" + tel + "%");
    }
    public static Specification<StoreEntity> equalsType(StoreType storeType) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.equal(root.get("storeType"),storeType);
    }
    public static Specification<StoreEntity> equalsHotelNo(Long hotelNo) {
        return (root, query, criteriaBuilder) -> {// StoreEntity와 HotelEntity 사이의 조인을 생성합니다.
            Join<StoreEntity, HotelEntity> hotelJoin = root.join("hotelEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(hotelJoin.get("hotelNo"), hotelNo);
            
        };
    }
    public static Specification<StoreEntity> equalsHotelName(String name) {
        return (root, query, criteriaBuilder) -> {// StoreEntity와 HotelEntity 사이의 조인을 생성합니다.
            Join<StoreEntity, HotelEntity> hotelJoin = root.join("hotelEntity");

            // 조인된 HotelEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.like(hotelJoin.get("name"),"%" + name + "%");

        };
    }
    public static Specification<StoreEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> {
            Join<StoreEntity, HotelEntity> hotelJoin = root.join("hotelEntity");
            return criteriaBuilder.equal(hotelJoin.get("adminEntity").get("adminNo"), adminNo);
        };
    }


    public static Specification<StoreEntity> hasValidReservationsForToday(Long userNo, LocalDate today) {
        return (root, query, criteriaBuilder) -> {
            // Store와 Hotel에 대한 조인을 수행
            Join<StoreEntity, HotelEntity> storeHotelJoin = root.join("hotelEntity");
            // Hotel과 Room에 대한 조인을 수행
            Join<HotelEntity, RoomEntity> hotelRoomJoin = storeHotelJoin.join("roomEntity");
            // Room과 Reservation에 대한 조인을 수행
            Join<RoomEntity, ReserveEntity> roomReservationJoin = hotelRoomJoin.join("reserveEntities");
            // Reservation과 User에 대한 조인을 수행
            Join<ReserveEntity, UserEntity> reservationUserJoin = roomReservationJoin.join("userEntity");

            // 오늘 날짜가 예약 기간 내인 조건
            Predicate reservationDatePredicate = criteriaBuilder.and(
                    criteriaBuilder.lessThanOrEqualTo(roomReservationJoin.get("startDate"), today),
                    criteriaBuilder.greaterThanOrEqualTo(roomReservationJoin.get("endDate"), today)
            );

            // userNo에 해당하는 사용자의 예약만 고려하는 조건
            Predicate userNoPredicate = criteriaBuilder.equal(reservationUserJoin.get("userNo"), userNo);

            // 조건들을 모두 결합
            Predicate finalPredicate = criteriaBuilder.and(reservationDatePredicate, userNoPredicate);

            // DISTINCT를 사용하여 중복을 제거
            query.distinct(true);

            return finalPredicate;
        };
    }



}

