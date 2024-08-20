package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*================

작업명:공지사항관리crud
작업자:김민주
작업일:2024-04-09
작업내용:
페이징,검색정상0412
===============*/
@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity,Long>
        , JpaSpecificationExecutor<NoticeEntity> {

    Page<NoticeEntity> findAll (Specification<NoticeEntity> spec, Pageable pageable);

    List<NoticeEntity> findByHotelEntity_Sido(String sido);
    /*호텔no으로조회*/
    List<NoticeEntity> findByHotelEntity_HotelNo(Long hotelNo);

    /*hotelNo을 통해서 name 가져오기*/
    @Query("SELECT h.name FROM NoticeEntity n JOIN n.hotelEntity h WHERE n.noticeNo = :noticeNo")
    String findHotelNameByNoticeNo(@Param("noticeNo") Long noticeNo);


    /*notice에 존재하는 hotel no를 중복없이 가져오기*/
    @Query("SELECT DISTINCT n.hotelEntity FROM NoticeEntity n WHERE n.hotelEntity IS NOT NULL")
    List<HotelEntity> findDistinctHotelNos();


}
