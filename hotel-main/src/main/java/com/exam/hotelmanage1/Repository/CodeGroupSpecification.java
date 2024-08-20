package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.CodeGroupEntity;
import org.springframework.data.jpa.domain.Specification;

public class CodeGroupSpecification {

    public static Specification<CodeGroupEntity> equalsCodeGroup(String codeGroup) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("codeGroup"), "%" + codeGroup + "%");
    }

    public static Specification<CodeGroupEntity> equalsName(String name) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("name"), "%" + name + "%");
    }
    public static Specification<CodeGroupEntity> equalsAdminNo(Long adminNo) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.equal(root.get("adminEntity").get("adminNo"), adminNo);
    }
}
