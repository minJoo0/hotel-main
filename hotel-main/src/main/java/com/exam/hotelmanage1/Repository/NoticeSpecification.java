package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.NoticeEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
@Log4j2
public class NoticeSpecification {

    public static Specification<NoticeEntity> equalsTitle(String title) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("title"),"%" + title + "%");
    }

    public static Specification<NoticeEntity> equalsContent(String content) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("content"),"%" + content + "%");
    }

    public static Specification<NoticeEntity> equalsAdminNo(Long adminNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("adminEntity").get("adminNo"), adminNo);
    }

    public static Specification<NoticeEntity> equalsHotel(Long hotelNo) {
        return (root, query, criteriaBuilder) -> {
            if (hotelNo == null) {
                return criteriaBuilder.isNull(root.get("hotelEntity").get("hotelNo"));
            } else {
                return criteriaBuilder.equal(root.get("hotelEntity").get("hotelNo"), hotelNo);
            }
        };
    }



}
