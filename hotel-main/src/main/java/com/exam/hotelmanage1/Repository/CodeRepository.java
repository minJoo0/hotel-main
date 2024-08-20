package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.CodeEntity;
import com.exam.hotelmanage1.Entity.CodeGroupEntity;
import org.aspectj.apache.bcel.classfile.Code;
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
프그램명 : 하위코드 CRUD
작성자 : 김준수
기능 : 코드 repository
작업내용 :
 */

@Repository
public interface CodeRepository extends JpaRepository<CodeEntity, Long>, JpaSpecificationExecutor<CodeEntity> {

    //코드그룹Entity 정보로 해당코드그룹과 관련있는 코드를 조회한다.

    Page<CodeEntity> findAll(Specification<CodeEntity> spec, Pageable pageable);

    List<CodeEntity> findByCodeGroupEntity_CodeGroupNo(Long codeGroupNo);

    @Query("SELECT c.name FROM CodeEntity c WHERE c.codeNo IN :codeNos")
    List<String> findNameByCodeNoIn(@Param("codeNos") List<Long> codeNos);

    @Query("SELECT c.fullCode FROM CodeEntity c WHERE c.codeNo IN :codeNos")
    List<String> findFullCodeByCodeNoIn(@Param("codeNos") List<Long> codeNos);



}
