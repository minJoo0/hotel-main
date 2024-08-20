package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.ReserveEntity;
import com.exam.hotelmanage1.Entity.RoomEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@RequiredArgsConstructor
public class RoomSpecification {

    //호텔 번호(hotelNo)를 기준으로 객실 엔티티(RoomEntity)를 조회
    public static Specification<RoomEntity> equalsHotelNo(Long hotelNo) {
        return (root, query, criteriaBuilder) -> {
            Join<RoomEntity, HotelEntity> hotelJoin = root.join("hotelEntity");
            return criteriaBuilder.equal(hotelJoin.get("hotelNo"), hotelNo);
        };
    }

    // adminNo와 일치하는 HotelEntity의 adminNo를 기준으로 RoomEntity를 필터링
    public static Specification<RoomEntity> equalsAdminNo (Long adminNo) {
        return (root, query, criteriaBuilder) -> {
            Join<RoomEntity, HotelEntity> hotelJoin = root.join("hotelEntity");
            return criteriaBuilder.equal(hotelJoin.get("adminEntity").get("adminNo"), adminNo);
        };
    }

    //입력한 예약기간에 예약이 가능한 객실 목록
    public static Specification<RoomEntity> isAvailable(LocalDate startDate, LocalDate endDate) {
        return (root, query, builder) -> {
            // 서브쿼리를 사용하여 해당 기간에 예약된 ReserveEntity를 찾습니다.
            Subquery<ReserveEntity> subquery = query.subquery(ReserveEntity.class);
            Root<ReserveEntity> reserveRoot = subquery.from(ReserveEntity.class);

            // 예약이 시작되는 날짜가 검색 기간 후에 시작하거나 예약이 끝나는 날짜가 검색 기간 전에 끝나는 경우를 찾습니다.
            Predicate overlaps = builder.or(
                    builder.greaterThan(reserveRoot.get("startDate"), endDate),
                    builder.lessThan(reserveRoot.get("endDate"), startDate)
            );

            // 서브쿼리는 예약이 겹치지 않는 경우를 찾습니다.
            subquery.select(reserveRoot).where(builder.not(overlaps), builder.equal(reserveRoot.get("roomEntity"), root)); // 현재 RoomEntity와 연결된 예약만 검사합니다.

            // NOT EXISTS를 사용하여, 주어진 기간 동안 예약이 없는 객실을 찾습니다.
            return builder.not(builder.exists(subquery));
        };
    }

}
