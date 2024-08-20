package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.PaymentEntity;
import com.exam.hotelmanage1.Entity.RegisterTypeEntity;
import com.exam.hotelmanage1.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisterTypeRepository extends JpaRepository<RegisterTypeEntity,Long> {

}
