package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.ReserveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReserveRepository extends JpaRepository<ReserveEntity, Long>, JpaSpecificationExecutor<ReserveEntity> {
    //객실별
    //startDate가 기존 예약의 시작과 종료 날짜 사이에 있거나, endDate가 기존 예약의 시작과 종료 날짜 사이에 있는 예약조회
    //예약을 할 때 날짜를 지정하고 겹치는 이 부분은 나오지 않게 만든다.
    @Query("SELECT e FROM ReserveEntity e WHERE " +
            "((e.startDate <= :startDate and e.endDate > :startDate) "+ //시작날짜가 기존 예약일 안에 포함되는가
            "or (e.startDate <= :endDate and e.endDate > :endDate)) "+  //끝나는 날짜가 기존 예약일 안에 포함되는가
            "and e.roomEntity.roomNo = :roomNo") // 특정 객실 번호에 대한 조건 추가

    Optional<ReserveEntity> revDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                    @Param("roomNo") Long roomNo);



    //reserveEntity에서 userNo와 hotelNo가 무엇인지 검색
    @Query("SELECT r FROM ReserveEntity r WHERE r.userEntity.userNo = :userNo AND r.roomEntity.hotelEntity.hotelNo = :hotelNo")
    List<ReserveEntity> findByUserNoAndHotelNo(@Param("userNo") Long userNo, @Param("hotelNo") Long hotelNo);



    //@Query("SELECT r from ReserveEntity r where r.roomEntity.hotelEntity.hotelNo = :hotelNo")




}
