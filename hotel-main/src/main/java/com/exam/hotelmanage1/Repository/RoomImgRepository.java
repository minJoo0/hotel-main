package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.HotelImgEntity;
import com.exam.hotelmanage1.Entity.RoomImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomImgRepository extends JpaRepository<RoomImgEntity,Long> {

    List<RoomImgEntity> findByRoomEntity_RoomNoOrderByRoomImgNoAsc(Long roomNo); //select b from BoardImg b where board_bno = :bno order by ino asc;


    Optional<RoomImgEntity> findByRoomEntity_RoomNo(Long roomNo); //select b from BoardImg b where board_bno = :bno order by ino asc;
}
