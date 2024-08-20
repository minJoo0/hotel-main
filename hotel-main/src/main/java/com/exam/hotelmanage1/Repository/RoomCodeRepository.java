package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.RoomCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomCodeRepository extends JpaRepository<RoomCodeEntity, Long> {

    @Query("SELECT rc FROM RoomCodeEntity rc JOIN rc.roomEntity cge WHERE cge.roomNo = :roomNo")
    List<RoomCodeEntity> findByRoomEntityRoomNo(@Param("roomNo") Long roomNo);

    List<RoomCodeEntity> findByCodeGroupEntityCodeGroupNo(Long codeGroupNo);

}
