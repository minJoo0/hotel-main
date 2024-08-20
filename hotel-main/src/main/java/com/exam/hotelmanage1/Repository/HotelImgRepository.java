package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.HotelImgEntity;
import com.exam.hotelmanage1.Entity.MenuImgEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelImgRepository extends JpaRepository<HotelImgEntity,Long> {

    List<HotelImgEntity> findByHotelEntity_HotelNoOrderByHotelImgNoAsc(Long hotelNo); //select b from BoardImg b where board_bno = :bno order by ino asc;


    Optional<HotelImgEntity> findByHotelEntity_HotelNo(Long hotelNo); //select b from BoardImg b where board_bno = :bno order by ino asc;
}
