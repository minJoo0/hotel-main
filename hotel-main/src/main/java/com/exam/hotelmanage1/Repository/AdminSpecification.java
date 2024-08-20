package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.Entity.AdminEntity;
import com.exam.hotelmanage1.Entity.CategoryEntity;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.StoreEntity;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class AdminSpecification {

    public static Specification<AdminEntity> equalsUserid(String userid) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("userid"),"%" + userid + "%");
    }

    public static Specification<AdminEntity> equalsEmail(String email) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("email"),"%" + email + "%");
    }
    public static Specification<AdminEntity> equalsName(String name) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("name"),"%" + name + "%");
    }

    public static Specification<AdminEntity> equalsPhone(String phone) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("phone"),"%" + phone + "%");
    }
    public static Specification<AdminEntity> equalsRole(RoleType roleType) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("roleType"),"%" + roleType.name() + "%");
    }

    public static Specification<AdminEntity> hasCategoryWithParentId() {
        return (root, query, cb) -> {
            Join<AdminEntity, HotelEntity> hotelJoin = root.join("hotels");
            Join<HotelEntity, StoreEntity> storeJoin = hotelJoin.join("stores");
            Join<StoreEntity, CategoryEntity> categoryJoin = storeJoin.join("categories");
            query.distinct(true); // 중복 제거
            return cb.isNotNull(categoryJoin.get("parentId"));
        };
    }

}
