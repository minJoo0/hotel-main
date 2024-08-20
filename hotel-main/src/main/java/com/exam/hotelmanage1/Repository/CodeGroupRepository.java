package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.CodeEntity;
import com.exam.hotelmanage1.Entity.CodeGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
프그램명 : 분류 코드 그룹 CRUD
작성자 : 김준수
기능 : 코드그룹 Repository
      코드 그룹에 join된 하위코드 갯수
작업내용 :
 */
@Repository

public interface CodeGroupRepository extends JpaRepository<CodeGroupEntity, Long>, JpaSpecificationExecutor<CodeGroupEntity> {

    //코드 그룹에 연결된 하위코드의 갯수 카운트 쿼리
    //SELECT cg.id, COUNT(c.id)
    //FROM CodeGroupEntity cg
    //LEFT JOIN cg.codeEntities c
    //GROUP BY cg.id;
    @Query("SELECT cg.codeGroupNo, COUNT(c) FROM CodeGroupEntity cg LEFT JOIN cg.codeEntities c GROUP BY cg.codeGroupNo")
    List<Object[]> countCodesPerGroup();

    @Query("SELECT COUNT(c) FROM CodeEntity c WHERE c.codeGroupEntity.codeGroupNo = :codeGroupNo")
    Long countByCodeGroupEntityCodeGroupNo(@Param("codeGroupNo") Long codeGroupNo);

    @Query("SELECT c.codeGroup FROM CodeGroupEntity c WHERE c.codeGroupNo = :codeGroupNo")
    String findCodeGroupByCodeGroupNo(@Param("codeGroupNo") Long codeGroupNo);

    List<CodeGroupEntity> findByAdminEntity_AdminNo(Long adminNo);


    Page<CodeGroupEntity> findAll(Specification<CodeGroupEntity> spec, Pageable pageable);

    @Query("SELECT c.name FROM CodeGroupEntity c WHERE c.codeGroupNo IN :codeGroupNos")
    List<String> findNameByCodeNoIn(@Param("codeGroupNos") List<Long> codeGroupNos);

    @Query("SELECT c.orderIdx FROM CodeGroupEntity c WHERE c.codeGroupNo IN :codeGroupNos")
    List<Integer> findOrderIdxByCodeNoIn(@Param("codeGroupNos") List<Long> codeGroupNos);



}
