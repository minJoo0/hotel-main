package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.MenuOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuOptionRepository extends JpaRepository<MenuOptionEntity, Long>, JpaSpecificationExecutor<MenuOptionEntity> {



    // 특정 menuNo를 가진 MenuOptionEntity 리스트 조회
    @Query("SELECT mo FROM MenuOptionEntity mo WHERE mo.menuEntity.menuNo = :menuNo")
    List<MenuOptionEntity> findAllByMenuNo(Long menuNo);



}
