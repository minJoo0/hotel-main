package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.Entity.*;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class MenuSpecification {
    
    public static Specification<MenuEntity> equalsName(String name) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("name"),"%" + name + "%");
    }
    public static Specification<MenuEntity> equalsStoreNo(Long storeNo) {
        return (root, query, CriteriaBuilder) -> {
            Join<MenuEntity, CategoryEntity> categoryJoin = root.join("category1");
            Join<CategoryEntity, StoreEntity> storeJoin = categoryJoin.join("storeEntity");
            return CriteriaBuilder.equal(storeJoin.get("storeNo"),storeNo);

        };
    }
    public static Specification<MenuEntity> equalsHotelNo(Long hotelNo) {
        return (root, query, CriteriaBuilder) -> {
            Join<MenuEntity, CategoryEntity> categoryJoin = root.join("category1");
            Join<CategoryEntity, StoreEntity> storeJoin = categoryJoin.join("storeEntity");
            Join<StoreEntity, HotelEntity> hotelJoin = storeJoin.join("hotelEntity");
            return CriteriaBuilder.equal(hotelJoin.get("hotelNo"), hotelNo);
        };
    }
    public static Specification<MenuEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> {
            Join<MenuEntity, CategoryEntity> categoryJoin = root.join("category1");
            Join<CategoryEntity, StoreEntity> storeJoin = categoryJoin.join("storeEntity");
            Join<StoreEntity, HotelEntity> hotelJoin = storeJoin.join("hotelEntity");
            Join<HotelEntity, AdminEntity> adminJoin = hotelJoin.join("adminEntity");
            return criteriaBuilder.equal(adminJoin.get("adminNo"), adminNo);
        };
    }
    public static Specification<MenuEntity> equalsCategory1(Long categoryNo) {
        return (root, query, CriteriaBuilder) -> {
            Join<MenuEntity, CategoryEntity> categoryJoin = root.join("category1");
            return CriteriaBuilder.equal(categoryJoin.get("categoryNo"),categoryNo);

        };
    }
    public static Specification<MenuEntity> equalsCategory2(Long categoryNo) {
        return (root, query, CriteriaBuilder) -> {
            Join<MenuEntity, CategoryEntity> categoryJoin = root.join("category2");
            return CriteriaBuilder.equal(categoryJoin.get("categoryNo"),categoryNo);

        };
    }
}
