package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u.userNo FROM UserEntity u WHERE u.email = :email")
    Long findUserNoByEmail(@Param("email") String email);

}
