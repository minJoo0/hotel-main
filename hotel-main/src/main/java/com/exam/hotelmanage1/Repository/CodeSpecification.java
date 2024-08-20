package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.CodeEntity;
import com.exam.hotelmanage1.Entity.CodeGroupEntity;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.StoreEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class CodeSpecification {

    public static Specification<CodeEntity> equalsCode(String code) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("code"), "%" + code + "%");
    }

    public static Specification<CodeEntity> equalsName(String name) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("name"), "%" + name + "%");
    }
    public static Specification<CodeEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> {// StoreEntity와 HotelEntity 사이의 조인을 생성합니다.
            Join<CodeEntity, CodeGroupEntity> codeGroupJoin = root.join("codeGroupEntity");
            return criteriaBuilder.equal(codeGroupJoin.get("adminEntity").get("adminNo"),adminNo);

        };    }


    public static Specification<CodeEntity> equalsCodeGroupNo(Long codeGroupNo) {
        return (root, query, criteriaBuilder) -> {// CodeEntity와 CodeGroupEntity 사이의 조인을 생성합니다.
            Join<CodeEntity, CodeGroupEntity> codeGroupJoin = root.join("codeGroupEntity");

            // 조인된 CodeGroupEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.equal(codeGroupJoin.get("codeGroupNo"),codeGroupNo);

        };
    }

    public static Specification<CodeEntity> equalsCodeGroupName(String name) {
        return (root, query, criteriaBuilder) -> {// CodeEntity와 CodeGroupEntity 사이의 조인을 생성합니다.
            Join<CodeEntity, CodeGroupEntity> codeGroupJoin = root.join("codeGroupEntity");

            // 조인된 CodeGroupEntity의 hotelNo 속성을 기준으로 검색 조건을 생성합니다.
            return criteriaBuilder.like(codeGroupJoin.get("name"),"%" + name + "%");

        };
    }

}
