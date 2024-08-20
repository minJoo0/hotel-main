package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.AdminEntity;
import com.exam.hotelmanage1.Entity.MenuEntity;
import com.exam.hotelmanage1.Entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity,Long>, JpaSpecificationExecutor<MenuEntity> {

    Page<MenuEntity> findAll(Specification<MenuEntity> spec, Pageable pageable);
    List<MenuEntity> findByCategory2_CategoryNo(Long categoryNo);

    List<MenuEntity> findByCategory1_CategoryNo(Long categoryNo);
}

