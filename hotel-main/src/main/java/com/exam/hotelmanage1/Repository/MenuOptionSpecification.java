package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.*;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class MenuOptionSpecification {

    public static Specification<MenuOptionEntity> equalsName(String name) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("name"),"%" + name + "%");
    }
    public static Specification<MenuOptionEntity> equalsMenuNo(Long menuNo) {
        return (root, query, CriteriaBuilder) -> {
            Join<MenuOptionEntity, MenuEntity> menuJoin = root.join("menuEntity");
            return CriteriaBuilder.equal(menuJoin.get("menuNo"),menuNo);

        };
    }
    public static Specification<MenuOptionEntity> equalsStoreNo(Long storeNo) {
        return (root, query, CriteriaBuilder) -> {
            Join<MenuOptionEntity, MenuEntity> menuJoin = root.join("menuEntity");
            Join<MenuEntity, CategoryEntity> categoryJoin = menuJoin.join("category1");
            Join<CategoryEntity, StoreEntity> storeJoin = categoryJoin.join("storeEntity");
            return CriteriaBuilder.equal(storeJoin.get("storeNo"), storeNo);
        };
    }
    public static Specification<MenuOptionEntity> equalsHotelNo(Long hotelNo) {
        return (root, query, criteriaBuilder) -> {
            Join<MenuOptionEntity, MenuEntity> menuJoin = root.join("menuEntity");
            Join<MenuEntity, CategoryEntity> categoryJoin = menuJoin.join("category1");
            Join<CategoryEntity, StoreEntity> storeJoin = categoryJoin.join("storeEntity");
            Join<StoreEntity, HotelEntity> hotelJoin = storeJoin.join("hotelEntity");
            return criteriaBuilder.equal(hotelJoin.get("hotelNo"), hotelNo);
        };
    }
    public static Specification<MenuOptionEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> {
            Join<MenuOptionEntity, MenuEntity> menuJoin = root.join("menuEntity");
            Join<MenuEntity, CategoryEntity> categoryJoin = menuJoin.join("category1");
            Join<CategoryEntity, StoreEntity> storeJoin = categoryJoin.join("storeEntity");
            Join<StoreEntity, HotelEntity> hotelJoin = storeJoin.join("hotelEntity");
            Join<HotelEntity, AdminEntity> adminJoin = hotelJoin.join("adminEntity");
            return criteriaBuilder.equal(adminJoin.get("adminNo"), adminNo);
        };
    }
    public static Specification<MenuOptionEntity> equalsCategory1(Long categoryNo) {
        return (root, query, CriteriaBuilder) -> {
            Join<MenuOptionEntity, MenuEntity> menuJoin = root.join("menuEntity");
            Join<MenuEntity, CategoryEntity> categoryJoin = menuJoin.join("category1");
            return CriteriaBuilder.equal(categoryJoin.get("categoryNo"),categoryNo);

        };
    }
    public static Specification<MenuOptionEntity> equalsCategory2(Long categoryNo) {
        return (root, query, CriteriaBuilder) -> {
            Join<MenuOptionEntity, MenuEntity> menuJoin = root.join("menuEntity");
            Join<MenuEntity, CategoryEntity> categoryJoin = menuJoin.join("category2");
            return CriteriaBuilder.equal(categoryJoin.get("categoryNo"),categoryNo);

        };
    }
}
