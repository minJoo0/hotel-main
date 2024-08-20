package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.AdminEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity,Long>, JpaSpecificationExecutor<AdminEntity> {
    Optional<AdminEntity> findByUserid(String userid);

    Page<AdminEntity> findAll(Specification<AdminEntity> spec, Pageable pageable);


}
