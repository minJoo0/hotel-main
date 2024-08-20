package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.RoomImgEntity;
import com.exam.hotelmanage1.Entity.StoreImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreImgRepository extends JpaRepository<StoreImgEntity,Long> {

    List<StoreImgEntity> findByStoreEntity_StoreNoOrderByStoreImgNoAsc(Long storeNo); //select b from BoardImg b where board_bno = :bno order by ino asc;


    Optional<StoreImgEntity> findByStoreEntity_StoreNo(Long storeNo); //select b from BoardImg b where board_bno = :bno order by ino asc;
}
