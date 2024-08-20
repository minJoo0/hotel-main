package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.AdminEntity;
import com.exam.hotelmanage1.Entity.CategoryEntity;
import com.exam.hotelmanage1.Entity.MenuEntity;
import com.exam.hotelmanage1.Entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long>, JpaSpecificationExecutor<CategoryEntity> {



    Page<CategoryEntity> findAll(Specification<CategoryEntity> spec, Pageable pageable);

    @Query("SELECT s FROM CategoryEntity s WHERE s.storeEntity.storeNo = :storeNo")
    List<CategoryEntity> findCategoryByStoreNo(@Param("storeNo") Long storeNo);

    @Query("SELECT s FROM CategoryEntity s WHERE s.storeEntity.storeNo = :storeNo AND s.parent IS NULL")
    List<CategoryEntity> findCategoryByStoreNoAndParentIsNull(@Param("storeNo") Long storeNo);

    @Query("SELECT s FROM CategoryEntity s WHERE s.storeEntity.storeNo = :storeNo AND s.parent IS NOT NULL")
    List<CategoryEntity> findCategoryByStoreNoAndParentIsNotNull(@Param("storeNo") Long storeNo);

    @Query("SELECT c FROM CategoryEntity c WHERE c.parent.categoryNo = :categoryNo")
    List<CategoryEntity> findByParentCategoryNo(Long categoryNo);


}
