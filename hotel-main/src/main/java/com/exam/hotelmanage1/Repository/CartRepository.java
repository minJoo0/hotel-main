package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.CartEntity;
import com.exam.hotelmanage1.Entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartEntity,Long>, JpaSpecificationExecutor<CartEntity> {

    List<CartEntity> findByUserEntityUserNo(Long userNo);

    @Transactional
    void deleteByUserEntity_UserNo(Long userNo);



}
