package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long>, JpaSpecificationExecutor<RoomEntity> {

    //룸 테이블에서 호텔번호에 해당하는 룸을 조회
    @Query("select r FROM RoomEntity r WHERE r.hotelEntity.hotelNo = :hotelNo")
    List<RoomEntity> findRoomsByHotelNo(@Param("hotelNo") Long hotelNo);

    @Query("select r FROM RoomEntity r WHERE r.roomNo = :roomNo")
    RoomEntity findRoomByRoomNo(@Param("roomNo") Long roomNo);


    //호텔번호를 기반으로 방 엔티티의 갯수 계산
    @Query("SELECT COUNT(r) FROM RoomEntity r WHERE r.hotelEntity.hotelNo = :hotelNo")
    Long countByHotelEntityHotelNo(@Param("hotelNo") Long hotelNo);


}
