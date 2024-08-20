package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.FaqEntity;
import com.exam.hotelmanage1.Entity.NoticeEntity;
import org.springframework.data.jpa.domain.Specification;

public class FaqSpecification {

    public static Specification<FaqEntity> equalsTitle(String title) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("title"),"%" + title + "%");
    }

    public static Specification<FaqEntity> equalsContent(String content) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("content"),"%" + content + "%");
    }

    public static Specification<FaqEntity> equalsCategory(String category) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("category"),"%"+category+"%");
    }

    public static Specification<FaqEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("adminEntity").get("adminNo"), adminNo);
    }

    public static Specification<FaqEntity> or(Specification<FaqEntity> spec1, Specification<FaqEntity> spec2) {
        return Specification.where(spec1).or(spec2);
    }

}
