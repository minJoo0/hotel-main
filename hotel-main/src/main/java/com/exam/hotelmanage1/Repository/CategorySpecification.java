package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.*;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class CategorySpecification {
    public static Specification<CategoryEntity> equalsName(String name) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("name"),"%" + name + "%");
    }
    public static Specification<CategoryEntity> equalsHotelNo(Long hotelNo) {
        return (root, query, criteriaBuilder) -> {
            Join<CategoryEntity, StoreEntity> storeJoin = root.join("storeEntity");
            Join<StoreEntity, HotelEntity> hotelJoin = storeJoin.join("hotelEntity");
            return criteriaBuilder.equal(hotelJoin.get("hotelNo"), hotelNo);
        };
    }
    public static Specification<CategoryEntity> equalsStoreNo(Long storeNo) {
        return (root, query, criteriaBuilder) -> {
            Join<CategoryEntity, StoreEntity> storeJoin = root.join("storeEntity");
            return criteriaBuilder.equal(storeJoin.get("storeNo"), storeNo);
        };
    }
    public static Specification<CategoryEntity> equalsParentId(Long parentId) {
        return (root, query, criteriaBuilder) -> {
            // 'parent' 엔티티에 조인합니다.
            Join<CategoryEntity, CategoryEntity> parentJoin = root.join("parent");
            // parent 엔티티의 'categoryNo' 필드와 parentId를 비교합니다.
            return criteriaBuilder.equal(parentJoin.get("categoryNo"), parentId);
        };
    }
    public static Specification<CategoryEntity> hasParent() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("parent"));
    }
    public static Specification<CategoryEntity> hasNotParent() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("parent"));
    }
    public static Specification<CategoryEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> {
            Join<CategoryEntity, StoreEntity> storeJoin = root.join("storeEntity");
            Join<StoreEntity, HotelEntity> hotelJoin = storeJoin.join("hotelEntity");
            Join<HotelEntity, AdminEntity> adminJoin = hotelJoin.join("adminEntity");
            return criteriaBuilder.equal(adminJoin.get("adminNo"), adminNo);
        };
    }
}
