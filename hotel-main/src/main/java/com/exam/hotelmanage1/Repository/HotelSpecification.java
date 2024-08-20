package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Constant.HotelType;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.Service.AuthService;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@RequiredArgsConstructor
public class HotelSpecification {


    public static Specification<HotelEntity> equalsName(String name) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("name"),"%" + name + "%");
    }
    public static Specification<HotelEntity> equalsPostNumber(String postNumber) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("postNumber"),"%" + postNumber + "%");
    }
    public static Specification<HotelEntity> equalsTel(String tel) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("tel"),"%" + tel + "%");
    }
    public static Specification<HotelEntity> equalsType(HotelType hotelType) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("hotelType"),"%" + hotelType + "%");
    }
    public static Specification<HotelEntity> equalsSido(String sido) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.equal(root.get("sido"),sido);
    }
    public static Specification<HotelEntity> equalsSigungu(String sigungu) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.equal(root.get("sigungu"),sigungu);
    }
    public static Specification<HotelEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("adminEntity").get("adminNo"), adminNo);
    }

    // 검색어가 시/도, 시/군/구에 포함되어 있는 호텔 검색
    public static Specification<HotelEntity> containsTextInSidoOrSigungu(String searchText) {
        return (Root<HotelEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            String likePattern = "%" + searchText + "%";
            Predicate sidoPredicate = criteriaBuilder.like(root.get("sido"), likePattern);
            Predicate sigunguPredicate = criteriaBuilder.like(root.get("sigungu"), likePattern);
            Predicate addressPredicate = criteriaBuilder.like(root.get("address"), likePattern);
            Predicate roadAddressPredicate = criteriaBuilder.like(root.get("roadAddress"), likePattern);
            return criteriaBuilder.or(sidoPredicate, sigunguPredicate,addressPredicate,roadAddressPredicate);
        };
    }

    public static Specification<HotelEntity> hasStoresWithParentIdCategories(Long adminNo) {
        return (root, query, cb) -> {
            // Store와 Category를 join
            Join<HotelEntity, StoreEntity> stores = root.join("stores");
            Join<StoreEntity, CategoryEntity> categories = stores.join("categories");

            // Category의 parentId가 null이 아닌 조건
            Predicate parentIdIsNotNull = cb.isNotNull(categories.get("parentId"));
            query.distinct(true);

            return cb.and(parentIdIsNotNull);

        };
    }

    //예약 날짜를 입력해 예약이 가능한 객실이 있는 호텔 목록 검색
    public static Specification<HotelEntity> hasAvailableRooms(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            // 호텔과 객실을 조인합니다.
            Join<HotelEntity, RoomEntity> rooms = root.join("roomEntity");
            // 서브쿼리를 생성하여, 입력된 기간 동안 예약된 객실을 찾습니다.
            Subquery<ReserveEntity> reservationSubquery = query.subquery(ReserveEntity.class);
            Root<ReserveEntity> reserveRoot = reservationSubquery.from(ReserveEntity.class);
            reservationSubquery.select(reserveRoot);
            // 서브쿼리 조건: 예약 시작일이 입력된 종료일 이전이고, 예약 종료일이 입력된 시작일 이후인 경우 (겹치는 경우)
            Predicate overlap = cb.and(
                    cb.lessThanOrEqualTo(reserveRoot.get("startDate"), endDate),
                    cb.greaterThanOrEqualTo(reserveRoot.get("endDate"), startDate)
            );
            // 서브쿼리 조건과 객실 연결
            reservationSubquery.where(cb.and(
                    overlap,
                    cb.equal(reserveRoot.get("roomEntity"), rooms)
            ));
            // 메인 쿼리에서, 서브쿼리에 해당하는 객실을 제외하는 조건을 적용합니다.
            Predicate notReserved = cb.not(cb.exists(reservationSubquery));
            // 중복된 호텔을 제거하기 위해 DISTINCT를 적용합니다.
            query.distinct(true);
            // 최종적으로, 예약되지 않은 객실이 있는 호텔을 선택합니다.
            return cb.and(notReserved);
        };
    }


}
