package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.MenuImgEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface MenuImgRepository extends JpaRepository<MenuImgEntity,Long> {


    List<MenuImgEntity> findByMenuEntity_MenuNoOrderByMenuImgNoAsc(Long menuNo); //select b from BoardImg b where board_bno = :bno order by ino asc;


    Optional<MenuImgEntity> findByMenuEntity_MenuNo(Long menuNo); //select b from BoardImg b where board_bno = :bno order by ino asc;
}
