package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.FaqEntity;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/*================

작업명:FAQ관리crud
작업자:김민주
작업일:2024-04-12
작업내용:

===============*/
@Repository
public interface FaqRepository extends JpaRepository<FaqEntity,Long>, JpaSpecificationExecutor<FaqEntity> {

    Page<FaqEntity> findAll (Specification<FaqEntity> spec, Pageable pageable);


    /*카테고리값들을 중복없이 추출*/
    @Query("SELECT DISTINCT f.category FROM FaqEntity f")
    List<String> findDistinctCategories();

}
